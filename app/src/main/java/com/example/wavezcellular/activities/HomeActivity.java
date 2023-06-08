package com.example.wavezcellular.activities;

import static com.example.wavezcellular.activities.ShowActivity.getDouble;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wavezcellular.Interfaces.BeachListener;
import com.example.wavezcellular.R;
import com.example.wavezcellular.adapters_holders.BeachHomeAdapter;
import com.example.wavezcellular.adapters_holders.UserReportAdapter;
import com.example.wavezcellular.utils.ActivityManager;
import com.example.wavezcellular.utils.User;
import com.example.wavezcellular.utils.UserReport;
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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HomeActivity
 * Activity responsible for displaying a list of beaches based on preferences of the user
 * Based on the user requirements the activity can show the results of a category ascending/descending.
 * The activity allows the user to switch to a detailed report of a beach he chooses.
 * The user can also:
 *      1. go back to menuActivity - clicking back
 *      2. go to the userActivity - clicking on the profile pictures.
 */
public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, BeachListener {
    private final int DEF_VAL = 50;
    private final double DEF_REVIEW_VAL = 3.0;
    private ActivityManager activityManager;
    String value = "Distance";
    private ImageView home_IMG_profile;
    private MaterialButton home_BTN_switch;
    private MaterialButton home_BTN_name;
    private MaterialButton home_BTN_back;
    private MaterialButton[] home_BTN_searches;
    private MaterialButton[] home_BTN_results;
    private RecyclerView home_RecyclerView_beachData;
    private EditText home_EditTXT_byName;
    private Spinner home_SP_listOfBeaches;
    private ArrayAdapter<CharSequence> adapter;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean isGuest;
    private String userID;
    private String photo;
    //firebase
    private FirebaseUser firebaseUserUser;
    private DatabaseReference photoRef;

    private double userLat;
    private double userLon;
    //map
    private DatabaseReference myRef;
    private int orderBy = 1;
    private String beachName;
    private Bundle bundle = null;
    private boolean hasPremission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        activityManager = new ActivityManager(this);
        bundle = getIntent().getExtras();
        if (bundle == null) {
            bundle = new Bundle();
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    userLat = location.getLatitude();
                    userLon = location.getLongitude();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                userLat = User.DEFAULTLAT;
                userLon = User.DEFAULTLON;
            }
        });
        setContentView(R.layout.activity_home_upgrade);
        findViews();

        firebaseUserUser = FirebaseAuth.getInstance().getCurrentUser();
        isGuest = firebaseUserUser == null;
        myRef = FirebaseDatabase.getInstance().getReference("Beaches");
        //setItemsForDemo();
        createSpinner();
        createListener();
        showProfilePic();
    }

    //display the right profile picture for the user
    private void showProfilePic() {
        if(isGuest){
            home_IMG_profile.setImageResource(R.drawable.ic_user);
        }else{
            userID = firebaseUserUser.getUid();
            photoRef = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Photo");

            photoRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        photo = dataSnapshot.getValue(String.class);
                        // Use the retrieved photo URL as needed
                        int resourceId = getResources().getIdentifier(photo, "drawable", getPackageName());
                        home_IMG_profile.setImageResource(resourceId);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error condition if needed
                }
            });
        }
    }

    //create the list of beaches sorted by names
    private void createBeaches(ArrayList<Map.Entry<String, Double>> list) {
        if (orderBy == 0)
            Collections.reverse(list);
        ArrayList<Map.Entry<String, String>> beachesSort = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String format = String.format("%.01f", list.get(i).getValue());
            String result = format;
            String name = list.get(i).getKey();
            Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<>(name, result);
            beachesSort.add(entry);
        }
        createBeachRec(beachesSort);
    }


    //create the list of beaches sorted by parameter
    private void createBeaches(String parameter, ArrayList<String> list) {
        if (orderBy == 0)
            Collections.reverse(list);
        ArrayList<Map.Entry<String, String>> beachesSort = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String name = list.get(i);
            Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<>(name, "");
            beachesSort.add(entry);
        }
        createBeachRec(beachesSort);
    }

    //after the list is created put it in a recycleView to show the user
    private void createBeachRec(ArrayList<Map.Entry<String, String>> list) {
        home_RecyclerView_beachData.setLayoutManager(new LinearLayoutManager(this));
        home_RecyclerView_beachData.setAdapter(new BeachHomeAdapter(getApplicationContext(), list, this ));
    }

    //an interface function so when clicked on a beach switch to that beach
    @Override
    public void onBeachClicked(String entry) {
        Log.d("myTag", "Im here");
        replaceActivity(entry);
    }

    //For each beach, calculate the average value of the category given, and put it in that beach's data table.
    //Then get the values of each beach from the database Data table to get all the average ratings for each category.
    //and compare them to the others thus creating a list of beaches compared by category.
    private void getBeaches(String category) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Double> beachesSort = new HashMap<>();
                HashMap<String, HashMap<String, HashMap<String, Object>>> beaches = (HashMap) dataSnapshot.getValue(Object.class);
                if (category.equalsIgnoreCase("distance")) {
                    LatLng user = new LatLng(userLat, userLon);
                    for (Map.Entry<String, HashMap<String, HashMap<String, Object>>> set :
                            beaches.entrySet()) {
                        String beachName = (String) set.getValue().get("Data").get("name");
                        LatLng loc = new LatLng(getDouble(set.getValue().get("Data").get("latitude")), getDouble(set.getValue().get("Data").get("longitude")));
                        Double val = getDistance(user, loc);
                        beachesSort.put(beachName, val);
                    }
                } else if (category.equalsIgnoreCase("name")) {
                    ArrayList<String> nameList = new ArrayList<>();
                    ArrayList<String> sortnameList = new ArrayList<>();
                    for (Map.Entry<String, HashMap<String, HashMap<String, Object>>> set :
                            beaches.entrySet()) {
                        String beachName = (String) set.getValue().get("Data").get("name");
                        nameList.add(beachName);
                    }
                    if (home_EditTXT_byName.getText().length() > 0) {
                        sortnameList = (ArrayList<String>) findSimilarStrings(nameList, home_EditTXT_byName.getText().toString());
                        createBeaches(category, sortnameList);
                    } else {
                        Collections.sort(nameList);
                        createBeaches(category, nameList);
                    }
                } else {
                    //If value is not distance or name
                    for (Map.Entry<String, HashMap<String, HashMap<String, Object>>> set :
                            beaches.entrySet()) {
                        String beachName = (String) set.getValue().get("Data").get("name");
                        double val = DEF_REVIEW_VAL;
                        if (set.getValue().get("Reports") != null) {
                            val = calcAVG(set.getValue().get("Reports"), category);
                            myRef.child(beachName).child("Data").child(category).setValue(val);
                        }
                        beachesSort.put(beachName, val);
                    }
                }
                if (!category.equalsIgnoreCase("name")) {
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

    //calc the average value of this category from all reports in a single beach
    private double calcAVG(HashMap<String, Object> reports, String category) {
        double sum = 0;
        int numObjects = 0;
        for (Map.Entry<String, Object> set :
                reports.entrySet()) {
            HashMap<String, Object> entry = (HashMap<String, Object>) set.getValue();
            numObjects++;
            double val = getDouble(entry.get(category));
            sum += val;
        }
        return sum / numObjects;
    }

    private void createReportsRec(ArrayList<UserReport> list) {
        home_RecyclerView_beachData.setLayoutManager(new LinearLayoutManager(this));
        home_RecyclerView_beachData.setAdapter(new UserReportAdapter(getApplicationContext(), list));
    }

    private void createListener() {
        home_IMG_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isGuest){
                    replaceActivity("WELCOME");
                }else{
                    replaceActivity("PROFILE");
                }
            }
        });
        home_BTN_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceActivity("MENU");
            }
        });
        home_BTN_switch.setOnClickListener(view -> switchMode());
        home_BTN_name.setOnClickListener(view -> getBeaches("name"));
    }


    //Switch order of the list of beaches by category
    private void switchMode() {
        if (orderBy == 0)
            orderBy = 1;
        else
            orderBy = 0;
        if (home_BTN_switch.getText().toString().equalsIgnoreCase("Ascending"))
            home_BTN_switch.setText("Descending");
        else
            home_BTN_switch.setText("Ascending");
        getBeaches(value.toLowerCase());
    }

    private void replaceActivity(String mode) {
        String upper = mode.toUpperCase();
        if (upper.equals("PROFILE")) {
            activityManager.startActivity(UserActivityUpgrade.class,bundle);
        } else if (upper.equals("REPORT")) {
            bundle.putString("BEACH_NAME", beachName);
            activityManager.startActivity(ReportActivity.class,bundle);
        } else if (upper.contains("BEACH")) {
            bundle.putString("BEACH_NAME", mode);
            activityManager.startActivity(ShowActivity.class,bundle);
        } else if (upper.equals("MENU")) {
            activityManager.startActivity(MenuActivity.class,bundle);
        }else {
            bundle.putString("LOGIN_STATE", "login");
            activityManager.startActivity(WelcomeActivity.class,bundle);
        }
    }

    private void createSpinner() {
        adapter = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.spinner_list);
        adapter.setDropDownViewResource(R.layout.spinner_list);
        home_SP_listOfBeaches.setAdapter(adapter);
        home_SP_listOfBeaches.setOnItemSelectedListener(this);
    }

    private void findViews() {
        home_IMG_profile = findViewById(R.id.home_IMG_profile);
        home_BTN_switch = findViewById(R.id.home_BTN_switch);
        home_BTN_back = findViewById(R.id.home_BTN_back);
        home_SP_listOfBeaches = findViewById(R.id.home_SP_listOfBeaches);
        home_RecyclerView_beachData = findViewById(R.id.home_RecyclerView_rec);
        home_EditTXT_byName = findViewById(R.id.home_EditTXT_byName);
        home_BTN_name = findViewById(R.id.home_BTN_name);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String parameter = adapterView.getItemAtPosition(position).toString();

        int i = parameter.indexOf(' ');
        String word = parameter.substring(0, i);
        value = word;
        getBeaches(word.toLowerCase());
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

    private double getDistance(LatLng location1, LatLng location2) {
        return (SphericalUtil.computeDistanceBetween(location1, location2) / 1000);
    }


    //rank the values of string to how close they are to the target
    public List<String> findSimilarStrings(ArrayList<String> strings, String target) {
        List<String> similarStrings = new ArrayList<>();

        // Compute the similarity score between the target string and each string in the array
        Map<String, Integer> scores = new HashMap<>();
        for (String s : strings) {
            scores.put(s, getSimilarityScore(s, target));
        }

        // Sort the array of strings by similarity score in descending order
        strings.sort((s1, s2) -> scores.get(s2) - scores.get(s1));

        // Add the sorted strings to the list of similar strings
        for (String s : strings) {
            if (scores.get(s) > 0) {
                similarStrings.add(s);
            }
        }
        return similarStrings;
    }

    //calculate how similar two strings are
    private int getSimilarityScore(String s1, String s2) {
        // Compute the similarity score between two strings using the compareTo() method
        int score = 0;
        int length = Math.min(s1.length(), s2.length());
        for (int i = 0; i < length; i++) {
            if (s1.charAt(i) == s2.charAt(i)) {
                score++;
            }
        }
        return score;
    }

    public String getName(String displayName){
        return displayName;
    }

}
