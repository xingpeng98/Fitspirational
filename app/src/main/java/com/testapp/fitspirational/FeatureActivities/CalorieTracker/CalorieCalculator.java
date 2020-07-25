package com.testapp.fitspirational.FeatureActivities.CalorieTracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.testapp.fitspirational.HelperClasses.CurrentDate;
import com.testapp.fitspirational.R;

public class CalorieCalculator extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText heightText, weightText, ageText;
    private Spinner activeLevelSpinner;
    private RadioGroup radioGroup;
    private RadioButton lastRadioBtn;
    private CardView calculateBtn;
    private TextView baseIntakeText;

    //values
    private boolean checked = false;
    private int height, weight, age, activityLevel;
    private String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_calculator);

        baseIntakeText = findViewById(R.id.calorie_calculator_baseIntakeText);
        heightText = findViewById(R.id.calorie_calculator_heightText);
        weightText = findViewById(R.id.calorie_calculator_weightText);
        ageText = findViewById(R.id.calorie_calculator_ageText);
        activeLevelSpinner = findViewById(R.id.calorie_calculator_activityLevel);
        radioGroup = findViewById(R.id.calorie_calculator_radioGrp);
        lastRadioBtn = findViewById(R.id.calorie_calculator_femaleRb);
        calculateBtn = findViewById(R.id.calorie_calculator_calculateBtn);
        heightText.setTransformationMethod(null);
        weightText.setTransformationMethod(null);
        ageText.setTransformationMethod(null);

        activeLevelSpinner.setOnItemSelectedListener(this);


        calculateBtn.setOnClickListener(v -> {
            String heightStr = heightText.getText().toString().trim();
            String weightStr = weightText.getText().toString().trim();
            String ageStr = ageText.getText().toString().trim();

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
            //Age check
            if (TextUtils.isEmpty(ageStr)) {
                ageText.setError("Please fill up this section.");
                return;
            } else {
                age = Integer.parseInt(ageStr);
            }
            if (age <= 5 | age >= 120) {
                ageText.setError("Please enter a reasonable year of birth");
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

            baseIntakeText.setText(getBaseCalorieIntake(gender, age, height, weight, activityLevel));

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
    }

    private String getBaseCalorieIntake(String gender, int age, int height, int weight, int activityLvl) {
        double bmr = getBmr(gender, age, height, weight);
        int baseIntake = (int) (bmr * (1.2 + 0.175 * activityLvl));
        return baseIntake + " kcal";
    }

    private double getBmr(String gender, int age, int height, int weight) {
        double bmr;
        if (gender.equals("M")) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }
        return bmr;
    }
}