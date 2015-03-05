package co.miranext.nosql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A batching implementation of JsonRepository where every calls
 * aren't auto commited
 *
 * close needs to be called to clean up.
 *
 */
public interface BatchingJsonRepository extends  JsonRepository {

    public void start();

    /**
     * Commit all the operation as one atomic process
     *
     * @throws  java.lang.RuntimeException if some error occured, everything is automatically rolled back
     */
    public void commit();

    public void rollback();

    /**
     * End the batch and release all resources
     *
     */
    public void release();

    /**
     * Acquire a connection used in the batch, calling commit,close or other mutable things will throw up
     *
     * @return
     */
    public Connection join() throws SQLException;
}
