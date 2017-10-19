package com.dosomething.commons.model.database;

import java.sql.Connection;
import java.sql.SQLException;

import com.dosomething.util.LogUtils;


public class DBPool {

	private PoolManager poolManager;

	/**
	 * The only available instance across the application
	 */
	private static DBPool theInstance = new DBPool();

	private DBPool() {
		try {
			poolManager = new PoolManager("CORAL", "write.properties");
		} catch (Exception ex) {
			LogUtils.coral.error(ex.getMessage(), ex);
		}
	}

	/**
	 * This is a singleton. The only available instance across the application
	 * 
	 * @return DBPool
	 */
	public static DBPool getInstance() {
		
		return theInstance;
	}

	public void closeAllDataSourcce() {
		poolManager.closeDataSourcce();
	}
	
	public Connection getReadConnection() throws SQLException {
		return poolManager.get(-1);
	}

	public Connection getDBNodeConnection(int id) throws SQLException {
		return poolManager.get(id);
	}

//	@Deprecated
//	public Connection getConnection() throws SQLException {
//		return poolManager.get(-1);
//	}

	public Connection getWriteConnection() throws SQLException {
		return poolManager.get(-1);
	}
}