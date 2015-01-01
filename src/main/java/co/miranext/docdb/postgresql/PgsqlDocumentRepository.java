package co.miranext.docdb.postgresql;

import co.miranext.docdb.*;
import co.miranext.docdb.sql.SQLBuilder;
import com.google.common.base.CaseFormat;
import org.boon.core.TypeType;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;
import org.postgresql.util.PGobject;

import javax.sql.DataSource;
import java.lang.reflect.Field;
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
        return this.findInternal(DocumentMeta.fromAnnotation(document),document, criteria != null ? criteria : new Criteria());
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

        FieldAccess idFieldAccess = fields.get(meta.getIdField());
        Field field = idFieldAccess.getField();
        //we only use UUID
        Object id = null;
        try {
            id = field.get(document);
        } catch ( Exception e ) {
            throw new RuntimeException("Unable to retrieve id: " + e.getMessage() ,e);
        }
        if ( id == null ) {
            try {
                field.set(document, UUID.randomUUID().toString());
            } catch ( Exception e ) {
                throw new RuntimeException("Unable to set id: " + e.getMessage(), e);
            }
        }

        SQLBuilder sql = new SQLBuilder(meta,fields,document);
        Map<String,Object> extraValues = SQLBuilder.generateExtraValues(document,true,meta,fields);
        if ( id == null ) {
            sql.generateInsert();
        } else {
            sql.generateUpdate(CRITERION_TRANSFORMER);
            extraValues.put(meta.getIdField(),id);
        }

        String sqlQuery = sql.getSqlQuery();

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sqlQuery) ){

            Map<Integer,String> mapping = sql.getIndexMapping();

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

            int count = pstmt.executeUpdate();
            if ( count <= 0 ) {
                throw new RuntimeException("Unable to save/update: count: '" + count);
            }
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
        throw new RuntimeException("Type not supported: " + value);
    }

    private void populateStatement(final PreparedStatement pstmt,final Criteria criteria) throws Exception {
        List<Criterion> criterionList = criteria.getCriteria();

        int size = criterionList.size();
        int pstmtIdx = 1;

        for ( int i=0; i < size; i++ ) {
            Criterion criterion = criterionList.get(i);
            Object value = criterion.getValue();
            if ( criterion instanceof  FieldCriterion ) {
                //for postgresql, all FieldCriterion are strings
                if ( value != null ) {
                    pstmt.setString(pstmtIdx++, value.toString());
                } else {
                    pstmt.setNull(pstmtIdx++,Types.NULL);
                }

            } else {
                pstmt.setObject(pstmtIdx++, value, valueToSqlType(value));
            }
        }
    }

    private <T> List<T> findAsList(Class<T> document,Criteria criteria)  {

        DocumentMeta meta = DocumentMeta.fromAnnotation(document);
        Map<String,FieldAccess> fields = BeanUtils.getFieldsFromObject(document);

        List<T> results = new ArrayList<T>();
        String query = getSelectQueryString(criteria, meta);

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query) ){

            //populate
            populateStatement(pstmt,criteria);
            ResultSet rs = pstmt.executeQuery();

            while ( rs.next() ) {
                T instance =  jsonDataToInstance(meta, document, rs);
                //populate the values with extras if available
                populateWithExtras(instance,rs,meta,fields);

                results.add( instance );
            }
        } catch ( Exception e ) {
            throw new RuntimeException("Error on find: " + e.getMessage(),e);
        }
        return results;

    }

    private static Field extractField( final String column, final Map<String,FieldAccess> fields) {
        String beanField = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,column);
        FieldAccess fieldAccess = fields.get(beanField);

        if ( fieldAccess == null ) {
            throw new RuntimeException("Unable to find field access for column extra: '" + column + "' field: '" + beanField + "'");
        }
        Field field = fieldAccess.getField();

        if ( field == null ) {
            throw new RuntimeException("Unable to find field for column extra: '" + column + "' field: '" + beanField + "'");
        }
        return field;
    }

    private static <T> void populateWithExtras(final T instance, final ResultSet rs,final DocumentMeta meta,final Map<String,FieldAccess> fields ) throws Exception {

        ColumnExtra[] extras = meta.getColumnExtras();

        for ( ColumnExtra extra : extras ) {

            String column = extra.getColumn();
            String beanField = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,column);
            Field field = extractField(column,fields);
            FieldAccess fas = fields.get(beanField);
            TypeType type = fas.typeEnum();



            Object rsVal;
            if ( TypeType.LONG.equals(type) || TypeType.LONG_WRAPPER.equals(type) )  {
                rsVal = rs.getLong(column);
            } else if ( TypeType.INTEGER_WRAPPER.equals(type) || TypeType.INT.equals(type) ) {
                rsVal = rs.getInt(column);
            } else if ( TypeType.STRING.equals(type) ) {
                rsVal = rs.getString(column);
            } else {
                throw new RuntimeException("don't know what to do: " + type + " for column: '" + column + "'");
            }

            field.set(instance,rsVal);
        }
    }

    /**
     *
     * @param criteria
     * @param meta
     * @return
     */
    private String getSelectQueryString(final Criteria criteria, final DocumentMeta meta) {
        return SQLBuilder.createSqlSelect(meta, criteria, CRITERION_TRANSFORMER);
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

    private static SQLBuilder.FieldCriterionTransformer CRITERION_TRANSFORMER = new SQLBuilder.FieldCriterionTransformer() {
        @Override
        public FieldCriterion transform(final DocumentMeta meta,final FieldCriterion criterion) {
            return new PsqlJsonFieldCriterion(meta.getColumnName(), criterion.getField(), criterion.getValue());
        }

        @Override
        public FieldCriterion idFieldCriterion(final DocumentMeta meta,final String value) {
            return new PsqlJsonFieldCriterion(meta.getColumnName(),meta.getIdField(),value);
        }
    };
}
