package com.testapp.fitspirational.FeatureActivities.CalorieTracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.testapp.fitspirational.HelperClasses.CurrentDate;
import com.testapp.fitspirational.MainActivity;
import com.testapp.fitspirational.NavigationBarActivities.Credits;
import com.testapp.fitspirational.NavigationBarActivities.Settings;
import com.testapp.fitspirational.NavigationBarActivities.UserProfile;
import com.testapp.fitspirational.R;
import com.testapp.fitspirational.StartActivities.Login;

public class CalorieMainPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String uId;

    //Side bar related
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private TextView baseIntakeTxt, currentIntakeTxt, caloriesBurntTxt, distanceTxt;
    private EditText updateCaloriesTxt;
    private Button updateCaloriesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_main_page);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        uId = firebaseAuth.getUid();

        /*---------------------- SideBar Content -------------------------*/
        //Set variables
        drawerLayout = findViewById(R.id.calorie_drawerLayout);
        navigationView = findViewById(R.id.calorie_navView);
        toolbar = findViewById(R.id.calorie_toolBar);

        //Tool bar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Navigation Drawer Menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_menu);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.mainMenu_Home);

        /*---------------------- Calorie Tracker Content -------------------------*/
        baseIntakeTxt = findViewById(R.id.calorie_main_baseIntake);
        currentIntakeTxt = findViewById(R.id.calorie_main_intakeToday);
        caloriesBurntTxt = findViewById(R.id.calorie_main_calorieBurnt);
        distanceTxt = findViewById(R.id.calorie_main_recommendedDist);
        updateCaloriesTxt = findViewById(R.id.calorie_main_updateText);
        updateCaloriesBtn = findViewById(R.id.calorie_main_updateBtn);
        updateCaloriesTxt.setTransformationMethod(null);

        loadDisplay();

        updateCaloriesBtn.setOnClickListener(v -> {
            String addCalorieStr = updateCaloriesTxt.getText().toString().trim();
            if (TextUtils.isEmpty(addCalorieStr)) {
                updateCaloriesTxt.setError("Please fill in");
            }

            int addCalorie = Integer.parseInt(addCalorieStr);
            updateDisplay(addCalorie);
        });

        /*---------------------- Cards -------------------------*/
        CardView calorieCalculator, calorieHistory;
        calorieCalculator = findViewById(R.id.calorie_main_calculatorCard);
        calorieHistory = findViewById(R.id.calorie_main_calorieHistoryCard);
        calorieHistory.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), CalorieHistory.class));
        });
        calorieCalculator.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), CalorieCalculator.class));
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainMenu_Home:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
            case R.id.mainMenu_Profile:
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                break;
            case R.id.mainMenu_Settings:
                startActivity(new Intent(getApplicationContext(), Settings.class));
                break;
            case R.id.mainMenu_Logout:
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();

                //signout
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
                break;
            case R.id.mainMenu_Credits:
                startActivity(new Intent(getApplicationContext(), Credits.class));
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadDisplay() {
        baseIntakeTxt.setVisibility(View.INVISIBLE);
        currentIntakeTxt.setVisibility(View.INVISIBLE);
        caloriesBurntTxt.setVisibility(View.INVISIBLE);
        distanceTxt.setVisibility(View.INVISIBLE);

        /*---------------------- Retrieve Data -------------------------*/
        DocumentReference docRef1 = firebaseFirestore.collection("users").document(uId);
        DocumentReference docRef2 = firebaseFirestore.collection("RunCalorie").document(uId);

        firebaseFirestore.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot1 = transaction.get(docRef1);
            DocumentSnapshot snapshot2 = transaction.get(docRef2);

            String gender = snapshot1.getString("gender");
            int birthYear = (int)(long)snapshot1.getLong("birthYear");
            int height = (int)(long)snapshot1.getLong("height");
            int weight = (int)(long)snapshot1.getLong("weight");
            int activityLvl = (int)(long)snapshot1.getLong("activityLevel");

            String calorieIntakeStr = snapshot2.getString("calorieIntake"); //use
            String calorieBurntStr = snapshot2.getString("calorieBurnt");
            double calorieBurnt = Double.parseDouble(calorieBurntStr);
            String formattedCalorieBurntStr = String.format("%.2f", calorieBurnt); //use
            String baseCalorieIntakeStr = getBaseCalorieIntake(gender, birthYear, height, weight, activityLvl); //use

            //set texts
            baseIntakeTxt.setText(baseCalorieIntakeStr);
            currentIntakeTxt.setText(calorieIntakeStr);
            caloriesBurntTxt.setText(formattedCalorieBurntStr);

            double calorieIntake = Double.parseDouble(calorieIntakeStr);
            double baseCalorieIntake = Double.parseDouble(baseCalorieIntakeStr);
            double extraCalorie = calorieIntake - baseCalorieIntake - calorieBurnt;
            if (extraCalorie > 0) {
                double distanceToRun = getRecommendedDistance(getBmr(gender, birthYear, height, weight), extraCalorie);
                String distanceToRunStr = String.format("%.2f", distanceToRun);
                distanceTxt.setText(distanceToRunStr);
            }

            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("UpdateData", "Transaction success!");

            //make visible
            baseIntakeTxt.setVisibility(View.VISIBLE);
            currentIntakeTxt.setVisibility(View.VISIBLE);
            caloriesBurntTxt.setVisibility(View.VISIBLE);
            distanceTxt.setVisibility(View.VISIBLE);
        }).addOnFailureListener(e -> {
            Log.w("UpdateData", "Transaction failure.", e);
            Toast.makeText(this, "Failed to load data. Please check your connection.", Toast.LENGTH_LONG).show();
        });
    }

    public void updateDisplay(int addCalorie) {
        currentIntakeTxt.setVisibility(View.INVISIBLE);
        distanceTxt.setVisibility(View.INVISIBLE);

        /*---------------------- Retrieve Data -------------------------*/
        DocumentReference docRef1 = firebaseFirestore.collection("users").document(uId);
        DocumentReference docRef2 = firebaseFirestore.collection("RunCalorie").document(uId);

        firebaseFirestore.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot1 = transaction.get(docRef1);
            DocumentSnapshot snapshot2 = transaction.get(docRef2);

            String gender = snapshot1.getString("gender");
            int birthYear = (int)(long)snapshot1.getLong("birthYear");
            int height = (int)(long)snapshot1.getLong("height");
            int weight = (int)(long)snapshot1.getLong("weight");
            int activityLvl = (int)(long)snapshot1.getLong("activityLevel");

            //update database and display calorieIntake
            String calorieIntakeStr = snapshot2.getString("calorieIntake"); //use
            int calorieIntake = Integer.parseInt(calorieIntakeStr) + addCalorie;
            calorieIntakeStr = calorieIntake + "";
            transaction.update(docRef2, "calorieIntake", calorieIntakeStr);
            currentIntakeTxt.setText(calorieIntake + "");

            String calorieBurntStr = snapshot2.getString("calorieBurnt");
            double calorieBurnt = Double.parseDouble(calorieBurntStr);
            String baseCalorieIntakeStr = getBaseCalorieIntake(gender, birthYear, height, weight, activityLvl); //use

            double baseCalorieIntake = Double.parseDouble(baseCalorieIntakeStr);
            double extraCalorie = calorieIntake - baseCalorieIntake - calorieBurnt;
            if (extraCalorie > 0) {
                double distanceToRun = getRecommendedDistance(getBmr(gender, birthYear, height, weight), extraCalorie);
                String distanceToRunStr = String.format("%.2f", distanceToRun);
                distanceTxt.setText(distanceToRunStr);
            }

            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("UpdateData", "Transaction success!");

            //make visible
            currentIntakeTxt.setVisibility(View.VISIBLE);
            distanceTxt.setVisibility(View.VISIBLE);
        }).addOnFailureListener(e -> {
            Log.w("UpdateData", "Transaction failure.", e);
            Toast.makeText(this, "Failed to load data. Please check your connection.", Toast.LENGTH_LONG).show();
        });
    }

    private String getBaseCalorieIntake(String gender, int birthYear, int height, int weight, int activityLvl) {
        double bmr = getBmr(gender, birthYear, height, weight);
        int baseIntake = (int) (bmr * (1.2 + 0.175 * activityLvl));
        return baseIntake + "";
    }

    private double getBmr(String gender, int birthYear, int height, int weight) {
        int age = CurrentDate.getCurrentYear() - birthYear;
        double bmr;
        if (gender.equals("M")) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }
        return bmr;
    }

    private double getRecommendedDistance(double bmr, double calories) {
        return (calories * 24 * 7.5) / (bmr * 7.776515);
    }
}