package com.example.jo.pushapp.activity;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;

public class GpsCheck extends Service implements LocationListener {

    Context mContext;

    // 현재 GPS 사용유무
    boolean isGPSEnabled = false;
    // GPS 상태값
    boolean isGetLocation = false;
    // GPS 변화값
    boolean isChangeLocation = false;
    // 위치
    Location location;
    // 위도
    double lat;
    // 경도
    double lon;

    // GPS 정보 업데이트 거리 1미터
    private static final long MIN_DISTANCE_UPDATES = 1;
    // GPS 정보 업데이트 시간 1초
    private static final long MIN_TIME_UPDATES = 1000;

    // Location Manager
    public static LocationManager locationManager;

    // Handler
    private Handler mHandler;

    public GpsCheck() {
    }

    public GpsCheck(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        getLocation();
    }

    public void Update() {
        getLocation();
    }

    public Location getLocation() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // GPS 상태 받기
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // 네트워크 상태 받기
            if (!isGPSEnabled) {
                this.isGetLocation = false;
                showSettingsAlert();
            } else {
                this.isGetLocation = true;
                // GPS 가 사용가능할 경우 위도/경도 위치 받기
                if (location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_UPDATES,
                            MIN_DISTANCE_UPDATES, this);
                } else {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                }
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * GPS 종료: 이 함수를 호출할 경우 어플에서 GPS 사용을 멈춤
     */
    public void stopUsingGPS() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locationManager != null) {
            locationManager.removeUpdates(GpsCheck.this);
        }
    }

    /**
     * 위도 얻기
     */
    public double getLatitude() {
        if (location != null) {
            lat = location.getLatitude();
        }
        return lat;
    }

    /**
     * 경도 얻기
     */
    public double getLongitude() {
        if (location != null) {
            lon = location.getLongitude();
        }
        return lon;
    }

    /**
     * GPS 사용가능 여부 확인
     */
    public boolean isGetLocation() {
        return this.isGetLocation;
    }

    /**
     * GPS 정보를 가져오지 못했을 때 설정값으로 갈지 물어보는 alert 창
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Dialog 제목
        alertDialog.setTitle("GPS 사용 설정");
        // Dialog 메시지
        alertDialog
                .setMessage("GPS 설정이 되지 않았을 수도 있습니다.\n 설정 창으로 가시겠습니까?");
        // 설정 버튼
        alertDialog.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,
                                int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // 취소 버튼
        alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,
                                int which) {
                dialog.cancel();
            }
        });

        // 경고 메시지 보이기
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            isChangeLocation = true;
            double latitude = Double.parseDouble(String.format("%.6f", location.getLatitude()));
            double longitude = Double.parseDouble(String.format("%.6f", location.getLongitude()));
           // if (MenuActivity.selectedActivity == 3) {
                ((MapActivity) MapActivity.context).drawMarker();
                sendString("onLocationChanged - " + "," + location.getProvider() + "," + MapActivity.isProximity + "," + latitude + "," + longitude + "\n");
           // }
        }
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
      //  if (MenuActivity.selectedActivity == 3) {
            //Toast.makeText(mContext, "onStatusChanged " + provider + " : " + status, Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessage(MapActivity.RENEW_GPS);
            //sendString("onStatusChanged - " + provider + " : " + status + ":" + printBundle(extras));
      //  }
    }

    public void onProviderEnabled(String provider) {
     //   if (MenuActivity.selectedActivity == 3) {
            //Toast.makeText(mContext, "onProviderEnabled " + provider, Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessage(MapActivity.RENEW_GPS);
            sendString("onProviderEnabled - " + "," + provider);
       // }
    }

    public void onProviderDisabled(String provider) {
      //  if (MenuActivity.selectedActivity == 3) {
            mHandler.sendEmptyMessage(MapActivity.RENEW_GPS);
            sendString("onProviderDisabled - " + "," + provider);
      //  }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendString(String str) {
   //     if (MenuActivity.selectedActivity == 3) {
            Message msg = mHandler.obtainMessage();
            msg.what = MapActivity.SEND_PRINT;
            msg.obj = str;
            mHandler.sendMessage(msg);
      //  }
    }

}
