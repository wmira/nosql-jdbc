package co.miranext.nosql.testbean;

import co.miranext.nosql.Document;
import co.miranext.nosql.DocumentRef;

/**
 *
 */
@Document
public class Parent {

    private String id;
    private String singleChildId;

    @DocumentRef(document=SingleChild.class)
    private SingleChild singleChild;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSingleChildId() {
        return singleChildId;
    }

    public void setSingleChildId(String singleChildId) {
        this.singleChildId = singleChildId;
    }

    public SingleChild getSingleChild() {
        return singleChild;
    }

    public void setSingleChild(SingleChild singleChild) {
        this.singleChild = singleChild;
    }
}
