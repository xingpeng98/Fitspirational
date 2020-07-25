package com.testapp.fitspirational.Models;

public class RunCalorie {

    public String calorieBurnt;
    public String calorieIntake;
    public String date;
    public String runDist;

    public RunCalorie() {}

    public RunCalorie(String date, String calorieBurnt, String calorieIntake, String runDist) {
        this.calorieBurnt = calorieBurnt;
        this.calorieIntake = calorieIntake;
        this.date = date;
        this.runDist = runDist;
    }
}
