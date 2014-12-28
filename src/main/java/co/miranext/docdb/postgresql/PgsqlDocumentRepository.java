package co.miranext.docdb.postgresql;

import co.miranext.docdb.Criteria;
import co.miranext.docdb.Criterion;
import co.miranext.docdb.DocumentMeta;
import co.miranext.docdb.DocumentRepository;
import co.miranext.docdb.sql.SQLBuilder;
import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;
import org.postgresql.util.PGobject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Postgresql implementation of DocumentRepository
 */
public class PgsqlDocumentRepository implements DocumentRepository {

    private DataSource dataSource;

    /**
     *
     *
     * @param dataSource
     */
    public PgsqlDocumentRepository(final DataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     *
     *
     * @param document
     * @param id
     * @param <T>
     * @return
     */
    @Override
    public <T> T find(Class<T> document, String id) {
        return this.findInternal(document, id, new Criteria());
    }

    @Override
    public <T> T find(Class<T> document, String id, Criteria criteria) {
        return this.findInternal(document, id, criteria != null ? criteria : new Criteria());
    }

    @Override
    public <T> T findOne(Class<T> document, Criteria criteria) {
        return this.findInternal(document, criteria != null ? criteria : new Criteria());
    }

    @Override
    public <T> List<T> find(Class<T> document, Criteria criteria) {
        return findAsList(document,criteria != null ? criteria : new Criteria());
    }


    //Internal

    private static int valueToSqlType(final Object value) {

        if ( value instanceof String ) {
            return Types.LONGVARCHAR;
        } else if ( value instanceof Integer ) {
            return Types.INTEGER;
        } else if ( value instanceof  Long ) {
            return Types.BIGINT;
        } else if ( value instanceof PGobject ) {
            return Types.OTHER;
        } //FIXME: COMPLETE This
        throw new RuntimeException("Type not supported: " + value.getClass());
    }

    private void populateStatement(final PreparedStatement pstmt,final Criteria criteria) throws Exception {
        List<Criterion> criterionList = criteria.getCriteria();

        int size = criterionList.size();
        int pstmtIdx = 1;

        for ( int i=0; i < size; i++ ) {
            Criterion criterion = criterionList.get(i);
            Object value = criterion.getValue();
            pstmt.setObject(pstmtIdx++,value,valueToSqlType(value));
        }
    }

    private <T> List<T> findAsList(Class<T> document,Criteria criteria)  {

        DocumentMeta meta = DocumentMeta.fromAnnotation(document);

        List<T> results = new ArrayList<T>();
        String query = SQLBuilder.createSqlSelect(meta, criteria);

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query) ){

            //populate
            populateStatement(pstmt,criteria);

            ResultSet rs = pstmt.executeQuery();

            while ( rs.next() ) {
                String jsonStr = rs.getString(meta.getColumnName());
                ObjectMapper mapper =  JsonFactory.create();
                //FIXME: retrieve extras
                results.add(mapper.readValue(jsonStr,document));
            }
        } catch ( Exception e ) {
            throw new RuntimeException("Error on find: " + e.getMessage(),e);
        }

        return null;
    }


    private <T> T findInternal(Class<T> document, Criteria criteria) {


        List<T> res = findAsList(document, criteria);
        if ( res != null && res.size() > 0 ) {
            return res.get(0);
        }
        return null;
    }

    private <T> T findInternal(Class<T> document, final String id, Criteria criteria) {

        DocumentMeta meta = DocumentMeta.fromAnnotation(document);
        PsqlJsonFieldCriterion idCriterion = new PsqlJsonFieldCriterion(meta.getColumnName(),meta.getIdField(),id);
        criteria.add(idCriterion);
        return findInternal(document,criteria);
    }

}
