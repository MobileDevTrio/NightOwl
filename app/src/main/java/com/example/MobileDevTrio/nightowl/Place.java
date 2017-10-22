package com.example.MobileDevTrio.nightowl;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;

public class Place {

    private String placeId;   // Google ID of a place, get more details of a place w/ this id
    private String name;
    private LatLng location;  // lat and lng coordinates of a place
    private double rating;
    private String[] types;   // types of a place (ie. restaurant, bar, night_club)

    private String type;
    private String simplifiedType;

    // Must make a 2nd url request using placeId to get these specific data
    private String phone;
    private String website;
    private String address;

    public Place() {
    }

    private Place(Builder builder) {
        setPlaceId(builder.placeId);
        setName(builder.name);
        setLocation(builder.location);
        setRating(builder.rating);
        //setTypes(builder.types);
        setSingleType(builder.type);
        setSimplifiedType(builder.simplifiedType);
    }

    @Override
    public String toString() {
        return "Place {" +
                "location=" + location +
                ", name='" + name + '\'' +
                ", placeId='" + placeId + '\'' +
                ", types=" + Arrays.toString(types) +
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
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String[] getTypes() {
        return types;
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

    public void setTypes(String[] types) {
        this.types = types;
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

    public double getLatitude(){
        return location.latitude;
    }

    public double getLongitude(){
        return location.longitude;
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
