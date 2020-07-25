package com.testapp.fitspirational.HelperClasses;

import android.location.Location;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.testapp.fitspirational.Executors.RunChronometerExecutor;
import com.testapp.fitspirational.Models.LocationPair;

import java.util.Calendar;
import java.util.Date;

public class LocationStatsUpdater {

    private static final String TAG = "LocationStatsUpdate";
    String uId;
    FirebaseFirestore firebaseFirestore;
    RunChronometerExecutor chronometer;
    LocationPair pair = new LocationPair();
    TextView distanceText;
    TextView paceText;
    TextView calorieText;
    double distance;
    double calorieBurnt;

    //user stats
    double bmr;

    public LocationStatsUpdater(RunChronometerExecutor chronometer, String uId, FirebaseFirestore firebaseFirestore, TextView distanceText, TextView paceText, TextView calorieText) {
        this.chronometer = chronometer;
        this.uId = uId;
        this.firebaseFirestore = firebaseFirestore;
        this.distanceText = distanceText;
        this.paceText = paceText;
        this.calorieText = calorieText;
        this.distance = 0;
        this.calorieBurnt = 0.0;
    }

    public void start() {
        chronometer.startChronometer();
    }

    public void end() {
        chronometer.stopChronometer();
    }

    public void calculateBmr() {
        DocumentReference documentReference = firebaseFirestore.collection("users").document(uId);

        documentReference.get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String gender = document.getString("gender");
                    long birthYear =  document.getLong("birthYear");
                    double height = document.getDouble("height");
                    double weight = document.getDouble("weight");
                    Date date = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int year = calendar.get(Calendar.YEAR);
                    int age = (int) (year - birthYear);

                    Log.d(TAG, "gender: " + gender + " birthYear: " + birthYear + " height: " + height + " weight: " + weight);
                    if (gender.toUpperCase().equals("M")) {
                        bmr = 10 * weight + 6.25 * height - 5.0 * age + 5.0;;
                    } else {
                        bmr = 10 * weight + 6.25 * height - 5.0 * age - 161.0;
                    }
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                bmr = 0.0;
            }
        });
    }

    public void update(Location location) {
        //distance update
        pair.updateLocation(location);
        distance += pair.getDistanceBetweenPoints() / (double) 1000;
        distanceText.setText(String.format("%.2f", distance));

        //pace & calorie update
        if (distance <= 0) {
            paceText.setText("0:00");
        } else {
            int timeInSeconds = chronometer.getTimeElapsedSeconds();
            int paceInSeconds = (int) (timeInSeconds / distance);
            int minutes = paceInSeconds / 60;
            int seconds = paceInSeconds % 60;
            String strSeconds = "";
            if (seconds < 10) {
                strSeconds = strSeconds + "0" + seconds;
            } else {
                strSeconds += seconds;
            }
            String pace = minutes + ":" + strSeconds;
            paceText.setText(pace);

            //calorie
            double timeInHrs = timeInSeconds/(double)3600;
            double speed = distance/timeInHrs;
            //double calorieUsed = calculateCalorieBurnt(speed, timeInHrs);
            calorieBurnt = calculateCalorieBurnt(speed, timeInHrs);
            calorieText.setText(String.format("%.2f", calorieBurnt));
        }
    }

    public double getDistance() {
        return distance;
    }

    public double getCalorieBurnt() {
        return calorieBurnt;
    }

    public double calculateCalorieBurnt(double speed, double timeInHrs) {
        double mets = 0.798238 * speed + 1.78973;
        double usedCalorie = ((bmr * mets) / 24) * timeInHrs;
        Log.d("BMR", ""+ bmr);
        return usedCalorie;
    }
}
