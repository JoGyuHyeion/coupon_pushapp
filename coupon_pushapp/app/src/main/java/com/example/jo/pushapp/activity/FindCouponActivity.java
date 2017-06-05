package com.example.jo.pushapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jo.pushapp.DbConnection.CouponTask;
import com.example.jo.pushapp.ListView.CouponAdapter2;
import com.example.jo.pushapp.ListView.Coupon_item;
import com.example.jo.pushapp.Location.LocationPermission;
import com.example.jo.pushapp.R;
import com.example.jo.pushapp.firebase.LoginData;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class FindCouponActivity extends AppCompatActivity {
    private   ListView allListview;
    private  CouponAdapter2 adapter2;
    private  LocationPermission locationPermission;
    private  Intent intent;
    private  LoginData loginData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        //////로그인 정보 받아오기
        intent = getIntent();
        loginData = (LoginData) intent.getSerializableExtra("loginData");
        Toast.makeText(FindCouponActivity.this, loginData.getEmail() + loginData.getName() + loginData.getPhotoUrl(), Toast.LENGTH_SHORT).show();

        locationPermission = new LocationPermission(this);

        allListview = (ListView) findViewById(R.id.listView);
        adapter2 = new CouponAdapter2(this);
        String place = intent.getStringExtra("place");
        try {
            String result = new CouponTask().execute(loginData.getEmail(), "", place, "", "", "", "", "", "C_findCoupon").get();
            //  String result = new Sin_InLoginTask().execute(loginData.getEmail(), "", "C_unUsedAll").get();
            adapter2.buildArrayList(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        allListview.setAdapter(adapter2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            allListview.setNestedScrollingEnabled(true);
        }

        allListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Coupon_item item = (Coupon_item) adapter2.getItem(position);
                new AlertDialog.Builder(FindCouponActivity.this)
                        .setMessage(item.getPlace() + "의\n\n" + item.getContents() + "\n\n쿠폰을 사용 하시겠습니까?")
                        .setPositiveButton("사용", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Log.d("쿠폰 번호확인", item.getNum());
                                    String result = new CouponTask().execute(loginData.getEmail(), "", item.getPlace(), item.getNum(), "", "", item.getExpireation(), "", "C_use").get();
                                    // String result = new UseCouponTask().execute(loginData.getEmail(), item.getPlace(), item.getNum(), item.getExpireation(), "C_use").get();
                                    if (result.equals("ok")) {
                                        Snackbar.make(allListview, "쿠폰 사용 되었습니다.", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        result = new CouponTask().execute(loginData.getEmail(), "", "", "", "", "", "", "", "C_unUsedAll").get();
                                        // result = new Sin_InLoginTask().execute(loginData.getEmail(), "", "C_unUsedAll").get();
                                        adapter2.buildArrayList(result);
                                        adapter2.notifyDataSetChanged();
                                    } else if (result.equals("false")) {
                                        Snackbar.make(allListview, "사용 불가능한 쿠폰입니다.", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                } catch (Exception e) {
                                }
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // not thing..
                            }
                        }).show();
            }
        });
    }
}
