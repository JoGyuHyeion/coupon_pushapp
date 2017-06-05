package com.example.jo.pushapp.Location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by jo on 2017-04-19.
 */

public class LocationPermission {

    private Context context;

    public LocationPermission(Context context) {
        this.context = context;
    }

    public void checkDangerousPermissions() {  //퍼미션체크
        Toast tost = Toast.makeText(context, "", Toast.LENGTH_SHORT);

        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(context, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            tost.setText("권한 있음");
            tost.show();
            //Toast.makeText(context, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
              tost.setText("권한 없음");
              tost.show();
            //Toast.makeText(context, "권한 없음", Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissions[0])) {
                   tost.setText("권한 설명 필요함.");
                   tost.show();
                //Toast.makeText(context, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions((Activity) context, permissions, 1);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Toast tost = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        tost.setText(permissions[i] + " 권한이 승인됨.");
                        tost.show();
                    //  Toast.makeText(context, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
                } else {
                          tost.setText(permissions[i] + " 권한이 승인되지 않음.");
                          tost.show();
                    // Toast.makeText(context, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
