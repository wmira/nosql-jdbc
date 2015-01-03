package co.miranext.nosql.postgresql;

import co.miranext.nosql.FieldCriterion;

/**
 *
 * TODO: we should just replace this and use directly
 *
 */
public class PgsqlJsonFieldCriterion extends FieldCriterion {

    public PgsqlJsonFieldCriterion(String field, Object value) {
        super(field, value);
    }

    public PgsqlJsonFieldCriterion(String columnName, String field, Object value) {
        super(columnName, field, value);
    }

    @Override
    public String toSQLString(String alias) {
        if ( this.columnName == null ) {
            throw new RuntimeException("column name is required!");
        }
        String prefix = "";
        if ( alias != null ) {
            prefix = alias + ".";
        }

        return prefix + this.columnName + "->>'" + this.field + "'=?";
    }

}
