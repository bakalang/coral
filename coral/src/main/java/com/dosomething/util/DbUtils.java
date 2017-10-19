package com.dosomething.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang.StringUtils;

import com.dosomething.commons.annotation.Column;
import com.dosomething.commons.annotation.ColumnMapping;
import com.dosomething.commons.annotation.ColumnMappingSet;
import com.dosomething.commons.model.Setting;
import com.dosomething.commons.model.database.ConnectionWrapper;

public class DbUtils {

	private DbUtils() {
		throw new AssertionError();
	}

	public static void closeAll(Connection conn, Statement stmt, Statement ps) {
		close(ps);
		close(stmt);
		close(conn);
	}

	public static void closeAll(Connection conn, Statement ps) {
		close(ps);
		close(conn);
	}

	public static void closeAll(Connection conn, CallableStatement ps) {
		close(ps);
		close(conn);
	}

	public static void closeAll(Connection conn, CachedRowSet crs) {
		close(crs);
		close(conn);
	}

	public static void closeAll(Connection conn, ResultSet rs) {
		close(rs);
		close(conn);
	}

	public static void closeAll(Statement stmt, ResultSet rs) {
		close(rs);
		close(stmt);
	}

	/**
	 * Utility method to close up all the stuff. It eases the syntax
	 * 
	 * @param conn
	 * @param stmt
	 * @param rs
	 */
	public static void closeAll(Connection conn, Statement stmt, ResultSet rs) {
		close(rs);
		close(stmt);
		close(conn);
	}

	/**
	 * Close connection
	 * 
	 * @param conn
	 */
	public static void close(Connection conn) {
		if (conn == null) {
			return;
		}
		try {
			if (!conn.isClosed()) {
				conn.clearWarnings();
			}
			// For PostgreSQL
			// Set AutoCommit to true before returning to Connection Pool
			try {
				if (!conn.getAutoCommit()) {
					conn.setAutoCommit(true);
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			//
			// try {
			// conn.rollback();
			// } catch (SQLException ex) {
			// ex.printStackTrace();
			// }
			conn.close();
		} catch (SQLException e) {
			LogUtils.coral.error(e.getMessage(), e);
		}
	}

	public static void close(Collection<Connection> conns) {
		for (Connection conn : conns) {
			close(conn);
		}
	}

	/**
	 * Close connection
	 * 
	 * @param conn
	 */
	public static void close(Connection... pConn) {
		if (null == pConn || pConn.length == 0) {
			return;
		}
		for (Connection conn : pConn) {
			if (conn == null) {
				continue;
			}
			try {
				conn.clearWarnings();
				conn.close();
			} catch (SQLException e) {
				LogUtils.coral.error(e.getMessage(), e);
			}
		}
	}

	public static void close(CallableStatement cstmt) {
		if (cstmt == null) {
			return;
		}
		try {
			cstmt.close();
		} catch (SQLException e) {
			LogUtils.coral.error(e.getMessage(), e);
		}
	}

	/**
	 * Close statement
	 * 
	 * @param stmt
	 */
	public static void close(Statement stmt) {
		if (stmt == null) {
			return;
		}
		try {
			stmt.close();
		} catch (SQLException e) {
			LogUtils.coral.error(e.getMessage(), e);
		}
	}

	/**
	 * Close statement
	 * 
	 * @param stmt
	 */
	public static void close(Statement... pStmt) {
		if (null == pStmt || pStmt.length == 0) {
			return;
		}
		for (Statement stmt : pStmt) {
			if (stmt == null) {
				continue;
			}
			try {
				stmt.close();
			} catch (SQLException e) {
				LogUtils.coral.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Close resultset
	 * 
	 * @param rs
	 */
	public static void close(ResultSet rs) {
		if (rs == null) {
			return;
		}
		try {
			rs.close();
		} catch (SQLException e) {
			LogUtils.coral.error(e.getMessage(), e);
		}
	}

	/**
	 * Close resultset
	 * 
	 * @param rs
	 */
	public static void close(ResultSet... pRs) {
		if (null == pRs || pRs.length == 0) {
			return;
		}
		for (ResultSet rs : pRs) {
			if (rs == null) {
				continue;
			}
			try {
				rs.close();
			} catch (SQLException e) {
				LogUtils.coral.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Rollback connection
	 * 
	 * @param conn
	 */
	public static void rollback(Connection conn) {
		if (conn != null) {
			try {
				if (!conn.getAutoCommit()) {// 不是AutoCommit時，才做rollback
					conn.rollback();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void rollback(Collection<Connection> conns) {
		for (Connection conn : conns) {
			rollback(conn);
		}
	}

	/**
	 * Rollback connection
	 * 
	 * @param conn
	 */
	public static void rollback(Connection... pConn) {
		if (null == pConn || pConn.length == 0) {
			return;
		}
		for (Connection conn : pConn) {
			if (conn == null) {
				continue;
			}
			try {
				conn.rollback();
			} catch (SQLException ex) {
				LogUtils.coral.error("error while rollback", ex);
			}
		}
	}

	public static Statement[] createStatements(Connection... pConn) throws SQLException {
		if (null == pConn || pConn.length == 0) {
			return null;
		}
		Statement[] stmt = new Statement[pConn.length];
		for (int i = 0; i < pConn.length; i++) {
			stmt[i] = pConn[i].createStatement();
		}
		return stmt;
	}

	
	/*
	public static Connection getNativeConnection0(Connection wrapConn) throws SQLException {
		if (Setting.ENABLE_CONNECTION_DEBUG && wrapConn instanceof ConnectionWrapper) {
			wrapConn = ((ConnectionWrapper) wrapConn).getRealConn();
		}
		if (wrapConn instanceof DelegatingConnection) {
			Connection nativeCon = ((DelegatingConnection) wrapConn).getInnermostDelegate();
			// For some reason, the innermost delegate can be null: not for a
			// Statement's Connection but for the Connection handle returned by
			// the pool.
			// We'll fall back to the MetaData's Connection in this case, which
			// is
			// a native unwrapped Connection with Commons DBCP 1.1.
			Connection oriConn = null;
			if (nativeCon != null) {
				oriConn = nativeCon;
			} else {
				oriConn = wrapConn.getMetaData().getConnection();
			}
			return ((PoolableConnection) oriConn).getDelegate();
		}
		return wrapConn;
	} 
	*/

	public static String getCachedRowSetClobString(CachedRowSet cachedRS, String name, StringBuffer sb)
		throws SQLException {
		// clear StringBuffer;
		sb.setLength(0);

		Clob clob = cachedRS.getClob(name);
		if (clob == null) {
			return null;
		}

		int length = (int) clob.length();
		if (length == 0) {
			return "";
		}

		Reader reader = clob.getCharacterStream();

		char[] buffer = new char[length];
		try {
			while ((reader.read(buffer)) != -1) {
				sb.append(buffer);
			}
		} catch (IOException e) {
			LogUtils.coral.error(e.getMessage(), e);
		}
		return sb.toString();

	}

	public static String getCachedRowSetClobString(CachedRowSet cachedRS, String name) throws SQLException {
		return getCachedRowSetClobString(cachedRS, name, new StringBuffer());
	}

	public static String getStringFromClob(Clob clob) {
		StringBuilder sb = new StringBuilder();
		try (Reader reader = clob.getCharacterStream(); BufferedReader br = new BufferedReader(reader)) {
			String line;

			while (null != (line = br.readLine())) {
				sb.append(line);
			}

		} catch (Exception e) {
			LogUtils.coral.error(e.getMessage(), e);
		}
		return sb.toString();
	}

	private static <T> T convertResultSet(ResultSet rs, ResultSetHandler<T> rsh) throws SQLException {
		T result = null;
		try {
			result = rsh.handle(rs);
		} catch (SQLException e) {
			throw e;
		}
		return result;
	}

	/**
	 * 設定自訂column名稱
	 * 
	 * @param clazz
	 * @return
	 */
	private static Map<String, String> getColumnProperties(Class<?> clazz) {
		Map<String, String> properties = new HashMap<String, String>();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(Column.class)) {
				Column column = field.getAnnotation(Column.class);
				// TODO, quick fix with migrate from Upper Case column name to
				// Lower Case
				properties.put(column.name(), field.getName());
			}

		}

		ColumnMappingSet columnMappSet = clazz.getAnnotation(ColumnMappingSet.class);
		if (columnMappSet != null) {
			ColumnMapping[] columnMappings = columnMappSet.columnMappings();
			for (int i = 0; i < columnMappings.length; i++) {
				// TODO, quick fix with migrate from Upper Case column name to
				// Lower Case
				properties.put(columnMappings[i].columnName(), columnMappings[i].fieldName());
			}
		}
		return properties;
	}

	/**
	 * 取得自訂column名稱處理器
	 * 
	 * @param clazz
	 * @return
	 */
	private static RowProcessor buildBeanProcessor(Class<?> clazz) {
		Map<String, String> props = getColumnProperties(clazz);
		BeanProcessor beanProcessor = new BeanProcessor(props);
		BasicRowProcessor rp = new BasicRowProcessor(beanProcessor);

		return rp;
	}

	/**
	 * 取得單一物件
	 * 
	 * @param <E>
	 * @param rs
	 * @param clazz
	 * @return
	 * @throws SQLException
	 */
	public static <E> E toBean(ResultSet rs, Class<E> clazz) throws SQLException {
		return (E) convertResultSet(rs, new BeanHandler<E>(clazz, buildBeanProcessor(clazz)));
	}

	/**
	 * 取得一個List集合, List裡面存放多個bean物件
	 * 
	 * @param <E>
	 * @param rs
	 * @param clazz
	 * @return
	 * @throws SQLException
	 */
	public static <E> List<E> toBeanList(ResultSet rs, Class<E> clazz) throws SQLException {
		return (List<E>) convertResultSet(rs, new BeanListHandler<E>(clazz, buildBeanProcessor(clazz)));
	}

	//TODO 2017-03-15 w/o test for validation
//	public static Connection getNativeConnection(Connection wrapConn) throws SQLException {
//		if (Setting.ENABLE_CONNECTION_DEBUG && wrapConn instanceof ConnectionWrapper) {
//			wrapConn = ((ConnectionWrapper) wrapConn).getRealConn();
//		}
//		if (wrapConn.isWrapperFor(oracle.jdbc.OracleConnection.class)){
//			oracle.jdbc.OracleConnection pooledConnection= wrapConn.unwrap(oracle.jdbc.OracleConnection.class); 
//			return pooledConnection;
//		}
//		return wrapConn;
//		
//	}

	public static boolean isPKException(Exception e) {
		if (e.getMessage().contains("ORA-00001:")) {
			return true;
		}
		return false;
	}
	
	public static boolean isLockedException(Exception e) {
		if (StringUtils.isNotBlank(e.getMessage()) && e.getMessage().contains("ORA-00054:")) {
			return true;
		}
		return false;
	}
}
