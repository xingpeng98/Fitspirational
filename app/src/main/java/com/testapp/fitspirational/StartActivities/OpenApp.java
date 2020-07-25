package com.testapp.fitspirational.StartActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.testapp.fitspirational.MainActivity;
import com.testapp.fitspirational.R;

public class OpenApp extends AppCompatActivity {

    Animation topAnim, bottomAnim;
    ImageView appLogo;
    Button loginBtn, signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_app);

        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        appLogo = findViewById(R.id.appLogo);
        loginBtn = findViewById(R.id.openapp_loginBtn);
        signUpBtn = findViewById(R.id.openapp_signUpBtn);

        appLogo.setAnimation(topAnim);
        loginBtn.setAnimation(bottomAnim);
        signUpBtn.setAnimation(bottomAnim);

        loginBtn.setOnClickListener((v) -> {
            startActivity(new Intent(getApplicationContext(), Login.class));
        });

        signUpBtn.setOnClickListener((v) -> {
            startActivity(new Intent(getApplicationContext(), Register.class));
        });

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");

        if (checkbox.equals("true")) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else {
            //do nothing
        }

    }
}
