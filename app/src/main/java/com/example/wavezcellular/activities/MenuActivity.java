package com.example.wavezcellular.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wavezcellular.R;
import com.google.android.material.button.MaterialButton;


public class MenuActivity extends AppCompatActivity {
    private MaterialButton menu_BTN_beachFound;
    private MaterialButton menu_BTN_beachdetails;
    private MaterialButton menu_BTN_searchBeach;
    private MaterialButton menu_BTN_signIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        findViews();
        setListeners();
    }

    private void setListeners() {
        menu_BTN_beachdetails.setOnClickListener(view->{
            goToBeach();
        });
        menu_BTN_searchBeach.setOnClickListener(view->{
            goToSearch();
        });
        menu_BTN_signIn.setOnClickListener(view->{
            replaceActivityWelcome();
        });
    }

    private void goToSearch() {
    }


    private void goToBeach() {
    }

    private void findViews() {
        menu_BTN_beachFound = findViewById(R.id.menu_BTN_beachFound);
        menu_BTN_beachdetails = findViewById(R.id.menu_BTN_beachdetails);
        menu_BTN_searchBeach = findViewById(R.id.menu_BTN_searchBeach);
        menu_BTN_signIn = findViewById(R.id.menu_BTN_signIn);

    }

    private void replaceActivityWelcome() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}
