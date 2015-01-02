package co.miranext.nosql.testbean;

import co.miranext.nosql.Document;
import co.miranext.nosql.DocumentRef;

/**
 *
 */
@Document(table="personnel",extras = {"row_id"})
public class Personnel {

    private String id;
    private String defaultChannelId;
    private Integer rowId;
    @DocumentRef(document=Channel.class)
    private Channel defaultChannel;
    private String fullname;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDefaultChannelId() {
        return defaultChannelId;
    }

    public void setDefaultChannelId(String defaultChannelId) {
        this.defaultChannelId = defaultChannelId;
    }

    public Integer getRowId() {
        return rowId;
    }

    public void setRowId(Integer rowId) {
        this.rowId = rowId;
    }

    public Channel getDefaultChannel() {
        return defaultChannel;
    }

    public void setDefaultChannel(Channel defaultChannel) {
        this.defaultChannel = defaultChannel;
    }
}
