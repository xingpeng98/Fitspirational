package com.testapp.fitspirational.NavigationBarActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.testapp.fitspirational.R;

public class Credits extends AppCompatActivity {

    private ImageView backImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        backImg = findViewById(R.id.credits_backImage);
        backImg.setOnClickListener(v -> onBackPressed());
    }
}