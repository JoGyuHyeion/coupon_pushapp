package com.example.jo.pushapp.DbConnection;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jo on 2017-04-06.
 */

public class CouponTask extends AsyncTask<String, Void, String> {
    String sendMsg, receiveMsg;

    @Override
    protected String doInBackground(String... strings) {
        try {
            String str;
            URL url = new URL("http://203.247.240.61:8080/android_GPS_DB/data.jsp");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            sendMsg = "id=" + strings[0] + "&pwd=" + strings[1] + "&place=" + strings[2] + "&num=" + strings[3] +"&name=" + strings[4]+
                    "&contents=" + strings[5] + "&expiration=" + strings[6]+"&photourl=" + strings[7] + "&type=" + strings[8];
            //sendMsg = "id=" + strings[0] + "&place=" + strings[1] + "&name=" + strings[2] + "&contents=" + strings[3] +"&photourl=" + strings[4] + "&type=" + strings[5];
            osw.write(sendMsg);
            osw.flush();
            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                receiveMsg = buffer.toString();

            } else {
                Log.i("통신 결과", conn.getResponseCode() + "에러");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receiveMsg;
    }
}
