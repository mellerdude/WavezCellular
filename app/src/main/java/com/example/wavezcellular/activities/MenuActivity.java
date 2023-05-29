package com.example.wavezcellular.activities;

import static com.example.wavezcellular.activities.ShowActivity.getDouble;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.wavezcellular.Interfaces.testActionsListener;
import com.example.wavezcellular.R;
import com.example.wavezcellular.utils.ActivityManager;
import com.example.wavezcellular.utils.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.HashMap;
import java.util.Map;


public class MenuActivity extends AppCompatActivity implements testActionsListener {
    private static final int MAXDISTANCE = 100000;

    private ActivityManager activityManager;
    public static String distance = "", closestBeach = "";
    private final double DEF_REVIEW_VAL = 3.0;
    private MaterialButton menu_BTN_beachFound, menu_BTN_beachdetails, menu_BTN_searchBeach, menu_BTN_signIn,menu_BTN_signUp;
    private TextView menu_TXT_Distance;
    private ArrayAdapter<CharSequence> adapter;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean hasPremission;
    private double userLon = 0, userLat = 0;
    private Bundle bundle = null;
    private FirebaseUser firebaseUser;
    private DatabaseReference myRef;
    private String userName;
    private String BeachName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        activityManager = new ActivityManager(this);
        checkPermission();
        findViews();
        setListeners();
        bundle = getIntent().getExtras();
        if (bundle == null) {
            bundle = new Bundle();
        }
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        myRef = FirebaseDatabase.getInstance().getReference("Beaches");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    userLat = location.getLatitude();
                    userLon = location.getLongitude();
                    findNearestBeach(userLat, userLon);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                closestBeach = "Location Services not working using default location";
                userLat = User.DEFAULTLAT;
                userLon = User.DEFAULTLON;
            }
        });
    }


    private void findNearestBeach(double lati, double longi) {
        LatLng user = new LatLng(lati, longi);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                double minDistance = MAXDISTANCE;
                double currDistance;
                HashMap<String, HashMap<String, HashMap<String, Object>>> beaches = (HashMap) dataSnapshot.getValue(Object.class);
                System.out.println("Stop");
                for (Map.Entry<String, HashMap<String, HashMap<String, Object>>> set : beaches.entrySet()) {
                    double x, y;
                    String beachName = (String) set.getValue().get("Data").get("name");

                    x = getDouble(set.getValue().get("Data").get("latitude"));
                    y = getDouble(set.getValue().get("Data").get("longitude"));

                    LatLng location = new LatLng(x, y);
                    currDistance = getDistance(user, location);
                    if (currDistance < minDistance) {
                        minDistance = currDistance;
                        closestBeach = beachName;
                    }

                }
                String format = String.format("%.01f", minDistance);
                distance = "Beach is " + format + "km from you";

                menu_TXT_Distance.setText(distance);
                menu_BTN_beachFound.setText(closestBeach);
                BeachName = closestBeach;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setListeners() {
        menu_BTN_beachdetails.setOnClickListener(view -> {
            replaceActivityShow();
        });
        menu_BTN_searchBeach.setOnClickListener(view -> {
            replaceActivitySearch();
        });
        menu_BTN_signIn.setOnClickListener(view -> {
            replaceActivityWelcome("login");
        });
        menu_BTN_signUp.setOnClickListener(view -> {
            replaceActivityWelcome("signup");
        });
    }

    private void replaceActivitySearch() {
        activityManager.startActivity(HomeActivity.class, bundle);
    }


    private void replaceActivityShow() {
        Intent intent;
        bundle.putString("BEACH_NAME", BeachName);
        intent = new Intent(this, ShowActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
    private void replaceActivityWelcome(String state) {
        Intent intent;
        bundle.putString("LOGIN_STATE", state);
        intent = new Intent(this, WelcomeActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void findViews() {
        menu_BTN_beachFound = findViewById(R.id.menu_BTN_beachFound);
        menu_BTN_beachdetails = findViewById(R.id.menu_BTN_beachdetails);
        menu_BTN_searchBeach = findViewById(R.id.menu_BTN_searchBeach);
        menu_BTN_signIn = findViewById(R.id.menu_BTN_signIn);
        menu_BTN_signUp = findViewById(R.id.menu_BTN_signUp);
        menu_TXT_Distance = findViewById(R.id.menu_TXT_Distance);
    }



    private void checkPermission() {
        int fineLocationStatus = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationStatus = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if ((fineLocationStatus != PackageManager.PERMISSION_GRANTED) && (coarseLocationStatus != PackageManager.PERMISSION_GRANTED)) {
            hasPremission = true;
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        } else {
            hasPremission = true;
        }

    }

    private double getDistance(LatLng location1, LatLng location2) {
        return (SphericalUtil.computeDistanceBetween(location1, location2) / 1000);
    }

    public void setItemsForDemo() {
        adapter = ArrayAdapter.createFromResource(this, R.array.beaches, android.R.layout.simple_spinner_item);
        for (int i = 0; i < adapter.getCount(); i++) {
            String location = adapter.getItem(i).toString();
            myRef.child(location).child("Data").child("latitude").setValue(0);
            myRef.child(location).child("Data").child("longitude").setValue(0);
            myRef.child(location).child("Data").child("name").setValue(location);
            myRef.child(location).child("Data").child("review").setValue(DEF_REVIEW_VAL);
            myRef.child(location).child("Data").child("warmth").setValue(DEF_REVIEW_VAL);
            myRef.child(location).child("Data").child("danger").setValue(DEF_REVIEW_VAL);
            myRef.child(location).child("Data").child("wind").setValue(DEF_REVIEW_VAL);
            myRef.child(location).child("Data").child("jellyfish").setValue(DEF_REVIEW_VAL);
            myRef.child(location).child("Data").child("density").setValue(DEF_REVIEW_VAL);
            myRef.child(location).child("Data").child("dog").setValue(DEF_REVIEW_VAL);
            myRef.child(location).child("Data").child("accessible").setValue(DEF_REVIEW_VAL);
            myRef.child(location).child("Data").child("hygiene").setValue(DEF_REVIEW_VAL);

        }
    }


    @Override
    public void testAction() {
        BeachName = "Bugrashov beach Tel Aviv";
        bundle.putString("BEACH_NAME", BeachName);
    }
}
