package com.example.wavezcellular.activities;

import static com.example.wavezcellular.utils.User.getGuest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.wavezcellular.R;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private ImageView home_IMG_profile;
    //private MaterialButton home_BTN_show;
    private MaterialButton show_BTN_report;
    private MaterialButton[] home_BTN_searches;
    private MaterialButton[] home_BTN_results;
    private Spinner home_SP_listOfBeaches;
    private ArrayAdapter<CharSequence> adapter;
    private FusedLocationProviderClient fusedLocationProviderClient;

    //firebase
    private FirebaseUser firebaseUserUser;
    private DatabaseReference myRef;

    //map

    private String beachName;
    private Bundle bundle = null;
    private final int MAX_SEARCH = 5;

    private boolean hasPremission;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        bundle = getIntent().getExtras();
        if (bundle == null){
            bundle = new Bundle();
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    bundle.putDouble("x", location.getLatitude());
                    bundle.putDouble("y", location.getLongitude());

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        setContentView(R.layout.activity_home_upgrade);
        findViews();
        firebaseUserUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUserUser == null){
            getGuest(bundle);
        }
        myRef = FirebaseDatabase.getInstance().getReference("Beaches");
        createSpinner();
        createListener();


    }

    private void createBeaches(ArrayList<Map.Entry<String,Double>> list) {
        for (int i = 0; i< MAX_SEARCH; i++){
            String format = String.format("%.01f", list.get(i).getValue());
            String result =   format ;
            String beachName = list.get(i).getKey().toString();
            home_BTN_searches[i].setText(beachName);
            home_BTN_results[i].setText(result);
            home_BTN_searches[i].setVisibility(View.VISIBLE);
        }
    }

    private void createBeaches(String parameter, ArrayList<String> list) {
        for (int i = 0; i< MAX_SEARCH; i++){
            String beachName = list.get(i).toString();
            home_BTN_searches[i].setText(beachName);
            home_BTN_searches[i].setVisibility(View.VISIBLE);
        }
    }

    private void getBeaches(String value) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                HashMap<String,Double> beachesSort = new HashMap<>();
                HashMap<String, HashMap<String,Object>> beaches = (HashMap) dataSnapshot.getValue(Object.class);
                if(value.equalsIgnoreCase("distance")) {
                    double userLat = (double) bundle.get("x");
                    double userLon = (double) bundle.get("y");
                    LatLng user = new LatLng(userLat,userLon);
                    for (Map.Entry<String, HashMap<String, Object>> set :
                            beaches.entrySet()) {
                        String beachName = (String) set.getValue().get("name");
                        LatLng loc = new LatLng((Double) set.getValue().get("latitude"), (Double)set.getValue().get("longitude"));
                        Double val = getDistance(user,loc);
                        beachesSort.put(beachName, val);

                    }
                }else if(value.equalsIgnoreCase("name")){
                    ArrayList<String> list = new ArrayList<>();
                    for (Map.Entry<String, HashMap<String, Object>> set :
                            beaches.entrySet()) {
                        String beachName = (String) set.getValue().get("name");
                        list.add(beachName);
                    }
                    Collections.sort(list);
                    createBeaches(value,list);
                }
                else{
                    for (Map.Entry<String, HashMap<String, Object>> set :
                            beaches.entrySet()) {
                        String beachName = (String) set.getValue().get("name");
                        Long l = (Long) set.getValue().get(value);
                        double val = (double)l;
                        beachesSort.put(beachName, val);
                    }
                }
                if(!value.equalsIgnoreCase("name")) {
                    ArrayList<Map.Entry<String, Double>> list = new ArrayList<>(beachesSort.entrySet());

                    Comparator<Map.Entry<String, Double>> valueComparator = new Comparator<Map.Entry<String, Double>>() {
                        @Override
                        public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                            return o1.getValue().compareTo(o2.getValue());
                        }

                    };
                    Collections.sort(list, valueComparator);
                    createBeaches(list);
                }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void createListener(){
        home_IMG_profile.setOnClickListener(view -> replaceActivity("Profile"));

        for (int i =0; i<MAX_SEARCH;i++){
            int pressed = i;
            home_BTN_searches[i].setOnClickListener(view -> clickedBeach(pressed));
        }

//        home_BTN_report.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                replaceActivity("Report");
//            }
//        });

//        home_BTN_show.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                replaceActivity("Show");
//            }
//        });
    }

    private void clickedBeach(int i) {
        beachName = (String) home_BTN_searches[i].getText();
        replaceActivity(beachName);
    }


    private void replaceActivity(String mode) {
        Intent intent;
        if(mode.equals("Profile")){
            intent = new Intent(this, UserActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }else if(mode.equals("Report")){
            intent = new Intent(this, ReportActivity.class);
            bundle.putString("BEACH_NAME",beachName);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }else if(mode.contains("beach")){
            intent = new Intent(this, ShowActivity.class);
            bundle.putString("BEACH_NAME",beachName);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }else{
            intent = new Intent(this, ShowActivity.class);
            bundle.putString("BEACH_NAME",beachName);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }

    }

    private void createSpinner() {
        adapter = ArrayAdapter.createFromResource(this,R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        home_SP_listOfBeaches.setAdapter(adapter);
        home_SP_listOfBeaches.setOnItemSelectedListener(this);


    }


    private void findViews() {
        home_IMG_profile = findViewById(R.id.home_IMG_profile);
//        home_BTN_show = findViewById(R.id.home_BTN_show);
        show_BTN_report = findViewById(R.id.show_BTN_reports);
        home_SP_listOfBeaches = findViewById(R.id.home_SP_listOfBeaches);
        home_BTN_searches = new MaterialButton[]{
                findViewById(R.id.home_BTN_searchBeach1),
                findViewById(R.id.home_BTN_searchBeach2),
                findViewById(R.id.home_BTN_searchBeach3),
                findViewById(R.id.home_BTN_searchBeach4),
                findViewById(R.id.home_BTN_searchBeach5)

        };

        home_BTN_results = new MaterialButton[]{
                findViewById(R.id.home_BTN_result1),
                findViewById(R.id.home_BTN_result2),
                findViewById(R.id.home_BTN_result3),
                findViewById(R.id.home_BTN_result4),
                findViewById(R.id.home_BTN_result5)

        };

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String parameter = adapterView.getItemAtPosition(position).toString();

        int i = parameter.indexOf(' ');
        String word = parameter.substring(0, i);
        getBeaches(word);
    }





    public void setItemsWhenEmpty() {
        adapter = ArrayAdapter.createFromResource(this,R.array.beaches, android.R.layout.simple_spinner_item);
        for (int i=0;i<adapter.getCount();i++) {
            String location = adapter.getItem(i).toString();
            beachName = location;
            Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
            try {
                List<Address> listAddress = geocoder.getFromLocationName(location, 1);
                if (listAddress.size() > 0) {
                    double latit = listAddress.get(0).getLatitude();
                    double logi = listAddress.get(0).getLongitude();
                    myRef.child(location).child("latitude").setValue(latit);
                    myRef.child(location).child("longitude").setValue(logi);
                    myRef.child(location).child("name").setValue(beachName);
                    myRef.child(location).child("review").setValue(3.0);
                    myRef.child(location).child("warmth").setValue(3.0);
                    myRef.child(location).child("danger").setValue(3.0);
                    myRef.child(location).child("wind").setValue(3.0);
                    myRef.child(location).child("jellyfish").setValue(3.0);
                    myRef.child(location).child("density").setValue(3.0);
                    myRef.child(location).child("dog").setValue(3.0);
                    myRef.child(location).child("accessible").setValue(3.0);
                    myRef.child(location).child("hygiene").setValue(3.0);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
