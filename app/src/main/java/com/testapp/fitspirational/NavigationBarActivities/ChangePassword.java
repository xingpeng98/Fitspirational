package com.testapp.fitspirational.NavigationBarActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.testapp.fitspirational.R;

public class ChangePassword extends AppCompatActivity {

    private static final String TAG = "ChangePassword";
    private EditText oldPw, newPw, cfmNewPw;
    private String currentPw;
    private Button cfmBtn;

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPw = findViewById(R.id.change_password_oldPasswordText);
        newPw = findViewById(R.id.change_password_newPasswordText);
        cfmNewPw = findViewById(R.id.change_password_confirmNewPasswordText);
        cfmBtn = findViewById(R.id.change_password_confirmBtn);
        currentPw = getIntent().getStringExtra("password");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        cfmBtn.setOnClickListener(v -> {
            String oldPwStr, newPwStr, cfmNewPwStr;
            oldPwStr = oldPw.getText().toString();
            newPwStr = newPw.getText().toString();
            cfmNewPwStr = cfmNewPw.getText().toString();

            if (TextUtils.isEmpty(oldPwStr)) {
                oldPw.setError("Password is required");
                return;
            } else if (!oldPwStr.equals(currentPw)) {
                oldPw.setError("Given Password not same as current password");
                return;
            }
            if (TextUtils.isEmpty(newPwStr)) {
                newPw.setError("New Password is required");
                return;
            }
            if (newPwStr.length() < 6) {
                newPw.setError("Password must be more than 6 characters long");
                return;
            }
            if (TextUtils.isEmpty(cfmNewPwStr)) {
                cfmNewPw.setError("Please confirm your password by re-entering it here");
                return;
            }
            if (!newPwStr.equals(cfmNewPwStr)) {
                newPw.setError("Password entered is not the same");
                cfmNewPw.setError("Password entered is not the same");
                return;
            }

            firebaseUser.updatePassword(newPwStr);
            updateDataBase(newPwStr);

            SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("remember", "true");
            editor.apply();
        });

    }

    public void updateDataBase(String password) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        String userId = firebaseUser.getUid();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        firebaseFirestore.runTransaction((Transaction.Function<Void>) transaction -> {
            transaction.update(documentReference, "password", password);
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "Transaction success!");
            Toast.makeText(getApplicationContext(),"Successfully changed password", Toast.LENGTH_LONG).show();
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Transaction failure.", e);
            Toast.makeText(getApplicationContext(),"Failed to Update: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}