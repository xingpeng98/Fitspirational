package com.testapp.fitspirational.FeatureActivities.CalorieTracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.testapp.fitspirational.FeatureActivities.RunTracker.RunHistory;
import com.testapp.fitspirational.FeatureActivities.RunTracker.RunImageDisplay;
import com.testapp.fitspirational.R;

import java.util.ArrayList;
import java.util.Map;

public class CalorieHistory extends AppCompatActivity {

    private static final String TAG = "Calorie History";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ListView listView;
    private TextView textView;
    private ArrayList<Map<String, Object>> calorieHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_history);

        listView = findViewById(R.id.calorie_history_listView);
        textView = findViewById(R.id.calorie_history_history_text);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firebaseFirestore.collection("CalorieHistory").document(firebaseAuth.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    calorieHistory = (ArrayList<Map<String, Object>>) document.get("history");
                    if (calorieHistory.size() <= 0) {
                        textView.setText("No Calorie History.");
                    } else {
                        textView.setVisibility(View.GONE);
                        CalorieHistory.CalorieHistoryAdapter calorieHistoryAdapter = new CalorieHistory.CalorieHistoryAdapter(calorieHistory, this);
                        listView.setAdapter(calorieHistoryAdapter);
                    }
                    Log.d(TAG, "Retrieval of data successful!");
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                textView.setText("Failed to load data: " + task.getException().getMessage());
            }
        });
    }

    private class CalorieHistoryAdapter extends BaseAdapter {

        private ArrayList<Map<String, Object>> calorieHistory;
        private Context context;
        private LayoutInflater layoutInflater;

        public CalorieHistoryAdapter(ArrayList<Map<String, Object>> calorieHistory, Context context) {
            this.calorieHistory = calorieHistory;
            this.context = context;
            this.layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return calorieHistory.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.calorie_history_listitem, parent, false);
            }

            TextView dateText = convertView.findViewById(R.id.list_item_dateText_calorieHistory);
            TextView calorieDiffText = convertView.findViewById(R.id.list_item_calorieText_calorieHistory);

            Map<String, Object> calorieHistoryItem = calorieHistory.get(position);
            String date = (String) calorieHistoryItem.get("date");
            String difference = (String) calorieHistoryItem.get("difference") + " kcal";

            dateText.setText(date);
            calorieDiffText.setText(difference);

            Character c1 = difference.charAt(0);

            if (c1.equals('+')) {
                calorieDiffText.setTextColor(getResources().getColor(R.color.colorRed));
            } else {
                calorieDiffText.setTextColor(getResources().getColor(R.color.colorGreen));
            }

            return convertView;
        }
    }
}