package com.testapp.fitspirational.FeatureActivities.RunTracker.GPSTracker;

import android.location.Location;

public interface MyServiceCallback {
    void updateUserLoc(Location location);
}
