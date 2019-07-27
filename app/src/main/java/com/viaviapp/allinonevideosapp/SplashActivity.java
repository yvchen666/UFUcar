package com.viaviapp.allinonevideosapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import static com.viaviapp.allinonevideosapp.SignUpActivity.getIMEI;

public class SplashActivity extends Activity {

    private String TAG = "SplashActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        final String account = preferences.getString("account", "");
        final String password = preferences.getString("password","");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String imei = getIMEI(SplashActivity.this);
                Log.d(TAG, "run: imei:" + imei);
                String result = new HttpUtils().sendPost("http://yc.mmajd.com/system/?type=login",
                        "user=" + account + "&pass=" + password + "&imei=" + imei);
                Log.d(TAG, "onClick: result  " + result);
                if (result.contains("登录成功")){
                    String response = new HttpUtils().sendGet("http://yc.mmajd.com/system/?type=data&id="+account);
                    if (response.contains("没有授权"));
                    SplashActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SplashActivity.this,"您的会员已过期，请及时续费",Toast.LENGTH_SHORT);
                        }
                    });
                }
            }
        }).start();
        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {

                    int waited = 0;
                    while (waited < 2000) {
                        sleep(100);
                        waited += 100;
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();

                }
            }
        };
        splashThread.start();
    }

}