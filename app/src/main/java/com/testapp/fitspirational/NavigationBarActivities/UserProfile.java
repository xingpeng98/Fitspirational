package com.testapp.fitspirational.NavigationBarActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.testapp.fitspirational.FeatureActivities.RunTracker.RunHistory;
import com.testapp.fitspirational.FeatureActivities.RunTracker.RunImageDisplay;
import com.testapp.fitspirational.R;

import java.util.ArrayList;
import java.util.Map;

public class UserProfile extends AppCompatActivity {

    private static final String TAG = "UserProfile";
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private TextView emailText, usernameText, heightText, weightText, birthYearText, activityLevelText;
    private ProgressBar progressBar;
    private Button editBtn, changePwBtn;
    private View infoLayout, buttonLayout;
    private String[] userData = new String[5];
    private String[] activityLevelDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        emailText = findViewById(R.id.user_profile_emailText);
        usernameText = findViewById(R.id.user_profile_usernameText);
        heightText = findViewById(R.id.user_profile_heightText);
        weightText = findViewById(R.id.user_profile_weightText);
        birthYearText = findViewById(R.id.user_profile_birthYearText);
        activityLevelText = findViewById(R.id.user_profile_activityLevelText);
        progressBar = findViewById(R.id.user_profile_pBar);
        editBtn = findViewById(R.id.user_profile_updateProfileBtn);
        changePwBtn = findViewById(R.id.user_profile_changePasswordBtn);
        infoLayout = findViewById(R.id.user_profile_infoLayout);
        buttonLayout = findViewById(R.id.user_profile_buttonLayout);

        activityLevelDesc = getResources().getStringArray(R.array.activityLevels);

        retrieveData();

        //Buttons
        editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EditProfile.class);
            intent.putExtra("username", userData[0]);
            intent.putExtra("height", userData[1]);
            intent.putExtra("weight", userData[2]);
            intent.putExtra("birthYear", userData[3]);
            startActivity(intent);
        });

        changePwBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
            intent.putExtra("password", userData[4]);
            startActivity(intent);
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        retrieveData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveData();
    }

    public void retrieveData() {
        infoLayout.setVisibility(View.INVISIBLE);
        buttonLayout.setVisibility(View.INVISIBLE);
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getUid());
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    String email, username, password, height, weight, birthYear, activityLevel;
                    email = document.getString("email");
                    username = document.getString("username");
                    password = document.getString("password");
                    Long heightNum = document.getLong("height");
                    height = heightNum + "";
                    Long weightNum = document.getLong("weight");
                    weight = weightNum + "";
                    Long birthYearNum = document.getLong("birthYear");
                    birthYear = birthYearNum + "";
                    long activityNum = document.getLong("activityLevel");
                    activityLevel =  activityLevelDesc[(int) activityNum];
                    emailText.setText(email);
                    usernameText.setText(username);
                    heightText.setText(height + " cm");
                    weightText.setText(weight + " kg");
                    birthYearText.setText(birthYear);
                    activityLevelText.setText(activityLevel);

                    userData[0] = username;
                    userData[1] = height;
                    userData[2] = weight;
                    userData[3] = birthYear;
                    userData[4] = password;

                    infoLayout.setVisibility(View.VISIBLE);
                    buttonLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);


                    Log.d(TAG, "Retrieval of data successful!");
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                Toast.makeText(this, "Failed to get information. Please check your connection.", Toast.LENGTH_LONG);
            }
        });
    }
}