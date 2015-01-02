package co.miranext.nosql.testbean;

import co.miranext.nosql.Document;

/**
 *
 */
@Document(table="parent_with_extras",extras = {"auto:record_id","owner_id"})
public class ParentWithExtras {

    private String id;
    private Long recordId;
    private Long ownerId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
