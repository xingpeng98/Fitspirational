package com.testapp.fitspirational.FeatureActivities.RunTracker.GPSTracker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.testapp.fitspirational.Executors.RunChronometerExecutor;
import com.testapp.fitspirational.FeatureActivities.RunTracker.MyLocationService;
import com.testapp.fitspirational.FeatureActivities.RunTracker.RunTracker2;
import com.testapp.fitspirational.FeatureActivities.RunTracker.ScreenshotActivity;
import com.testapp.fitspirational.HelperClasses.CurrentDate;
import com.testapp.fitspirational.HelperClasses.LocationStatsUpdater;
import com.testapp.fitspirational.HelperClasses.Screenshot;
import com.testapp.fitspirational.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GpsTracker extends AppCompatActivity implements OnMapReadyCallback, MyServiceCallback {

    private int LOCATION_REQUEST_CODE = 10001;
    private GoogleMap map;

    private MyLocService myService;
    private boolean bound = false;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;

    private Button startStopBtn;
    private boolean running = false;

    private ArrayList<LatLng> routeLatLng = new ArrayList<>();
    private LocationStatsUpdater locationStatsUpdater;
    private List<Double> latList = new ArrayList<>();
    private List<Double> lngList = new ArrayList<>();
    private double x0, x1, y0, y1;
    private boolean initialised = false;

    private String runDate;

    static GpsTracker instance;

    public static GpsTracker getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_tracker2);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map_run_tracker2);
        supportMapFragment.getMapAsync(this);

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String uId = firebaseAuth.getUid();

        instance = this;

        //Running stats
        TextView distanceText = findViewById(R.id.run_tracker_distance);
        TextView paceText = findViewById(R.id.run_tracker_pace);
        TextView calorieText = findViewById(R.id.run_tracker_calorie);
        Chronometer chronometer = findViewById(R.id.run_tracker_chronometer);
        RunChronometerExecutor runChronometerExecutor = new RunChronometerExecutor(chronometer);

        locationStatsUpdater = new LocationStatsUpdater(runChronometerExecutor, uId, fStore, distanceText, paceText, calorieText);
        locationStatsUpdater.calculateBmr();

        //StartStop
        startStopBtn = findViewById(R.id.run_tracker_startStopBtn);
        startStopBtn.setOnClickListener((v) -> {
            if (!running) {
                //Start run
                locationStatsUpdater.start();
                running = true;
                runDate = CurrentDate.getCurrentDatetime();
                startStopBtn.setText(R.string.EndRun);
            } else {
                //End location and run stats updates
                locationStatsUpdater.end();
                unBindService();

                //Move Camera
                LatLngBounds latLngBounds = new LatLngBounds(new LatLng(x0, y0), new LatLng(x1, y1));
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150));

                //End Run
                running = false;
                startStopBtn.setText(R.string.RunEnded);

                double dist = locationStatsUpdater.getDistance();
                double calorie = locationStatsUpdater.getCalorieBurnt();

                View view = findViewById(R.id.run_tracker_display);
                Bitmap bitmap = Screenshot.screenShot(view);
                passData(new LatLng(x1, y1), new LatLng(x0, y0), latList, lngList, bitmap, runDate, dist, calorie);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean mapDark = prefs.getBoolean("map_dark", false);
        if (mapDark) {
            map.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_json)));
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(1.28967, 103.85007), 10));
        //Check Permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Bind to service
            bindToService();
        } else {
            askLocationPermission();
        }
    }

    private void setUserLocationMarker(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(17)
                .bearing(location.getBearing())
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void drawPolyline(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        routeLatLng.add(latLng);
        if (routeLatLng.size() > 1) {
            PolylineOptions polylineOptions = new PolylineOptions().addAll(routeLatLng).color(Color.RED);
            map.addPolyline(polylineOptions);
        }
    }


    /* -----------  Permissions  ------------*/
    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission Granted
                //Bind service
                bindToService();
            } else {
                finish();
            }
        }

    }

    private void passData(LatLng northEast, LatLng southWest, List<Double> latList, List<Double> lngList, Bitmap bitmap, String runDate, double dist, double calorie) {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] byteArray = bStream.toByteArray();

        double[] latArray = new double[latList.size()];
        double[] lngArray = new double[lngList.size()];
        for (int i = 0; i < latList.size(); i++) {
            latArray[i] = latList.get(i);
            lngArray[i] = lngList.get(i);
        }


        Intent anotherIntent = new Intent(this, ScreenshotActivity.class);
        anotherIntent.putExtra("image", byteArray);
        anotherIntent.putExtra("northEastLat", northEast.latitude);
        anotherIntent.putExtra("northEastLng", northEast.longitude);
        anotherIntent.putExtra("southWestLat", southWest.latitude);
        anotherIntent.putExtra("southWestLng", southWest.longitude);
        anotherIntent.putExtra("latArray", latArray);
        anotherIntent.putExtra("lngArray", lngArray);
        anotherIntent.putExtra("runDate", runDate);
        anotherIntent.putExtra("runDist", dist);
        anotherIntent.putExtra("calorie", calorie);
        startActivity(anotherIntent);
        finish();
    }

    /** Callbacks for service binding, passed to bindService() */
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            Toast.makeText(getApplicationContext(), "Please wait to initialise user location", Toast.LENGTH_LONG).show();
            MyLocService.LocalBinder binder = (MyLocService.LocalBinder) service;
            myService = binder.getService();
            bound = true;
            myService.setCallbacks(GpsTracker.this); // register
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    @Override
    public void updateUserLoc(Location location) {
        if (map != null) {
            setUserLocationMarker(location);
            if (running) {
                drawPolyline(location);
                locationStatsUpdater.update(location);

                //Update Location List
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                latList.add(lat);
                lngList.add(lng);

                if (!initialised) {
                    x0 = x1 = lat;
                    y0 = y1 = lng;
                    initialised = true;
                } else {
                    if (lat > x1) x1 = lat;
                    if (lat < x0) x0 = lat;
                    if (lng > y1) y1 = lng;
                    if (lng < y0) y0 = lng;
                }

            }

        }
    }

    @SuppressLint("MissingPermission")
    public void bindToService() {
        map.setMyLocationEnabled(true);
        Intent intent = new Intent(this, MyLocService.class);
        ContextCompat.startForegroundService(this, intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unBindService() {
        if (bound) {
            myService.setCallbacks(null); // unregister
            unbindService(serviceConnection);
            bound = false;
        }
        Intent intent = new Intent(this, MyLocService.class);
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindService();
    }
}