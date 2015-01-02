package co.miranext.nosql;

/**
 * Created by miranext on 12/28/14.
 */
public interface Criterion {

    public String toSQLString(String alias);

    public Object getValue();
}
