package co.miranext.nosql.criteria;

/**
 *
 */
public class FieldCriterion implements Criterion {

    protected String field;
    protected Object value;
    protected String columnName;
    protected CriterionOperator operator;

    /**
     *
     *
     * @param field
     */
    public FieldCriterion(final String field, Object value) {
        this(field,null,value);
    }

    public FieldCriterion(final String field, CriterionOperator operator, Object value) {
        this.field = field;
        this.value = value;
        this.operator = operator;

    }

    public String toSQLString(final String alias) {
        throw new RuntimeException("Not implemented");
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    public String getColumnName() {
        return columnName;
    }

    public CriterionOperator getOperator() {
        return operator;
    }
}
