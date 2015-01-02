package co.miranext.nosql.testbean;

import co.miranext.nosql.Document;

/**
 *
 */
@Document(table="channel")
public class Channel {

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
