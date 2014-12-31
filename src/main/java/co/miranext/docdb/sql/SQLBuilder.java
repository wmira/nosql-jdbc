package co.miranext.docdb.sql;

import co.miranext.docdb.*;
import com.google.common.base.CaseFormat;
import org.boon.core.reflection.fields.FieldAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: finish this
 */
public class SQLBuilder {

    public static String SQL_STMT_DELIM = " , ";

    private String sqlQuery;
    private Map<Integer,String> indexMapping;

    private final DocumentMeta meta;
    private final Map<String,FieldAccess> fields;

    /**
     *
     *
     * @param meta
     * @param fields
     */
    public SQLBuilder(final DocumentMeta meta,final Map<String,FieldAccess> fields) {
        this.meta = meta;
        this.fields = fields;
    }



    /**
     *
     */
    public void generateInsert() {
        if ( sqlQuery != null ) {
            throw  new RuntimeException("Already generated.");
        }
        this.indexMapping = new HashMap<>();
        StringBuilder builder = new StringBuilder("INSERT INTO " + meta.getTableName() + " ( " + SQLBuilder.generateInsertFields(meta) + " ) VALUES ( " + generateInsertParams(meta,indexMapping) + " ) ");
        this.sqlQuery = builder.toString();
    }

    public Map<Integer, String> getIndexMapping() {
        return indexMapping;
    }

    public void generateUpdate() {
        throw new RuntimeException("Implement me pls");
    }

    public String getSqlQuery() {
        return sqlQuery;
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

    /**
     * Generate ?,?,? and this will create a proper index for the preparedstatement
     *
     */
    public static String generateInsertParams(final DocumentMeta meta,final Map<Integer,String> indexMap) {


        int startCount = 1;

        StringBuilder sb = new StringBuilder();
        sb.append(" ? ");
        indexMap.put(startCount++,meta.getColumnName());


        ColumnExtra[] extras = meta.getColumnExtras();
        if ( extras != null && extras.length > 0 ) {
            sb.append(SQL_STMT_DELIM);
            for ( int i=0; i < extras.length; i++ ) {
                ColumnExtra ce = extras[i];
                if ( ce.isAuto() ) {
                    continue;
                }
                sb.append(ce.getColumn());
                indexMap.put(startCount++,ce.getColumn());
                if ( i != extras.length -1 ) {
                    sb.append(SQL_STMT_DELIM);
                }
            }
        }
        sb.append(" ) ");
        return sb.toString();
    }

    public static String join(final String[] strings,String joinString) {

        StringBuilder sb = new StringBuilder();
        for ( int i=0; i < strings.length; i++ ) {
            sb.append(strings[i]);
            if ( i != strings.length -1 ) {
                sb.append(joinString);
            }

        }
        return sb.toString();

    }


    /**
     *
     *
     * @param meta
     * @return
     */
    public static String createSqlSelect(DocumentMeta meta,final Criteria criteria, FieldCriterionTransformer transformer) {

        StringBuilder builder = new StringBuilder("SELECT " + meta.getColumnName() + " ");
        if ( meta.getColumnExtras().length > 0 ) {
            builder.append(" , ");
            builder.append(SQLBuilder.join(meta.getExtras(), " , "));
        }

        builder.append(" FROM " + meta.getTableName() + " ");

        if ( !criteria.isEmpty() ) {
            builder.append(" WHERE ");
            builder.append(SQLBuilder.toSQLString(meta,transformer,criteria));
        }


        return builder.toString();
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

    /**
     * Generates data,field1,field2 for Insert into (..)
     *
     * FIXME: if we ever use something like mariadb's dynamic column, then this will not work
     *
     * @param meta
     * @return
     */
    public static String generateInsertFields(final DocumentMeta meta) {
        StringBuilder sb = new StringBuilder();
        sb.append(meta.getColumnName());

        ColumnExtra[] extras = meta.getColumnExtras();

        if ( extras != null && extras.length > 0 ) {
            sb.append(",");
            sb.append(join(columnExtrasToString(true,extras)," , "));
        }
        return sb.toString();
    }
    /**
     *
     *
     * @param meta
     * @param fields
     * @return
     */
    public static String createSqlInsert(final DocumentMeta meta,final Map<String,FieldAccess> fields) {

        StringBuilder builder = new StringBuilder("INSERT INTO " + meta.getTableName() + " ( " + generateInsertFields(meta) + " ) VALUES ( ? ) ");

        //if extras is set, we need to set the values in external columns as well
        //TODO: do that later

        return builder.toString();
    }

    /**
     * Creates a ?
     *
     * @return
     */
    public static String toSQLString(final DocumentMeta meta,final FieldCriterionTransformer transformer, final Criteria criteria) {

        List<String> strings = new ArrayList<String>();

        for ( Criterion criterion :criteria.getCriteria()  ) {
            Criterion toprocess = criterion;
            if ( transformer != null && toprocess instanceof FieldCriterion ) {
                toprocess = transformer.transform(meta,(FieldCriterion)toprocess);
            }

            strings.add(toprocess.toSQLString());
        }

        return SQLBuilder.join(strings.toArray(new String[strings.size()]),SQL_STMT_DELIM);
    }

    public interface FieldCriterionTransformer {
        public FieldCriterion transform(final DocumentMeta meta,final FieldCriterion criterion);
    }
}
