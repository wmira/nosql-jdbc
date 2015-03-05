package co.miranext.nosql.criteria;

/**
 *
 */
public class ColumnCriterion implements Criterion {

    protected final String column;
    protected final Object value;
    protected final CriterionOperator operator;

    public ColumnCriterion(final String column,final Object value) {
        this(CriterionOperator.EQ, column, value);
    }

    public ColumnCriterion(final CriterionOperator operator,final String column,final Object value) {
        this.operator = operator;
        this.column = column;
        this.value = value;
    }

    @Override
    public CriterionOperator getOperator() {
        return operator;
    }

    /**
     *
     * @return
     */
    public String getColumn() {
        return column;
    }

    @Override
    public String toSQLString(String alias) {
        String prefix = "";
        if ( alias != null ) {
            prefix = alias + ".";
        }
        //FIXME: do something with contains,startsWith and endsWith
        if ( operator.equals(CriterionOperator.IS_NOT_NULL) ) {
            return prefix + column + " IS NOT NULL ";
        } else if ( operator.equals(CriterionOperator.IS_NULL) ) {
            return prefix + column + " IS NULL ";
        } else {
            return prefix + column + operator.operator() + "?";
        }
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    public static ColumnCriterion EQ(final String column,final Object value) {
        return new ColumnCriterion(column,value);
    }
    public static ColumnCriterion GT(final String column,final Object value) {
        return new ColumnCriterion(CriterionOperator.GT,column,value);
    }
    public static ColumnCriterion LT(final String column,final Object value) {
        return new ColumnCriterion(CriterionOperator.LT,column,value);
    }
    public static ColumnCriterion GTE(final String column,final Object value) {
        return new ColumnCriterion(CriterionOperator.GTE,column,value);
    }
    public static ColumnCriterion LTE(final String column,final Object value) {
        return new ColumnCriterion(CriterionOperator.LTE,column,value);
    }
    public static ColumnCriterion CONTAINS(final String column,final Object value) {
        return new ColumnCriterion(CriterionOperator.CONTAINS,column,value);
    }
    public static ColumnCriterion STARTSWITH(final String column,final Object value) {
        return new ColumnCriterion(CriterionOperator.STARTSWITH,column,value);
    }
    public static ColumnCriterion ENDSWITH(final String column,final Object value) {
        return new ColumnCriterion(CriterionOperator.ENDSWITH,column,value);
    }
}
