package project.hotelsystem.database.models;

public class order_food {
    private int id;
    private booking bk;
    private room rood_no;
    private food fid;
    private String description;
    private int qnt;
    private double total_price;

    public order_food(int id, booking bk, room rood_no, food fid, String description, int qnt, double total_price) {
        this.id = id;
        this.bk = bk;
        this.rood_no = rood_no;
        this.fid = fid;
        this.description = description;
        this.qnt = qnt;
        this.total_price = total_price;
    }

    public order_food( food fid, String description, int qnt, double total_price) {
        this.fid = fid;
        this.description = description;
        this.qnt = qnt;
        this.total_price = total_price;
    }

    public order_food(int id, food fid, int qnt, double total_price) {
        this.id = id;
        this.fid = fid;
        this.qnt = qnt;
        this.total_price = total_price;
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

    public room getRood_no() {
        return rood_no;
    }

    public void setRood_no(room rood_no) {
        this.rood_no = rood_no;
    }

    public food getFid() {
        return fid;
    }

    public void setFid(food fid) {
        this.fid = fid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "order_food{" +
                "id='" + id + '\'' +
                ", bk=" + bk +
                ", rood_no=" + rood_no +
                ", fid=" + fid +
                ", description='" + description + '\'' +
                ", qnt=" + qnt +
                ", total_price=" + total_price +
                '}';
    }
}