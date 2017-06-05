package com.example.jo.pushapp.activity;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jo.pushapp.DbConnection.GpsTask;
import com.example.jo.pushapp.DbConnection.JsonData;
import com.example.jo.pushapp.Location.GPS_Service;
import com.example.jo.pushapp.Location.LocationPermission;
import com.example.jo.pushapp.R;
import com.example.jo.pushapp.firebase.LoginData;
import com.example.jo.pushapp.model.Gps_model;
import com.example.jo.pushapp.proximity.PlaceIntentReceiver;

import org.json.JSONException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * 일정한 지점을 좌표로 지정하고 그 지점에 가까이 갔을 때 알 수 있는 근접경보 기능 사용 방법에 대해 알 수 있습니다.
 *
 * @author Mike
 */
public class ProximityActivity extends AppCompatActivity {
    private static final String TAG = "ProximityActivity";

    private LocationManager mLocationManager;   //
    private PlaceIntentReceiver mIntentReceiver;
    private  ArrayList<Gps_model> gpsArray;

    private   ArrayList mPendingIntentList;
    private   ArrayList idArray;
    private  TextView textView01,textView02;

    private  Intent Gps_intent;

    private  String intentKey1 = "3cs";
    private   Snackbar snackbar;
    private   LoginData loginData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proximityactivity_main);

        textView01 = (TextView) findViewById(R.id.textView01);
        textView02 = (TextView) findViewById(R.id.textView02);
        //////로그인 정보 받아오기
        Intent intent = getIntent();
        loginData = (LoginData) intent.getSerializableExtra("loginData");
        Toast.makeText(ProximityActivity.this, loginData.getEmail() + loginData.getName() + loginData.getPhotoUrl(), Toast.LENGTH_SHORT).show();

        // 위치 관리자 객체 참조
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mPendingIntentList = new ArrayList();
        idArray=new ArrayList();    //id에 따른 장소 이름 저장 int 형 id을 인덱스로 활용하여 장소 저장
        gpsArray = new ArrayList<Gps_model>();

        Gps_intent = new Intent(getApplicationContext(), GPS_Service.class);

        // 버튼 이벤트 처리
        Button startBtn = (Button) findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                textView01.setText("");
                int id,radius,effective_time;
                double latitude,longitude;
                String key;
                String result = null;
                try {
                    result = new GpsTask().execute("", "", "", "", "selectAll").get();
                    JsonData jsonGpsData=new JsonData();
                    gpsArray=jsonGpsData.getGpsArrayList(result);
                   /* JsonGpsData jsonGpsData=new JsonGpsData();
                    gpsArray=jsonGpsData.getArrayList(result);*/
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for(int i = 0; i<gpsArray.size();i++){
                    id=gpsArray.get(i).getId_num();
                    latitude=gpsArray.get(i).getLatitude();
                    longitude=gpsArray.get(i).getLongitude();
                    radius=gpsArray.get(i).getRadius();
                    effective_time=gpsArray.get(i).getEffective_time();
                    key=gpsArray.get(i).getName();
                    Log.d(TAG, "id : "+id+"     la : "+latitude+"      lon : "+longitude+"      ra : "+radius+"     ef : "+effective_time+"     key : "+key+"등록 완료");
                    register(id,latitude,longitude,1,effective_time);


                    textView01.append(key+"/      ");

                    mIntentReceiver = new PlaceIntentReceiver(intentKey1);
                    registerReceiver(mIntentReceiver, mIntentReceiver.getFilter());
                }


                textView02.setText(gpsArray.size()+"개 지점 등록 되었습니다.");

                //Toast.makeText(getApplicationContext(), gpsArray.size() + "개 지점에 대한 근접 리스너 등록", Toast.LENGTH_SHORT).show();
                Snackbar.make(textView02, gpsArray.size() + "개 지점에 대한 근접 리스너 등록", Snackbar.LENGTH_LONG).show();
                startService(Gps_intent);
            }
        });

        Button stopBtn = (Button) findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                unregister();
                Snackbar.make(textView02, mPendingIntentList.size() + "개 근접 리스너 해제", Snackbar.LENGTH_LONG).show();
              //  Toast.makeText(getApplicationContext(), "근접 리스너 해제", Toast.LENGTH_SHORT).show();
                stopService(Gps_intent);
            }
        });
        LocationPermission checkPermission =new LocationPermission(this);
        checkPermission.checkDangerousPermissions();
    }


    /**
     * register the proximity intent receiver
     */
    private void register(int id, double latitude, double longitude, float radius, long expiration) {   //위치등록
        Intent proximityIntent = new Intent(intentKey1);
        proximityIntent.putExtra("id", id);
        proximityIntent.putExtra("latitude", latitude);
        proximityIntent.putExtra("longitude", longitude);
        PendingIntent intent = PendingIntent.getBroadcast(this, id, proximityIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mLocationManager.addProximityAlert(latitude, longitude, radius, expiration, intent);

        mPendingIntentList.add(intent);
    }

    /**
     * 등록한 정보 해제
     */
    private void unregister() {
        if (mPendingIntentList != null) {
            for (int i = 0; i < mPendingIntentList.size(); i++) {
                PendingIntent curIntent = (PendingIntent) mPendingIntentList.get(i);
                mLocationManager.removeProximityAlert(curIntent);
                mPendingIntentList.remove(i);
            }
        }
        if (mIntentReceiver != null) {
            unregisterReceiver(mIntentReceiver);
            mIntentReceiver = null;
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        unregister();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);

        super.onNewIntent(intent);
    }

    private void processIntent(Intent intent) {
        if (intent != null) {
            double latitude = intent.getDoubleExtra("latitude", 0.0D);
            double longitude = intent.getDoubleExtra("longitude", 0.0D);
            int id=intent.getIntExtra("id",0);
            String place = null;

            for(int i=0;i<gpsArray.size();i++){
                if(gpsArray.get(i).getLatitude()==latitude&&gpsArray.get(i).getLongitude()==longitude){
                    place=gpsArray.get(i).getName();
                }
            }
            Snackbar.make(textView02, place + id + ", " + latitude + ", " + longitude, Snackbar.LENGTH_SHORT).show();
           // Toast.makeText(getApplicationContext(), place + id + ", " + latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
            //push start
            Resources resources = getResources();
            Uri soundUri = RingtoneManager.getActualDefaultRingtoneUri(this,RingtoneManager.TYPE_NOTIFICATION);
            Intent P_intent = new Intent(ProximityActivity.this, FindCouponActivity.class);
            P_intent.putExtra("place",place);
            P_intent.putExtra("loginData", loginData);
           // PendingIntent pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,Search.class),0);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,P_intent,PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(resources.getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher) // 알림 영역에 노출 될 아이콘.
                    .setContentTitle(getString(R.string.app_name)) // 알림 영역에 노출 될 타이틀
                    .setContentText(place) // Firebase Console 에서 사용자가 전달한 메시지내용
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)//눌렀을때  통지함에서 삭제
                    .setSound(soundUri)
                    .setVibrate(new long[]{0,1000})
                    .build();
            //------------------------------------------------------------------
            // Notify
            NotificationManagerCompat notiManager = NotificationManagerCompat.from(this);
            notiManager.notify(0, notification);
//push end
        }
    }
}
