package com.example.quanlyphuongtien.Entities;

public class Location {
    private double lat,lng;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
