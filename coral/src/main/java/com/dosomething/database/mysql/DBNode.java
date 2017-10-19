package com.dosomething.database.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.apache.juli.logging.Log;
//import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.DataSource;

public class DBNode {
	private int id;
	private String name;
	private volatile DataSource ds;
	private volatile DataSource dsMonitor;
	private volatile boolean enabled;
	private boolean healthMonitor;
	private DBConnector connector;
	private AtomicInteger failCount = new AtomicInteger(0);
	private ExecutorService mainES = Executors.newSingleThreadExecutor(new DaemonThreadFactory("DBNode"));
	private ExecutorService healthMonitorES = Executors.newSingleThreadExecutor(new DaemonThreadFactory("HealthMonitor"));
	private final static int SUCCESS = 0;
	private final static int ERROR = 1;
	private final long retryMillisecond = 60000;

	private static final Logger logger = Logger.getLogger(DBNode.class.getName());
	public static final String HEALTH_MONITOR_POOL_NAME = "HealthMonitor Pool ";

	protected DBNode(int id, String name, DataSource ds, boolean healthMonitor, DBConnector connector) {
		this.id = id;
		this.name = name;
		this.ds = ds;
		this.healthMonitor = healthMonitor;
		this.connector = connector;

		if (!this.healthMonitor)
			return;

		try {
			initMonitorDataSource();
		} catch (Exception ex) {
			logger.log(Level.INFO, "error occured while trying to init monitor datasource " + getName(), ex);
//			ex.printStackTrace();
		}

		Runnable r = new Runnable() {
			public void run() {
				while (true) {
					boolean monitorExp = false;
					boolean DBAExp = false;
					if (DBNode.this.dsMonitor == null) {
						logger.info("monitor is null, skipping conn test " + DBNode.this.getName());
						monitorExp = true;
					} else {
						try {
							FutureTask<Integer> task = new FutureTask<Integer>(new healthMonitorCallable(DBNode.this.getId()));
							healthMonitorES.submit(task);
							try {
								int taskStatus = task.get(4L, TimeUnit.SECONDS);
								if (taskStatus == DBNode.ERROR) {
									DBAExp = true;
								} else if (taskStatus == DBNode.SUCCESS && !DBNode.this.isEnabled()) {
									DBNode.this.reinitalizeDataSource();
								}
							} catch (Exception ex2) {
//								ex2.printStackTrace();
								logger.log(Level.FINE, ex2.toString(), ex2);
								monitorExp = true;
								task.cancel(true);
							}
						} catch (Exception ex) {
//							ex.printStackTrace();
							logger.log(Level.FINE, ex.toString(), ex);
							monitorExp = true;
						}
					}
					try {
						if (monitorExp) {
							failCount.incrementAndGet();
							logger.warning(getGMTDatetime() + " - " + DBNode.this.getName() + " -- Exception detected db node TIMEOUT 4sec FAIL_COUNT [" + DBNode.this.failCount.get()
									+ "]");
							if (DBNode.this.failCount.get() >= 5) {
								if (DBNode.this.isEnabled()) {
									logger.warning("disabling node : " + DBNode.this.getName());
									DBNode.this.setEnabled(false);
								}
								logger.warning(getGMTDatetime() + " - " + DBNode.this.getName() + " -- Exception ASSUME db node DOWN. FAIL_COUNT [" + DBNode.this.failCount
										+ "]. Retrying every " + (retryMillisecond / 1000) + " seconds.");
								Thread.sleep(retryMillisecond);
								DBNode.this.initMonitorDataSource();
							}
						} else if (DBAExp) {
							if (DBNode.this.isEnabled()) {
								logger.warning("disabling node : " + DBNode.this.getName());
								DBNode.this.setEnabled(false);
							}
							logger.warning(getGMTDatetime() + " - " + DBNode.this.getName()
									+ " -- Exception ASSUME db node DOWN. Error infomation from table available_nodes. Retrying every " + (retryMillisecond / 1000) + " seconds.");
							Thread.sleep(retryMillisecond);
							DBNode.this.initMonitorDataSource();
						}
						Thread.sleep(2000L);
					} catch (Exception ex1) {
//						ex1.printStackTrace();
						logger.log(Level.FINE, ex1.toString(), ex1);
					}
				}
			}
		};
		mainES.execute(r);

	}

	class healthMonitorCallable implements Callable<Integer> {
		private int nodeID = 0;

		healthMonitorCallable(int nodeID) {
			this.nodeID = nodeID;
		}

//		@Override
		public Integer call() throws Exception {
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				conn = DBNode.this.dsMonitor.getConnection();
				pstmt = conn.prepareStatement("select status from available_nodes where id = ? ");
				pstmt.setInt(1, this.nodeID);
				rs = pstmt.executeQuery();
				int status = -1;
				if (rs.next()) {
					status = rs.getInt("status");
				}
				if (status == DBNode.SUCCESS) {
					DBNode.this.failCount.set(0);
					return DBNode.SUCCESS;
				} else {
					return DBNode.ERROR;
				}
			} catch (Exception e) {
				throw e;
			} finally {
				// @formatter:off  just ignore eclipse code format (enable eclipse off/on tags)
				if(rs!=null){ try {rs.close(); rs = null;} catch (SQLException e) {}}
				if(pstmt!=null){ try {pstmt.close(); pstmt = null;} catch (SQLException e) {}}
				if(conn!=null){ try {conn.close(); conn = null;} catch (SQLException e) {}}
				// @formatter:on
			}
		}

	}

	private void initMonitorDataSource() throws Exception {
		if (this.dsMonitor != null) {
			this.dsMonitor.close(true);
			this.dsMonitor = null;
		}
		this.dsMonitor = new DataSource();
		this.dsMonitor.setName(DBNode.HEALTH_MONITOR_POOL_NAME + "\t" + this.name);
		this.dsMonitor.setUsername(this.ds.getUsername());
		this.dsMonitor.setPassword(this.ds.getPoolProperties().getPassword());
		this.dsMonitor.setDriverClassName(this.ds.getDriverClassName());
		this.dsMonitor.setInitialSize(3);
		this.dsMonitor.setMaxActive(3);
		this.dsMonitor.setMinIdle(3);
		this.dsMonitor.setMaxIdle(3);
		this.dsMonitor.setMaxWait(5000);
		this.dsMonitor.setLogAbandoned(false);
		this.dsMonitor.setRemoveAbandoned(true);
		this.dsMonitor.setRemoveAbandonedTimeout(10);
		this.dsMonitor.setValidationQuery("");
		this.dsMonitor.setNumTestsPerEvictionRun(0);
		this.dsMonitor.setTimeBetweenEvictionRunsMillis(60 * 1000);
		this.dsMonitor.setMinEvictableIdleTimeMillis(-1);
		this.dsMonitor.setTestOnBorrow(false);
		this.dsMonitor.setTestOnReturn(false);
		this.dsMonitor.setTestWhileIdle(false);
		this.dsMonitor.setUrl(this.ds.getUrl());
		Connection conn = this.dsMonitor.getConnection();
		conn.close();
		logger.info(getGMTDatetime() + " - " + getName() + " - health monitor init successfully");
	}

	public void reinitalizeDataSource() throws Exception {
		setEnabled(false);

		DataSource dsNew = new DataSource();
		dsNew.setName(this.ds.getName());
		dsNew.setUsername(this.ds.getUsername());
		dsNew.setPassword(this.ds.getPoolProperties().getPassword());
		dsNew.setDriverClassName(this.ds.getDriverClassName());
		dsNew.setInitialSize(this.ds.getInitialSize());
		dsNew.setMaxActive(this.ds.getMaxActive());
		dsNew.setMinIdle(this.ds.getMinIdle());
		dsNew.setMaxIdle(this.ds.getMaxIdle());
		dsNew.setMaxWait(this.ds.getMaxWait());
		dsNew.setLogAbandoned(this.ds.isLogAbandoned());
		dsNew.setRemoveAbandoned(this.ds.isRemoveAbandoned());
		dsNew.setRemoveAbandonedTimeout(this.ds.getRemoveAbandonedTimeout());
		dsNew.setValidationQuery(this.ds.getValidationQuery());
		dsNew.setNumTestsPerEvictionRun(this.ds.getNumTestsPerEvictionRun());
		dsNew.setTimeBetweenEvictionRunsMillis(this.ds.getTimeBetweenEvictionRunsMillis());
		dsNew.setMinEvictableIdleTimeMillis(this.ds.getMinEvictableIdleTimeMillis());
		dsNew.setTestOnReturn(this.ds.isTestOnReturn());
		dsNew.setTestWhileIdle(this.ds.isTestWhileIdle());
		dsNew.setTestOnBorrow(this.ds.isTestOnBorrow());
		dsNew.setDefaultAutoCommit(this.ds.isDefaultAutoCommit());
		dsNew.setUrl(this.ds.getUrl());
		
		//TODO 2017-03-15
		dsNew.setJdbcInterceptors(this.ds.getJdbcInterceptors());
		//TODO 2017-03-15

		this.ds = dsNew;
		Connection conn = this.ds.getConnection();
		conn.close();
		this.failCount.set(0);
		this.setEnabled(true);
		logger.info(getGMTDatetime() + " - " + getName() + " - reinitalize dataSource successfully");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEnabled(boolean enabled) {
		//TODO 2017-03-15
		if (this.enabled != enabled) {
			logger.warning(getName() + " Enabled: " + this.enabled + " -> " + enabled);
		}
		//TODO 2017-03-15
		
		this.enabled = enabled;
		if (enabled) {
			this.connector.addNode(this);
		} else {
			this.connector.removeNode(this);
			if (this.ds != null) {
				this.ds.purge();
			}
		}

	}

	public String getName() {
		return this.name;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public DataSource getDataSource() {
		return this.ds;
	}

	public Connection getConnection() throws SQLException {
		if (logger.isLoggable(Level.FINER) )
			logger.finer("Using DataSource: " + this.name + " " + "active : " + this.ds.getActive() + " idle : " + this.ds.getIdle() + " properties : " + this.ds);
		return this.ds.getConnection();
	}

	public int getActiveCount() {
		return this.ds.getNumActive();
	}

	public int getIdleCount() {
		return this.ds.getNumIdle();
	}

	public int getMaxActive() {
		return this.ds.getMaxActive();
	}

	public int getInitialSize() {
		return this.ds.getInitialSize();
	}

	public int getMinIdle() {
		return this.ds.getMinIdle();
	}

	public int getMaxIdle() {
		return this.ds.getMaxIdle();
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String getGMTDatetime() {
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		String date = sdf.format(new Date(System.currentTimeMillis()));
		return date + " GMT+8 ";
	}

	class DaemonThreadFactory implements ThreadFactory {
		private String threadName = "";

		public DaemonThreadFactory(String threadName) {
			if (threadName != null) {
				this.threadName = threadName;
			}
		}

		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setDaemon(true);
			thread.setName(this.threadName);
			return thread;
		}

	}
}
