package com.dosomething.commons.model.database;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import com.dosomething.commons.model.Setting;





public class PreparedStatementWrapper implements PreparedStatement {
	
	private PreparedStatement statement = null;
	private ConnectionWrapper conn;
	private String sql;
	private Map<Integer, Object> parameterMap = new TreeMap<Integer, Object>();
	
	public PreparedStatementWrapper(ConnectionWrapper conn, PreparedStatement statement, String sql) {
		this.conn = conn;
		this.statement = statement;
		this.sql = sql;
	}
	
	public String getSql() {
		return sql;
	}
	
	
	private void printSQL() {
		if(!Setting.ENABLE_CONNECTION_SHOW_SQL) {
			return;
		}
		StringBuffer msg = new StringBuffer();

		msg.append(" Query: ");
		msg.append(sql);
		msg.append(" Parameters: ");

		if (parameterMap.isEmpty()) {
			msg.append("[]");
		} else {
			Collection<Object> c = parameterMap.values();
			Object[] objs = c.toArray(new Object[c.size()]);
			msg.append(Arrays.deepToString(objs));
		}
		//LogUtils.cfbook.info(msg.toString());
		System.out.println (msg.toString());
	}
	
	//----------------------------
	
	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		this.sql = sql;
		printSQL();
		return this.statement.executeQuery(sql);
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		this.sql = sql;
		printSQL();
		return this.statement.executeUpdate(sql);
	}

	@Override
	public void close() throws SQLException {
		this.statement.close();
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		return this.statement.getMaxFieldSize();
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		this.statement.setMaxFieldSize(max);
	}

	@Override
	public int getMaxRows() throws SQLException {
		return this.statement.getMaxRows();
	}

	@Override
	public void setMaxRows(int max) throws SQLException {
		this.statement.setMaxRows(max);
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		this.statement.setEscapeProcessing(enable);
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		return this.statement.getQueryTimeout();
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		this.statement.setQueryTimeout(seconds);
	}

	@Override
	public void cancel() throws SQLException {
		this.statement.cancel();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return this.statement.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		this.statement.clearWarnings();
	}

	@Override
	public void setCursorName(String name) throws SQLException {
		this.statement.setCursorName(name);
	}

	@Override
	public boolean execute(String sql) throws SQLException {
		this.sql = sql;
		printSQL();
		return this.statement.execute(sql);
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		return this.statement.getResultSet();
	}

	@Override
	public int getUpdateCount() throws SQLException {
		return this.statement.getUpdateCount();
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		return this.statement.getMoreResults();
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		this.statement.setFetchDirection(direction);
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return this.statement.getFetchDirection();
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		this.statement.setFetchSize(rows);
	}

	@Override
	public int getFetchSize() throws SQLException {
		return this.statement.getFetchSize();
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		return this.statement.getResultSetConcurrency();
	}

	@Override
	public int getResultSetType() throws SQLException {
		return this.statement.getResultSetType();
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		this.sql = sql;
		this.statement.addBatch(sql);
	}

	@Override
	public void clearBatch() throws SQLException {
		this.statement.clearBatch();
	}

	@Override
	public int[] executeBatch() throws SQLException {
		return this.statement.executeBatch();
	}

	@Override
	public Connection getConnection() throws SQLException {
		//return this.statement.getConnection();
		return this.conn;
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {
		return this.statement.getMoreResults(current);
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		return this.statement.getGeneratedKeys();
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		this.sql = sql;
		printSQL();
		return this.statement.executeUpdate(sql, autoGeneratedKeys);
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		this.sql = sql;
		printSQL();
		return this.statement.executeUpdate(sql, columnIndexes);
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		this.sql = sql;
		printSQL();
		return this.statement.executeUpdate(sql, columnNames);
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		this.sql = sql;
		printSQL();
		return this.statement.execute(sql, autoGeneratedKeys);
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		this.sql = sql;
		printSQL();
		return this.statement.execute(sql, columnIndexes);
	}

	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		this.sql = sql;
		printSQL();
		return this.statement.execute(sql, columnNames);
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		return this.statement.getResultSetHoldability();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return this.statement.isClosed();
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		this.statement.setPoolable(poolable);
	}

	@Override
	public boolean isPoolable() throws SQLException {
		return this.statement.isPoolable();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return this.statement.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return this.statement.isWrapperFor(iface);
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		printSQL();
		return this.statement.executeQuery();
	}

	@Override
	public int executeUpdate() throws SQLException {
		printSQL();
		return this.statement.executeUpdate();
	}

	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		this.parameterMap.put(parameterIndex, Integer.valueOf(sqlType));
		this.statement.setNull(parameterIndex, sqlType);
	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		this.parameterMap.put(parameterIndex, Boolean.valueOf(x));
		this.statement.setBoolean(parameterIndex, x);
	}

	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {
		this.parameterMap.put(parameterIndex, Byte.valueOf(x));
		this.statement.setByte(parameterIndex, x);
	}

	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {
		this.parameterMap.put(parameterIndex, Short.valueOf(x));
		this.statement.setShort(parameterIndex, x);
	}

	@Override
	public void setInt(int parameterIndex, int x) throws SQLException {
		this.parameterMap.put(parameterIndex, Integer.valueOf(x));
		this.statement.setInt(parameterIndex, x);
	}

	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {
		this.parameterMap.put(parameterIndex, Long.valueOf(x));
		this.statement.setLong(parameterIndex, x);
	}

	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {
		this.parameterMap.put(parameterIndex, Float.valueOf(x));
		this.statement.setFloat(parameterIndex, x);
	}

	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {
		this.parameterMap.put(parameterIndex, Double.valueOf(x));
		this.statement.setDouble(parameterIndex, x);
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setBigDecimal(parameterIndex, x);
	}

	@Override
	public void setString(int parameterIndex, String x) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setString(parameterIndex, x);
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setBytes(parameterIndex, x);
	}

	@Override
	public void setDate(int parameterIndex, Date x) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setDate(parameterIndex, x);
	}

	@Override
	public void setTime(int parameterIndex, Time x) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setTime(parameterIndex, x);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setTimestamp(parameterIndex, x);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setAsciiStream(parameterIndex, x, length);
	}

	@Override
	@Deprecated
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setUnicodeStream(parameterIndex, x, length);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setBinaryStream(parameterIndex, x, length);
	}

	@Override
	public void clearParameters() throws SQLException {
		this.statement.clearParameters();
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setObject(parameterIndex, x, targetSqlType);
	}

	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setObject(parameterIndex, x);
	}

	@Override
	public boolean execute() throws SQLException {
		return this.statement.execute();
	}

	@Override
	public void addBatch() throws SQLException {
		this.statement.addBatch();
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		this.parameterMap.put(parameterIndex, reader);
		this.statement.setCharacterStream(parameterIndex, reader, length);
		
	}

	@Override
	public void setRef(int parameterIndex, Ref x) throws SQLException {
		this.statement.setRef(parameterIndex, x);
	}

	@Override
	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		this.statement.setBlob(parameterIndex, x);
	}

	@Override
	public void setClob(int parameterIndex, Clob x) throws SQLException {
		this.statement.setClob(parameterIndex, x);
	}

	@Override
	public void setArray(int parameterIndex, Array x) throws SQLException {
		this.statement.setArray(parameterIndex, x);
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return this.statement.getMetaData();
	}

	@Override
	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setDate(parameterIndex, x, cal);
	}

	@Override
	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setTime(parameterIndex, x, cal);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setTimestamp(parameterIndex, x, cal);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		this.parameterMap.put(parameterIndex, sqlType);
		this.statement.setNull(parameterIndex, sqlType, typeName);
	}

	@Override
	public void setURL(int parameterIndex, URL x) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setURL(parameterIndex, x);
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return this.statement.getParameterMetaData();
	}

	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setRowId(parameterIndex, x);
	}

	@Override
	public void setNString(int parameterIndex, String value) throws SQLException {
		this.parameterMap.put(parameterIndex, value);
		this.statement.setNString(parameterIndex, value);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		this.parameterMap.put(parameterIndex, value);
		this.statement.setNCharacterStream(parameterIndex, value, length);
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		this.parameterMap.put(parameterIndex, value);
		this.statement.setNClob(parameterIndex, value);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		this.parameterMap.put(parameterIndex, reader);
		this.statement.setClob(parameterIndex, reader, length);
		
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		this.parameterMap.put(parameterIndex, inputStream);
		this.statement.setBlob(parameterIndex, inputStream, length);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		this.parameterMap.put(parameterIndex, reader);
		this.statement.setNClob(parameterIndex, reader, length);
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		this.parameterMap.put(parameterIndex, xmlObject);
		this.statement.setSQLXML(parameterIndex, xmlObject);
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength)
		throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setAsciiStream(parameterIndex, x, length);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setBinaryStream(parameterIndex, x, length);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		this.parameterMap.put(parameterIndex, reader);
		this.statement.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setAsciiStream(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		this.parameterMap.put(parameterIndex, x);
		this.statement.setBinaryStream(parameterIndex, x);
		
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		this.parameterMap.put(parameterIndex, reader);
		this.statement.setCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		this.parameterMap.put(parameterIndex, value);
		this.statement.setNCharacterStream(parameterIndex, value);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		this.parameterMap.put(parameterIndex, reader);
		this.statement.setClob(parameterIndex, reader);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		this.parameterMap.put(parameterIndex, inputStream);
		this.statement.setBlob(parameterIndex, inputStream);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		this.parameterMap.put(parameterIndex, reader);
		this.statement.setNClob(parameterIndex, reader);
		
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		this.statement.closeOnCompletion();
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return this.statement.isCloseOnCompletion();
	}

}
