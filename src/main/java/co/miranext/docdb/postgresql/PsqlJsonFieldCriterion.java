package co.miranext.docdb.postgresql;

import co.miranext.docdb.FieldCriterion;
import com.google.common.base.CaseFormat;

/**
 * Created by miranext on 12/28/14.
 */
public class PsqlJsonFieldCriterion extends FieldCriterion {

    public PsqlJsonFieldCriterion(String field, Object value) {
        super(field, value);
    }

    public PsqlJsonFieldCriterion(String columnName, String field, Object value) {
        super(columnName, field, value);
    }

    @Override
    public String toSQLString() {
        if ( this.columnName == null ) {
            throw new RuntimeException("column name is required!");
        }
        return this.columnName + "->>'" + this.field + "'=?";
    }
}
