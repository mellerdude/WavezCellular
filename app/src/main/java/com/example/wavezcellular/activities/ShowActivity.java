package com.example.wavezcellular.activities;

import static com.example.wavezcellular.utils.User.getGuest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.example.wavezcellular.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ShowActivity extends AppCompatActivity {

    private MaterialButton show_BTN_back, show_BTN_reports;
    private ImageView show_IMG_profile;
    private MaterialButton show_TXT_density,show_TXT_wave,show_TXT_jellyfish,
            show_TXT_danger,show_TXT_wind, show_TXT_accessible, show_TXT_dog;
    private MaterialTextView show_TXT_nameBeach,show_TXT_temperature;
    private RatingBar show_RB_review;
    private Bundle bundle;
    private String BeachName;
    private final double HIGH_VALUE = 70;
    private FirebaseUser firebaseUserUser;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        bundle = getIntent().getExtras();
        if (bundle != null) {
            BeachName = bundle.getString("BEACH_NAME");
        } else {
            this.bundle = new Bundle();
            BeachName = "";
        }
        firebaseUserUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUserUser == null){
            getGuest(bundle);
        }
        myRef = FirebaseDatabase.getInstance().getReference("Beaches");


        findViews();
        createListeners();
        showInfo();


    }



    private void showInfo() {
        show_TXT_nameBeach.setText(""+ BeachName);
        myRef.child(BeachName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                HashMap<String, Object> beach = (HashMap<String, Object>) dataSnapshot.getValue();
                double review = getDouble(beach,"review");
                double warmth = getDouble(beach,"warmth");
                double wind = getDouble(beach,"wind");
                double danger = getDouble(beach,"danger");
                double accessible = getDouble(beach,"accessible");
                double density = getDouble(beach,"density");
                double jellyfish = getDouble(beach,"jellyfish");
                double dog = getDouble(beach,"dog");

                show_RB_review.setRating((float) review);
                if(wind>HIGH_VALUE)
                    show_TXT_wind.setText("Windy");
                else
                    show_TXT_wind.setText("OK wind");
                if(danger>HIGH_VALUE)
                    show_TXT_danger.setText("Dangerous");
                else
                    show_TXT_danger.setText("Not Dangerous");
                if(accessible>HIGH_VALUE)
                    show_TXT_accessible.setText("accessible");
                else
                    show_TXT_accessible.setText("Not accessible");
                if(density>HIGH_VALUE)
                    show_TXT_density.setText("Crowded");
                else
                    show_TXT_density.setText("Uncrowded");
                if(jellyfish>HIGH_VALUE)
                    show_TXT_jellyfish.setText("Many Jellyfish");
                else
                    show_TXT_jellyfish.setText("No Jellyfish");
                if(dog>HIGH_VALUE)
                    show_TXT_dog.setText("Many Jellyfish");
                else
                    show_TXT_dog.setText("Uncrowded");
                if(warmth>HIGH_VALUE)
                    show_TXT_dog.setText("Many Jellyfish");
                else
                    show_TXT_dog.setText("Uncrowded");



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private double getDouble(HashMap<String, Object> hashMap, String valueName) {
        Long l = (Long) hashMap.get(valueName);
        double val;
        if(l != null)
            val = (double)l;
        else
            val = 0;
        return val;
    }

    private void createListeners() {
        show_BTN_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceActivity("Home");
            }
        });
        show_IMG_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceActivity("Profile");
            }
        });
        show_BTN_reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceActivity("Report");
            }
        });
    }

        private void replaceActivity(String mode) {
            Intent intent;
            if (mode.equals("Profile")) {
                intent = new Intent(this, UserActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
            if (mode.equals("Report")) {
                intent = new Intent(this, ReportActivity.class);
                bundle.putString("BEACH_NAME", BeachName);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
            if (mode.equals("Home")) {
                intent = new Intent(this, HomeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }

        }


    private void findViews() {
        show_BTN_reports = findViewById(R.id.show_BTN_reports);
        show_BTN_back = findViewById(R.id.show_BTN_back);
        show_IMG_profile = findViewById(R.id.show_IMG_profile);
        show_TXT_nameBeach = findViewById(R.id.show_TXT_nameBeach);
        show_TXT_density = findViewById(R.id.show_TXT_density);
        show_TXT_wave = findViewById(R.id.show_TXT_wave);
        show_TXT_jellyfish = findViewById(R.id.show_TXT_jellyfish);
        show_TXT_temperature = findViewById(R.id.show_TXT_temperature);
        show_TXT_danger = findViewById(R.id.show_TXT_danger);
        show_TXT_wind = findViewById(R.id.show_TXT_wind);
        show_RB_review = findViewById(R.id.show_RB_review);
        show_TXT_accessible = findViewById(R.id.show_TXT_accessible);
        show_TXT_dog = findViewById(R.id.show_TXT_dogs);
    }
}