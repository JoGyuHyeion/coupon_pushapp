package com.example.jo.pushapp.Location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.example.jo.pushapp.activity.GpsMainActivity;


public class GPS_Service extends Service implements LocationListener {
    Toast toast;
    LocationManager manager;
    GPSListener gpsListener;
    private Double latitudes, longitudes;

    public Double getLatitudes() {return latitudes;}
    public void setLatitudes(Double latitudes) {this.latitudes = latitudes;}
    public Double getLongitudes() {return longitudes;}
    public void setLongitudes(Double longitudes) {this.longitudes = longitudes;}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        if (intent == null) {
            return Service.START_STICKY;
        } else {
            manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // 위치 정보를 받을 리스너 생성
            gpsListener = new GPSListener();
            long minTime = 10000;
            float minDistance = 0;

            try {
                // GPS를 이용한 위치 요청
                manager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        minTime,
                        minDistance,
                        gpsListener);

                // 네트워크를 이용한 위치 요청
                manager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        minTime,
                        minDistance,
                        gpsListener);

                // 위치 확인이 안되는 경우에도 최근에 확인된 위치 정보 먼저 확인
                Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null) {
                    Double latitudes = lastLocation.getLatitude();
                    Double longitudes = lastLocation.getLongitude();
                    toast.setText("Last Known Location : " + "Latitude : " + latitudes + "\nLongitude:" + longitudes);
                    toast.show();
                }
            } catch (SecurityException ex) {
                ex.printStackTrace();
            }
            processCommand(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void processCommand(Intent intent) {
        String latitude = String.valueOf(getLatitudes());
        String longitude = String.valueOf(getLongitudes());

        Intent showIntent = new Intent(getApplicationContext(), GpsMainActivity.class);
        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        showIntent.putExtra("latitude", ""+latitude);
        showIntent.putExtra("longitude", ""+longitude);
        startActivity(showIntent);
    }

    private class GPSListener implements LocationListener {
        /**
         * 위치 정보가 확인될 때 자동 호출되는 메소드
         */
        public void onLocationChanged(Location location) {
            setLatitudes(location.getLatitude());
            setLongitudes(location.getLongitude());

            Intent showIntent = new Intent(getApplicationContext(), GpsMainActivity.class);
            showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            showIntent.putExtra("latitude", ""+location.getLatitude());
            showIntent.putExtra("longitude",""+location.getLongitude());
            startActivity(showIntent);

            String msg = "Latitude : " + latitudes + "\nLongitude:" + longitudes;
            toast.setText(msg);
            toast.show();
            stopGps();
        }
        public void stopGps(){manager.removeUpdates(gpsListener);}
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onLocationChanged(Location location) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}
}
