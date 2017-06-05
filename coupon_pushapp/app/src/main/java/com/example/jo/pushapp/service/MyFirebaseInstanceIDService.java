package com.example.jo.pushapp.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

//클라우드 서버에 단말을 등록하는 역할을 하는 클래스
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG ="FCM_ID";

    @Override
    public void onTokenRefresh( ){
        //단믈의 등록ID를 전달받으면 onToenRefresh()메소드가 호출된다.
        //재정의하여 전달받은 등록 ID를 확인할수 있다.
        Log.d(TAG,"onTokenRefresh() 호출됨");

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"FirebaseInstanceId Refreshed token: " + refreshedToken);
    }
}
