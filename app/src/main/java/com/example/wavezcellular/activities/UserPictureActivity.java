package com.example.wavezcellular.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.wavezcellular.Interfaces.BeachListener;
import com.example.wavezcellular.Interfaces.PhotoListener;
import com.example.wavezcellular.R;
import com.example.wavezcellular.adapters_holders.UserPhotoAdapter;
import com.example.wavezcellular.adapters_holders.UserReportAdapter;
import com.example.wavezcellular.utils.ActivityManager;
import com.example.wavezcellular.utils.UserReport;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * UserPictureActivity
 * Activity to enable the user to change his profile picture
 */
public class UserPictureActivity extends AppCompatActivity implements PhotoListener {
    private static final int NUMBER_IMAGES = 20;
    private ActivityManager activityManager;
    private RecyclerView userPhoto_RecyclerView_photos;
    private MaterialButton userPhoto_BTN_back, userPhoto_BTN_select;
    private MaterialTextView userPhoto_TXT_headline;
    private List<String> imageNamesArray;
    private String selectedPhoto;

    private FirebaseUser firebaseUser;
    private DatabaseReference myRef;
    private String userID;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_picture);
        activityManager = new ActivityManager(this);
        bundle = getIntent().getExtras();
        if (bundle == null) {
            bundle = new Bundle();
        }
        findViews();
        initUser();
        createArrayOfPhotos();
        createListeners();
    }

    private void initUser() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("Users");
        userID = firebaseUser.getUid();
        selectedPhoto = "ic_user";
    }

    private void createListeners() {
        userPhoto_BTN_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToProfile();
            }
        });
        userPhoto_BTN_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child(userID).child("Photo").setValue(selectedPhoto);
                returnToProfile();
            }
        });
    }

    private void createArrayOfPhotos() {
        imageNamesArray = new ArrayList<>();
        for (int i = 1; i <= NUMBER_IMAGES; i++) {
            String imageName = "ic_user" + i;
            imageNamesArray.add(imageName);
        }
        createPhotosRec(imageNamesArray);
    }

    private void createPhotosRec(List<String> list) {
        userPhoto_RecyclerView_photos.setLayoutManager(new LinearLayoutManager(this));
        userPhoto_RecyclerView_photos.setAdapter(new UserPhotoAdapter(getApplicationContext(), list,this));
    }

    private void findViews() {
        userPhoto_RecyclerView_photos = findViewById(R.id.userPhoto_RecyclerView_photos);
        userPhoto_BTN_back = findViewById(R.id.userPhoto_BTN_back);
        userPhoto_BTN_select = findViewById(R.id.userPhoto_BTN_select);
        userPhoto_TXT_headline = findViewById(R.id.userPhoto_TXT_headline);
    }

    public void returnToProfile(){
        activityManager.startActivity(UserActivityUpgrade.class,bundle);
        /*Intent intent = new Intent(UserPictureActivity.this, UserActivityUpgrade.class);
        startActivity(intent);
        finish();*/

    }


    @Override
    public void onPhotoClick(String imageName) {
        selectedPhoto = imageName;
    }
}