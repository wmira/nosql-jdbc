package co.miranext.docdb;

/**
 *
 */
public abstract class FieldCriterion implements Criterion {

    protected String field;
    protected Object value;
    protected String columnName;

    /**
     *
     *
     * @param field
     */
    public FieldCriterion(final String field, Object value) {
        this(null,field,value);
    }

    /**
     * We need to replace this with an actual column name
     *
     *
     * @param columnName
     * @param field
     * @param value
     */
    public FieldCriterion(final String columnName,final String field, Object value) {
        this.columnName = columnName;
        this.field = field;
        this.value = value;
    }

    @Override
    public abstract String toSQLString();

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    public String getColumnName() {
        return columnName;
    }
}
