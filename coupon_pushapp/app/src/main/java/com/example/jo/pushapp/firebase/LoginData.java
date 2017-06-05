package com.example.jo.pushapp.firebase;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

/**
 * Created by jo on 2017-05-21.
 */

public class LoginData implements Serializable {
    private String email;
    private String name;
    private String pass;
    private String photoUrl;

    public LoginData(String email, String name, String pass, String photoUrl) {
        this.email = email;
        this.name = name;
        this.pass = pass;
        this.photoUrl = photoUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
