package co.miranext.nosql;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 *
 */
public class CloseIgnoringConnection implements Connection {

    private Connection actual;
    public CloseIgnoringConnection(final Connection actual) {
        this.actual = actual;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return actual.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return actual.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return actual.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return actual.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        //do nothing
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return actual.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        //do nothing
    }

    @Override
    public void rollback() throws SQLException {
        //do nothing
    }

    @Override
    public void close() throws SQLException {
        //do nothing
    }

    @Override
    public boolean isClosed() throws SQLException {
        return actual.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return actual.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        actual.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return actual.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        actual.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return actual.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        actual.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return actual.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return actual.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        actual.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return actual.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return actual.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return actual.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return actual.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        actual.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        actual.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return actual.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return actual.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return actual.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        actual.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        actual.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return actual.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return actual.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return actual.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return actual.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return actual.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return actual.prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return actual.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return actual.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return actual.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return actual.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return actual.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        actual.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        actual.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return actual.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return actual.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return actual.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return actual.createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        actual.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return actual.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        actual.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        actual.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return actual.getNetworkTimeout();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return actual.isWrapperFor(iface);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return actual.unwrap(iface);
    }
}
