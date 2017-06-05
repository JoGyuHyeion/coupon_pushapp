package com.example.jo.pushapp.ListView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.example.jo.pushapp.DbConnection.JsonData;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by jo on 2017-04-29.
 */

public class GpsAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Gpsdata_Item> items = new ArrayList<Gpsdata_Item>();

    public GpsAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void buildArrayList(String result) throws InterruptedException, ExecutionException, JSONException {
        JsonData jsonGpsData=new JsonData();
        this.items=jsonGpsData.getArrayList(result);
    }

    public String addItem(Gpsdata_Item item) {
        String add_mesg;
        for (int i = 0; i < items.size(); i++) {
            if(items.get(i).getLatitude()==(item.getLatitude())&&items.get(i).getLongitude()==(item.getLongitude())) {
                return add_mesg="duplicate_items";
            }
        }
        items.add(item);
        return add_mesg="sucess";
    }

    public void remove(int position) {
        items.remove(position);
    }

    public void remove(double latitude,double longitude) {
        int index = 0;
        for (int i = 0; i < items.size(); i++) {
            if(items.get(i).getLatitude()==(latitude)&&items.get(i).getLongitude()==(longitude)) {
                index = i;
            }
        }
        items.remove(index);
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        GpsItemView view = new GpsItemView(mContext);

        Gpsdata_Item item = items.get(position);

        view.setLatitude(item.getLatitude());
        view.setLongitude(item.getLongitude());
        view.setRadius(item.getRadius());
        view.setName(item.getName());
        view.setId_num(item.getId_num());
        view.setImage(item.getImage());

        return view;
    }
}