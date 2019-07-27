package com.viaviapp.allinonevideosapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bug.getpost.BugHttpClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignInActivity extends AppCompatActivity {

    public static void start(Activity activity, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setExitTransition(null);
            activity.getWindow().setEnterTransition(null);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, view, view.getTransitionName());
            activity.startActivity(new Intent(activity, SignInActivity.class), options.toBundle());
        }
    }
    private FloatingActionButton mDoClose;
    private CardView mCardView;
    private String TAG = "yc";
    private EditText username;
    private EditText password1;
    private EditText password2;
    private Button signin;

    public static final int success = 1;
    public static final int failed = 2;
    public static final int elsess = 3;
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case success:
                    Toast.makeText(SignInActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
                    break;
                case failed:
                    Toast.makeText(SignInActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                    break;
                case elsess:
                    Toast.makeText(SignInActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resiger);
        ShowEnterAnimation();
        BugHttpClient bug = new BugHttpClient();
        bug.setCheck_Capture(true);
        mCardView = findViewById(R.id.cv_add);
        username = findViewById(R.id.et_username);
        password1 = findViewById(R.id.et_password);
        password2 = findViewById(R.id.et_repeatpassword);
        signin = findViewById(R.id.bt_go);
        mDoClose = findViewById(R.id.fab);
        mDoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });
        signin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View p1) {
                String use = username.getText().toString();
                String ps1 = password1.getText().toString();
                String ps2 = password2.getText().toString();
                if (use.length() <= 5 | ps1.length() <= 5 | ps2.length() <= 5) {
                    Toast.makeText(SignInActivity.this, "账号或密码长度不能小于五位", Toast.LENGTH_SHORT).show();
                } else if (use.length() >= 15 | ps1.length() >= 15 | ps2.length() >= 15) {
                    Toast.makeText(SignInActivity.this, "账号或密码长度不能多于15位", Toast.LENGTH_SHORT).show();
                } else if (ps1.equals(ps2) == false) {
                    Toast.makeText(SignInActivity.this, "两次密码输入不一致，请核对", Toast.LENGTH_SHORT).show();
                } else {
                    String imei = getIMEI(SignInActivity.this);
                    sendPost("http://yc.mmajd.com/system/?type=reg", "user=" + use + "&pass=" + ps1 + "&imei=" + imei);

                }
            }
        });
//        dialogg.setOnClickListener(new OnClickListener(){
//            private TextView ok;
//
//            @Override
//            public void onClick(View p1)
//            {
//                AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
//                View view = View
//                        .inflate(getActivity(), R.layout.dialog, null);
//                builder.setView(view);
//                builder.setCancelable(true);
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//            private Context getActivity()
//            {
//
//                return SignInActivity.this;
//            }
//        });
    }


    public static final String getIMEI(Context context) {
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMEI号
            @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();
            //在次做个验证，也不是什么时候都能获取到的啊
            if (imei == null) {
                imei = "";
            }
            return imei;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    private void sendGet(final String url1) {
        String showresponse = "";
        // TODO: Implement this method

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(url1);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    show(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }

    private void show(String rep) {
        Log.d(TAG, "show: " + rep);
        if (rep.contains("注册成功")) {
            Message message = new Message();
            message.what = success;
            handler.sendMessage(message);
        } else if (rep.contains("注册失败")) {
            Message message = new Message();
            message.what = failed;
            handler.sendMessage(message);
        } else {
            Message message = new Message();
            message.what = elsess;
            handler.sendMessage(message);
        }
    }

    private void sendPost(final String url1, final String data) {
        String showresponse = "";
        // TODO: Implement this method
        Log.d(TAG, "url:" + url1 + "   data: " + data);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(url1);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes(data);
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    show(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }

    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new SimpleTransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                mCardView.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(mCardView, mCardView.getWidth() / 2, 0, mDoClose.getWidth() / 2, mCardView
                .getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mCardView.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(mCardView, mCardView.getWidth() / 2, 0, mCardView.getHeight(), mDoClose
                .getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCardView.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                mDoClose.setImageResource(R.drawable.plus);
                SignInActivity.super.onBackPressed();
            }
        });
        mAnimator.start();
    }

    @Override
    public void onBackPressed() {
        animateRevealClose();
    }
}
