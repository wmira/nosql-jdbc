package co.miranext.nosql;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;


/**
 * Creates a connection wherein all calls are
 *
 *
 */
public class BatchingDatasourceDelegate implements DataSource {


    private DataSource actualDs;
    private Connection underlyingCon;
    private Connection delegateCon;
    private boolean originalAutoCommitFlag = false;

    public BatchingDatasourceDelegate(final DataSource actual) {
        this.actualDs = actual;

    }
    public void start() {
        try {
            this.underlyingCon = this.actualDs.getConnection();
            this.originalAutoCommitFlag = this.underlyingCon.getAutoCommit();
            this.underlyingCon.setAutoCommit(false);
            this.delegateCon = new CloseIgnoringConnection(this.underlyingCon);
        } catch ( Exception e ) {
            throw new RuntimeException("Unable to retrieving connection: " + e.getMessage(),e);
        }
    }

    public void commit() {
        try {
            this.underlyingCon.commit();
        } catch ( Exception e ) {
            try {
                this.underlyingCon.rollback();
            } catch ( Exception rollbackE ) {
                //Seriously?
                System.err.println("Warning, error on rollback: " + rollbackE.getMessage());
                rollbackE.printStackTrace();
            }
            throw new RuntimeException("Exception occured on commit.",e);
        }
    }

    public void rollback() {
        try {
            this.underlyingCon.rollback();
        } catch ( Exception rollbackE ) {
            throw new RuntimeException("Error on rollback.");
        }
    }


    public void release() {
        try {
            try {
                this.underlyingCon.setAutoCommit(this.originalAutoCommitFlag);
            } catch ( Exception ignored ) {
                ignored.printStackTrace();
                System.err.println("Warnign: Error resetting auto commit flag: " + ignored.getMessage());
            }
            this.underlyingCon.close();
        } catch ( Exception e ) {
            throw new RuntimeException("Error releasing connection: " + e.getMessage(),e);
        }

    }
    @Override
    public Connection getConnection() throws SQLException {
        return this.delegateCon;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.actualDs.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.actualDs.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.actualDs.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return this.actualDs.getLoginTimeout();
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.actualDs.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.actualDs.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.actualDs.isWrapperFor(iface);
    }
}
