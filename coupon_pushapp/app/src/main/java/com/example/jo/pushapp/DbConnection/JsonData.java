package com.example.jo.pushapp.DbConnection;


import android.util.Log;
import android.widget.Toast;

import com.example.jo.pushapp.ListView.Coupon_item;
import com.example.jo.pushapp.ListView.Gpsdata_Item;
import com.example.jo.pushapp.R;
import com.example.jo.pushapp.model.Gps_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by jo on 2017-04-29.
 */

public class JsonData {
    private ArrayList<Gpsdata_Item> arrayList = new ArrayList<Gpsdata_Item>();
    private ArrayList<Gps_model> gpsarrayList = new ArrayList<Gps_model>();
    private ArrayList<Coupon_item> couponarrayList = new ArrayList<Coupon_item>();
    public ArrayList<Gps_model> getGpsArrayList(String result) throws ExecutionException, InterruptedException, JSONException {
        // String result = new CustomTask().execute("", "", "", "", "selectAll").get();

        JSONObject json = new JSONObject(result);
        JSONArray jArr = json.getJSONArray("sendData");
        for (int i = 0; i < jArr.length(); i++) {
            json = jArr.getJSONObject(i);
            if (json != null) {
                Gps_model item = new Gps_model(json.getInt("id_num"),json.getDouble("latitude"), json.getDouble("longitude"), json.getInt("radius"), json.getString("name"));
                gpsarrayList.add(item);
            }
        }
        return gpsarrayList;
    }

    public ArrayList<Gpsdata_Item> getArrayList(String result) throws ExecutionException, InterruptedException, JSONException {
       // String result = new CustomTask().execute("", "", "", "", "selectAll").get();

        JSONObject json = new JSONObject(result);
        JSONArray jArr = json.getJSONArray("sendData");
        for (int i = 0; i < jArr.length(); i++) {
            json = jArr.getJSONObject(i);
            if (json != null) {
                Gpsdata_Item item = new Gpsdata_Item(json.getString("latitude"), json.getString("longitude"), json.getString("radius"), json.getString("name"), json.getString("id_num"));
                item.setImage(R.drawable.ic_cloud);
                arrayList.add(item);
            }
        }
        return arrayList;
    }
    public ArrayList<Coupon_item> getcouponArrayList(String result) throws ExecutionException, InterruptedException, JSONException {
        // String result = new CustomTask().execute("", "", "", "", "selectAll").get();

        JSONObject json = new JSONObject(result);
        JSONArray jArr = json.getJSONArray("sendData");
        for (int i = 0; i < jArr.length(); i++) {
            json = jArr.getJSONObject(i);
            if (json != null) {
                Log.d("어레이 ",json.getString("place")+"place");
                Log.d("어레이 ",json.getString("id")+"id");
                Log.d("어레이 ",json.getString("writedate")+"writedate");
                Log.d("어레이 ",json.getString("num")+"num");
                Log.d("어레이 ",json.getString("expiration")+"expiration");
                Log.d("어레이 ",json.getString("contents")+"contents");
                Log.d("어레이 ",json.getString("contents")+"contents");
                Coupon_item item = new Coupon_item(json.getString("place"), json.getString("id"), json.getString("writedate"), json.getString("expiration"),json.getString("contents"),json.getString("num"),json.getString("photourl"));
                item.setImage(R.drawable.ic_cloud);
                Log.d("어레이 ",item.getWritedate());
                Log.d("어레이 ",item.getExpireation());

                couponarrayList.add(item);
            }
        }
        return couponarrayList;
    }


}
