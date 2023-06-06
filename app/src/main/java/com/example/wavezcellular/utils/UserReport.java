package com.example.wavezcellular.utils;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserReport {
    private String name;
    private String comment;
    private int photo = 0;
    private Resources res;
    private Context context;


    private HashMap<String, Object> parameters = new HashMap<>();

    public UserReport(HashMap<String, Object> parameters, String photo, Context applicationContext){
        context = applicationContext;
        res = context.getResources();
        this.parameters = parameters;
        this.comment = (String) parameters.get("comment");
        this.name = (String)parameters.get("name");
        if(parameters.get("photo") != null) {
            createPhoto(photo==null, photo, (String)parameters.get("photo"));
        }
    }



    public Object getValue(String value){
        if(value.equalsIgnoreCase("comment"))
            return null;
        else
            return parameters.get(value);
    }

    public boolean hasComment(){
        String comment = (String) getValue("comment");
        return comment.length() != 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private void createPhoto(boolean isDemo_User, String userPhoto, String demoPhoto) {
        if(isDemo_User){
            photo = res.getIdentifier(demoPhoto, "drawable", context.getPackageName());
        }else{
            photo = res.getIdentifier(userPhoto, "drawable", context.getPackageName());
        }
    }


        public int getPhoto () {
            return photo;
        }
        public void setPhoto ( int photo){
            this.photo = photo;
        }

    }