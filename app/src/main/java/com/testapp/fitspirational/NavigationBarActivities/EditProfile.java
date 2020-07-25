package com.testapp.fitspirational.NavigationBarActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.testapp.fitspirational.FeatureActivities.RunTracker.RunMainPage;
import com.testapp.fitspirational.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "Edit Profile";
    private EditText usernameText, heightText, weightText, birthYearText;
    private Spinner activeLevelSpinner;
    private Button confirmBtn;

    private FirebaseFirestore fStore;
    private String userId;
    private int height, weight, birthYear, activityLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        String sHeight = getIntent().getStringExtra("height");
        String sWeight = getIntent().getStringExtra("weight");
        String sUsername = getIntent().getStringExtra("username");
        String sBirthYear = getIntent().getStringExtra("birthYear");

        usernameText = findViewById(R.id.edit_profile_usernameText);
        heightText = findViewById(R.id.edit_profile_heightText);
        weightText = findViewById(R.id.edit_profile_weightText);
        birthYearText = findViewById(R.id.edit_profile_birthYearText);
        activeLevelSpinner = findViewById(R.id.edit_profile_activityLevel);
        confirmBtn = findViewById(R.id.edit_profile_confirmBtn);
        userId = FirebaseAuth.getInstance().getUid();
        fStore = FirebaseFirestore.getInstance();

        heightText.setTransformationMethod(null);
        weightText.setTransformationMethod(null);
        birthYearText.setTransformationMethod(null);

        usernameText.setText(sUsername);
        heightText.setText(sHeight);
        weightText.setText(sWeight);
        birthYearText.setText(sBirthYear);

        activeLevelSpinner.setOnItemSelectedListener(this);

        confirmBtn.setOnClickListener(v -> {
            String username = usernameText.getText().toString().trim();
            String heightStr = heightText.getText().toString().trim();
            String weightStr = weightText.getText().toString().trim();
            String birthYearStr = birthYearText.getText().toString().trim();

            //usernameCheck
            if (TextUtils.isEmpty(username)) {
                heightText.setError("Please fill up this section.");
                return;
            }
            //Height Check
            if (TextUtils.isEmpty(heightStr)) {
                heightText.setError("Please fill up this section.");
                return;
            } else {
                height = Integer.parseInt(heightText.getText().toString().trim());
            }
            if (height <= 50 | height >= 300) {
                heightText.setError("Please enter a reasonable height");
                return;
            }
            //Weight Check
            if (TextUtils.isEmpty(weightStr)) {
                weightText.setError("Please fill up this section.");
                return;
            } else {
                weight = Integer.parseInt(weightText.getText().toString().trim());
            }
            if (weight <= 20 | weight >= 400) {
                weightText.setError("Please enter a reasonable weight");
                return;
            }
            //Birth check
            if (TextUtils.isEmpty(birthYearStr)) {
                birthYearText.setError("Please fill up this section.");
                return;
            } else {
                birthYear = Integer.parseInt(birthYearText.getText().toString().trim());
            }
            if (birthYear <= 1920 | birthYear >= 2021) {
                birthYearText.setError("Please enter a reasonable year of birth");
                return;
            }

            updateInfo(username, height, weight, birthYear, activityLevel);
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        activityLevel = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void updateInfo(String username, int height, int weight, int birthYear, int activityLevel) {
        DocumentReference documentReference = fStore.collection("users").document(userId);
        fStore.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(documentReference);

            //updates
            transaction.update(documentReference, "username", username);
            transaction.update(documentReference, "height", height);
            transaction.update(documentReference, "weight", weight);
            transaction.update(documentReference, "birthYear", birthYear);
            transaction.update(documentReference, "activityLevel", activityLevel);

            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "Transaction success!");
            Toast.makeText(getApplicationContext(),"Successfully updated profile", Toast.LENGTH_LONG).show();
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Transaction failure.", e);
            Toast.makeText(getApplicationContext(),"Failed to Update: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}