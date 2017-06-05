package com.example.jo.pushapp.activity;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.example.jo.pushapp.DbConnection.CouponTask;
import com.example.jo.pushapp.ListView.CouponAdapter2;
import com.example.jo.pushapp.ListView.Coupon_item;
import com.example.jo.pushapp.R;
import com.example.jo.pushapp.firebase.LoginData;
import org.json.JSONException;
import java.util.concurrent.ExecutionException;

public class AllCouponMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private   ListView allListview;
    private  CouponAdapter2 adapter2;
    private  LoginData loginData;
    private   EditText editPlace;
    private   EditText editContents;
    private   FloatingActionButton fab;
    private   View couponRistlayout;
  //  ImageView imageview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.allactivity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//////로그인 정보 받아오기
        Intent intent = getIntent();
        loginData = (LoginData) intent.getSerializableExtra("loginData");
        Toast.makeText(AllCouponMainActivity.this, loginData.getEmail() + loginData.getName() + loginData.getPhotoUrl(), Toast.LENGTH_SHORT).show();

        allListview = (ListView) findViewById(R.id.allListView);
        adapter2 = new CouponAdapter2(this, loginData.getPhotoUrl());
        Snackbar.make(allListview, "로그인 성공", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();


        try {
            String result = new CouponTask().execute(loginData.getEmail(), "", "", "", "", "", "", "", "C_select").get();
           // String result = new Sin_InLoginTask().execute(loginData.getEmail(), "", "C_select").get();
            adapter2.buildArrayList(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        allListview.setAdapter(adapter2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            allListview.setNestedScrollingEnabled(true);
        }

//////////////////////////////dialog
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
          couponRistlayout = inflater.inflate(R.layout.coupon_regist, (ViewGroup) findViewById(R.id.coupon_regist));
        editPlace = ((EditText) couponRistlayout.findViewById(R.id.place));
        editContents = (EditText) couponRistlayout.findViewById(R.id.contents);
        /////////////////////////

         fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                removeView();

                Snackbar.make(view, "쿠폰 등록 클릭하였습니다.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //final EditText editText = new EditText(AllCouponMainActivity.this);
                new AlertDialog.Builder(AllCouponMainActivity.this)
                        .setMessage("쿠폰등록할 내용 입력")
                        .setView(couponRistlayout)
                        .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //디비에 인설뜨 하는걸로
                                String result = null;
                                try {
                                    result = new CouponTask().execute(loginData.getEmail(), "",  editPlace.getText().toString(), "", loginData.getName(), editContents.getText().toString(), "", loginData.getPhotoUrl(), "Cadd").get();
                                    if (result.equals("false")) {
                                        Snackbar.make(view, "다시 작성해주시기 바랍니다.", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    } else if (result.equals("ok")) {
                                        Snackbar.make(view, "쿠폰이 등록 되었습니다.", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        result = new CouponTask().execute(loginData.getEmail(), "", "", "", "", "", "", "", "C_select").get();

                                        adapter2.buildArrayList(result);
                                        adapter2.notifyDataSetChanged();
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        allListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Coupon_item item = (Coupon_item) adapter2.getItem(position);
                new AlertDialog.Builder(AllCouponMainActivity.this)
                        .setMessage(item.getPlace() + "의\n\n쿠폰번호 :" +item.getNum()+"\n\n내용 : "+ item.getContents() + "\n\n쿠폰을 삭제 하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    String result = new CouponTask().execute(item.getId(), "", item.getPlace(), "", "", item.getContents(), "", "", "C_delete").get();
                                    if (result.equals("delete")) {
                                        Snackbar.make(allListview, "쿠폰 삭제되었습니다.", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        result = new CouponTask().execute(loginData.getEmail(), "", "", "", "", "", "", "", "C_select").get();
                                        adapter2.buildArrayList(result);
                                        adapter2.notifyDataSetChanged();
                                    } else if (result.equals("false")) {
                                        Snackbar.make(allListview, "삭제 실패되었습니다..", Snackbar.LENGTH_LONG)
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
                return false;
            }
        });
    }

    private void removeView() {
        editContents.setText("");
        editPlace.setText("");
        if(couponRistlayout.getParent() != null) {
            ((ViewGroup) couponRistlayout.getParent()).removeView(couponRistlayout);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Snackbar.make(allListview, "앱을 종료하시겠습니까?", Snackbar.LENGTH_LONG).setAction("종료", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  finish();
                    System.exit(0);
                }
            }).show();
            //   super.onBackPressed();
        }
    }

    public MenuItem searchItem;
    public SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("검색");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Snackbar.make(allListview, searchView.getQuery().toString() + " : 검색 완료", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                try {
                   // String result = new UseCouponTask().execute("", searchView.getQuery().toString(), "", "", "c_search").get();
                    String result = new CouponTask().execute("", "", searchView.getQuery().toString(), "", "", "", "", "", "c_search").get();
                    adapter2.buildArrayList(result);
                    adapter2.notifyDataSetChanged();
                } catch (Exception e) {
                }
                return false;
            }
        });
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
      //  FragmentManager manager = getFragmentManager();
        if (id == R.id.ALL_Map) {
            getWindow().setExitTransition(new Explode());
            Intent intent = new Intent(AllCouponMainActivity.this, MapActivity.class);
            intent.putExtra("loginData", loginData);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
         //   startActivity(intent);
        } else if (id == R.id.ALL_Gps_register) {
            getWindow().setExitTransition(new Explode());
            // Handle the camera action
            Intent intent = new Intent(AllCouponMainActivity.this, GpsMainActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
           // startActivity(intent);

        } else if (id == R.id.ALL_Search) {
            getWindow().setExitTransition(new Explode());
            Intent intent = new Intent(AllCouponMainActivity.this, Search.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            //startActivity(intent);

        } else if (id == R.id.ALL_list) {
            getWindow().setExitTransition(new Explode());
            Intent intent = new Intent(AllCouponMainActivity.this, Used_list.class);
            intent.putExtra("loginData", loginData);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
           // startActivity(intent);

        } else if (id == R.id.ALL_Chatting) {
            getWindow().setExitTransition(new Explode());
            Intent intent = new Intent(AllCouponMainActivity.this, ChatActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
         //   startActivity(intent);

        } else if (id == R.id.ALL_client) {
            getWindow().setExitTransition(new Explode());
           // manager.beginTransaction().replace(R.id.content_main, new FirstLayout()).commit();
            Intent intent = new Intent(AllCouponMainActivity.this, Client_modeActivity.class);
            intent.putExtra("loginData", loginData);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            //startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
