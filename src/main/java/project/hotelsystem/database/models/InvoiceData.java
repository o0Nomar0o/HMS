package project.hotelsystem.database.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InvoiceData {
    private String guestName;
    private String phoneNo;
    private String email;
    private LocalDateTime bookingDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String roomNo;
    private String roomType;
    private double total_room_cost;
    private double room_unit_cost;

    private List<order_service> services = new ArrayList<>();
    private List<order_food> foods = new ArrayList<>();


    public InvoiceData() {
        this.services = services;
        this.foods = foods;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public List<order_service> getServices() {
        return services;
    }

    public void setServices(List<order_service> services) {
        this.services = services;
    }

    public List<order_food> getFoods() {
        return foods;
    }

    public void setFoods(List<order_food> foods) {
        this.foods = foods;
    }

    public void addService(order_service service) {
        this.services.add(service);
    }

    public void addFood(order_food food) {
        this.foods.add(food);
    }

    public double getTotal_room_cost() {
        return total_room_cost;
    }

    public void setTotal_room_cost(double total_room_cost) {
        this.total_room_cost = total_room_cost;
    }

    public double getRoom_unit_cost() {
        return room_unit_cost;
    }

    public void setRoom_unit_cost(double room_unit_cost) {
        this.room_unit_cost = room_unit_cost;
    }

    @Override
    public String toString() {
        return "InvoiceData{" +
                "guestName='" + guestName + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", email='" + email + '\'' +
                ", bookingDate=" + bookingDate +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", roomNo='" + roomNo + '\'' +
                ", roomType='" + roomType + '\'' +
                ", services=" + services +
                ", foods=" + foods +
                '}';
    }
}