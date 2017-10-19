package com.dosomething.commons.model.database;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

import org.apache.commons.dbutils.ResultSetHandler;

import com.dosomething.util.DbUtils;
import com.dosomething.util.JSONUtils;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Copy from org.apache.commons.dbutils.QueryRunner Executes SQL queries with
 * pluggable strategies for handling <code>ResultSet</code>s. This class is
 * thread safe.
 * 
 * @see ResultSetHandler
 */
public class DBQueryRunner {

	/**
	 * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.
	 * 
	 * @param conn
	 *            The Connection to use to run the query. The caller is
	 *            responsible for closing this Connection.
	 * @param sql
	 *            The SQL to execute.
	 * @param params
	 *            An array of query replacement parameters. Each row in this
	 *            array is one set of batch replacement values.
	 * @return The number of rows updated per statement.
	 * @throws SQLException
	 *             if a database access error occurs
	 * @since DbUtils 1.1
	 */
	public static int[] batch(Connection conn, String sql, Object[][] params) throws SQLException {


		PreparedStatement stmt = null;
		int[] rows = null;
		try {
			stmt = conn.prepareStatement(sql);
			ParameterMetaData pmd = null;
			if (params.length > 0) {
				pmd = stmt.getParameterMetaData();
			}
			for (int i = 0; i < params.length; i++) {
				if (params[i] != null && params.length > 0) {
					fillStatement(stmt, pmd, params[i]);
				}
				stmt.addBatch();
			}
			rows = stmt.executeBatch();

		} catch (SQLException e) {
			rethrow(e, sql, (Object[]) params);
		} finally {
			DbUtils.close(stmt);
		}

		return rows;
	}

	/**
	 * Fill the <code>PreparedStatement</code> replacement parameters with the
	 * given objects.
	 * 
	 * @param stmt
	 *            PreparedStatement to fill
	 * @param params
	 *            Query replacement parameters; <code>null</code> is a valid
	 *            value to pass in.
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public static void fillStatement(PreparedStatement stmt, ParameterMetaData pmd, Object... params)
		throws SQLException {
		if (params == null) {
			return;
		}

		if (pmd.getParameterCount() < params.length) {
			throw new SQLException("Too many parameters: expected " + pmd.getParameterCount()
				+ ", was given " + params.length);
		}

		boolean pmdKnownBroken = false;
		for (int i = 0; i < params.length; i++) {
			if (params[i] != null) {
				stmt.setObject(i + 1, params[i]);
			} else {
				// VARCHAR works with many drivers regardless
				// of the actual column type. Oddly, NULL and
				// OTHER don't work with Oracle's drivers.
				int sqlType = Types.VARCHAR;
				if (!pmdKnownBroken) {
					try {
						sqlType = pmd.getParameterType(i + 1);
					} catch (SQLException e) {
						pmdKnownBroken = true;
					}
				}
				stmt.setNull(i + 1, sqlType);
			}
		}
	}

	public static void fillStatement(PreparedStatement stmt, ParameterMetaData pmd, Collection<Object> params)
		throws SQLException {
		if (params == null) {
			return;
		}
		if (pmd.getParameterCount() < params.size()) {
			throw new SQLException("Too many parameters: expected " + pmd.getParameterCount()
				+ ", was given " + params.size());
		}

		boolean pmdKnownBroken = false;
		int i = 0;
		for (Object obj : params) {
			if (obj != null) {
				stmt.setObject(i + 1, obj);
			} else {
				// VARCHAR works with many drivers regardless
				// of the actual column type. Oddly, NULL and
				// OTHER don't work with Oracle's drivers.
				int sqlType = Types.VARCHAR;
				if (!pmdKnownBroken) {
					try {
						sqlType = pmd.getParameterType(i + 1);
					} catch (SQLException e) {
						pmdKnownBroken = true;
					}
				}
				stmt.setNull(i + 1, sqlType);
			}
			i++;
		}
	}

	/**
	 * Throws a new exception with a more informative error message.
	 * 
	 * @param cause
	 *            The original exception that will be chained to the new
	 *            exception when it's rethrown.
	 * 
	 * @param sql
	 *            The query that was executing when the exception happened.
	 * 
	 * @param params
	 *            The query replacement parameters; <code>null</code> is a valid
	 *            value to pass in.
	 * 
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	private static void rethrow(SQLException cause, String sql, Object... params) throws SQLException {

		String causeMessage = cause.getMessage();
		if (causeMessage == null) {
			causeMessage = "";
		}
		StringBuffer msg = new StringBuffer(causeMessage);

		msg.append(" Query: ");
		msg.append(sql);
		msg.append(" Parameters: ");

		if (params == null) {
			msg.append("[]");
		} else {
			msg.append(Arrays.deepToString(params));
		}

		SQLException e = new SQLException(msg.toString(), cause.getSQLState(), cause.getErrorCode());
		e.setNextException(cause);

		throw e;
	}

	/**
	 * Execute an SQL INSERT, UPDATE, or DELETE query without replacement
	 * parameters.
	 * 
	 * @param conn
	 *            The connection to use to run the query.
	 * @param sql
	 *            The SQL to execute.
	 * @return The number of rows updated.
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public static int update(Connection conn, String sql) throws SQLException {
		return update(conn, sql, (Object[]) null);
	}

	public static int update(Connection conn, String sql, Collection<Object> params) throws SQLException {
		PreparedStatement stmt = null;
		int rows = 0;

		try {
			stmt = conn.prepareStatement(sql);
			if (params != null && params.size() > 0) {
				fillStatement(stmt, stmt.getParameterMetaData(), params);
			}
			rows = stmt.executeUpdate();

		} catch (SQLException e) {
			rethrow(e, sql, params);
		} finally {
			DbUtils.close(stmt);
		}

		return rows;
	}

	/**
	 * Execute an SQL INSERT, UPDATE, or DELETE query.
	 * 
	 * @param conn
	 *            The connection to use to run the query.
	 * @param sql
	 *            The SQL to execute.
	 * @param params
	 *            The query replacement parameters.
	 * @return The number of rows updated.
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public static int update(Connection conn, String sql, Object... params) throws SQLException {

		PreparedStatement stmt = null;
		int rows = 0;

		try {
			stmt = conn.prepareStatement(sql);
			if (params != null && params.length > 0) {
				fillStatement(stmt, stmt.getParameterMetaData(), params);
			}
			rows = stmt.executeUpdate();

		} catch (SQLException e) {
			rethrow(e, sql, params);
		} finally {
			DbUtils.close(stmt);
		}

		return rows;
	}

	/**
	 * 回傳ResultSet，並延遲關閉Statement，請務必呼叫close();
	 * 
	 * @param conn
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public static ResultSet getResultSet(Connection conn, String sql) throws SQLException {
		return getResultSet(conn, sql, (Object[]) null);
	}

	/**
	 * 回傳ResultSet，並延遲關閉Statement，請務必呼叫close();
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public static ResultSet getResultSet(Connection conn, String sql, Collection<Object> params)
		throws SQLException {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			final PreparedStatement enclosingStmt = stmt = conn.prepareStatement(sql);
			if (params != null && params.size() > 0) {
				fillStatement(stmt, stmt.getParameterMetaData(), params);
			}
			rs = stmt.executeQuery();
			ResultSetWrapper rsWrapper = new ResultSetWrapper(rs, () -> {
				try {
					DbUtils.close(enclosingStmt);
				} catch (Exception e) {
					// LogUtils.cfbook.error(e.getMessage(), e);
				}
			});
			return rsWrapper;
		} catch (SQLException e) {
			DbUtils.closeAll(stmt, rs);
			rethrow(e, sql, params);
		}
		return null;
	}

	/**
	 * 回傳ResultSet，並延遲關閉Statement，請務必呼叫close();
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public static ResultSet getResultSet(Connection conn, String sql, Object... params) throws SQLException {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			final PreparedStatement enclosingStmt = stmt = conn.prepareStatement(sql);
			if (params != null && params.length > 0) {
				fillStatement(stmt, stmt.getParameterMetaData(), params);
			}
			rs = stmt.executeQuery();
			ResultSetWrapper rsWrapper = new ResultSetWrapper(rs, () -> {
				try {
					DbUtils.close(enclosingStmt);
				} catch (Exception e) {
					// LogUtils.cfbook.error(e.getMessage(), e);
				}
			});
			return rsWrapper;

		} catch (SQLException e) {
			DbUtils.closeAll(stmt, rs);
			rethrow(e, sql, params);
		}
		return null;
	}

	/**
	 * Notice : 1.CachedRowSet.execute will Auto commit Connection. 2.Don't use
	 * this method to get DB lock(ex : select for update).
	 * 
	 * @param conn
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public static CachedRowSet query(Connection conn, String sql) throws SQLException {
		return query(conn, sql, (Object[]) null);
	}

	/**
	 * Notice : 1.CachedRowSet.execute will Auto commit Connection. 2.Don't use
	 * this method to get DB lock(ex : select for update).
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public static CachedRowSet query(Connection conn, String sql, Collection<Object> params)
		throws SQLException {

		RowSetFactory rowSetFactory = RowSetProvider.newFactory();
		CachedRowSet rowset = rowSetFactory.createCachedRowSet();
		rowset.setCommand(sql);

		if (params != null) {
			int i = 0;
			for (Object obj : params) {
				if (obj != null) {
					rowset.setObject(i + 1, obj);
				} else {
					// VARCHAR works with many drivers regardless
					// of the actual column type. Oddly, NULL and
					// OTHER don't work with Oracle's drivers.
					rowset.setNull(i + 1, Types.VARCHAR);
				}
				i++;
			}
		}
		rowset.execute(conn);
		return rowset;
	}

	public static CachedRowSet query(Connection conn, String sql, Object... params) throws SQLException {

		RowSetFactory rowSetFactory = RowSetProvider.newFactory();
		CachedRowSet rowset = rowSetFactory.createCachedRowSet();
		rowset.setCommand(sql);

		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				if (params[i] != null) {
					rowset.setObject(i + 1, params[i]);
				} else {
					// VARCHAR works with many drivers regardless
					// of the actual column type. Oddly, NULL and
					// OTHER don't work with Oracle's drivers.
					rowset.setNull(i + 1, Types.VARCHAR);
				}
			}
		}
		rowset.execute(conn);
		return rowset;
	}

	public static <E> List<E> getBeanList(Connection conn, Class<E> clazz, String sql) throws SQLException {
		return getBeanList(conn, clazz, sql, (Object[]) null);
	}

	public static <E> List<E> getBeanList(Connection conn, Class<E> clazz, String sql, Object... params)
		throws SQLException {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(sql);
			if (params != null && params.length > 0) {
				fillStatement(stmt, stmt.getParameterMetaData(), params);
			}
			rs = stmt.executeQuery();
			return DbUtils.toBeanList(rs, clazz);
		} catch (SQLException e) {
			rethrow(e, sql, params);
		} finally {
			DbUtils.closeAll(stmt, rs);
		}
		return null;
	}

	public static <E> List<E> getBeanList(Connection conn, Class<E> clazz, String sql,
		Collection<Object> values) throws SQLException {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(sql);
			if (values != null && values.size() > 0) {
				fillStatement(stmt, stmt.getParameterMetaData(), values);
			}
			rs = stmt.executeQuery();
			return DbUtils.toBeanList(rs, clazz);
		} catch (SQLException e) {
			rethrow(e, sql, values);
		} finally {
			DbUtils.closeAll(stmt, rs);
		}
		return null;
	}

	public static <E> E getBean(Connection conn, Class<E> clazz, String sql) throws SQLException {
		return getBean(conn, clazz, sql, (Object[]) null);
	}

	public static <E> E getBean(Connection conn, Class<E> clazz, String sql, Object... values)
		throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(sql);
			if (values != null && values.length > 0) {
				fillStatement(stmt, stmt.getParameterMetaData(), values);
			}
			rs = stmt.executeQuery();
			return DbUtils.toBean(rs, clazz);
		} catch (SQLException e) {
			e.printStackTrace();
			rethrow(e, sql, values);
		} finally {
			DbUtils.close(stmt);
			DbUtils.close(rs);
		}
		return null;
	}

	public static <E> E getBean(Connection conn, Class<E> clazz, String sql, Collection<Object> values)
		throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(sql);
			if (values != null && values.size() > 0) {
				fillStatement(stmt, stmt.getParameterMetaData(), values);
			}
			rs = stmt.executeQuery();
			return DbUtils.toBean(rs, clazz);
		} catch (SQLException e) {
			rethrow(e, sql, values);
		} finally {
			DbUtils.close(stmt);
			DbUtils.close(rs);
		}
		return null;
	}

	public static final ArrayNode getJsonArray(Connection conn, String sql) throws SQLException {
		return getJsonArray(conn, sql, (Object[]) null);
	}

	public static final ArrayNode getJsonArray(Connection conn, String sql, Object... params)
		throws SQLException {
		ArrayNode jsonArray = JSONUtils.getObjectMapper().createArrayNode();
		ObjectNode jsonNode = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(sql);
			if (params != null && params.length > 0) {
				fillStatement(stmt, stmt.getParameterMetaData(), params);
			}
			rs = stmt.executeQuery();
			String columnName, columnValue = null;
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				jsonNode = JSONUtils.getObjectMapper().createObjectNode();
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					columnName = rsmd.getColumnName(i + 1);
					columnValue = rs.getString(columnName);
					jsonNode.put(columnName, columnValue);
				}
				jsonArray.add(jsonNode);
			}
		} catch (SQLException e) {
			rethrow(e, sql, params);
		} finally {
			DbUtils.closeAll(stmt, rs);
		}
		return jsonArray;
	}

	public static final ArrayNode getJsonArray(Connection conn, String sql, Collection<Object> values)
		throws SQLException {
		ArrayNode jsonArray = JSONUtils.getObjectMapper().createArrayNode();
		ObjectNode jsonNode = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(sql);
			if (values != null && values.size() > 0) {
				fillStatement(stmt, stmt.getParameterMetaData(), values);
			}
			rs = stmt.executeQuery();
			String columnName, columnValue = null;
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				jsonNode = JSONUtils.getObjectMapper().createObjectNode();
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					columnName = rsmd.getColumnName(i + 1);
					columnValue = rs.getString(columnName);
					jsonNode.put(columnName, columnValue);
				}
				jsonArray.add(jsonNode);
			}
		} catch (SQLException e) {
			rethrow(e, sql, values);
		} finally {
			DbUtils.closeAll(stmt, rs);
		}
		return jsonArray;
	}

	public static Number getNumber(Connection conn, String sql) throws SQLException {
		return getNumber(conn, sql, (Object[]) null);
	}

	public static Number getNumber(Connection conn, String sql, Object... values) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(sql);
			if (values != null && values.length > 0) {
				fillStatement(stmt, stmt.getParameterMetaData(), values);
			}
			rs = stmt.executeQuery();
			if (rs.next()) {
				return (Number) rs.getObject(1);
			}
		} catch (SQLException e) {
			rethrow(e, sql, values);
		} finally {
			DbUtils.close(stmt);
			DbUtils.close(rs);
		}
		return null;
	}

	public static Number getNumber(Connection conn, String sql, Collection<Object> values)
		throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(sql);
			if (values != null && values.size() > 0) {
				fillStatement(stmt, stmt.getParameterMetaData(), values);
			}
			rs = stmt.executeQuery();
			if (rs.next()) {
				return (Number) rs.getObject(1);
			}
		} catch (SQLException e) {
			rethrow(e, sql, values);
		} finally {
			DbUtils.close(stmt);
			DbUtils.close(rs);
		}
		return null;
	}

	public static String getString(Connection conn, String sql) throws SQLException {
		return getString(conn, sql, (Object[]) null);
	}

	public static String getString(Connection conn, String sql, Object... values) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(sql);
			if (values != null && values.length > 0) {
				fillStatement(stmt, stmt.getParameterMetaData(), values);
			}
			rs = stmt.executeQuery();
			if (rs.next()) {
				return (String) rs.getObject(1);
			}
		} catch (SQLException e) {
			rethrow(e, sql, values);
		} finally {
			DbUtils.close(stmt);
			DbUtils.close(rs);
		}
		return null;
	}

	public static String getString(Connection conn, String sql, Collection<Object> values)
		throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(sql);
			if (values != null && values.size() > 0) {
				fillStatement(stmt, stmt.getParameterMetaData(), values);
			}
			rs = stmt.executeQuery();
			if (rs.next()) {
				return (String) rs.getObject(1);
			}
		} catch (SQLException e) {
			rethrow(e, sql, values);
		} finally {
			DbUtils.close(stmt);
			DbUtils.close(rs);
		}
		return null;
	}

	public static void processResultSet(Connection conn, ResultSetProcessor resultSetProcessor, String sql,
		Collection<Object> values) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(sql);
			if (values != null && values.size() > 0) {
				fillStatement(stmt, stmt.getParameterMetaData(), values);
			}
			rs = stmt.executeQuery();
			long i = 0;
			while (rs.next()) {
				resultSetProcessor.process(i++, rs);
			}
		} catch (SQLException e) {
			rethrow(e, sql, values);
		} finally {
			DbUtils.close(stmt);
			DbUtils.close(rs);
		}
	}

	public static void processResultSet(Connection conn, ResultSetProcessor resultSetProcessor, String sql,
		Object... values) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(sql);
			if (values != null && values.length > 0) {
				fillStatement(stmt, stmt.getParameterMetaData(), values);
			}
			rs = stmt.executeQuery();
			long i = 0;
			while (rs.next()) {
				resultSetProcessor.process(i++, rs);
			}
		} catch (SQLException e) {
			rethrow(e, sql, values);
		} finally {
			DbUtils.close(stmt);
			DbUtils.close(rs);
		}
	}

//	public static <T> PageResult<T> getPageResult(Connection conn, Class<T> clazz, String sql, long pageNumber,
//		long showCount, Collection<Object> parameters) throws Exception {
//
//		Number totalCount = DBQueryRunner.getNumber(conn, String.format(" SELECT COUNT(*) FROM (%s) ", sql),
//			parameters);
//
//		if (totalCount == null || totalCount.longValue() == 0) {
//			return new PageResult<>();
//		}
//
//		if (showCount < 1) {
//			showCount = 1;
//		}
//
//		long totalPage = totalCount.longValue() / showCount;
//		if (totalCount.longValue() % showCount != 0) {
//			totalPage = totalPage + 1;
//		}
//
//		if (pageNumber < 1) {
//			pageNumber = 1;
//		} else if (pageNumber > totalPage) {
//			pageNumber = totalPage;
//		}
//
//		long firstRowNumber = (pageNumber - 1) * showCount;
//		long lastRowNumber = pageNumber * showCount;
//
//		List<Object> objects = new ArrayList<>(parameters);
//		objects.add(lastRowNumber);
//		objects.add(firstRowNumber);
//		
//		String pageSql = "SELECT * FROM ( SELECT row_.*, rownum rownum_ FROM ( %s ) row_ WHERE rownum <= ? ) WHERE rownum_ > ? ";
//		
//		List<T> resultList = DBQueryRunner.getBeanList(conn, clazz, String.format(pageSql, sql), objects);
//
//		if (resultList == null || resultList.isEmpty()) {
//			return new PageResult<>();
//		}
//
//		PageResult<T> pageResult = new PageResult<>();
//		pageResult.setResultList(resultList);
//		pageResult.setTotalCount(totalCount.longValue());
//		pageResult.setShowCount(resultList.size());
//		pageResult.setCurrentPage(pageNumber);
//		pageResult.setTotalPage(totalPage);
//
//		return pageResult;
//	}
//
//	public static <T> PageResult<T> getPageResult(Connection conn, Class<T> clazz, String sql, long pageNumber,
//		long showCount, Object... parameters) throws Exception {
//
//		return getPageResult(conn, clazz, sql, pageNumber, showCount, Arrays.asList(parameters));
//	}

}
