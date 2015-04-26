package com.example.denis.argusnew;

/**
 * Created by set-user on 23.04.2015.
 */
public class LocationData {
    private double latitude, longitude;

    public LocationData()
    {
        latitude=0;
        longitude=0;
    }

    public LocationData(double lat, double lon){

        latitude=lat;
        longitude=lon;
    }

    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }

}
