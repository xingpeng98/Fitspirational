package com.testapp.fitspirational.StartActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.testapp.fitspirational.HelperClasses.CurrentDate;
import com.testapp.fitspirational.MainActivity;
import com.testapp.fitspirational.Models.RunCalorie;
import com.testapp.fitspirational.Models.User;
import com.testapp.fitspirational.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class UserInfo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "UserInfo";
    private EditText heightText, weightText, birthYearText;
    private Spinner activeLevelSpinner;
    private RadioGroup radioGroup;
    private RadioButton lastRadioBtn;
    private Button registerBtn;
    private ProgressBar progressBar;

    //values
    private boolean checked = false;
    private int height, weight, birthYear, activityLevel;
    private String gender, email, password, username;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        heightText = findViewById(R.id.user_info_height);
        weightText = findViewById(R.id.user_info_weight);
        birthYearText = findViewById(R.id.user_info_birthYear);
        activeLevelSpinner = findViewById(R.id.user_info_activityLevel);
        radioGroup = findViewById(R.id.user_info_radioGrp);
        lastRadioBtn = findViewById(R.id.user_info_femaleRb);
        registerBtn = findViewById(R.id.user_info_registerBtn);
        progressBar = findViewById(R.id.user_info_progressBar);
        heightText.setTransformationMethod(null);
        weightText.setTransformationMethod(null);
        birthYearText.setTransformationMethod(null);

        activeLevelSpinner.setOnItemSelectedListener(this);
        progressBar.setVisibility(View.INVISIBLE);

        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        username = getIntent().getStringExtra("username");

        registerBtn.setOnClickListener((v) -> {

            String heightStr = heightText.getText().toString().trim();
            String weightStr = weightText.getText().toString().trim();
            String birthYearStr = birthYearText.getText().toString().trim();

            //Gender check
            if (!checked) {
                lastRadioBtn.setError("Please Select One");
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
            //Activity Level check
            if (activityLevel < 0) {
                TextView errorText = (TextView)activeLevelSpinner.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);
                errorText.setText("Please select one");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = fAuth.getCurrentUser();

                    user.sendEmailVerification().addOnSuccessListener(aVoid -> Toast.makeText(UserInfo.this, "Verification email has been sent", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "onFailure: Email not sent" + e.getMessage());
                    });

                    //sends user data to database
                    userId = fAuth.getCurrentUser().getUid();
                    DocumentReference docRef = fStore.collection("users").document(userId);
                    User user1 = new User(username, email, password, gender, birthYear, height, weight, activityLevel);
                    docRef.set(user1).addOnFailureListener(e -> Log.d(TAG, "Data Creation Failed: " + e.getMessage()));

                    //Set runCalorie
                    DocumentReference docRef2 = fStore.collection("RunCalorie").document(userId);
                    docRef2.set(new RunCalorie(CurrentDate.getCurrentDate(), "0", "0", "0" ));

                    //Set runHistory
                    Map<String, Object> hashMap = new HashMap<>();
                    hashMap.put("history", new LinkedList<>());
                    DocumentReference docRef3 = fStore.collection("RunHistory").document(userId);
                    docRef3.set(hashMap);

                    //Set calorieHistory
                    DocumentReference docRef4 = fStore.collection("CalorieHistory").document(userId);
                    docRef4.set(hashMap);

                    Toast.makeText(UserInfo.this, "User created", Toast.LENGTH_SHORT).show();

                    //starts the next activity
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(
                            UserInfo.this,
                            "Error" + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    public void checkButton(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(radioId);
        if (((String) radioButton.getText()).equals("Male")) {
            gender = "M";
        } else {
            gender = "F";
        }
        checked = true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        activityLevel = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        activityLevel = -1;
    }
}