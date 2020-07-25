package com.testapp.fitspirational.StartActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.testapp.fitspirational.MainActivity;
import com.testapp.fitspirational.R;

public class Login extends AppCompatActivity {

    EditText email, password;
    Button loginBtn;
    TextView registerText, forgotPWText;
    ProgressBar pBar;
    FirebaseAuth fAuth;
    CheckBox rememberUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_emailText);
        password = findViewById(R.id.login_passwordText);
        loginBtn = findViewById(R.id.login_loginBtn);
        registerText = findViewById(R.id.login_RegisterText);
        pBar = findViewById(R.id.login_progressBar);
        forgotPWText = findViewById(R.id.login_forgotPWText);
        rememberUser = findViewById(R.id.login_checkBox);
        fAuth = FirebaseAuth.getInstance();

        pBar.setVisibility(View.INVISIBLE);

        loginBtn.setOnClickListener((view) -> {
            String emailStr = email.getText().toString().trim();
            String passwordStr = password.getText().toString();

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

            pBar.setVisibility(View.VISIBLE);

            fAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener((task) -> {
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Logged in", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    pBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(
                            Login.this,
                            "Email or password may be incorrect",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        registerText.setOnClickListener((view) -> {
            startActivity(new Intent(getApplicationContext(), Register.class));
            finish();
        });

        forgotPWText.setOnClickListener((v) -> {
            final EditText resetMail = new EditText(v.getContext());
            AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle("Reset Password?");
            passwordResetDialog.setMessage("Enter your email to receive reset link.");
            passwordResetDialog.setView(resetMail);

            passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String mail = resetMail.getText().toString().trim();
                    fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Login.this, "Reset link sent to email", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login.this, "Error! Reset link is not sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            passwordResetDialog.create().show();
        });

        // Remember Me set preferences
        rememberUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                } else {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                }
            }
        });
    }
}
