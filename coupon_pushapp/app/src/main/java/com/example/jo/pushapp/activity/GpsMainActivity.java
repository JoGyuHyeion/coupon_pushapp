package com.example.jo.pushapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import com.example.jo.pushapp.DbConnection.GpsTask;
import com.example.jo.pushapp.ListView.GpsAdapter;
import com.example.jo.pushapp.ListView.Gpsdata_Item;
import com.example.jo.pushapp.Location.GPS_Service;
import com.example.jo.pushapp.Location.LocationPermission;
import com.example.jo.pushapp.R;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class GpsMainActivity extends AppCompatActivity {
    private Toast toast;
    private  EditText latitude, longitude, name;
    private  Button addBtn, nameSearchBtn, deleteBtn, selectAll, location;
    private  NumberPicker radius;

    private TextView settingRadius;

    private  ListView listView;
    private  GpsAdapter adapter;

    //키보드 내리기
    private  InputMethodManager imm;
    private  LocationPermission locationPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_main);

        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        locationPermission = new LocationPermission(this);
        locationPermission.checkDangerousPermissions();

        latitude = (EditText) findViewById(R.id.latitude);
        longitude = (EditText) findViewById(R.id.longitude);
        name = (EditText) findViewById(R.id.name);

        addBtn = (Button) findViewById(R.id.addBtn);
        nameSearchBtn = (Button) findViewById(R.id.nameSearchBtn);
        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        selectAll = (Button) findViewById(R.id.selectAll);
        location = (Button) findViewById(R.id.location);

        addBtn.setOnClickListener(btnListener);
        nameSearchBtn.setOnClickListener(btnListener);
        deleteBtn.setOnClickListener(btnListener);
        selectAll.setOnClickListener(btnListener);
        location.setOnClickListener(btnListener);

        radius = (NumberPicker) findViewById(R.id.radius);
        radius.setMaxValue(100);
        radius.setMinValue(0);
        settingRadius = (TextView) findViewById(R.id.settingRadius);

        listView = (ListView) findViewById(R.id.gps_listView);
        adapter = new GpsAdapter(this);

        Intent passedIntent = getIntent();
        processIntent(passedIntent);

        try {
            String result = new GpsTask().execute("", "", "", "", "selectAll").get();
            adapter.buildArrayList(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Gpsdata_Item item = (Gpsdata_Item) adapter.getItem(position);

                latitude.setText(String.valueOf(item.getLatitude()));
                longitude.setText(String.valueOf(item.getLongitude()));
                name.setText(String.valueOf(item.getName()));

                toast.setText("선택 : " + item.getName());
                toast.show();

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Gpsdata_Item item = (Gpsdata_Item) adapter.getItem(position);
                String delLatitude = String.valueOf(item.getLatitude());
                String delLongitude = String.valueOf(item.getLongitude());
                String delRadius = String.valueOf(item.getRadius());
                String delname = String.valueOf(item.getName());

                try {
                    String result = new GpsTask().execute(delLatitude, delLongitude, delRadius, delname, "delete").get();
                    if (result.equals("delete")) {
                        adapter.remove(position);
                        adapter.notifyDataSetChanged();
                        toast.setText("삭제 되었습니다.");
                        toast.show();
                        //Toast.makeText(GpsMainActivity.this, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                    } else if (result.equals("noData")) {
                        toast.setText("위도와 경도 확인바랍니다.");
                        toast.show();
                        // Toast.makeText(GpsMainActivity.this, "위도와 경도 확인바랍니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }
                return false;
            }
        });
    }

    public void linearOnclick(View v) {  //키보드 내리기 리스너
        imm.hideSoftInputFromWindow(latitude.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(longitude.getWindowToken(), 0);
    }

    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.addBtn: //DB추가 버튼튼
                    String addLatitude = latitude.getText().toString();
                    String addLongitude = longitude.getText().toString();
                    String addRadius = String.valueOf(radius.getValue());
                    String addname = name.getText().toString();
                    String add_mesg;
                    String id_num = String.valueOf(adapter.getCount() + 1);
                    try {
                        String result = new GpsTask().execute(addLatitude, addLongitude, addRadius, addname, "add").get();
                        if (result.equals("exist")) {
                            toast.setText("이미 존재하는 위도 경도 입니다..");
                            toast.show();

                            latitude.setText("");
                            longitude.setText("");
                            name.setText("");
                        } else if (result.equals("ok")) {
                            latitude.setText("");
                            longitude.setText("");
                            name.setText("");
                            add_mesg = adapter.addItem(new Gpsdata_Item(addLatitude, addLongitude, addRadius, addname, id_num, R.drawable.ic_android));
                            adapter.notifyDataSetChanged();
                            toast.setText("좌표가 추가 되었습니다..");
                            toast.show();
                        }
                    } catch (Exception e) {
                    }
                    break;

                case R.id.nameSearchBtn: // 매칭 버튼 눌렀을 경우
                    Intent intent = new Intent(GpsMainActivity.this, Search.class);
                    startActivity(intent);
                    break;

                case R.id.deleteBtn: // 삭제
                    String delLatitude = latitude.getText().toString();
                    String delLongitude = longitude.getText().toString();
                    String delRadius = String.valueOf(radius.getValue());
                    String delhname = name.getText().toString();

                    try {
                        String result = new GpsTask().execute(delLatitude, delLongitude, delRadius, delhname, "delete").get();
                        if (result.equals("delete")) {
                            toast.setText("삭제 되었습니다.");
                            toast.show();
                            adapter.remove(Double.parseDouble(delLatitude), Double.parseDouble(delLongitude));
                            adapter.notifyDataSetChanged();
                        } else if (result.equals("noData")) {
                            toast.setText("좌표 를 확인바랍니다.");
                            toast.show();
                        }
                    } catch (Exception e) {
                    }
                    break;

                case R.id.selectAll:
                    try {
                        String result = new GpsTask().execute("", "", "", "", "selectAll").get();
                        adapter.buildArrayList(result);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                    }
                    break;
                case R.id.location:
                    Intent Gps_intent = new Intent(getApplicationContext(), GPS_Service.class);
                    startService(Gps_intent);
                    break;
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);
        super.onNewIntent(intent);
    }

    private void processIntent(Intent intent) {
        if (intent != null) {
            String Tex_latitude = intent.getStringExtra("latitude");
            String Tex_longitude = intent.getStringExtra("longitude");
            latitude.setText(Tex_latitude);
            longitude.setText(Tex_longitude);
        }
    }
}