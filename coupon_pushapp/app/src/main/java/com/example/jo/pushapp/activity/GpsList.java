package com.example.jo.pushapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jo.pushapp.DbConnection.CouponTask;
import com.example.jo.pushapp.DbConnection.GpsTask;
import com.example.jo.pushapp.ListView.GpsAdapter;
import com.example.jo.pushapp.ListView.Gpsdata_Item;
import com.example.jo.pushapp.R;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

/**
 * Created by jo on 2017-05-21.
 */

public class GpsList extends AppCompatActivity {
    private  Toast toast;
    private   ListView listView;
    private  GpsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        toast=Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new GpsAdapter(this);


        try {
            String result = new CouponTask().execute("", "", "", "", "", "", "", "",  "selectAll").get();
            // String result = new GpsTask().execute("", "", "", "", "selectAll").get();
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
                toast.setText("선택 : " + item.getName());
                toast.show();

            }
        });

    }
}
