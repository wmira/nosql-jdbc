package co.miranext.docdb.sql;

import co.miranext.docdb.Criteria;
import co.miranext.docdb.DocumentMeta;
import org.boon.core.reflection.fields.FieldAccess;

import java.util.Map;

/**
 * TODO: finish this
 */
public class SQLBuilder {

    private DocumentMeta meta;

    public SQLBuilder(final DocumentMeta meta) {
        this.meta = meta;
    }

    public SQLBuilder select() {
        return null;
    }

    public static SQLBuilder create(final DocumentMeta meta) {
        return new SQLBuilder(meta);
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
    public static String createSqlSelect(DocumentMeta meta,final Criteria criteria) {

        StringBuilder builder = new StringBuilder("SELECT " + meta.getColumnName() + " ");
        builder.append(SQLBuilder.join(meta.getExtras(), " , "));
        builder.append("FROM " + meta.getTableName());
        builder.append(criteria.toSQLString());

        return builder.toString();
    }

    /**
     *
     *
     * @param meta
     * @param fields
     * @return
     */
    public static String createSqlInsert(DocumentMeta meta,final Map<String,FieldAccess> fields) {

        StringBuilder builder = new StringBuilder("INSERT INTO " + meta.getTableName() + " ( " + meta.getColumnName() + " ) VALUES ( ? ) ");

        //if extras is set, we need to set the values in external columns as well
        //TODO: do that later

        return builder.toString();
    }
}
