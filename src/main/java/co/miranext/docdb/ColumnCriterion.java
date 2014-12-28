package co.miranext.docdb;

/**
 *
 */
public class ColumnCriterion implements Criterion {

    protected String column;
    protected Object value;

    public ColumnCriterion(final String column,final Object value) {
        this.column = column;
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toSQLString() {
        return column + "=?";
    }
}
