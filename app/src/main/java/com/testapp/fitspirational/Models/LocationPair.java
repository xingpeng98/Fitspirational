package com.testapp.fitspirational.Models;

import android.location.Location;

import com.mapbox.geojson.Point;

public class LocationPair {
    Location p1;
    Location p2;
    float[] results;

    public LocationPair() {
    }

    public void updateLocation(Location p) {
        if (p1 == null && p2 == null) {
            p1 = p;
        } else if (p1 != null && p2 == null) {
            p2 = p;
        } else {
            p1 = p2;
            p2 = p;
        }
    }

    public double getDistanceBetweenPoints() {
        if (p2 == null) {
            return 0;
        } else {
            results = new float[1];
            Location.distanceBetween(p1.getLatitude(), p1.getLongitude(), p2.getLatitude(), p2.getLongitude(), results);
            return results[0];
        }
    }
}
