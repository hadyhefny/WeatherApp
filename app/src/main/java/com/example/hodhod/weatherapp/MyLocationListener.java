package com.example.hodhod.weatherapp;

import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;

public class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location loc) {

        /*String longitude = "Longitude: " + loc.getLongitude();
        Log.v("gpsLocation", longitude);
        String latitude = "Latitude: " + loc.getLatitude();
        Log.v("gpsLocation", latitude);*/
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}


