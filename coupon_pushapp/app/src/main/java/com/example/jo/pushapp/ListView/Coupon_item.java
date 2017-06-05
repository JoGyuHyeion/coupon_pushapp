package com.example.jo.pushapp.ListView;

public class Coupon_item {
    private String place;
    private String id;
    private String writedate;
    private String expireation;
    private String num;
    private String contents;
    private String photoUrl;
    private int image;

    public Coupon_item(String place, String id, String writedate, String expireation, String num) {
        this.place = place;
        this.id = id;
        this.writedate = writedate;
        this.expireation = expireation;
        this.num = num;
    }

    public Coupon_item(String place, String id, String writedate, String expireation, String contents, String num) {
        this.place = place;
        this.id = id;
        this.writedate = writedate;
        this.expireation = expireation;
        this.contents = contents;
        this.num = num;
    }

    public Coupon_item(String place, String id, String writedate, String expireation, String contents, String num, String photoUrl) {
        this.place = place;
        this.id = id;
        this.writedate = writedate;
        this.expireation = expireation;
        this.contents = contents;
        this.num = num;
        this.photoUrl=photoUrl;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWritedate() {
        return writedate;
    }

    public void setWritedate(String writedate) {
        this.writedate = writedate;
    }

    public String getExpireation() {
        return expireation;
    }

    public void setExpireation(String expireation) {
        this.expireation = expireation;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
