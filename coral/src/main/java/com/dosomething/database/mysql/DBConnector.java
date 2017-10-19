package com.dosomething.database.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tomcat.jdbc.pool.DataSource;

import com.dosomething.properties.Props;


public class DBConnector {
	private String filename;
	private int totalNodeCount;
	private boolean healthMonitor = false;
	private ConcurrentSkipListMap<Integer, DBNode> nodeMaps = new ConcurrentSkipListMap<Integer, DBNode>();
	private static final Logger logger = Logger.getLogger(DBConnector.class.getName());

	public DBConnector(String filename) throws SQLException {
		this.filename = filename;
		initDB();
	}

	private void initDB() throws SQLException {
		Properties p = new Props(this.filename).getProperties("db");
		totalNodeCount = Integer.parseInt(p.getProperty("nodeSize"));
		String hmProp = p.getProperty("healthMonitor");
		if (hmProp != null) {
			this.healthMonitor = Boolean.parseBoolean(hmProp);
		}
		for (int i = 0; i < totalNodeCount; i++) {
			int index = i + 1;
			String name = this.filename + " - jdbc/db" + index;
//			DataSource datasource = this.setDataSource(p, index);
			DataSource datasource = this.setMysqlDataSource(p, index);
			try {
				Connection testConn = datasource.getConnection();
				testConn.close();
				DBNode node = new DBNode(index, name, datasource, this.healthMonitor, this);
				node.setEnabled(true);
			} catch (Exception ex) {
//				ex.printStackTrace();
				logger.log(Level.SEVERE,
				"Error trying to initDB , datasource url : " + datasource.getUrl(), ex);
			}
		}
	}

	public int getNodesSize() {
		return this.totalNodeCount;
	}

	public int getAvailableNodesSize() {
		return this.nodeMaps.size();
	}

	public DBNode getNode(int nodeID) {
		if (getAvailableNodesSize() == 0) {
			logger.warning("Exception : " + this.filename + " no available nodes ");
			return null;
		}
		DBNode node = null;
		try {
			if (nodeID > totalNodeCount || nodeID <= 0) {
				nodeID = nodeMaps.higherKey(0);
			}
			node = this.nodeMaps.get(nodeID);
		} catch (Exception ex) {
			logger.log(Level.WARNING, "Get Node Exception : " + this.filename + " \t", ex);
		}
		return node;
	}

	public Integer getHigherKey(int k) {
		return nodeMaps.higherKey(k);
	}

	protected void removeNode(DBNode node) {
		this.nodeMaps.remove(node.getId());
	}

	protected void addNode(DBNode node) {
		if (!nodeMaps.containsKey(node.getId())) {
			this.nodeMaps.put(node.getId(), node);
		} else {
			if (nodeMaps.get(node.getId()) != node) {
				removeNode(node);
				this.nodeMaps.put(node.getId(), node);
			}
		}
	}

	public void completelyClosed() {
		Iterator<Entry<Integer, DBNode>> entries = nodeMaps.entrySet().iterator();
		while (entries.hasNext()) {
			Entry thisEntry = (Entry) entries.next();
			((DBNode) thisEntry.getValue()).getDataSource().close();
		}
	}

	 private DataSource setMysqlDataSource(Properties p, int index) {
	 DataSource datasource = new DataSource();
	 String url = "jdbc:mysql://" + p.getProperty("host" + index) + ":" +
	 p.getProperty("port" + index) + "/STOCK_TW";
	
	 datasource.setName(this.filename + " - jdbc/db" + index);
	 datasource.setUsername(p.getProperty("user" + index));
	 datasource.setPassword(p.getProperty("pass" + index));
	 datasource.setDriverClassName("com.mysql.jdbc.Driver");
	 datasource.setInitialSize(Integer.parseInt(p.getProperty("initSize" + index)));
	 datasource.setMaxActive(Integer.parseInt(p.getProperty("maxActive" + index)));
	 datasource.setMinIdle(Integer.parseInt(p.getProperty("minIdle" + index)));
	 datasource.setMaxIdle(Integer.parseInt(p.getProperty("maxIdle" + index)));
	 datasource.setMaxWait(Integer.parseInt(p.getProperty("maxWait" + index)));
	 datasource.setLogAbandoned(Boolean.parseBoolean(p.getProperty("logAbandoned" + index)));
	 datasource.setRemoveAbandoned(Boolean.parseBoolean(p.getProperty("removeAbandoned" + index)));
	 datasource.setRemoveAbandonedTimeout(Integer.parseInt(p.getProperty("removeAbandonedTimeout" + index)));
	 datasource.setNumTestsPerEvictionRun(0);
	 datasource.setTimeBetweenEvictionRunsMillis(-1);
	 datasource.setMinEvictableIdleTimeMillis(-1);
	 datasource.setTestOnReturn(false);
	 datasource.setTestWhileIdle(false);
	 datasource.setDefaultAutoCommit(false);
	 datasource.setUrl(url);
	 return datasource;
	 }
	 


	private DataSource setDataSource(Properties p, int index) {
		DataSource datasource = new DataSource();
		String url = "jdbc:oracle:thin:@ (DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = " + p.getProperty(new StringBuilder().append("host").append(index).toString())
				+ ")(PORT = " + p.getProperty(new StringBuilder().append("port").append(index).toString()) + ")) (CONNECT_DATA=(SERVER = DEDICATED)(SERVICE_NAME = "
				+ p.getProperty(new StringBuilder().append("serviceName").append(index).toString()) + ")(INSTANCE_NAME = "
				+ p.getProperty(new StringBuilder().append("instanceName").append(index).toString()) + ")))";

		datasource.setName("DBConnector \t" + this.filename + " - jdbc/db" + index);
		datasource.setUsername(p.getProperty("user" + index));
		datasource.setPassword(p.getProperty("pass" + index));
		datasource.setDriverClassName("oracle.jdbc.OracleDriver");
		datasource.setInitialSize(Integer.parseInt(p.getProperty("initSize" + index)));
		datasource.setMaxActive(Integer.parseInt(p.getProperty("maxActive" + index)));
		datasource.setMinIdle(Integer.parseInt(p.getProperty("minIdle" + index)));
		datasource.setMaxIdle(Integer.parseInt(p.getProperty("maxIdle" + index)));
		datasource.setMaxWait(Integer.parseInt(p.getProperty("maxWait" + index)));
		datasource.setLogAbandoned(Boolean.parseBoolean(p.getProperty("logAbandoned" + index)));
		datasource.setRemoveAbandoned(Boolean.parseBoolean(p.getProperty("removeAbandoned" + index)));
		datasource.setRemoveAbandonedTimeout(Integer.parseInt(p.getProperty("removeAbandonedTimeout" + index)));
		datasource.setNumTestsPerEvictionRun(0);
		datasource.setTimeBetweenEvictionRunsMillis(60 * 1000);
		datasource.setMinEvictableIdleTimeMillis(-1);
		datasource.setTestOnReturn(false);
		datasource.setTestWhileIdle(false);
		datasource.setDefaultAutoCommit(false);
		datasource.setUrl(url);
		
		//TODO 2017-03-15
		String interceptors = p.getProperty("JdbcInterceptors" + index);
		if (interceptors != null && interceptors.length() > 0)
			datasource.setJdbcInterceptors(interceptors);
		//TODO 2017-03-15
		
		
		return datasource;
	}
}
