package co.miranext.docdb;

import co.miranext.docdb.sql.SQLBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miranext on 12/28/14.
 */
public  class Criteria {

    protected List<Criterion> criteria;

    public Criteria() {
        this.criteria = new ArrayList<Criterion>();
    }

    public Criteria add(final Criterion criterion) {
        this.criteria.add(criterion);
        return this;
    }


    public boolean isEmpty() {
        return this.criteria.size() == 0;
    }


    public List<Criterion> getCriteria() {
        return this.criteria;
    }
}
