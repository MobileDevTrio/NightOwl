package com.example.MobileDevTrio.nightowl;

import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.Arrays;

public class Place extends SugarRecord<Place> {

    private String placeId;   // Google ID of a place, get more details of a place w/ this id
    private String name;
    private double rating;
    private String simplifiedType;

    // Must make a 2nd url request using placeId to get these specific data
    private String phone;
    private String website;
    private String address;
    private boolean open247;
    private String closingHours;
    private double latitude;
    private double longitude;

    // Ignore attributes we don't want saved in the DB
    @Ignore
    private String type;

//    @Ignore
//    private LatLng location;  // lat and lng coordinates of a place

    @Ignore
    private boolean isFavorited;

    public Place() {
    }

    private Place(Builder builder) {
        setPlaceId(builder.placeId);
        setName(builder.name);
//        setLocation(builder.location);
        setRating(builder.rating);
        setSingleType(builder.type);
        setSimplifiedType(builder.simplifiedType);
        closingHours = "";
    }

    @Override
    public String toString() {
        return "Place {" +
//                "location=" + location +
                ", name='" + name + '\'' +
                ", placeId='" + placeId + '\'' +
                ", type=" + type +
                ", address='" + address + '\'' +
                "}";
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLocation() {
        return new LatLng(latitude, longitude);
    }

//    public void setLocation(LatLng location) {
//        this.location = location;
//    }

    public double getLatitude(){
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    public String getSimplifiedType() {
        return simplifiedType;
    }

    public void setSingleType(String type) {
        this.type = type;
    }

    public void setSimplifiedType(String simplifiedType) {
        this.simplifiedType = simplifiedType;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
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

    /**
     *  A helper class to build/initialize place object
     */
    public static final class Builder {
        private String placeId;
        private String name;
        private LatLng location;
        private double rating;
        private String[] types;
        private String type;
        private String simplifiedType;

        public Builder() {
        }

        public Builder placeId(String val) {
            placeId = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder location(LatLng val) {
            location = val;
            return this;
        }

        public Builder rating(double val) {
            rating = val;
            return this;
        }

        public Builder types(String[] val) {
            types = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Builder simplifiedType(String val) {
            simplifiedType = val;
            return this;
        }

        public Place build() {
            return new Place(this);
        }
    }
}
