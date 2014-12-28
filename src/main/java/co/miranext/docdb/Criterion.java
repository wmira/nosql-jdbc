package co.miranext.docdb;

/**
 * Created by miranext on 12/28/14.
 */
public interface Criterion {

    public String toSQLString();

    public Object getValue();
}
