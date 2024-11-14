package project.hotelsystem.database.models;

public class room_price {
    String id;
    double pricePerNight;
    double pricePerHour;

    public room_price(String id, double pricePerNight, double pricePerHour) {
        this.id = id;
        this.pricePerNight = pricePerNight;
        this.pricePerHour = pricePerHour;
    }

    public room_price() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }
}
