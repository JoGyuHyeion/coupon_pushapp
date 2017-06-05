package com.example.jo.pushapp.model;

/**
 * Created by jo on 2017-05-12.
 */

public class Gps_model {
    private double latitude;
    private double longitude;
    private int radius;
    private final int effective_time = -1;
    private String name;
    private int id_num;
    private int image;

    public Gps_model(int id_num, double latitude, double longitude, int radius, String name) {
        this.id_num=id_num;
        this.latitude=latitude;
        this.longitude=longitude;
        this.radius=radius;
        this.name=name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getEffective_time() {
        return effective_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId_num() {return id_num;}

    public void setId_num(int id_num) {this.id_num = id_num;}

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
