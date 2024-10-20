package project.hotelsystem.database.models;

public class room_type {
    private String rtid;
    private String room_type_name;
    private String description;

    public room_type(String rtid, String room_type_name, String description) {
        this.rtid = rtid;
        this.room_type_name = room_type_name;
        this.description = description;
    }

    public room_type(String rtid, String description) {
        this.rtid = rtid;
        this.description = description;
    }

    public room_type(String rtid) {
        this.rtid = rtid;
    }

    public String getRtid() {
        return rtid;
    }

    public void setRtid(String rtid) {
        this.rtid = rtid;
    }

    public String getRoom_type_name() {
        return room_type_name;
    }

    public void setRoom_type_name(String room_type_name) {
        this.room_type_name = room_type_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
