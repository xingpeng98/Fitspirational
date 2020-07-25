package com.testapp.fitspirational.HelperClasses;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateData {

    public static void update(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore) {

        String uId = firebaseAuth.getUid();

        DocumentReference docRef1 = firebaseFirestore.collection("RunCalorie").document(uId);
        DocumentReference docRef2 = firebaseFirestore.collection("CalorieHistory").document(uId);
        DocumentReference docRef3 = firebaseFirestore.collection("users").document(uId);

        firebaseFirestore.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot1 = transaction.get(docRef1);
            DocumentSnapshot snapshot2 = transaction.get(docRef2);
            DocumentSnapshot snapshot3 = transaction.get(docRef3);

            String dataDate = snapshot1.getString("date");
            String currDate = CurrentDate.getCurrentDate();
            if (!dataDate.equals(currDate)) {
                transaction.update(docRef1, "date", currDate);
                transaction.update(docRef1, "calorieBurnt", "0");
                transaction.update(docRef1, "calorieIntake", "0");
                transaction.update(docRef1, "runDist", "0");

                String gender = snapshot3.getString("gender");
                int birthYear = (int)(long)snapshot3.getLong("birthYear");
                int height = (int)(long)snapshot3.getLong("height");
                int weight = (int)(long)snapshot3.getLong("weight");
                int activityLvl = (int)(long)snapshot3.getLong("activityLevel");

                double calorieIntake = Double.parseDouble(snapshot1.getString("calorieIntake"));
                double calorieBurnt = Double.parseDouble(snapshot1.getString("calorieBurnt"));
                int calorieBaseIntake = getBaseCalorieIntake(gender, birthYear, height, weight, activityLvl);

                double calorieDiff = calorieIntake - calorieBaseIntake - calorieBurnt;
                String calorieDiffStr;
                if (calorieDiff > 0) {
                    calorieDiffStr = String.format("+ %.2f", calorieDiff);
                } else if (calorieDiff < 0){
                    calorieDiffStr = String.format("%.2f", calorieDiff);
                } else {
                    calorieDiffStr = String.format("%.2f", calorieDiff);
                }


                ArrayList<Map<String, Object>> calorieHistory = (ArrayList<Map<String, Object>>) snapshot2.get("history");
                Map<String, Object> calorie = new HashMap<>();
                calorie.put("date", dataDate);
                calorie.put("difference", calorieDiffStr);
                calorieHistory.add(0, calorie);
                transaction.update(docRef2, "history", calorieHistory);
            }

            // Success
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("UpdateData", "Transaction success!");
        }).addOnFailureListener(e -> {
            Log.w("UpdateData", "Transaction failure.", e);
        });
    }

    private static int getBaseCalorieIntake(String gender, int birthYear, int height, int weight, int activityLvl) {
        int age = CurrentDate.getCurrentYear() - birthYear;
        double bmr;
        if (gender.equals("M")) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }
        return (int) (bmr * (1.2 + 0.175 * activityLvl));
    }

}
