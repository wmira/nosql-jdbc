package co.miranext.docdb.postgresql;

import co.miranext.docdb.*;
import co.miranext.docdb.sql.SQLBuilder;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;
import org.postgresql.util.PGobject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

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
        return this.findInternal(DocumentMeta.fromAnnotation(document.getClass()),document, criteria != null ? criteria : new Criteria());
    }

    @Override
    public <T> List<T> find(Class<T> document, Criteria criteria) {
        return findAsList(document,criteria != null ? criteria : new Criteria());
    }

    /**
     * Save this
     *
     * @param document
     * @param <T>
     */
    @Override
    public <T> void saveOrUpdate(T document) {

        DocumentMeta meta = DocumentMeta.fromAnnotation(document.getClass());
        Map<String,FieldAccess> fields = BeanUtils.getFieldsFromObject(document);

        FieldAccess idField = fields.get(meta.getIdField());

        //we only use UUID
        Object id = idField.getObject(document);
        if ( id != null ) {
            idField.setObject(document, UUID.randomUUID().toString());
        }

        SQLBuilder sql = new SQLBuilder(meta,fields);
        Map<String,Object> extraValues = SQLBuilder.generateExtraValues(document,id == null ? true : false,meta,fields);
        if ( id == null ) {
            sql.generateInsert();
        } else {
            sql.generateUpdate();
        }

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql.getSqlQuery()) ){

            Map<Integer,String> mapping = new HashMap<>();

            for ( Integer idx : mapping.keySet() ) {
                String name = mapping.get(idx);
                if ( name.equals(meta.getColumnName())) { //this is
                    PGobject jsonObj = toPGObject(toJsonString(document));
                    pstmt.setObject(idx,jsonObj,valueToSqlType(jsonObj));
                } else {
                    //FIXME, i think we should allow to set values of extras to be passed in and not be set on the document
                    Object val = extraValues.get(name);
                    pstmt.setObject(idx,extraValues.get(name),valueToSqlType(val));
                }
            }

            pstmt.executeUpdate();
            //TODO: should we fail silently?
        } catch ( Exception e ) {
            throw new RuntimeException("Unable to save document: " + e.getMessage(),e);
        }

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
        String query = getSelectQueryString(criteria, meta);

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query) ){

            //populate
            populateStatement(pstmt,criteria);
            ResultSet rs = pstmt.executeQuery();

            while ( rs.next() ) {
                results.add( jsonDataToInstance(meta, document, rs));
            }
        } catch ( Exception e ) {
            throw new RuntimeException("Error on find: " + e.getMessage(),e);
        }
        return results;

    }

    private String getSelectQueryString(final Criteria criteria, final DocumentMeta meta) {
        return SQLBuilder.createSqlSelect(meta, criteria, new SQLBuilder.FieldCriterionTransformer() {
            @Override
            public FieldCriterion transform(DocumentMeta meta, FieldCriterion criterion) {
                return new PsqlJsonFieldCriterion(meta.getColumnName(), criterion.getField(), criterion.getValue());
            }
        });
    }


    private <T> T findInternal(final DocumentMeta meta, final Class<T> document,final Criteria criteria) {

        String query = getSelectQueryString(criteria, meta);

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query) ){

            //populate
            populateStatement(pstmt,criteria);
            ResultSet rs = pstmt.executeQuery();

            if ( rs.next() ) {
                T value = jsonDataToInstance(meta, document, rs);
                return value;
            }
        } catch ( Exception e ) {
            throw new RuntimeException("Error on find: " + e.getMessage(),e);
        }
        return null;
    }

    private <T> T jsonDataToInstance(DocumentMeta meta, Class<T> document, ResultSet rs) throws SQLException {
        String jsonStr = rs.getString(meta.getColumnName());
        ObjectMapper mapper =  JsonFactory.create();
        return mapper.readValue(jsonStr,document);
    }

    private <T> T findInternal(Class<T> document, final String id, Criteria criteria) {

        DocumentMeta meta = DocumentMeta.fromAnnotation(document);
        PsqlJsonFieldCriterion idCriterion = new PsqlJsonFieldCriterion(meta.getColumnName(),meta.getIdField(),id);
        criteria.add(idCriterion);
        return findInternal(meta,document,criteria);
    }

    //utils
    public static PGobject toPGObject(final String json) throws Exception {
        PGobject jsonObject = new PGobject();
        jsonObject.setType("jsonb");
        jsonObject.setValue(json);
        return jsonObject;
    }

    public static <T> String toJsonString(T document) {

        ObjectMapper mapper =  JsonFactory.create();
        String data = mapper.toJson(document);
        return data;

    }

}
