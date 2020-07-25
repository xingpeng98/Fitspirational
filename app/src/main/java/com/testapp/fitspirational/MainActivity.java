package com.testapp.fitspirational;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.testapp.fitspirational.FeatureActivities.Breathe.Breathe;
import com.testapp.fitspirational.FeatureActivities.CalorieTracker.CalorieMainPage;
import com.testapp.fitspirational.FeatureActivities.Diet.Diet;
import com.testapp.fitspirational.FeatureActivities.Fitness.GeneralFitness;
import com.testapp.fitspirational.FeatureActivities.RunTracker.RunHistory;
import com.testapp.fitspirational.FeatureActivities.RunTracker.RunMainPage;
import com.testapp.fitspirational.Executors.FitnessContentExecutor;
import com.testapp.fitspirational.FeatureActivities.RunTracker.RunTracker2;
import com.testapp.fitspirational.HelperClasses.UpdateData;
import com.testapp.fitspirational.NavigationBarActivities.Credits;
import com.testapp.fitspirational.NavigationBarActivities.Settings;
import com.testapp.fitspirational.NavigationBarActivities.UserProfile;
import com.testapp.fitspirational.StartActivities.Login;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth fAuth;
    FirebaseFirestore firebaseFirestore;

    //Side bar related
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    //Run Tracker
    CardView runTracker;

    //Search Bar
    private String[] activitiesArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        UpdateData.update(fAuth, firebaseFirestore);

        /*---------------------- SideBar Content -------------------------*/
        //Set variables
        drawerLayout = findViewById(R.id.dash_drawerLayout);
        navigationView = findViewById(R.id.dash_navView);
        toolbar = findViewById(R.id.dash_toolBar);

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

        /*-------------- Search Bar------------*/
        AutoCompleteTextView searchBarText = findViewById(R.id.dash_searchText);
        activitiesArray = getResources().getStringArray(R.array.appActivities);
        ArrayAdapter<String> searchItemAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                activitiesArray);
        searchBarText.setAdapter(searchItemAdapter);
        searchBarText.setOnItemClickListener((parent, view, position, id) -> {
            String selectedString = (String) parent.getItemAtPosition(position);
            int selectedId = getStringPosition(selectedString);
            switch (selectedId) {
                case -1:
                    break;
                case 0: //Breathe
                    startActivity(new Intent(getApplicationContext(), Breathe.class));
                    break;
                case 1: //Calorie Tracker
                    startActivity(new Intent(getApplicationContext(), CalorieMainPage.class));
                    break;
                case 2: //Credits
                    startActivity(new Intent(getApplicationContext(), Credits.class));
                    break;
                case 3: //Diet
                    startActivity(new Intent(getApplicationContext(), Diet.class));
                    break;
                case 4: //GPS Tracker
                    startActivity(new Intent(getApplicationContext(), RunTracker2.class));
                    break;
                case 5: //Run Tracker Main Page
                    startActivity(new Intent(getApplicationContext(), RunMainPage.class));
                    break;
                case 6: //Run History
                    startActivity(new Intent(getApplicationContext(), RunHistory.class));
                    break;
                case 7: //Settings
                    startActivity(new Intent(getApplicationContext(), Settings.class));
                    break;
                case 8: //User Profile
                    startActivity(new Intent(getApplicationContext(), UserProfile.class));
                    break;
            }
        });


        /*-------------- Run Tracker Card------------*/
        runTracker = findViewById(R.id.dash_runTrackerCard);
        runTracker.setOnClickListener((v) -> {
            startActivity(new Intent(getApplicationContext(), RunMainPage.class));
        });

        /*---------------------- Breathe Card----------------------*/
        CardView calorieTracker = findViewById(R.id.dash_calorieTrackerCard);
        calorieTracker.setOnClickListener((v) -> {
            startActivity(new Intent(getApplicationContext(), CalorieMainPage.class));
        });

        /*---------------------- Breathe Card----------------------*/
        CardView breathe = findViewById(R.id.dash_breatheCard);
        breathe.setOnClickListener((v) -> {
            startActivity(new Intent(getApplicationContext(), Breathe.class));
        });

        /*---------------------- Breathe Card----------------------*/
        CardView diet = findViewById(R.id.dash_dietCard);
        diet.setOnClickListener((v) -> {
            startActivity(new Intent(getApplicationContext(), Diet.class));
        });

        /*---------------------- Recycler View Content ----------------------*/
        TextView fitnessViewAll = findViewById(R.id.dash_fitnessExpandText);
        fitnessViewAll.setOnClickListener((v) -> {
            startActivity(new Intent(getApplicationContext(), GeneralFitness.class));
        });

        ProgressBar progressBar = findViewById(R.id.dash_fitnessProgressBar);
        RecyclerView recyclerView = findViewById(R.id.dash_fitnessRecycler);
        FitnessContentExecutor fitnessContentExecutor =
                new FitnessContentExecutor(this, progressBar,
                        recyclerView, R.layout.fitness_card_design,
                        R.id.dash_fitnessCardImage, R.id.dash_fitnessCardText);

        fitnessContentExecutor.run();
    }

    private int getStringPosition(String selectedString) {
        List<String> activitiesList = Arrays.asList(activitiesArray);
        return activitiesList.indexOf(selectedString);
    }

    @Override
    public void onBackPressed() {
        //drawer close if back pressed
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mainMenu_Home:
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
                fAuth.signOut();
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

    @Override
    protected void onResume() {
        super.onResume();
        UpdateData.update(fAuth, firebaseFirestore);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        UpdateData.update(fAuth, firebaseFirestore);
    }
}
