package com.dosomething.commons.model.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.dosomething.commons.model.Setting;
import com.dosomething.database.mysql.DBConnector;
import com.dosomething.database.mysql.DBNode;
import com.dosomething.properties.Props;
import com.dosomething.util.LogUtils;


public class PoolManager {

	private DBConnector connector = null;

	private String name = null;

	private int counter = 0;

	private int reservedNode = -1;

	public PoolManager(String name, String properties) {
		try {
			this.name = name;
			connector = new DBConnector(properties);
			
			Properties p = new Props(properties).getProperties("db");
			
			String checkDBTime = p.getProperty("checkDBTime");
			if(checkDBTime != null && Boolean.parseBoolean(checkDBTime) == true) {
				checkDBTime(); // check db time
			}
			
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		}
	}

	public void setReservedNode(int reservedNode) {
		this.reservedNode = reservedNode;
	}

	public int getReservedNode() {
		return this.reservedNode;
	}

	public void disableNode(int id) {
		DBNode node = (DBNode) connector.getNode(id);
		node.setEnabled(false);
//		System.out.println("Disabling " + this.name + " DB Node: " + node.getNodeID());
		System.out.println("Disabling " + this.name + " DB Node: " + node.getName());
	}

	public void enableNode(int id) {
		DBNode node = (DBNode) connector.getNode(id);
		node.setEnabled(true);
//		System.out.println("Enabling " + this.name + " DB Node: " + node.getNodeID());
		System.out.println("Enabling " + this.name + " DB Node: " + node.getName());
	}

	public Connection get() {
		return get(-1);
	}

	public Connection get(int dbIndex) {

		DBNode node = null;
		Connection conn = null;
		if (dbIndex == -1) {
			dbIndex = incrementRACCounter();
			if (reservedNode > -1 && reservedNode < connector.getNodesSize() && dbIndex == reservedNode) {
				// skip node if node is used for specialized operations
				dbIndex = incrementRACCounter(reservedNode);
			}
		}

		if (dbIndex >= connector.getNodesSize()) {
			dbIndex = 0;
		}
		
		try {
			node = (DBNode) connector.getNode(dbIndex);
			if (node != null && node.isEnabled()) {
				// System.out.println(node.getNodeID());
				conn = node.getConnection();
			} else {
				if (connector.getNodesSize() == 0) {
					System.out.println("Exception : No " + this.name + " servers left to remove. Size 0.");
					return null;
				} else if (connector.getNodesSize() == 1) {
					node = (DBNode) connector.getNode(dbIndex);
					if (node == null || (node != null && !node.isEnabled())) {
						System.out.println("Exception : No " + this.name
							+ " servers left to remove. Invalid Node.");
						return null;
					}
					// System.out.println(node.getNodeID());
					return node.getConnection();
				}
				return get();
			}
		} catch (Exception ex) {
			System.out.println("Exception occurred while trying to get " + this.name + " connection");
			ex.printStackTrace();
			if (connector == null || connector.getNodesSize() == 0) {
				System.out.println("Exception : No " + this.name + " servers left to remove.");
				return null;
			}
			return get();
		}
		if (Setting.ENABLE_CONNECTION_DEBUG) {
			ConnectionWrapper connectionWrapper = new ConnectionWrapper(conn);
			ConnectionMonitor.getInstance().addConnection(connectionWrapper);
			return connectionWrapper;
		} else {
			return conn;
		}
	}

	private int incrementRACCounter() {
		return incrementRACCounter(-1);
	}

	private synchronized int incrementRACCounter(int RESERVED_NODE) {
		++counter;
		if (counter >= connector.getNodesSize()) {
			counter = 0;
		}
		if (RESERVED_NODE != -1 && counter == RESERVED_NODE && connector.getNodesSize() > 1) {
			++counter;
			if (counter >= connector.getNodesSize())
				counter = 0;
		}
		return counter;
	}

	public void closeDataSourcce() {
		int nodeSize = connector.getNodesSize();
		DBNode node = null;
		for (int i = 0; i < nodeSize; i++) {
			node = (DBNode) connector.getNode(i);
			try {
				node.getDataSource().close();
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}
	}

	public int getNodesSize() {
		return this.connector.getNodesSize();
	}
	

	
	private void checkDBTime() {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		Timestamp ts = null;

		try {
			conn = get();
			stmt = conn.createStatement();
//			rs = stmt.executeQuery("SELECT SYSTIMESTAMP FROM DUAL");	// Oracle
			rs = stmt.executeQuery("SELECT CURRENT_TIMESTAMP");			// Mysql
			while (rs.next()) {
				ts = rs.getTimestamp(1);
			}
			
			// 跟DB的時間比較，誤差不應該超過一分鐘
			if (Math.abs(ts.getTime() - System.currentTimeMillis()) > 60_000L) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S [z]");
				String localtimeStr = sdf.format(new Date());
				LogUtils.coral.error(String.format("Different DBTime %s with ClientTime %s", ts.toString(), localtimeStr));
			}
			
		} catch (Exception e) {
			LogUtils.coral.error("check db time error ", e);
			e.printStackTrace();
		} finally {
			closeAll(rs, stmt, conn);
		}
	}
	
	private static void closeAll(AutoCloseable... closeables) {
		if (closeables == null) {
			return;
		}
		
		for (AutoCloseable resource : closeables) {
			if (resource == null) {
				continue;
			}
			try {
				resource.close();
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}
	

}
