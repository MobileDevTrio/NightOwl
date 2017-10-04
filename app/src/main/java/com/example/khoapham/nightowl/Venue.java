package com.example.khoapham.nightowl;

/**
 * Created by illyabalakin on 10/3/17.
 */

public class Venue {
    private String name;
    private double rating;
    private String type;
    private String address;
    private String description;
    private double closeTime;
    private double openTime;

    /**
     * CONSTRUCTORS
     */
    public Venue() {

    }

    public Venue(String name, double rating, String type, String address, String description,
                    double closeTime, double openTime) {
        this.name = name;
        this.rating = rating;
        this.type = type;
        this.address = address;
        this.description = description;
        this.closeTime = closeTime;
        this.openTime = openTime;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(double closeTime) {
        this.closeTime = closeTime;
    }

    public double getOpenTime() {
        return openTime;
    }

    public void setOpenTime(double openTime) {
        this.openTime = openTime;
    }
}
