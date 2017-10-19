package com.dosomething.commons.model.database;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import com.dosomething.util.LogUtils;




public class ConnectionWrapper implements Connection {

	private Connection conn = null;

	private long ts= System.currentTimeMillis();
	
	private long seq = ConnectionMonitor.getInstance().getAtomicInteger();
	
	private String sql;
	
	private PreparedStatementWrapper preparedStatementWrapper;
	
	private StatementWrapper statementWrapper;
	
	private String stackTrace;
	
	private boolean autoCommit = true;
	
	private boolean commit = false;
	
	public ConnectionWrapper(Connection conn) {
		try {
			throw new Exception("");
		} catch(Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			this.stackTrace = errors.toString();
		}
		this.conn = conn;
	}

	public Connection getRealConn() {
		return this.conn;
	}
	
	public long getSeq() {
		return seq;
	}
	
	public long getTimeStamp() {
		return ts;
	}
	
	public String getSql() {
		if(statementWrapper != null) {
			return statementWrapper.getSql();
		}
		if(preparedStatementWrapper != null) {
			return preparedStatementWrapper.getSql();
		}
		return this.sql;
	}
	
	public String getStackTrace() {
		return this.stackTrace;
	}
	
	@Override
	public void close() throws SQLException {
		if(!this.autoCommit && !commit) {
			LogUtils.coral.error("[No Commit] sql : " + getSql() + "\n" + this.stackTrace);
		}
		ConnectionMonitor.getInstance().removeConnection(this);
		conn.close();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return conn.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return conn.isWrapperFor(iface);
	}

	@Override
	public Statement createStatement() throws SQLException {
		Statement statement = conn.createStatement();
		StatementWrapper sw = new StatementWrapper(this, statement);
		this.statementWrapper = sw;
		return sw;
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		PreparedStatement statement = conn.prepareStatement(sql);
		PreparedStatementWrapper psWrapper = new PreparedStatementWrapper(this, statement, sql);
		this.preparedStatementWrapper = psWrapper;
		return psWrapper;
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		this.sql = sql;
		return conn.prepareCall(sql);
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		this.sql = sql;
		return conn.nativeSQL(sql);
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		this.autoCommit = autoCommit;
		conn.setAutoCommit(autoCommit);
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return conn.getAutoCommit();
	}

	@Override
	public void commit() throws SQLException {
		this.commit = true;
		conn.commit();
	}

	@Override
	public void rollback() throws SQLException {
		this.commit = true;
		conn.rollback();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return conn.isClosed();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return conn.getMetaData();
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		conn.setReadOnly(readOnly);
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return conn.isReadOnly();
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		conn.setCatalog(catalog);
	}

	@Override
	public String getCatalog() throws SQLException {
		return conn.getCatalog();
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		conn.setTransactionIsolation(level);
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return conn.getTransactionIsolation();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return conn.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		conn.clearWarnings();
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		Statement statement = conn.createStatement(resultSetType, resultSetConcurrency);
		StatementWrapper sw = new StatementWrapper(this, statement);
		this.statementWrapper = sw;
		return sw;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
		throws SQLException {
		//this.sql = sql;
		PreparedStatement statement = conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
		PreparedStatementWrapper psWrapper = new PreparedStatementWrapper(this, statement, sql);
		this.preparedStatementWrapper = psWrapper;
		return psWrapper;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
		throws SQLException {
		this.sql = sql;
		return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return conn.getTypeMap();
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		conn.setTypeMap(map);
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		conn.setHoldability(holdability);

	}

	@Override
	public int getHoldability() throws SQLException {
		return conn.getHoldability();
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return conn.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		return conn.setSavepoint(name);
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		conn.rollback(savepoint);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		conn.releaseSavepoint(savepoint);
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
		throws SQLException {
		Statement statement = conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
		StatementWrapper sw = new StatementWrapper(this, statement);
		this.statementWrapper = sw;
		return sw;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
		int resultSetHoldability) throws SQLException {
		//this.sql = sql;
		PreparedStatement statement = conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		PreparedStatementWrapper psWrapper = new PreparedStatementWrapper(this, statement, sql);
		this.preparedStatementWrapper = psWrapper;
		return psWrapper;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
		int resultSetHoldability) throws SQLException {
		this.sql = sql;
		return conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		//this.sql = sql;
		PreparedStatement statement = conn.prepareStatement(sql, autoGeneratedKeys);
		PreparedStatementWrapper psWrapper = new PreparedStatementWrapper(this, statement, sql);
		this.preparedStatementWrapper = psWrapper;
		return psWrapper;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		//this.sql = sql;
		PreparedStatement statement = conn.prepareStatement(sql, columnIndexes);
		PreparedStatementWrapper psWrapper = new PreparedStatementWrapper(this, statement, sql);
		this.preparedStatementWrapper = psWrapper;
		return psWrapper;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		//this.sql = sql;
		PreparedStatement statement = conn.prepareStatement(sql, columnNames);
		PreparedStatementWrapper psWrapper = new PreparedStatementWrapper(this, statement, sql);
		this.preparedStatementWrapper = psWrapper;
		return psWrapper;
	}

	@Override
	public Clob createClob() throws SQLException {
		return conn.createClob();
	}

	@Override
	public Blob createBlob() throws SQLException {
		return conn.createBlob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return conn.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return conn.createSQLXML();
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return conn.isValid(timeout);
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		conn.setClientInfo(name, value);
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		conn.setClientInfo(properties);
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		return conn.getClientInfo(name);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return conn.getClientInfo();
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return conn.createArrayOf(typeName, elements);
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return conn.createStruct(typeName, attributes);
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		conn.setSchema(schema);
	}

	@Override
	public String getSchema() throws SQLException {
		return conn.getSchema();
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		conn.abort(executor);
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		conn.setNetworkTimeout(executor, milliseconds);
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return conn.getNetworkTimeout();
	}

}
