package com.testapp.fitspirational.FeatureActivities.RunTracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.testapp.fitspirational.MainActivity;
import com.testapp.fitspirational.NavigationBarActivities.Credits;
import com.testapp.fitspirational.NavigationBarActivities.Settings;
import com.testapp.fitspirational.NavigationBarActivities.UserProfile;
import com.testapp.fitspirational.R;
import com.testapp.fitspirational.StartActivities.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class RunMainPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "RunMainPage";
    CardView startRun;
    CardView runHistory;
    TextView runDistText;

    FirebaseAuth fAuth;
    FirebaseFirestore firebaseFirestore;

    //Side bar related
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_main_page);

        fAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        runDistText = findViewById(R.id.run_main_distanceRan);

        updateDistanceText();

        /*---------------------- Start Run Card -------------------------*/
        startRun = findViewById(R.id.run_main_letsRunCard);
        startRun.setOnClickListener((v) -> {
            startActivity(new Intent(getApplicationContext(), RunTracker2.class));
        });

        /*---------------------- Run History Card -------------------------*/
        runHistory = findViewById(R.id.run_main_runHistoryCard);
        runHistory.setOnClickListener((v) -> {
            startActivity(new Intent(getApplicationContext(), RunHistory.class));
        });

        /*---------------------- SideBar Content -------------------------*/
        //Set variables
        drawerLayout = findViewById(R.id.run_drawerLayout);
        navigationView = findViewById(R.id.run_navView);
        toolbar = findViewById(R.id.run_toolBar);

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
    }

    public void updateDistanceText() {
        runDistText.setVisibility(View.INVISIBLE);
        final DocumentReference docRef = firebaseFirestore.collection("RunCalorie").document(fAuth.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String runDistStr = document.getString("runDist");
                    double runDist = Double.parseDouble(runDistStr);
                    runDistText.setText(String.format("%.2f KM", runDist));
                    runDistText.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDistanceText();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateDistanceText();
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
}
