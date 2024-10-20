package project.hotelsystem.database.models;
import project.hotelsystem.database.models.room_type;
public class room {
    private String room_no;
    private room_type room_type;
    private int floor;
    private String room_status;

    public room() {
    }

    public room(String room_no, project.hotelsystem.database.models.room_type room_type, int floor, String room_status) {
        this.room_no = room_no;
        this.room_type = room_type;
        this.floor = floor;
        this.room_status = room_status;
    }

    public room(String room_no) {
        this.room_no = room_no;
    }

    public String getRoom_no() {
        return room_no;
    }

    public void setRoom_no(String room_no) {
        this.room_no = room_no;
    }

    public project.hotelsystem.database.models.room_type getRoom_type() {
        return room_type;
    }

    public void setRoom_type(project.hotelsystem.database.models.room_type room_type) {
        this.room_type = room_type;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getRoom_status() {
        return room_status;
    }

    public void setRoom_status(String room_status) {
        this.room_status = room_status;
    }
}