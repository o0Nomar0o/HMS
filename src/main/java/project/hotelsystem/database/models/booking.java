package project.hotelsystem.database.models;


import java.time.LocalDateTime;

public class booking {
    private String booking_id;
    private guest guest;
    private room room;
    private LocalDateTime booking_date;
    private LocalDateTime check_in;
    private LocalDateTime check_out;
    private String booking_status;

    public booking() {
    }

    public booking(String booking_id, guest guest, room room, LocalDateTime booking_date, LocalDateTime check_in, LocalDateTime check_out,
                   String booking_status) {
        super();
        this.booking_id = booking_id;
        this.guest = guest;
        this.room = room;
        this.booking_date = booking_date;
        this.check_in = check_in;
        this.check_out = check_out;
        this.booking_status = booking_status;
    }

    public booking(String booking_id,guest guest, room room, LocalDateTime booking_date, LocalDateTime check_in, LocalDateTime check_out) {
        this.booking_id = booking_id;
        this.guest = guest;
        this.room = room;
        this.booking_date = booking_date;
        this.check_in = check_in;
        this.check_out = check_out;
    }


    public booking(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public guest getGuest() {
        return guest;
    }

    public void setGuest(guest guest) {
        this.guest = guest;
    }

    public room getRoom() {
        return room;
    }

    public void setRoom(room room) {
        this.room = room;
    }

    public LocalDateTime getCheck_in() {
        return check_in;
    }

    public void setCheck_in(LocalDateTime check_in) {
        this.check_in = check_in;
    }

    public LocalDateTime getCheck_out() {
        return check_out;
    }

    public void setCheck_out(LocalDateTime check_out) {
        this.check_out = check_out;
    }

    public String getBooking_status() {
        return booking_status;
    }

    public void setBooking_status(String booking_status) {
        this.booking_status = booking_status;
    }

    public LocalDateTime getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(LocalDateTime booking_date) {
        this.booking_date = booking_date;
    }

}