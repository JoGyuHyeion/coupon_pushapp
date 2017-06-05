package com.example.jo.pushapp.ListView;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jo.pushapp.R;


/**
 * Created by jo on 2017-04-29.
 */

public class GpsItemView extends LinearLayout {
    TextView latitude;
    TextView longitude;
    TextView radius;
    TextView name;
    TextView id_num;
    ImageView imageView;

    public GpsItemView(Context context) {
        super(context);

        init(context);
    }

    public GpsItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.gps_item, this, true);

        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
        radius = (TextView) findViewById(R.id.radius);
        name = (TextView) findViewById(R.id.name);
        id_num = (TextView) findViewById(R.id.id_num);
        imageView = (ImageView) findViewById(R.id.imageView);
    }


    public void setLatitude(double latit) {latitude.append(String.valueOf(latit));}
    public void setLongitude(double longi) {
        longitude.append(String.valueOf(longi));
    }
    public void setRadius(int rai) {
        radius.append(String.valueOf(rai));
    }
    public void setName(String names) {name.append(names);}
    public void setId_num(int idNum) {
        id_num.append(String.valueOf(idNum));
    }
    public void setImage(int resId) {
        imageView.setImageResource(resId);
    }
}
