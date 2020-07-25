package com.testapp.fitspirational.Models;

import com.google.firebase.firestore.Blob;

import java.util.LinkedList;
import java.util.Map;

public class User {
    public String username;
    public String email;
    public String password;
    public String gender;
    public int birthYear;
    public int height;
    public int weight;
    public int activityLevel;

    public User() {
    }

    public User(String username, String email, String password, String gender, int birthYear, int height, int weight, int activityLevel) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.birthYear = birthYear;
        this.height = height;
        this.weight = weight;
        this.activityLevel = activityLevel;
    }
}
