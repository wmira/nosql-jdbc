package co.miranext.docdb.sql;

import co.miranext.docdb.Criteria;
import co.miranext.docdb.DocumentMeta;

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
}
