package project.hotelsystem.database.models;

import java.time.LocalDateTime;
import java.util.List;

public class bookingDetails {
    booking bk_booking;
    customer bk_customer;
    room bk_room;

    LocalDateTime check_in;
    String ci_payment_method;

    LocalDateTime check_out;
    String co_payment_method;

    List<order_food> food_orders;
    List<order_service> services_orders;

    public bookingDetails(booking bk_booking, customer bk_customer, room bk_room, LocalDateTime check_in, String ci_payment_method, LocalDateTime check_out, String co_payment_method) {
        this.bk_booking = bk_booking;
        this.bk_customer = bk_customer;
        this.bk_room = bk_room;
        this.check_in = check_in;
        this.ci_payment_method = ci_payment_method;
        this.check_out = check_out;
        this.co_payment_method = co_payment_method;
    }

    public booking getBk_booking() {
        return bk_booking;
    }

    public void setBk_booking(booking bk_booking) {
        this.bk_booking = bk_booking;
    }

    public customer getBk_customer() {
        return bk_customer;
    }

    public void setBk_customer(customer bk_customer) {
        this.bk_customer = bk_customer;
    }

    public room getBk_room() {
        return bk_room;
    }

    public void setBk_room(room bk_room) {
        this.bk_room = bk_room;
    }

    public LocalDateTime getCheck_in() {
        return check_in;
    }

    public void setCheck_in(LocalDateTime check_in) {
        this.check_in = check_in;
    }

    public String getCi_payment_method() {
        return ci_payment_method;
    }

    public void setCi_payment_method(String ci_payment_method) {
        this.ci_payment_method = ci_payment_method;
    }

    public LocalDateTime getCheck_out() {
        return check_out;
    }

    public void setCheck_out(LocalDateTime check_out) {
        this.check_out = check_out;
    }

    public String getCo_payment_method() {
        return co_payment_method;
    }

    public void setCo_payment_method(String co_payment_method) {
        this.co_payment_method = co_payment_method;
    }
}
