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

public class CouponItemView2 extends LinearLayout {
    ImageView imageView;
    TextView place;
    TextView coupon;
    TextView expireation;

    String photoUrl;
    Context context;

    public CouponItemView2(Context context) {
        super(context);

        init(context);
    }

    public CouponItemView2(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public CouponItemView2(Context context, String photoUrl) {
        super(context);
        this.photoUrl=photoUrl;
        this.context=context;
        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.coupon_item_v2, this, true);
        imageView = (ImageView) findViewById(R.id.c2_imageView);
        place = (TextView) findViewById(R.id.c2_place);
        coupon = (TextView) findViewById(R.id.coupon2);
        expireation = (TextView) findViewById(R.id.expireation2);


    }

    public void setImageView(int c_imageView) {imageView.setImageResource(c_imageView);}
    public void setplace(String coupon_place) {place.append(coupon_place);}
    public void setcoupon(String c_writedate) {coupon.append(c_writedate);}
    public void setExpireation(String c_expireation) {expireation.append(c_expireation);}



    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        Picasso.with(context).load(photoUrl).into(imageView);
    }
}
