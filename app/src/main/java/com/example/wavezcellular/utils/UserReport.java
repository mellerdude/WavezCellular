package com.example.wavezcellular.utils;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class UserReport {
    //private DatabaseReference photoRef;
    private String name;
    private String comment;
    private int photo = 0;



    private HashMap<String, Object> parameters = new HashMap<>();

    public UserReport(HashMap<String, Object> parameters){
        this.parameters = parameters;
        this.comment = (String) parameters.get("comment");
        this.name = (String)parameters.get("name");
        if(parameters.get("photo") != null) {
            long photoVal = (Long)parameters.get("photo");
            photo = (int) photoVal;
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

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }
}