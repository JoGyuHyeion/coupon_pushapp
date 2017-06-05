package com.example.jo.pushapp.proximity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.example.jo.pushapp.activity.ProximityActivity;

import java.util.ArrayList;

/**
 * 브로드캐스팅 메시지를 받았을 때 처리할 수신자 정의
 */

public class PlaceIntentReceiver extends BroadcastReceiver {
    ArrayList idArray;
    private String mExpectedAction;
    private Intent mLastReceivedIntent;

    public PlaceIntentReceiver(String expectedAction) {
        mExpectedAction = expectedAction;
        mLastReceivedIntent = null;
    }
    public PlaceIntentReceiver() {
    }

    public IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter(mExpectedAction);
        return filter;
    }

    /**
     * 받았을 때 호출되는 메소드
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        idArray = new ArrayList();
        if (intent != null) {
            mLastReceivedIntent = intent;

            int id = intent.getIntExtra("id", 0);
            double latitude = intent.getDoubleExtra("latitude", 0.0D);
            double longitude = intent.getDoubleExtra("longitude", 0.0D);
            //String idPlace = idArray.get(id).toString();
            sendToActivity(context, id, latitude, longitude);
        }
    }

    private void sendToActivity(Context context, int id, double latitude, double longitude) {
        // 메시지를 보여줄 액티비티를 띄워줍니다.
        Intent myIntent = new Intent(context, ProximityActivity.class);

        // 플래그를 이용합니다.
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        myIntent.putExtra("latitude", latitude);
        myIntent.putExtra("longitude", longitude);
        myIntent.putExtra("id", id);
        //myIntent.putExtra("idPlace", idPlace);

        context.startActivity(myIntent);
    }

    public Intent getLastReceivedIntent() {
        return mLastReceivedIntent;
    }

    public void clearReceivedIntents() {
        mLastReceivedIntent = null;
    }
}

