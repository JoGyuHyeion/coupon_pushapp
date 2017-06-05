package com.example.jo.pushapp.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jo.pushapp.DbConnection.CouponTask;
import com.example.jo.pushapp.R;
import com.example.jo.pushapp.firebase.*;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.Random;

/**
 * Created by jo on 2017-05-21.
 */

public class GoogleLogin extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 1001;

    // Firebase - Authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;

    private SignInButton mBtnGoogleSignIn; // 로그인 버튼
    private Button mBtnGoogleSignOut; // 로그아웃 버튼
    private TextView mTxtProfileInfo; // 사용자 정보 표시
    private ImageView mImgProfile; // 사용자 프로필 이미지 표시

    private String userName;

    // UI references.


    private   LinearLayout layout;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_login);
        initViews();
        initFirebaseAuth();
        initValues();

        layout = (LinearLayout) findViewById(R.id.login_inflater);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        Button loginButton = (Button) findViewById(R.id.login_button);
        Button SignInButton = (Button) findViewById(R.id.sign_in_button);
        loginButton.setOnClickListener(btnListener);
        SignInButton.setOnClickListener(btnListener);

    }

    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.login_button: // 로그인 버튼 눌렀을 경우

                    String loginid = mEmailView.getText().toString();
                    String loginpwd = mPasswordView.getText().toString();
                    if (loginpwd.equals("")) {
                     //   Toast.makeText(GoogleLogin.this, "password를 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
                        Snackbar.make(layout, "password를 입력하지 않았습니다.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        try {

                            String result = new CouponTask().execute(loginid, loginpwd, "", "", "", "", "", "",  "login").get();
                            // String result = new Sin_InLoginTask().execute(loginid, loginpwd, "login").get();

                            if (result.equals("3")) {//루트권한 실행

                                Intent intent = new Intent(GoogleLogin.this, AllCouponMainActivity.class);
                                //로그인 정보 전송
                                FirebaseUser user = mAuth.getCurrentUser();
                                LoginData loginData=new LoginData(user.getEmail(),user.getDisplayName(),loginpwd,String.valueOf(user.getPhotoUrl()));
                                intent.putExtra("loginData",loginData);

                                startActivity(intent);

                                finish();
                            }else if(result.equals("2")){//판매자용

                                Intent intent = new Intent(GoogleLogin.this, UserCouponMainActivity.class);
                                //로그인 정보 전송
                                FirebaseUser user = mAuth.getCurrentUser();
                                LoginData loginData=new LoginData(user.getEmail(),user.getDisplayName(),loginpwd,String.valueOf(user.getPhotoUrl()));
                                intent.putExtra("loginData",loginData);

                                startActivity(intent);

                                finish();
                            } else if(result.equals("1")){//사용자용

                                Intent intent = new Intent(GoogleLogin.this, ClientCouponMainActivity.class);
                                //로그인 정보 전송
                                FirebaseUser user = mAuth.getCurrentUser();
                                LoginData loginData=new LoginData(user.getEmail(),user.getDisplayName(),loginpwd,String.valueOf(user.getPhotoUrl()));
                                intent.putExtra("loginData",loginData);

                                startActivity(intent);

                                finish();
                            } else if (result.equals("false")) {
                                Snackbar.make(layout, "아이디 또는 비밀번호가 틀렸음", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                mPasswordView.setText("");
                            } else if (result.equals("noId")) {
                                Snackbar.make(layout, "존재하지 않는 아이디", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                mPasswordView.setText("");
                            }
                        } catch (Exception e) {
                        }
                    }
                    break;
                case R.id.sign_in_button: // 회원가입
                    String joinid = mEmailView.getText().toString();
                    String joinpwd = mPasswordView.getText().toString();
                    try {
                        String result = new CouponTask().execute(joinid, joinpwd, "", "", "", "", "", "",  "join").get();
                        // String result = new Sin_InLoginTask().execute(joinid, joinpwd, "join").get();
                        if (result.equals("id")) {
                            Snackbar.make(layout, "이미 존재하는 아이디입니다.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            //Toast.makeText(GoogleLogin.this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                            mPasswordView.setText("");
                        } else if (result.equals("ok")) {
                            mPasswordView.setText("");
                            Snackbar.make(layout, "회원가입을 축하합니다.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                           // Toast.makeText(GoogleLogin.this, "회원가입을 축하합니다.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                    }
                    break;
            }
        }
    };


    //////////////////////////////////////////////구글 로그인 시작 부분
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_google_signin1:
                signIn();
                showProgress(true);
                break;
            case R.id.btn_google_signout1:
                signOut();
                break;
        }
    }

    private void initFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateProfile();
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // 비 로그인 상태 (메시지를 전송할 수 없다.)
            mBtnGoogleSignIn.setVisibility(View.VISIBLE);
            mBtnGoogleSignOut.setVisibility(View.GONE);
            mTxtProfileInfo.setVisibility(View.GONE);
            mImgProfile.setVisibility(View.GONE);

        } else {
            // 로그인 상태
            mBtnGoogleSignIn.setVisibility(View.GONE);
            mBtnGoogleSignOut.setVisibility(View.VISIBLE);
            mTxtProfileInfo.setVisibility(View.VISIBLE);
            mImgProfile.setVisibility(View.VISIBLE);

            userName = user.getDisplayName(); // 채팅에 사용 될 닉네임 설정
            String email = user.getEmail();
            StringBuilder profile = new StringBuilder();
            profile.append(userName).append("\n").append(user.getEmail());
            mTxtProfileInfo.setText(profile);

            UserData userData = new UserData();
            userData.userEmailID = email.substring(0, email.indexOf('@'));
            userData.fcmToken = FirebaseInstanceId.getInstance().getToken();
            Picasso.with(this).load(user.getPhotoUrl()).into(mImgProfile);
            layout.setVisibility(View.VISIBLE);
            mEmailView.setText(email);
            mEmailView.setInputType(0x00000000);
            showProgress(false);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initViews() {
        mBtnGoogleSignIn = (SignInButton) findViewById(R.id.btn_google_signin1);
        mBtnGoogleSignOut = (Button) findViewById(R.id.btn_google_signout1);
        mBtnGoogleSignIn.setOnClickListener(this);
        mBtnGoogleSignOut.setOnClickListener(this);

        mTxtProfileInfo = (TextView) findViewById(R.id.txt_profile_info1);
        mImgProfile = (ImageView) findViewById(R.id.img_profile1);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    private void signOut() {
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateProfile();
                    }
                });
    }

    private void initValues() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            userName = "Guest" + new Random().nextInt(5000);
        } else {
            userName = user.getDisplayName();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(GoogleLogin.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                updateProfile();
            }
        }
        layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

//////////////////////////////////////////////구글 로그인 마지막 부분///////////////////////


    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}