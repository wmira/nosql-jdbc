package co.miranext.nosql.query;

import co.miranext.nosql.ColumnExtra;
import co.miranext.nosql.DocumentMeta;
import co.miranext.nosql.DocumentRefMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This creates JOINS and FIELDS
 */
public class SQLColumnQuery {

    private final SQLObjectQuery sqlObjectQuery;
    private final DocumentMeta selfMeta;
    private final DocumentRefMeta selfRefMeta;


    // generate query fields, so basically this is alias.column subalias
    private Map<String,ColumnAlias> queryFields = new HashMap<>();

    private String joinQuery;
    private String fieldsQuery;

    public SQLColumnQuery(final SQLObjectQuery sqlObjectQuery, final DocumentRefMeta refMeta, final SQLObjectQuery parent) {

        this.sqlObjectQuery = sqlObjectQuery;
        this.selfMeta = sqlObjectQuery.getMeta();
        this.selfRefMeta = refMeta;


        int aliasFieldCounter = 1;
        String documentAlias = sqlObjectQuery.getDocumentAlias();

        List<String> queryStrings = new ArrayList<>();
        //we start document
        queryFields.put(selfMeta.getColumnName(), new ColumnAlias(selfMeta.getColumnName(),documentAlias,aliasFieldCounter++)  );
        queryStrings.add(queryFields.get(selfMeta.getColumnName()).getQueryAlias());


        //extras
        ColumnExtra[] extras = selfMeta.getColumnExtras();
        for ( ColumnExtra extra : extras ) {
            String column = extra.getColumn();
            queryFields.put(column,new ColumnAlias(column,documentAlias,aliasFieldCounter++) );
            queryStrings.add(queryFields.get(column).getQueryAlias());
        }

        this.fieldsQuery = SQLObjectQuery.join(queryStrings.toArray(new String[queryStrings.size()]), " , ");

        if ( parent != null ) {
            this.joinQuery = //This query is so postgresql specific
             " LEFT OUTER JOIN " + this.selfMeta.getTableName()  + " " + documentAlias + " ON " +
                     documentAlias + "." + selfMeta.getColumnName()  +"->>'" + selfMeta.getIdField() +"'=" + parent.getDocumentAlias() + "." + parent.getMeta().getColumnName() +
                    "->>'" + selfRefMeta.getRefIdFieldName() +"'";
        }
    }

    public String getColumnAliasFor(final String column) {
        return queryFields.get(column).getQueryAlias();
    }

    public String getFieldAliasFor(final String column) {
        return queryFields.get(column).getFieldAlias();
    }

    public String toFromQuery() {
        return this.selfMeta.getTableName() + " " + this.sqlObjectQuery.getDocumentAlias();
    }

    public String getJoinQuery() {
        return joinQuery;
    }

    public String getFieldsQuery() {
        return fieldsQuery;
    }
}
