package com.example.jo.pushapp.ListView;

/**
 * Created by jo on 2017-04-26.
 */

public class Gpsdata_Item {
    private double latitude;
    private double longitude;
    private int radius;
    private final int effective_time = -1;
    private String name;
    private int id_num;
    private int image;


    public Gpsdata_Item(String latitude, String longitude, String radius, String name, String id_num) { //efc_time 은 항상 -1이니까
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
        this.radius = Integer.parseInt(radius);
        this.name = name;
        this.id_num = Integer.parseInt(id_num);
    }

    public Gpsdata_Item(String latitude, String longitude, String radius, String name, int imasge) { //id_num은 우리가 넣는게 아니니
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
        this.radius = Integer.parseInt(radius);
        this.name = name;
        this.image = imasge;
    }

    public Gpsdata_Item(String latitude, String longitude, String radius, String name, String id_num, int imasge) {
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
        this.radius = Integer.parseInt(radius);
        this.name = name;
        this.id_num = Integer.parseInt(id_num);
        this.image = imasge;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
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

    public int getId_num() {
        return id_num;
    }

    public void setId_num(int id_num) {
        this.id_num = id_num;
    }


}
