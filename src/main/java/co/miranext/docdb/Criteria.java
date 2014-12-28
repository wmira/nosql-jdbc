package co.miranext.docdb;

import co.miranext.docdb.sql.SQLBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miranext on 12/28/14.
 */
public  class Criteria {
    public static String SQL_STMT_DELIM = " , ";
    protected List<Criterion> criteria;

    public Criteria() {
        this.criteria = new ArrayList<Criterion>();
    }

    public void add(final Criterion criterion) {
        this.criteria.add(criterion);
    }

    /**
     * Creates a ?
     *
     * @return
     */
    public String toSQLString() {

        List<String> strings = new ArrayList<String>();

        for ( Criterion criterion : criteria ) {
            strings.add(criterion.toSQLString());
        }

        return SQLBuilder.join(strings.toArray(new String[strings.size()]),SQL_STMT_DELIM);
    }

    public List<Criterion> getCriteria() {
        return this.criteria;
    }
}
