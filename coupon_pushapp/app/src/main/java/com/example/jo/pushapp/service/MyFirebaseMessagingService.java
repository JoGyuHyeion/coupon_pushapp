package com.example.jo.pushapp.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;


import com.example.jo.pushapp.R;
import com.example.jo.pushapp.activity.ChatActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

//구글 클라우드 서버에서 보내오는 메시지는 이 클래스에서 받을수 있다.
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG ="FCM_MESSAGE";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //http://anhana.tistory.com/6 사이트 참고 해서 앱이 구동중일 대에도 알림바 영역에서 FCM 정보를 노출하고 싶은경우
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Notification Body: " + body);

   /*         NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher) // 알림 영역에 노출 될 아이콘.
                    .setContentTitle(getString(R.string.app_name)) // 알림 영역에 노출 될 타이틀
                    .setContentText(body); // Firebase Console 에서 사용자가 전달한 메시지내용

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify(0x1001, notificationBuilder.build());*/



            Resources resources = getResources();

            Uri soundUri = RingtoneManager.getActualDefaultRingtoneUri(this,RingtoneManager.TYPE_NOTIFICATION);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,ChatActivity.class),0);
          //  PendingIntent pendingIntent = PendingIntent.getActivity(this,0,new Intent(),0);
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(resources.getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher) // 알림 영역에 노출 될 아이콘.
                    .setContentTitle(getString(R.string.app_name)) // 알림 영역에 노출 될 타이틀
                    .setContentText(body) // Firebase Console 에서 사용자가 전달한 메시지내용
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
        //여기까지가 사이트 참고 부분임
        //푸시 메시지를 받았을 때 그 내용 확인후 액티비티 쪽으로 보내는 메소드 호출
        //==구글 클라우드 서버에서 보내오는 메시지를 받는 메소드

    }
}
