package project.hotelsystem.database.models;

public class room_type_details {
    String id;
    String description;
    double pricePerNight;
    double pricePerHour;

    public room_type_details(String id, String description, double pricePerNight, double pricePerHour) {
        this.id = id;
        this.description = description;
        this.pricePerNight = pricePerNight;
        this.pricePerHour = pricePerHour;
    }

    public room_type_details(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
