package com.example.jo.pushapp.activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.jo.pushapp.DbConnection.CouponTask;
import com.example.jo.pushapp.DbConnection.GpsTask;
import com.example.jo.pushapp.DbConnection.JsonData;
import com.example.jo.pushapp.ListView.CouponAdapter2;
import com.example.jo.pushapp.ListView.Coupon_item;
import com.example.jo.pushapp.Location.LocationPermission;
import com.example.jo.pushapp.R;
import com.example.jo.pushapp.firebase.LoginData;
import com.example.jo.pushapp.model.Gps_model;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    protected LocationManager locationManager;
    public static Context context;


    // 현재 위치 버튼
    private Button btnShowLocation3;

    // 위치 기록 텍스트
    private EditText editText3;

    // 파일 생성
    private File file = new File(Environment.getExternalStorageDirectory() + "/Download");
    private StringBuffer sb_title = new StringBuffer();
    private StringBuffer sb = new StringBuffer();

    // GpsCheck 클래스
    private GpsCheck gps3 = null;
    private GoogleMap mobileMap2;

    public static Handler mHandler;

    public static int RENEW_GPS = 1;
    public static int SEND_PRINT = 2;
    /////////////////////////////////규현쓰
    private LoginData loginData;
    private ArrayList<Gps_model> gpsArray;
    private Toast toast;
    private PopupWindow pop;
    private CouponAdapter2 adapter2;
    private ListView popListView;
    private View popWindow;

    //근접 확인
    public static boolean isProximity = false;
    int width, height;
    String checkSame_Place = "out";
    boolean checkarray = false;
    double proxi_la = 0.0, proxi_lo = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        ///////////////////////////////규현쓰 gps data
        Intent intent = getIntent();
        loginData = (LoginData) intent.getSerializableExtra("loginData");
        gpsArray = new ArrayList<Gps_model>();
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);

        ////////////////////////////////////디스플레이구하기
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        width = dm.widthPixels;
        height = dm.heightPixels;


        // activity_map 레이아웃 호출
        setContentView(R.layout.map_activity);

        // BitmapDescriptorFactory 생성하기 위한 소스
        MapsInitializer.initialize(getApplicationContext());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map3);
        mapFragment.getMapAsync(this);

        // 위치 관리자 객체 참조
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        ////////////DB로 부터 gps 데이터 가져오기
        try {
            String result = new GpsTask().execute("", "", "", "", "selectAll").get();
            JsonData jsonGpsData = new JsonData();
            gpsArray = jsonGpsData.getGpsArrayList(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //////////////////////////////////////////PopWindow
        popWindow = this.getLayoutInflater().inflate(R.layout.list, null);
        popListView = (ListView) popWindow.findViewById(R.id.listView);
        adapter2 = new CouponAdapter2(this, loginData.getPhotoUrl());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popListView.setNestedScrollingEnabled(true);
        }
        //pop = new PopupWindow(popWindow, width - width / 3, 500);

        /////////////////////////////////////////////////PopWindow
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
        popListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Coupon_item item = (Coupon_item) adapter2.getItem(position);
                new AlertDialog.Builder(MapActivity.this)
                        .setMessage(item.getPlace() + "의\n\n쿠폰번호 :" + item.getNum() + "\n\n내용 : " + item.getContents() + "\n\n쿠폰을 사용 하시겠습니까?")
                        .setPositiveButton("사용", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Log.d("쿠폰 번호확인", item.getNum());
                                    String result = new CouponTask().execute(loginData.getEmail(), "", item.getPlace(), item.getNum(), "", "", item.getExpireation(), "", "C_use").get();
                                    if (result.equals("ok")) {
                                        Snackbar.make(popListView, "쿠폰 사용 되었습니다.", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        result = new CouponTask().execute("", "", checkSame_Place, "", "", "", "", "", "c_search").get();
                                        adapter2.buildArrayList(result);
                                        adapter2.notifyDataSetChanged();
                                    } else if (result.equals("false")) {
                                        Snackbar.make(popListView, "사용 불가능한 쿠폰입니다.", Snackbar.LENGTH_LONG)
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

        // 파일 생성
        if (!file.exists()) {
            file.mkdir();
        }

        file = new File(Environment.getExternalStorageDirectory() + "/Download/Mobile_values2.csv");

        editText3 = (EditText) findViewById(R.id.editText3);
        editText3.setMovementMethod(new ScrollingMovementMethod());
        editText3.setTextSize(15);

        btnShowLocation3 = (Button) findViewById(R.id.btnShowLocation3);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == RENEW_GPS) {
                    makeNewGpsService();
                }
                if (msg.what == SEND_PRINT) {
                    logPrint((String) msg.obj);
                }
            }
        };

        //클래스 객체 생성
        if (gps3 == null) {
            gps3 = new GpsCheck(MapActivity.this, mHandler);
        } else {
            gps3.Update();
        }

        // GPS 가 사용가능한지 확인
        if (gps3.isGetLocation()) {
            gps3.Update();
        } else {
            // GPS 또는 네트워크가 사용가능하지 않아 위치를 구할 수 없음
            // 사용자에게 GPS/네트워크 설정 활성화 경고
            gps3.showSettingsAlert();
        }

        // 위치 버튼 클릭 이벤트
        btnShowLocation3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // GPS 가 사용가능한지 확인
                if (gps3.isGetLocation()) {
                    gps3.Update();
                    double latitude = Double.parseDouble(String.format("%.6f", gps3.getLatitude()));
                    double longitude = Double.parseDouble(String.format("%.6f", gps3.getLongitude()));

                    // 현재 위치를 위한 LatLng 객체 생성
                    LatLng latLng = new LatLng(latitude, longitude);

                    //현재 위치로 구글맵 맞추기
                    mobileMap2.moveCamera(CameraUpdateFactory.zoomTo(19));
                    mobileMap2.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    // 현재 위치 마커 설정
                    MarkerOptions optFirst = new MarkerOptions();
                    optFirst.position(latLng);
                    optFirst.title("(버튼)현재 위치");
                    optFirst.snippet("사용자");
                    optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_now));
                    mobileMap2.addMarker(optFirst).showInfoWindow();

                    //실시간 위치 바뀔 때마다 바뀐 좌표와 등록된 장소 좌표 비교 함수 호출
                    //   compareGPS();
                    for (int i = 0; i < gpsArray.size(); i++) {
                        if (isProximity == false) {
                            //움직이면 다시 비교 한다 하지만 움직이기 이젆에 gpsarry이의 값과
                            if (!gpsArray.get(i).getName().equals(checkSame_Place)) //   if (!gpsArray.get(i).getName().equals(checkSame_Place)) 같은 지점은 다시 울림 방지
                                compareGPS_jo(gpsArray.get(i).getLatitude(), gpsArray.get(i).getLongitude(), gpsArray.get(i).getName());
                        }
                    }
                    compareGps_bound(proxi_la, proxi_lo);
                } else {
                    // GPS 또는 네트워크가 사용가능하지 않아 위치를 구할 수 없음
                    // 사용자에게 GPS/네트워크 설정 활성화 경고
                    gps3.showSettingsAlert();
                }
            }
        });
    }

    public void drawMarker() {
        // GPS 가 사용가능한지 확인
        if (gps3.isGetLocation()) {
            gps3.Update();
            double latitude = Double.parseDouble(String.format("%.6f", gps3.getLatitude()));
            double longitude = Double.parseDouble(String.format("%.6f", gps3.getLongitude()));

            // 현재 위치를 위한 LatLng 객체 생성
            LatLng latLng = new LatLng(latitude, longitude);

            //현재 위치로 구글맵 맞추기
            mobileMap2.moveCamera(CameraUpdateFactory.zoomTo(19));
            mobileMap2.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // 현재 위치 마커 설정
            MarkerOptions optFirst = new MarkerOptions();
            optFirst.position(latLng);
            optFirst.title("현재 위치");
            optFirst.snippet("사용자");
            optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name));
            mobileMap2.addMarker(optFirst).showInfoWindow();

            //실시간 위치 바뀔 때마다 바뀐 좌표와 등록된 장소 좌표 비교 함수 호출
            //compareGPS();
            for (int i = 0; i < gpsArray.size(); i++) {
                if (isProximity == false) {
                    //움직이면 다시 비교 한다 하지만 움직이기 이젆에 gpsarry이의 값과
                    if (!gpsArray.get(i).getName().equals(checkSame_Place)) //   if (!gpsArray.get(i).getName().equals(checkSame_Place)) 같은 지점은 다시 울림 방지
                        compareGPS_jo(gpsArray.get(i).getLatitude(), gpsArray.get(i).getLongitude(), gpsArray.get(i).getName());
                }
            }
            compareGps_bound(proxi_la, proxi_lo);
        } else {
            // GPS 또는 네트워크가 사용가능하지 않아 위치를 구할 수 없음
            // 사용자에게 GPS/네트워크 설정 활성화 경고
            gps3.showSettingsAlert();
        }
        isProximity = false;
    }

    private void compareGps_bound(double lat, double lon) {
        double mobile_lat = Double.parseDouble(String.format("%.6f", gps3.getLatitude()));
        double mobile_long = Double.parseDouble(String.format("%.6f", gps3.getLongitude()));
        int distance_radius = 10; //미터 단위
        if (distance1(mobile_lat, mobile_long, lat, lon) * 1000 <= distance_radius) {

        } else {
            if (pop != null && pop.isShowing()) {
                pop.dismiss();
                checkSame_Place = "out";
            }
        }
    }

    private void compareGPS_jo(double lat, double lon, String place) {

        double mobile_lat = Double.parseDouble(String.format("%.6f", gps3.getLatitude()));
        double mobile_long = Double.parseDouble(String.format("%.6f", gps3.getLongitude()));
        int distance_radius = 10; //미터 단위

        if (distance1(mobile_lat, mobile_long, lat, lon) * 1000 <= distance_radius) {
            removeView();
            isProximity = true;
            if (checkarray == false) {
                checkarray = true;
            }
            checkSame_Place = place;
            proxi_la = lat;
            proxi_lo = lon;
            Toast.makeText(this, place + " 근접 - \n위도: " + lat + "\n경도: " + lon, Toast.LENGTH_LONG).show();

            try {
                String result = new CouponTask().execute("", "", place, "", "", "", "", "", "c_search").get();
                adapter2.buildArrayList(result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            popListView.setAdapter(adapter2);

            //push start
            Resources resources = getResources();
            Uri soundUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION);
            Intent P_intent = new Intent(MapActivity.this, FindCouponActivity.class);
            P_intent.putExtra("place", place);
            P_intent.putExtra("loginData", loginData);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, P_intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(resources.getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher) // 알림 영역에 노출 될 아이콘.
                    .setContentTitle(getString(R.string.app_name)) // 알림 영역에 노출 될 타이틀
                    .setContentText(place) // Firebase Console 에서 사용자가 전달한 메시지내용
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)//눌렀을때  통지함에서 삭제
                    .setSound(soundUri)
                    .setVibrate(new long[]{0, 1000})
                    .build();
            //------------------------------------------------------------------
            // Notify
            NotificationManagerCompat notiManager = NotificationManagerCompat.from(this);
            notiManager.notify(0, notification);
//push end

            pop = new PopupWindow(popWindow, width - width / 3, 500);
            pop.showAtLocation(findViewById(R.id.activity_mobile2), Gravity.TOP, 0, 0);

        } else if (pop != null && pop.isShowing()) {

            //  pop.dismiss();
        }
    }

    private void removeView() {
        if (popWindow.getParent() != null) {

            ((ViewGroup) popWindow.getParent()).removeView(popWindow);
        }
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double distance1(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }

    public void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(MapActivity.this)
                .setMessage("홈키를 누르시면 push 서비스를 받을수 있습니다.")
                .setPositiveButton("메뉴로 돌아가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {// not thing..

                    }
                }).show();
    }

    private void scrollBottom(EditText editText) {
        int lineTop = editText.getLayout().getLineTop(editText.getLineCount());
        int scrollY = lineTop - editText.getHeight();

        if (scrollY > 0) {
            editText.scrollTo(0, scrollY);
        } else {
            editText.scrollTo(0, 0);
        }
    }

    public void makeNewGpsService() {
        gps3.Update();
    }

    /**
     * 로그 출력
     */
    public void logPrint(String str) {
        sb_title = new StringBuffer();
        sb = new StringBuffer();
        String date = "날짜";
        String stat = "상태";
        String prov = "제공자";
        String prox = "근접";
        String la = "위도";
        String lo = "경도";

        editText3.append(getTimeStr() + " " + str + "\n");
        scrollBottom(editText3);

        try {
            OutputStreamWriter print;
            print = new OutputStreamWriter(new FileOutputStream(file, true), "euc-kr");

            if (!file.exists()) {
                sb_title.append(date).append(",");
                sb_title.append(stat).append(",");
                sb_title.append(prov).append(",");
                sb_title.append(prox).append(",");
                sb_title.append(la).append(",");
                sb_title.append(lo).append(",");
                sb_title.append("\n");

                String st = sb_title.toString();
                print.write(st);
                print.close();

                sb.append(getTimeStr()).append(",");
                sb.append(str).append(",");
                sb.append("\n");

                String s = sb.toString();
                print.write(s);
                print.close();
            } else {
                sb.append(getTimeStr()).append(",");
                sb.append(str).append(",");
                sb.append("\n");

                String s = sb.toString();
                print.write(s);
                print.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 시간 설정
     */
    public String getTimeStr() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfNow = new SimpleDateFormat("MM월 dd일 HH시 mm분 ss초");
        return sdfNow.format(date);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mobileMap2 = googleMap;

        // 교촌마을 중앙에 구글맵 맞추기
        LatLng mMapCenter = new LatLng(35.829863, 129.2162315);
        mobileMap2.moveCamera(CameraUpdateFactory.zoomTo(17));
        mobileMap2.moveCamera(CameraUpdateFactory.newLatLng(mMapCenter));

        // Add a marker in A1,A2,B1,B2,C1,C2,D1,D2
        LatLng A1 = new LatLng(35.829352, 129.214637);
        mobileMap2.addMarker(new MarkerOptions().position(A1).title("A1 마커"));
        //mobileMap.moveCamera(CameraUpdateFactory.newLatLng(A1));
        LatLng A2 = new LatLng(35.829272, 129.214653);
        mobileMap2.addMarker(new MarkerOptions().position(A2).title("A2 마커"));
        //mobileMap.moveCamera(CameraUpdateFactory.newLatLng(A2));

        LatLng B1 = new LatLng(35.829702, 129.217623);
        mobileMap2.addMarker(new MarkerOptions().position(B1).title("B1 마커"));
        //mobileMap.moveCamera(CameraUpdateFactory.newLatLng(B1));
        LatLng B2 = new LatLng(35.829859, 129.21781);
        mobileMap2.addMarker(new MarkerOptions().position(B2).title("B2 마커"));
        //mobileMap.moveCamera(CameraUpdateFactory.newLatLng(B2));

        LatLng C1 = new LatLng(35.831094, 129.217962);
        mobileMap2.addMarker(new MarkerOptions().position(C1).title("C1 마커"));
        //mobileMap.moveCamera(CameraUpdateFactory.newLatLng(C1));
        LatLng C2 = new LatLng(35.831137, 129.217908);
        mobileMap2.addMarker(new MarkerOptions().position(C2).title("C2 마커"));
        //mobileMap.moveCamera(CameraUpdateFactory.newLatLng(C2));

        LatLng D1 = new LatLng(35.830454, 129.214379);
        mobileMap2.addMarker(new MarkerOptions().position(D1).title("D1 마커"));
        //mobileMap.moveCamera(CameraUpdateFactory.newLatLng(D1));
        LatLng D2 = new LatLng(35.83042, 129.2144706);
        mobileMap2.addMarker(new MarkerOptions().position(D2).title("D2 마커"));
        //mobileMap.moveCamera(CameraUpdateFactory.newLatLng(D2));

        // Add a marker in 구글장소(1~11)
        LatLng P1 = new LatLng(35.829545, 129.215163);
        mobileMap2.addMarker(new MarkerOptions().position(P1).title("01. 가비").snippet("카페").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cafe)));
        LatLng P2 = new LatLng(35.829557, 129.215324);
        mobileMap2.addMarker(new MarkerOptions().position(P2).title("02. 최가밥상").snippet("레스토랑").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant)));
        LatLng P3 = new LatLng(35.829816, 129.216903);
        mobileMap2.addMarker(new MarkerOptions().position(P3).title("03. 카페사바하").snippet("카페").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cafe)));
        LatLng P4 = new LatLng(35.829915, 129.214967);
        mobileMap2.addMarker(new MarkerOptions().position(P4).title("04. 유리공방").snippet("체험장").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_store)));
        LatLng P5 = new LatLng(35.830095, 129.215501);
        mobileMap2.addMarker(new MarkerOptions().position(P5).title("05. 한정식명가").snippet("레스토랑").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant)));
        LatLng P6 = new LatLng(35.830159, 129.215904);
        mobileMap2.addMarker(new MarkerOptions().position(P6).title("06. 교동실크로드").snippet("상가").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_store)));
        LatLng P7 = new LatLng(35.830034, 129.216597);
        mobileMap2.addMarker(new MarkerOptions().position(P7).title("07. 요석궁").snippet("레스토랑").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant)));
        LatLng P8 = new LatLng(35.830089, 129.215048);
        mobileMap2.addMarker(new MarkerOptions().position(P8).title("08. 동경이").snippet("전시장").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dog)));
        LatLng P9 = new LatLng(35.830512, 129.216613);
        mobileMap2.addMarker(new MarkerOptions().position(P9).title("09. 교리김밥").snippet("분식집").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant)));
        LatLng P10 = new LatLng(35.830267, 129.214842);
        mobileMap2.addMarker(new MarkerOptions().position(P10).title("10. 토기").snippet("체험장").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_store)));
        LatLng P11 = new LatLng(35.830714, 129.216146);
        mobileMap2.addMarker(new MarkerOptions().position(P11).title("11. 교동법주").snippet("상가").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bar)));

        ////////////////////////////////////규현 DB 추가////////////////////////////////////////////////
        LatLng jo[] = new LatLng[gpsArray.size()];
        for (int i = 0; i < gpsArray.size(); i++) {
            jo[i] = new LatLng(gpsArray.get(i).getLatitude(), gpsArray.get(i).getLongitude());
            mobileMap2.addMarker(new MarkerOptions().position(jo[i]).title(gpsArray.get(i).getName()).snippet("상가").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_store)));
        }

        // GPS 가 사용가능한지 확인
        if (gps3.isGetLocation()) {
            gps3.Update();
            double latitude = Double.parseDouble(String.format("%.6f", gps3.getLatitude()));
            double longitude = Double.parseDouble(String.format("%.6f", gps3.getLongitude()));

            // 처음 위치를 위한 LatLng 객체 생성
            LatLng latLng = new LatLng(latitude, longitude);

            // 현재 위치 마커 설정
            MarkerOptions optFirst = new MarkerOptions();
            optFirst.position(latLng);
            optFirst.title("처음 위치");
            optFirst.snippet("사용자");
            optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_button));
            mobileMap2.addMarker(optFirst).showInfoWindow();

            Toast.makeText(getApplicationContext(), "당신의 처음 위치 - \n위도: " + latitude + "\n경도: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // GPS 또는 네트워크가 사용가능하지 않아 위치를 구할 수 없음
            // 사용자에게 GPS/네트워크 설정 활성화 경고
            gps3.showSettingsAlert();
        }
    }
}


