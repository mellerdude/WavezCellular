package com.example.wavezcellular.activities;

import static com.example.wavezcellular.utils.User.getGuest;

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

import com.example.wavezcellular.R;
import com.example.wavezcellular.utils.Beach;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MenuActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private MaterialButton menu_BTN_beachFound;
    private MaterialButton menu_BTN_beachdetails;
    private MaterialButton menu_BTN_searchBeach;
    private MaterialButton menu_BTN_signIn;
    private ArrayAdapter<CharSequence> adapter;
    private TextView menu_TXT_Distance;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean hasPremission;
    public static String distance = "";
    private double longi = 0;
    private double lati = 0;
    private Bundle bundle = null;
    public static String maxBeach = "";
    private int open = 0;
    private Double x;
    private Double y;
    private String guest;
    private LocationManager locationManager;

    private FirebaseUser firebaseUserUser;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        setContentView(R.layout.activity_menu);
        findViews();
        setListeners();
        bundle = getIntent().getExtras();
        if (bundle == null) {
            bundle = new Bundle();
        }

        firebaseUserUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUserUser == null){
            getGuest(bundle);
        }
        myRef = FirebaseDatabase.getInstance().getReference("Beaches");


            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        lati = location.getLatitude();
                        longi = location.getLongitude();
                        findNearestBeach(lati, longi);
                        bundle.putDouble("x", lati);
                        bundle.putDouble("y", longi);

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    maxBeach = "Location Services not working";
                    distance = "" + 0 + "," + 0;
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
                double minDistance = 100000;

                double currDistance = 100000;
                HashMap<String,HashMap<String,Object>> beaches = (HashMap) dataSnapshot.getValue(Object.class);
                System.out.println("Stop");
                for (Map.Entry<String, HashMap<String,Object>> set :
                        beaches.entrySet()) {
                    double x,y;
                    String beachName = (String) set.getValue().get("name");
                    x = (double) set.getValue().get("latitude");
                    y = (double) set.getValue().get("longitude");
                    LatLng location = new LatLng(x,y);
                    currDistance = getDistance(user,location);
                    if(currDistance<minDistance) {
                        minDistance = currDistance;
                        maxBeach = beachName;
                    }

                }
                String format = String.format("%.01f", minDistance);
                distance = "Beach is " + format +"km from you";

                menu_TXT_Distance.setText(distance);
                menu_BTN_beachFound.setText(maxBeach);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void setListeners() {
        menu_BTN_beachdetails.setOnClickListener(view->{
            replaceActivityShow();
        });
        menu_BTN_searchBeach.setOnClickListener(view->{
            replaceActivitySearch();
        });
        menu_BTN_signIn.setOnClickListener(view->{
            replaceActivityWelcome();
        });
    }

    private void replaceActivitySearch() {
        Intent intent;
        intent = new Intent(this, HomeActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }


    private void replaceActivityShow()  {
        Intent intent;
        bundle.putString("BEACH_NAME",menu_BTN_beachFound.getText().toString());
        intent = new Intent(this, ShowActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void findViews() {
        menu_BTN_beachFound = findViewById(R.id.menu_BTN_beachFound);
        menu_BTN_beachdetails = findViewById(R.id.menu_BTN_beachdetails);
        menu_BTN_searchBeach = findViewById(R.id.menu_BTN_searchBeach);
        menu_BTN_signIn = findViewById(R.id.menu_BTN_signIn);
        menu_TXT_Distance = findViewById(R.id.menu_TXT_Distance);
    }

    private void replaceActivityWelcome() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkPermission() {
        int fineLocationStatus = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationStatus = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if ((fineLocationStatus != PackageManager.PERMISSION_GRANTED) &&
                (coarseLocationStatus != PackageManager.PERMISSION_GRANTED)) {
            hasPremission = true;
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    101);
        } else {
            hasPremission = true;
        }

    }

    private double getDistance(LatLng location1, LatLng location2){
        return (SphericalUtil.computeDistanceBetween(location1,location2)/1000);
    }


}
