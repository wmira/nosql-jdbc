package co.miranext.nosql;

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
    public String toSQLString(String alias) {
        String prefix = "";
        if ( alias != null ) {
            prefix = alias + ".";
        }
        return prefix + column + "=?";
    }
}
