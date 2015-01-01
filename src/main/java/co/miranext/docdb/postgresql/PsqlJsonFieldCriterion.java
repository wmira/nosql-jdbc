package co.miranext.docdb.postgresql;

import co.miranext.docdb.FieldCriterion;
import com.google.common.base.CaseFormat;

import java.sql.Types;

/**
 *
 *
 *
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
