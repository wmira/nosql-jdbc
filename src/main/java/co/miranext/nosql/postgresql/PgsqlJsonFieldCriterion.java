package co.miranext.nosql.postgresql;

import co.miranext.nosql.criteria.CriterionOperator;
import co.miranext.nosql.criteria.FieldCriterion;

/**
 *
 * TODO: we should just replace this and use directly
 *
 */
public class PgsqlJsonFieldCriterion extends FieldCriterion {
    private String columnName;

    public PgsqlJsonFieldCriterion(String columnName, String field,  CriterionOperator operator, Object value) {
        super(field,operator, value);
        this.columnName = columnName;
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

        if ( operator != null && operator.equals(CriterionOperator.IS_NOT_NULL) ) {
            return "(" + prefix + this.columnName + "->>'" + this.field + "') IS NOT NULL ";
        } else if ( operator != null  && operator.equals(CriterionOperator.IS_NULL) ) {
            return "(" + prefix + this.columnName + "->>'" + this.field + "') IS NULL ";
        } else  if ( operator != null  && CriterionOperator.BETWEEN.equals(operator) ) {
            return " cast(" + prefix + this.columnName + "->>'" + this.field + "' as bigint ) " + CriterionOperator.BETWEEN.name() + " ? and ? ";
        } else  {
            return prefix + this.columnName + "->>'" + this.field + "'=?";
        }


    }


}
