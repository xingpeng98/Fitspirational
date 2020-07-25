package com.testapp.fitspirational.NavigationBarActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import com.testapp.fitspirational.R;

public class Settings extends AppCompatActivity {

    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager().beginTransaction().replace(R.id.settings_fragment_container, new SettingsFragment()).commit();

        backBtn = findViewById(R.id.settings_backImage);
        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
        }

    }
}