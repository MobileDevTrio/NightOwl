package com.example.MobileDevTrio.nightowl;

import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;

public class FavoritePlace extends SugarRecord<FavoritePlace> {
    private String name;
    private double locationLatitude;  // lat and lng coordinates of a place
    private double locationLongitude;
    private double rating;
    private String type;

    private String phone;
    private String website;
    private String address;
    private boolean open247;
    private String closingHours;

    public FavoritePlace() {}

    public FavoritePlace(Place place) {
        name = place.getName();
        locationLatitude = place.getLatitude();
        locationLongitude = place.getLongitude();
        rating = place.getRating();
        type = place.getSimplifiedType();
        phone = place.getPhone();
        website = place.getWebsite();
        address = place.getAddress();
        open247 = place.isOpen247();
        closingHours = place.getClosingHours();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(LatLng location) {
        locationLatitude = location.latitude;
        locationLongitude = location.longitude;
    }

    public double getLatitude(){
        return locationLatitude;
    }

    public double getLongitude(){
        return locationLongitude;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getSingleType() {
        return type;
    }

    public void setSingleType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isOpen247() {
        return open247;
    }

    public void setOpen247(boolean open247) {
        this.open247 = open247;
    }

    public String getClosingHours() {
        return closingHours;
    }

    public void setClosingHours(String closingHours) {
        this.closingHours = closingHours;
    }

}
