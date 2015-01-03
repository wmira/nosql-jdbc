package co.miranext.nosql.postgresql;

import co.miranext.nosql.*;
import co.miranext.nosql.query.SQLColumnQuery;
import co.miranext.nosql.query.SQLDMLObject;
import co.miranext.nosql.query.SQLObjectQuery;
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
 * Postgresql implementation of JsonRepository
 */
public class PgsqlJsonRepository implements JsonRepository {



    private DataSource dataSource;

    /**
     *
     *
     * @param dataSource
     */
    public PgsqlJsonRepository(final DataSource dataSource) {
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

    /**
     * Save this
     *
     * @param document
     * @param <T>
     */
    @Override
    public <T> void saveOrUpdate(T document) {

        final SQLDMLObject dmlObj = new SQLDMLObject(document,CRITERION_TRANSFORMER);
        final String sqlQuery = dmlObj.getSqlQuery();
        final DocumentMeta meta = dmlObj.getDocumentMeta();
        final Map<String,Object> extraValues = dmlObj.getExtraValues();

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sqlQuery) ){

            Map<Integer,String> mapping = dmlObj.getIndexMapping();

            for ( Integer idx : mapping.keySet() ) {
                String name = mapping.get(idx);
                if ( name.equals(meta.getColumnName())) { //this is
                    PGobject jsonObj = toPGObject(toJsonString(document));
                    pstmt.setObject(idx,jsonObj,valueToSqlType(jsonObj));
                } else {
                    //TODO, i think we should allow to set values of extras to be passed in and not be set on the document
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

        SQLObjectQuery<T> sqlObjectQuery = new SQLObjectQuery<T>(document);
        List<T> results = new ArrayList<T>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sqlObjectQuery.toSQLSelectQuery(criteria,CRITERION_TRANSFORMER)) ){

            //populate
            populateStatement(pstmt,criteria);
            ResultSet rs = pstmt.executeQuery();

            while ( rs.next() ) {
                results.add( createDocument(document, sqlObjectQuery, rs));
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


    /**
     *
     *

     * @param document
     * @param criteria
     * @param <T>
     * @return
     */
    private <T> T findInternal(final Class<T> document,final Criteria criteria) {

        SQLObjectQuery<T> sqlObjectQuery = new SQLObjectQuery<T>(document);

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sqlObjectQuery.toSQLSelectQuery(criteria,CRITERION_TRANSFORMER)) ){

            //populate
            populateStatement(pstmt,criteria);
            ResultSet rs = pstmt.executeQuery();

            if ( rs.next() ) {
                return createDocument(document, sqlObjectQuery, rs);

            }
        } catch ( Exception e ) {
            throw new RuntimeException("Error on find: " + e.getMessage(),e);
        }
        return null;
    }

    public static <T> T createDocument(final Class<T> clsdocument,final SQLObjectQuery<T> sqlObjectQuery,
                                             final ResultSet rs) throws  Exception {

        T instance = createDocumentFromRs(clsdocument,sqlObjectQuery,rs);

        //we need to populate other fields
        Map<String,DocumentRefMeta> refs = sqlObjectQuery.getFieldRefs();
        Map<String,SQLObjectQuery> refsQuery = sqlObjectQuery.getRefSQLObjectQuery();

        if ( refs != null && refs.size() > 0 ) {
            for ( String refKey : refs.keySet() ) {
                DocumentRefMeta refMeta = refs.get(refKey);
                SQLObjectQuery refObjectQuery = refsQuery.get(refKey);

                setObjectRef(instance,sqlObjectQuery,refMeta,refObjectQuery,rs);
            }
        }
        return instance;
    }

    public static <T> void setObjectRef(final T instance,final SQLObjectQuery parentObjectQuery, final DocumentRefMeta refMeta,
        final SQLObjectQuery refObjectQuery,final ResultSet rs) throws Exception {

        Object ref = createDocumentFromRs(refMeta.getDocumentRef().document(),refObjectQuery,rs);
        if ( ref == null ) {
            return;
        }

        Map<String,FieldAccess> faccessMap = parentObjectQuery.getObjectFields();

        FieldAccess fieldAccess = faccessMap.get(refMeta.getFieldName());

        if ( fieldAccess != null ) {
            fieldAccess.setObject(instance,ref);
        }

        FieldAccess fieldIdAccess = faccessMap.get(refMeta.getRefIdFieldName());

        //set the ID
        DocumentMeta thisMeta = refMeta.getMeta();
        FieldAccess refFieldAccess = BeanUtils.getField(ref,thisMeta.getIdField());

        if ( refFieldAccess != null ) {
            Object id = refFieldAccess.getObject(ref);
            fieldIdAccess.setObject(instance,id);
        }


    }

    public static<T> T createDocumentFromRs(final Class<T> clsdocument,final SQLObjectQuery<T> sqlObjectQuery,
                                            final ResultSet rs) throws  Exception {

        SQLColumnQuery columnQuery = sqlObjectQuery.getColumnQuery();
        DocumentMeta meta = sqlObjectQuery.getMeta();

        String jsonStr = rs.getString(columnQuery.getFieldAliasFor(meta.getColumnName()));
        if ( jsonStr == null ) {
            return null;
        }
        T instance = createInstanceFromJsonString(clsdocument,
                jsonStr);
        //now extras
        ColumnExtra[] extras = meta.getColumnExtras();

        Map<String,FieldAccess> fields = sqlObjectQuery.getObjectFields();

        for ( ColumnExtra extra : extras ) {
            String rsColumn = columnQuery.getFieldAliasFor(extra.getColumn());
            String column =  extra.getColumn();
            //Object rsData = rs.getObject(column);
            String beanField = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,column);

            Field field = extractField(column,fields);
            FieldAccess fas = fields.get(beanField);
            TypeType type = fas.typeEnum();

            Object rsVal;
            if ( TypeType.LONG.equals(type) || TypeType.LONG_WRAPPER.equals(type) )  {
                rsVal = rs.getLong(rsColumn);
            } else if ( TypeType.INTEGER_WRAPPER.equals(type) || TypeType.INT.equals(type) ) {
                rsVal = rs.getInt(rsColumn);
            } else if ( TypeType.STRING.equals(type) ) {
                rsVal = rs.getString(rsColumn);
            } else {
                throw new RuntimeException("don't know what to do: " + type + " for column: '" + column + "'");
            }

            field.set(instance,rsVal);
        }

        return instance;
    }
    public static <T> T createInstanceFromJsonString(final Class<T> document,final String jsonData) {
        ObjectMapper mapper =  JsonFactory.create();
        return mapper.readValue(jsonData,document);
    }


    private <T> T findInternal(Class<T> document, final String id, Criteria criteria) {

        DocumentMeta meta = DocumentMeta.fromAnnotation(document);
        PgsqlJsonFieldCriterion idCriterion = new PgsqlJsonFieldCriterion(meta.getColumnName(),meta.getIdField(),id);
        criteria.add(idCriterion);
        return findInternal(document,criteria);
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

    public static FieldCriterionTransformer CRITERION_TRANSFORMER = new FieldCriterionTransformer() {
        @Override
        public FieldCriterion transform(final DocumentMeta meta,final FieldCriterion criterion) {
            return new PgsqlJsonFieldCriterion(meta.getColumnName(), criterion.getField(), criterion.getValue());
        }

        @Override
        public FieldCriterion idFieldCriterion(final DocumentMeta meta,final String value) {
            return new PgsqlJsonFieldCriterion(meta.getColumnName(),meta.getIdField(),value);
        }
    };
}
