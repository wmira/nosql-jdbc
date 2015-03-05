package co.miranext.nosql.postgresql;

import co.miranext.nosql.BatchingDatasourceDelegate;
import co.miranext.nosql.BatchingJsonRepository;
import co.miranext.nosql.DocumentRefMeta;
import co.miranext.nosql.JsonRepository;
import co.miranext.nosql.criteria.Criteria;
import co.miranext.nosql.query.SQLDMLObject;
import co.miranext.nosql.query.SQLObjectQuery;
import org.postgresql.util.PGobject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 */
public class BatchingPgsqlJsonRepository implements JsonRepository, co.miranext.nosql.BatchingJsonRepository {

    private BatchingDatasourceDelegate dsDelegate;
    private PgsqlJsonRepository repository;

    public BatchingPgsqlJsonRepository(final DataSource ds) {
        dsDelegate = new BatchingDatasourceDelegate(ds);
        this.repository = new PgsqlJsonRepository(dsDelegate);
    }

    @Override
    public void start() {
        this.dsDelegate.start();
    }

    @Override
    public void commit() {
        this.dsDelegate.commit();
    }

    @Override
    public void rollback() {
        this.dsDelegate.rollback();
    }

    @Override
    public void release() {
        this.dsDelegate.release();
    }

    @Override
    public Connection join() throws SQLException {

        return this.dsDelegate.getConnection();

    }

    @Override
    public <T> T find(Class<T> document, String id) {
        return repository.find(document, id);
    }

    @Override
    public <T> T find(Class<T> document, String id, Criteria criteria) {
        return repository.find(document, id, criteria);
    }

    @Override
    public <T> T findOne(Class<T> document, Criteria criteria) {
        return repository.findOne(document, criteria);
    }

    @Override
    public <T> List<T> find(Class<T> document, Criteria criteria) {
        return repository.find(document, criteria);
    }

    @Override
    public <T> void persist(T document) {
        repository.persist(document);
    }

    @Override
    public <T> void saveOrUpdate(T document) {
        repository.saveOrUpdate(document);
    }


    @Override
    public BatchingJsonRepository batch() {
        return this;
    }
}
