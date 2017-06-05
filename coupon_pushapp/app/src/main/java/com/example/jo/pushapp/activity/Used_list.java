package com.example.jo.pushapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jo.pushapp.DbConnection.CouponTask;
import com.example.jo.pushapp.ListView.CouponAdapter2;
import com.example.jo.pushapp.ListView.Coupon_item;
import com.example.jo.pushapp.R;
import com.example.jo.pushapp.firebase.LoginData;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

/**
 * Created by jo on 2017-05-23.
 */

public class Used_list extends AppCompatActivity {
    private  Toast toast;
    private  ListView allListview;
    private  CouponAdapter2 adapter2;
    private  LoginData loginData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        Intent intent = getIntent();
        loginData = (LoginData) intent.getSerializableExtra("loginData");
        Toast.makeText(Used_list.this, loginData.getEmail() + loginData.getName() + loginData.getPhotoUrl(), Toast.LENGTH_SHORT).show();

        toast=Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT);
        allListview = (ListView) findViewById(R.id.listView);
        adapter2 = new CouponAdapter2(this);


        try {
            String result = new CouponTask().execute(loginData.getEmail(), "", "", "", "", "", "", "",  "C_UsedAll").get();
            //String result = new Sin_InLoginTask().execute(loginData.getEmail(), "", "C_UsedAll").get();
            adapter2.buildArrayList(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        allListview.setAdapter(adapter2);

        allListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Coupon_item item = (Coupon_item) adapter2.getItem(position);
                new AlertDialog.Builder(Used_list.this)
                        .setMessage(item.getPlace() + "에서 사용한 쿠폰\n\n" + item.getContents())
                        .setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // not thing..
                            }
                        }).show();
            }
        });

    }
}