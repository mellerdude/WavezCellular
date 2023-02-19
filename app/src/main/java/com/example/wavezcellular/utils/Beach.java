package com.example.wavezcellular.utils;

import java.util.Comparator;

public class Beach {
    private String name;
    private double longitude;
    private double latitude;
    private double density;
    private double jellyfish;
    private double flag;
    private double wave;
    private double temperature;
    private double wind;
    private double warmth;
    private double dog;
    private double accessible;
    private double review;

    public Beach(){

    }

    public Beach(String name, double lon, double lat){
        this.name = name;
        this.longitude = lon;
        this.latitude = lat;
        density = 3;
        jellyfish = 3;
        flag = 3;
        wave = 3;
        temperature = 3;
        wind = 3;
        review = 3;
        dog = 3;
        accessible = 3;
        warmth = 3;
    }


    public double getWarmth() {
        return warmth;
    }

    public void setWarmth(double warmth) {
        this.warmth = warmth;
    }

    public double getDog() {
        return dog;
    }

    public void setDog(double dog) {
        this.dog = dog;
    }

    public double getAccessible() {
        return accessible;
    }

    public void setAccessible(double accessible) {
        this.accessible = accessible;
    }

    public double getReview() {
        return review;
    }

    public void setReview(double review) {
        this.review = review;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public double getJellyfish() {
        return jellyfish;
    }

    public void setJellyfish(double jellyfish) {
        this.jellyfish = jellyfish;
    }

    public double getFlag() {
        return flag;
    }

    public void setFlag(double flag) {
        this.flag = flag;
    }

    public double getWave() {
        return wave;
    }

    public void setWave(double wave) {
        this.wave = wave;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getWind() {
        return wind;
    }

    public void setWind(double wind) {
        this.wind = wind;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
