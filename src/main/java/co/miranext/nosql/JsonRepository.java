package co.miranext.nosql;

import co.miranext.nosql.criteria.Criteria;

import java.util.List;

/**
 * JsonRepository instance that manages persistance of
 * json objects via rdbms
 *
 *
 */
public interface JsonRepository {

    /**
     * Create a batching json repo instance. Null if not supported
     *
     * @return
     */
    public BatchingJsonRepository batch();
    /**
     * Find using the given annotated class
     *
     * @param document
     * @param id
     * @param <T>
     * @return
     */
    public <T> T find(final Class<T> document,final String id);

    /**
     * Find using the given annotated class
     *
     * @param document
     * @param id
     * @param <T>
     * @return
     */
    public <T> T find(final Class<T> document,final String id,final Criteria criteria);

    /**
     * Find 1
     *
     * @param document
     * @param criteria
     * @param <T>
     * @return
     */
    public <T> T findOne(final Class<T> document, final Criteria criteria);

    /**
     *
     *
     * @param document
     * @param criteria
     * @param <T>
     * @return
     */
    public <T> List<T> find(final Class<T> document, final Criteria criteria);


    /**
     * Persist the given document
     *
     * @param document
     * @param <T>
     */
    public <T> void saveOrUpdate(T document);

    /**
     * Persist the given document
     *
     * @param document
     * @param <T>
     */
    public <T> void persist(T document);

}
