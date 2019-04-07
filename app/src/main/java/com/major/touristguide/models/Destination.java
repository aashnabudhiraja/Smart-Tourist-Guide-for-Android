package com.major.touristguide.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class Destination {

    private LatLng pos;
    private String name;

    public Destination(double latitude, double longitude, String name) {
        this.pos = new LatLng(latitude, longitude);
        this.name = name;
    }

    // direct distance ------ distance by roads??
    public double distanceTo(Destination destination) {

        double lat1 = Math.toRadians(this.pos.latitude);
        double lng1 = Math.toRadians(this.pos.longitude);
        double lat2 = Math.toRadians(destination.getLatLng().latitude);
        double lng2 = Math.toRadians(destination.getLatLng().longitude);

        // Haversine Formula (Distance of points on sphere)
        double Radius = 6371; // radius of earth in km

        double dLat = lat2 - lat1;
        double dLng = lng2 - lng1;
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLng / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return Radius * c;
    }

    public LatLng getLatLng() { return pos; }

    @Override
    public String toString(){
        return String.valueOf(pos.latitude) + ", " + String.valueOf(pos.longitude);
    }


}
