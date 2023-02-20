package com.example.wavezcellular.activities;

import static com.example.wavezcellular.utils.User.getGuest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.wavezcellular.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ReportActivity extends AppCompatActivity {
    private TextView report_TXT_nameBeach;
    private MaterialButton report_BTN_back, report_BTN_submit;
    private RatingBar report_RB_review;
    private SeekBar report_SB_density, report_SB_danger,report_SB_wind,report_SB_dog,report_SB_warmth,report_SB_accessible,report_SB_jellyfish, report_SB_hygiene;
    private EditText report_EditTXT_comment;
    private ImageView report_IMG_profile;
    private Bundle bundle;
    private String BeachName;
    private FirebaseUser firebaseUserUser;
    private DatabaseReference myRef;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_upgrade);
        bundle = getIntent().getExtras();
        if (bundle != null) {
            BeachName = bundle.getString("BEACH_NAME");
        } else {
            this.bundle = new Bundle();
            BeachName = "";
        }
        firebaseUserUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUserUser == null){
            user = getGuest(bundle);
        }else
            user = firebaseUserUser.getDisplayName();
        myRef = FirebaseDatabase.getInstance().getReference("Beaches").child(BeachName);


        findViews();
        createListeners();
        report_TXT_nameBeach.setText(""+ BeachName);
    }

    private void createListeners() {
        report_BTN_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReportActivity.this, HomeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
        report_IMG_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReportActivity.this, UserActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        report_BTN_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitValues();
                Intent intent = new Intent( ReportActivity.this, HomeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }

    private void submitValues() {
        myRef.child("Reports").child(user).child("review").setValue(report_RB_review.getRating());
        myRef.child("Reports").child(user).child("density").setValue(report_SB_density.getProgress());
        myRef.child("Reports").child(user).child("jellyfish").setValue(report_SB_jellyfish.getProgress());
        myRef.child("Reports").child(user).child("accessible").setValue(report_SB_accessible.getProgress());
        myRef.child("Reports").child(user).child("danger").setValue(report_SB_danger.getProgress());
        myRef.child("Reports").child(user).child("dog").setValue(report_SB_dog.getProgress());
        myRef.child("Reports").child(user).child("hygiene").setValue(report_SB_hygiene.getProgress());
        myRef.child("Reports").child(user).child("warmth").setValue(report_SB_warmth.getProgress());
        myRef.child("Reports").child(user).child("wind").setValue(report_SB_wind.getProgress());
        myRef.child("Reports").child(user).child("comment").setValue(report_EditTXT_comment.getText());

    }


    private void findViews() {
        report_BTN_back = findViewById(R.id.report_BTN_back);
        report_IMG_profile = findViewById(R.id.report_IMG_profile);
        report_TXT_nameBeach = findViewById(R.id.report_TXT_nameBeach);
        report_RB_review = findViewById(R.id.report_RB_review);
        report_SB_density = findViewById(R.id.report_SB_density);
        report_SB_jellyfish = findViewById(R.id.report_SB_jellyfish);
        report_SB_accessible = findViewById(R.id.report_SB_accessible);
        report_SB_danger = findViewById(R.id.report_SB_danger);
        report_SB_dog = findViewById(R.id.report_SB_dog);
        report_SB_hygiene = findViewById(R.id.report_SB_Hygiene);
        report_SB_warmth = findViewById(R.id.report_SB_warmth);
        report_SB_wind = findViewById(R.id.report_SB_wind);
        report_BTN_submit = findViewById(R.id.report_BTN_submit);
        report_EditTXT_comment = findViewById(R.id.report_EditTXT_comment);
    }
}