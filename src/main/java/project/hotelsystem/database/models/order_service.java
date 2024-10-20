package project.hotelsystem.database.models;

import java.time.LocalDateTime;

public class order_service {
    private int id;
    private booking bk;
    private room room_no;
    private service sid;
    private int qnt;
    private LocalDateTime order_time;
    private double total_price;

    public order_service(int id, service sid, LocalDateTime order_time, double total_price) {
        this.id = id;
        this.sid = sid;
        this.order_time = order_time;
        this.total_price = total_price;
    }

    public order_service(int id, booking bk, room room_no, service sid, int qnt, double total_price) {
        this.id = id;
        this.bk = bk;
        this.room_no = room_no;
        this.sid = sid;
        this.qnt = qnt;
        this.total_price = total_price;
    }

    public order_service(booking bk, room room_no, service sid, int qnt, double total_price) {
        this.bk = bk;
        this.room_no = room_no;
        this.sid = sid;
        this.qnt = qnt;
        this.total_price = total_price;
    }

    public order_service(int id, service sid, int qnt, double total_price) {
       this.id = id;
        this.sid = sid;
        this.qnt = qnt;
        this.total_price = total_price;
    }

    public order_service(booking bk, room room_no, service sid, int qnt) {
        this.bk = bk;
        this.room_no = room_no;
        this.sid = sid;
        this.qnt = qnt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public booking getBk() {
        return bk;
    }

    public void setBk(booking bk) {
        this.bk = bk;
    }

    public room getRoom_no() {
        return room_no;
    }

    public void setRoom_no(room room_no) {
        this.room_no = room_no;
    }

    public service getSid() {
        return sid;
    }

    public void setSid(service sid) {
        this.sid = sid;
    }

    public int getQnt() {
        return qnt;
    }

    public void setQnt(int qnt) {
        this.qnt = qnt;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    @Override
    public String toString() {
        return "order_service{" +
                "id='" + id + '\'' +
                ", bk=" + bk +
                ", room_no=" + room_no +
                ", sid=" + sid +
                ", qnt=" + qnt +
                ", total_price=" + total_price +
                '}';
    }
}
