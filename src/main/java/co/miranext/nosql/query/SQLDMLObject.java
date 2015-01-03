package co.miranext.nosql.query;

import co.miranext.nosql.ColumnExtra;
import co.miranext.nosql.DocumentMeta;
import co.miranext.nosql.criteria.FieldCriterion;
import co.miranext.nosql.criteria.FieldCriterionTransformer;
import com.google.common.base.CaseFormat;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.fields.FieldAccess;

import java.util.*;

/**
 * Insert and Update helpers
 *
 */
public class SQLDMLObject<T> {


    private final Map<String,FieldAccess> fieldAccess;
    private final Map<Integer,String> indexMapping = new HashMap<>();
    private final DocumentMeta documentMeta;
    private final String sqlQuery;
    private final Map<String,Object> extraValues;

    public SQLDMLObject(final T documentInstance) {
        this(documentInstance,null);
    }
    /**
     * Create a SQLDML query for the given instance
     *
     * @param documentInstance
     */
    public SQLDMLObject(final T documentInstance,final FieldCriterionTransformer transformer) {

        this.documentMeta = DocumentMeta.fromAnnotation(documentInstance.getClass());
        fieldAccess = BeanUtils.getFieldsFromObject(documentInstance.getClass());

        Object id = BeanUtils.getPropertyValue(documentInstance, documentMeta.getIdField());
        extraValues  = generateExtraValues(documentInstance,true,this.documentMeta,fieldAccess);

        if ( id == null ) {
            id = UUID.randomUUID().toString();
            fieldAccess.get("id").setObject(documentInstance,id);
            sqlQuery = generateInsert(documentMeta,indexMapping);
        } else {
            sqlQuery = generateUpdate(documentInstance,documentMeta,indexMapping,transformer);
            extraValues.put(documentMeta.getIdField(),id);
        }



    }

    /**
     *
     *
     * @return
     */
    public String getSqlQuery() {
        return sqlQuery;
    }

    public Map<Integer, String> getIndexMapping() {
        return indexMapping;
    }


    public DocumentMeta getDocumentMeta() {
        return documentMeta;
    }

    public Map<String, Object> getExtraValues() {
        return extraValues;
    }

    public static String generateInsert(final DocumentMeta documentMeta,final Map<Integer,String> indexMapping ) {
        StringBuilder builder = new StringBuilder("INSERT INTO " + documentMeta.getTableName() + " ( " + SQLDMLObject.generateInsertFields(documentMeta) + " ) " +
                "VALUES ( " + generateInsertParams(documentMeta,indexMapping) + " ) ");
        return builder.toString();
    }

    public static <T> String generateUpdate(final T documentInstance,final DocumentMeta meta,final Map<Integer,String> indexMapping,final FieldCriterionTransformer transformer) {

        Object id = BeanUtils.getPropertyValue(documentInstance, meta.getIdField());
        String idStr;
        try {
            idStr = id.toString();
        } catch ( Exception e) {
            throw new RuntimeException("Unable to retrieve id field: " + e.getMessage(),e);
        }

        FieldCriterion fd = transformer.idFieldCriterion(meta,idStr);
        StringBuilder builder = new StringBuilder("UPDATE " + meta.getTableName() + " SET " + meta.getColumnName() + "=? WHERE " + fd.toSQLString(null));

        indexMapping.put(1,meta.getColumnName());
        indexMapping.put(2,meta.getIdField());

        return builder.toString();
    }

    public static String generateInsertFields(final DocumentMeta meta) {
        StringBuilder sb = new StringBuilder();
        sb.append(meta.getColumnName());

        ColumnExtra[] extras = meta.getColumnExtras();

        if ( extras != null && extras.length > 0 ) {
            sb.append(",");
            sb.append(SQLObjectQuery.join(columnExtrasToString(true, extras), " , "));
        }
        return sb.toString();
    }

    public static String generateInsertParams(final DocumentMeta meta,final Map<Integer,String> indexMap) {


        int startCount = 1;

        StringBuilder sb = new StringBuilder();
        sb.append(" ? ");
        indexMap.put(startCount++,meta.getColumnName());


        ColumnExtra[] extras = meta.getColumnExtras();
        if ( extras != null && extras.length > 0 ) {
            sb.append(SQLObjectQuery.SQL_STMT_DELIM);
            for ( int i=0; i < extras.length; i++ ) {
                ColumnExtra ce = extras[i];
                if ( ce.isAuto() ) {
                    continue;
                }
                sb.append(" ? " );//ce.getColumn());
                indexMap.put(startCount++, ce.getColumn());
                if ( i != extras.length -1 ) {
                    sb.append(SQLObjectQuery.SQL_STMT_DELIM);
                }
            }
        }

        return sb.toString();
    }

    public static String[] columnExtrasToString(boolean skipAuto,final ColumnExtra[] extras) {

        List<String> extrasStr = new ArrayList<>();
        int count = 0;
        for ( int i=0; i < extras.length;i++ ) {
            ColumnExtra ce = extras[i];
            if ( skipAuto && ce.isAuto() ) {//on insert, auto field is ignored
                continue;
            }
            extrasStr.add(ce.getColumn());
            count++;
        }
        return extrasStr.toArray(new String[count]);
    }

    public static  <T> Map<String,Object> generateExtraValues(final T document,final boolean skipAuto,final DocumentMeta meta,final Map<String,FieldAccess> fields) {

        final Map<String,Object> extraVals = new HashMap<>();
        ColumnExtra extras[] = meta.getColumnExtras();

        if ( extras != null && extras.length > 0 ) {
            for ( int i =0; i < extras.length; i++ ) {
                ColumnExtra ce = extras[i]; //column extra is database table based. convert to bean type
                if ( skipAuto && ce.isAuto() ) {
                    continue;
                }
                FieldAccess field = fields.get(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,ce.getColumn()));
                if (field != null) {
                    Object value = field.getValue(document);
                    if (value == null) {
                        throw new RuntimeException("extra field: '" + ce + "' value is null for document " + document.getClass().getName());
                    }
                    extraVals.put(ce.getColumn(), value);
                } else {
                    throw new RuntimeException("Extra field "  + ce + " not found on document.");

                }
            }
        }
        return extraVals;
    }
}
