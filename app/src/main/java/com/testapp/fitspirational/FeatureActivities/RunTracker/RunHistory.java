package com.testapp.fitspirational.FeatureActivities.RunTracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.testapp.fitspirational.R;

import java.util.ArrayList;
import java.util.Map;

public class RunHistory extends AppCompatActivity {

    private static final String TAG = "RunHistory";
    private GridView gridView;
    private TextView textView;
    private ArrayList<Map<String, Object>> runHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_history);

        gridView = findViewById(R.id.run_history_gridView);
        textView = findViewById(R.id.run_history_no_history_text);

        //Retrieve data
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        String uId = FirebaseAuth.getInstance().getUid();
        DocumentReference documentReference = fStore.collection("RunHistory").document(uId);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    runHistory = (ArrayList<Map<String, Object>>) document.get("history");
                    if (runHistory.size() <= 0) {
                        textView.setText("No Run History. Start Running Now!");
                    } else {
                        textView.setVisibility(View.INVISIBLE);
                        RunHistoryAdapter runHistoryAdapter = new RunHistoryAdapter(runHistory, this);
                        gridView.setAdapter(runHistoryAdapter);

                        gridView.setOnItemClickListener((parent, view, position, id) -> {
                            Map<String, Object> runItem = runHistory.get(position);
                            byte[] byteArray = ((Blob) runItem.get("blob")).toBytes();
                            startActivity(new Intent(getApplicationContext(), RunImageDisplay.class).putExtra("image", byteArray));
                        });

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

    private class RunHistoryAdapter extends BaseAdapter {

        private ArrayList<Map<String, Object>> runHistory;
        private Context context;
        private LayoutInflater layoutInflater;

        public RunHistoryAdapter(ArrayList<Map<String, Object>> runHistory, Context context) {
            this.runHistory = runHistory;
            this.context = context;
            this.layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return runHistory.size();
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
                convertView = layoutInflater.inflate(R.layout.run_history_item, parent, false);
            }

            TextView date = convertView.findViewById(R.id.run_history_textDate);
            ImageView runImage = convertView.findViewById(R.id.run_history_image);

            Map<String, Object> runItem = runHistory.get(position);
            byte[] byteArray = ((Blob) runItem.get("blob")).toBytes();
            String runDate = (String) runItem.get("date");
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            date.setText(runDate);
            runImage.setImageBitmap(bitmap);
            return convertView;
        }
    }
}