package com.testapp.fitspirational.StartActivities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.testapp.fitspirational.R;

public class Register extends AppCompatActivity {

    private static final String TAG = "Register_Activity";
    EditText userName, email, password, cfmPassword;
    Button nextBtn;
    TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        userName = findViewById(R.id.register_userNameText);
        email = findViewById(R.id.register_emailText);
        password = findViewById(R.id.register_passwordText);
        cfmPassword = findViewById(R.id.register_confirmPasswordText);
        nextBtn = findViewById(R.id.register_nextBtn);
        loginText = findViewById(R.id.register_loginText);


        nextBtn.setOnClickListener(v -> {
            final String userNameStr = userName.getText().toString().trim();
            final String emailStr = email.getText().toString().trim();
            final String passwordStr = password.getText().toString();
            final String cfmPasswordStr = cfmPassword.getText().toString();

            if (TextUtils.isEmpty(userNameStr)) {
                userName.setError("Username is required");
                return;
            }
            if (TextUtils.isEmpty(emailStr)) {
                email.setError("Email is required");
                return;
            }
            if (TextUtils.isEmpty(passwordStr)) {
                password.setError("Password is required");
                return;
            }
            if (passwordStr.length() < 6) {
                password.setError("Password must be more than 6 characters long");
                return;
            }
            if (TextUtils.isEmpty(cfmPasswordStr)) {
                cfmPassword.setError("Please confirm your password by re-entering it here");
                return;
            }
            if (!passwordStr.equals(cfmPasswordStr)) {
                password.setError("Password entered is not the same");
                cfmPassword.setError("Password entered is not the same");
                return;
            }

            startActivity(new Intent(getApplicationContext(), UserInfo.class)
                    .putExtra("email", emailStr)
                    .putExtra("password", passwordStr)
                    .putExtra("username", userNameStr));
        });



        loginText.setOnClickListener((view) -> {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });
    }
}
