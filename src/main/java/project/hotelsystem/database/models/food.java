package project.hotelsystem.database.models;

import java.sql.Blob;

public class food {
    private int id;
    private String name;
    private double price;
    private String category;
    private String stock_status;
    private int stock;

    private String imagePath;
    private Blob image;


    public food(int id, String name, double price, String category, String stock_status) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.stock_status = stock_status;
    }

    public food(int id, String name, double price, String category, String stock_status, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.stock_status = stock_status;
        this.imagePath = imagePath;
    }

    public food(int id, String name, double price, String category, String stock_status, Blob image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.stock_status = stock_status;
        this.image = image;
    }

    public food(String name, int stock, String stock_status) {
        this.stock = stock;
        this.stock_status = stock_status;
        this.name = name;
    }

    public food(String name, double price, Blob image, int stock) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.stock = stock;
    }

    public food(String name, double price, Blob image, int stock, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.image = image;
        this.stock = stock;
    }

    public food(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public food(String name, Blob image) {
        this.name = name;
        this.image = image;
    }

    public food(String name, int stock) {
        this.name = name;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStock_status() {
        return stock_status;
    }

    public void setStock_status(String stock_status) {
        this.stock_status = stock_status;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}