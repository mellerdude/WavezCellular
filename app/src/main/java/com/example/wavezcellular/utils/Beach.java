package com.example.wavezcellular.utils;

public class Beach {
    private String name;
    private double longitude;
    private double latitude;
    private String density;
    private String jellyfish;
    private String flag;
    private String wave;
    private String temperature;
    private String wind;

    public Beach(){

    }

    public Beach(String name, double lon, double lat){
        this.name = name;
        this.longitude = lon;
        this.latitude = lat;
        density = "";
        jellyfish = "";
        flag = "";
        wave = "";
        temperature = "";
        wind = "";
    }

    public String getDensity() {
        return density;
    }

    public void setDensity(String density) {
        this.density = density;
    }

    public String getJellyfish() {
        return jellyfish;
    }

    public void setJellyfish(String jellyfish) {
        this.jellyfish = jellyfish;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getWave() {
        return wave;
    }

    public void setWave(String wave) {
        this.wave = wave;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
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
