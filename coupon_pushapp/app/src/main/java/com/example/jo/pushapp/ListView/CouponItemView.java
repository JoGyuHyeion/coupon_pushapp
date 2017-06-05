package com.example.jo.pushapp.ListView;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jo.pushapp.R;
import com.squareup.picasso.Picasso;


/**
 * Created by jo on 2017-04-29.
 */

public class CouponItemView extends LinearLayout {
    TextView couponplace;
    TextView id;
    TextView writedate;
    TextView expireation;
    TextView num;
    ImageView imageView;
    String photoUrl;
    Context context;

    public CouponItemView(Context context) {
        super(context);

        init(context);
    }

    public CouponItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public CouponItemView(Context context, String photoUrl) {
        super(context);
        this.photoUrl=photoUrl;
        this.context=context;
        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.coupon_item, this, true);

        num = (TextView) findViewById(R.id.num);
        id = (TextView) findViewById(R.id.c_id);
        couponplace = (TextView) findViewById(R.id.couponplace);
        expireation = (TextView) findViewById(R.id.expireation);
        writedate = (TextView) findViewById(R.id.writedate);
        imageView = (ImageView) findViewById(R.id.c_imageView);
    }

    public void setNum(String c_num) {num.append(c_num);}
    public void setId(String c_id) {id.append(c_id);}
    public void setCouponplace(String coupon_place) {couponplace.append(coupon_place);}
    public void setExpireation(String c_expireation) {expireation.append(c_expireation);}
    public void setWritedate(String c_writedate) {writedate.append(c_writedate);}
    public void setImageView(int c_imageView) {imageView.setImageResource(c_imageView);}

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        Picasso.with(context).load(photoUrl).into(imageView);
    }
}
