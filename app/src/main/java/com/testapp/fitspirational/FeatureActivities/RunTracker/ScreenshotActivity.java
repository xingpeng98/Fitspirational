package com.testapp.fitspirational.FeatureActivities.RunTracker;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.mapbox.mapboxsdk.maps.Style;
import com.testapp.fitspirational.HelperClasses.Screenshot;
import com.testapp.fitspirational.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScreenshotActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "Update Run Data";
    private LatLngBounds latLngBounds;
    private List<LatLng> route = new ArrayList<>();
    private Bitmap bitmap;
    private String runDate;
    private double runDist, calorie;

    private GoogleMap map;
    private SupportMapFragment mapFragment;

    private FirebaseFirestore fStore;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshot);

        fStore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();

        Bundle bundle = getIntent().getExtras();
        byte[] byteArray = bundle.getByteArray("image");
        double northEastLat = bundle.getDouble("northEastLat", 0.0);
        double northEastLng = bundle.getDouble("northEastLng", 0.0);
        double southWestLat = bundle.getDouble("southWestLat", 0.0);
        double southWestLng = bundle.getDouble("southWestLng", 0.0);
        runDist = bundle.getDouble("runDist");
        runDate = bundle.getString("runDate");
        calorie = bundle.getDouble("calorie");

        //Debug
        Log.d("Test Activity", "northEastLat: " + northEastLat + " northEastLng: " + northEastLng + " southWestLat: " + southWestLat + " southWestLng: " + southWestLng);

        double[] latArray = bundle.getDoubleArray("latArray");
        double[] lngArray = bundle.getDoubleArray("lngArray");

        for (int i = 0; i < latArray.length; i++) {
            route.add(new LatLng(latArray[i], lngArray[i]));
        }

        latLngBounds = new LatLngBounds(new LatLng(southWestLat, southWestLng), new LatLng(northEastLat,northEastLng));


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView imageView = findViewById(R.id.test_display);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        int i = route.size() - 1;
        map = googleMap;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean mapDark = prefs.getBoolean("map_dark", false);
        if (mapDark) {
            map.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_json)));
        }

        map.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150));
        map.addPolyline(new PolylineOptions().addAll(route).width(12).color(Color.RED));
        map.addMarker(new MarkerOptions().position(route.get(i)).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.run_image_end_ic))).anchor((float) 0.5, (float) 0.5));

        final GoogleMap.SnapshotReadyCallback callback = snapshot -> {
            Bitmap image = snapshot;
            ImageView imageView = findViewById(R.id.map_image);
            imageView.setImageBitmap(image);

            ImageView finalImage = findViewById(R.id.final_image);
            View finalView = findViewById(R.id.screenshot_view);
            Bitmap finalImageBitmap = Screenshot.screenShot(finalView);
            finalImage.setImageBitmap(finalImageBitmap);

            //send ByteData to firestore
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            finalImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
            byte[] byteArray = bStream.toByteArray();
            Blob blob = Blob.fromBytes(byteArray);

            DocumentReference documentReference1 = fStore.collection("RunHistory").document(userId);
            DocumentReference documentReference2 = fStore.collection("RunCalorie").document(userId);

            fStore.runTransaction((Transaction.Function<Void>) transaction -> {
                DocumentSnapshot snapshot1 = transaction.get(documentReference1);
                DocumentSnapshot snapshot2 = transaction.get(documentReference2);

                // Note: this could be done without a transaction
                //       by updating the population using FieldValue.increment()
                @SuppressWarnings("Unchecked")
                ArrayList<Map<String, Object>> runHistory = (ArrayList<Map<String, Object>>) snapshot1.get("history");
                if (runHistory.size() > 5) {
                    runHistory.remove(5);
                }
                Map<String, Object> run = new HashMap<>();
                run.put("date", runDate);
                run.put("blob", blob);
                runHistory.add(0, run);

                String runDistStr = snapshot2.getString("runDist");
                double currentRunDist = Double.parseDouble(runDistStr);
                double totalRunDist = currentRunDist + runDist;
                String totalRunDistStr = totalRunDist + "";
                String calorieStr = snapshot2.getString("calorieBurnt");
                double currentCalorie = Double.parseDouble(calorieStr);
                double totalCalorie = currentCalorie + calorie;
                String totalCalorieStr = totalCalorie + "";

                transaction.update(documentReference1, "history", runHistory);
                transaction.update(documentReference2, "runDist", totalRunDistStr);
                transaction.update(documentReference2, "calorieBurnt", totalCalorieStr);

                // Success
                return null;
            }).addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Transaction success!");
                Toast.makeText(getApplicationContext(),"Image Saved to Run History", Toast.LENGTH_LONG).show();
                onBackPressed();
                finish();
            }).addOnFailureListener(e -> {
                Log.w(TAG, "Transaction failure.", e);
                Toast.makeText(getApplicationContext(),"Image failed to save: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "You may want to take a screenshot manually", Toast.LENGTH_LONG).show();
            });
        };

        map.setOnMapLoadedCallback(() -> map.snapshot(callback));
    }
}