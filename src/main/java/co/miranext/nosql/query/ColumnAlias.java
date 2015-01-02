package co.miranext.nosql.query;

/**
 *
 */
public class ColumnAlias {

    private String columnName;
    private String tableAliasName;
    private String queryAlias; //this will be something like tablealias.columnName fieldAlias
    private String fieldAlias; //fieldAlias is fieldAlias



    public ColumnAlias(final String columnName,final String tableAliasName,final int index) {
        this.columnName = columnName;
        this.tableAliasName = tableAliasName;
        this.fieldAlias = tableAliasName + "" + index;
        this.queryAlias =  this.tableAliasName + "." + this.columnName + " " + this.fieldAlias;
    }

    public String getQueryAlias() {
        return queryAlias;
    }

    public String getFieldAlias() {
        return fieldAlias;
    }
}
