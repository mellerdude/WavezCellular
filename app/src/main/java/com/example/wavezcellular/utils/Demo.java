package com.example.wavezcellular.utils;

import android.content.Context;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wavezcellular.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Demo extends AppCompatActivity {
    private static final Random random = new Random();
    private final double DEF_REVIEW_VAL = 3.0;
    private final double MAX_REVIEW_VAL = 5.0;
    private final int PHOTOS_NUM = 20;

    private final int DEF_REPORTS = 3;
    private DatabaseReference myRef;
    private Resources resources;
    private Context context;
    private String[] beaches;
    private String[] categories;
    private FusedLocationProviderClient fusedLocationProviderClient;
    public Demo(Context context, FusedLocationProviderClient fusedLocationProviderClient, DatabaseReference myRef){
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        this.myRef = myRef;
        this.context = context;
        resources = context.getResources();
        beaches = resources.getStringArray(R.array.beaches);
        categories = resources.getStringArray(R.array.categories);
        setDataForDemo();
        setReportsForDemo();
    }

    public void setDataForDemo() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 0; i < beaches.length; i++) {
                    String beachName = beaches[i];
                    double[] locations;
                    locations = getBeachLocation(beachName);
                    myRef.child(beachName).child("Data").setValue(null);
                    myRef.child(beachName).child("Data").child("latitude").setValue(locations[0]);
                    myRef.child(beachName).child("Data").child("longitude").setValue(locations[1]);
                    myRef.child(beachName).child("Data").child("name").setValue(beachName);
                    for (int j = 0; j < categories.length; j++) {
                        if(!categories[j].equalsIgnoreCase("Name of the Beach"))
                            myRef.child(beachName).child("Data").child(categories[j].toLowerCase().split(" ")[0]).setValue(DEF_REVIEW_VAL);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void setReportsForDemo(){


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CommentGenerator generator = new CommentGenerator();
                for (int i = 0; i < beaches.length; i++) {
                    String beachName = beaches[i];
                    myRef.child(beachName).child("Reports").setValue(null);
                    for (int j = 0; j < DEF_REPORTS; j++) {
                        String name = generateRandomName();
                        int photo = generateRandomPhoto();
                        String email = "not@important.com";
                        myRef.child(beachName).child("Reports").child("DEMO_USER_" + j).child("name").setValue(name);
                        myRef.child(beachName).child("Reports").child("DEMO_USER_" + j).child("email").setValue(email);
                        myRef.child(beachName).child("Reports").child("DEMO_USER_" + j).child("photo").setValue(photo);
                        myRef.child(beachName).child("Reports").child("DEMO_USER_" + j).child("comment").setValue(generator.generateComment(beachName));
                        for (String category : categories) {
                            if(!category.equalsIgnoreCase("Name of the Beach")) {
                                double randNum = generateRandomNumber();
                                myRef.child(beachName).child("Reports").child("DEMO_USER_" + j).child(category.toLowerCase().split(" ")[0]).setValue(randNum);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private double generateRandomNumber() {
        return random.nextDouble() * MAX_REVIEW_VAL;
    }

    private int generateRandomPhoto() {
        int num = random.nextInt(20) + 1;
        int res = resources.getIdentifier("ic_user" + num, "drawable", context.getPackageName());
        return res;
    }

    private String generateRandomName() {
        return NameGenerator.generateRandomName();
    }

    public double[] getBeachLocation(String beachName) {
        double[] locations = new double[]{0, 0};
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocationName(beachName, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                locations[0] = address.getLatitude();
                locations[1] = address.getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return locations;
    }
}
