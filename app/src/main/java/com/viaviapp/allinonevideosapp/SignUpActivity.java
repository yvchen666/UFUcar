package com.viaviapp.allinonevideosapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.bug.getpost.BugHttpClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {
    private FloatingActionButton mGoRegister;
    private static String TAG = "SignUpActivity";
    private EditText usernameEdit;
    private EditText passwordEdit;
    private Button signup;
    private Button signin;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    public static final int success = 1;
    public static final int failed = 2;
    public static final int elsess = 3;
    public static final int nullkm = 4;
    public static final int nokm = 5;
    public static final int buysucced = 6;
    public static final int failed_imei = 7;
    public static final int buy = 8;
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case success:

                    Toast.makeText(SignUpActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    break;
                case failed:
                    Toast.makeText(SignUpActivity.this, "登录失败：账号或密码错误", Toast.LENGTH_SHORT).show();
                    break;
                case elsess:
                    Toast.makeText(SignUpActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                    break;
                case nullkm:
                    Toast.makeText(SignUpActivity.this, "请输入卡密！", Toast.LENGTH_SHORT).show();
                    break;
                case nokm:
                    Toast.makeText(SignUpActivity.this, "卡密不存在！", Toast.LENGTH_SHORT).show();
                    break;
                case buysucced:
                    Toast.makeText(SignUpActivity.this, "充值成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case failed_imei:
                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(SignUpActivity.this);
                    alertdialog.setTitle("Warn！");
                    alertdialog.setMessage("设备错误！！");
                    alertdialog.setCancelable(false);

                    alertdialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.exit(0);
                        }
                    });
                    alertdialog.show();
                    break;
                case buy:
                    String text = (String) message.obj;
                    AlertDialog.Builder alertdialog2 = new AlertDialog.Builder(SignUpActivity.this);
                    alertdialog2.setTitle("购买");
                    alertdialog2.setMessage(text);
                    alertdialog2.setCancelable(false);

                    alertdialog2.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alertdialog2.show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        BugHttpClient bug = new BugHttpClient();
        bug.setCheck_Capture(true);

        mGoRegister = findViewById(R.id.fab);
        usernameEdit = findViewById(R.id.et_username);
        passwordEdit = findViewById(R.id.et_password);
        signup = findViewById(R.id.bt_go);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);


        boolean isRemember = preferences.getBoolean("remember_password", true);
        if (isRemember) {
            SharedPreferences preferencess = getSharedPreferences("data", Context.MODE_PRIVATE);
            String account = preferencess.getString("account", "");
            String password = preferencess.getString("password", "");
            usernameEdit.setText(account);
            passwordEdit.setText(password);
        }
        //注册
        mGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInActivity.start(SignUpActivity.this, view);
            }
        });
//        注册
        signup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String account = usernameEdit.getText().toString();
                        String password = passwordEdit.getText().toString();
                        String imei = getIMEI(SignUpActivity.this);
                        Log.d(TAG, "run: imei:" + imei);
                        String result = sendPost("http://yc.mmajd.com/system/?type=login",
                                "user=" + account + "&pass=" + password + "&imei=" + imei);
                        Log.d(TAG, "onClick: result  " + result);
                        if (result.contains("登录成功")) {
                            preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                            editor = preferences.edit();

                                editor.putBoolean("remember_password", true);
                                editor.putString("account", account);
                                editor.putString("password", password);
                            editor.apply();
                            String response = sendGet("http://yc.mmajd.com/system/?type=data&id=" + account);
                            if (response.contains("已授权")) {
                                Log.d(TAG, "onClick: response" + response);
                                Intent intent = new Intent();
                                intent.setClass(SignUpActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (response.contains("没有授权")) {
                                Looper.prepare();
                                showDialog(account);
                                Looper.loop();
                            }
                        } else if (result.contains("账号或密码错误")) {

                            Message message = new Message();
                            message.what = failed;
                            handler.sendMessage(message);

                        } else if (result.contains("IMEI错误")) {
                            Message message = new Message();
                            message.what = failed_imei;
                            handler.sendMessage(message);
                        }
                    }
                }).start();

            }
        });
        //注册

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

    public String sendPost(String requestUrl, String requestbody) {

        try {
            //建立连接
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //设置连接属性
            connection.setDoOutput(true); //使用URL连接进行输出
            connection.setDoInput(true); //使用URL连接进行输入
            connection.setUseCaches(false); //忽略缓存
            connection.setRequestMethod("POST"); //设置URL请求方法

            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

            //建立输出流,并写入数据
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(requestbody);

            //获取响应状态
            int responseCode = connection.getResponseCode();

            if (HttpURLConnection.HTTP_OK == responseCode) { //连接成功
                //当正确响应时处理数据
                StringBuffer buffer = new StringBuffer();
                String readLine;
                BufferedReader responseReader;
                //处理响应流
                responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((readLine = responseReader.readLine()) != null) {
                    buffer.append(readLine).append("\n");
                }
                responseReader.close();
                Log.d("HttpPOST", buffer.toString());
                return buffer.toString();//成功
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("" + e.toString());
        }
        return 2 + "";//失败

    }

    /**
     * Get服务请求
     *
     * @param requestUrl
     * @return
     */
    public String sendGet(String requestUrl) {
        try {
            //建立连接
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setDoOutput(false);
            connection.setDoInput(true);

            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

            connection.connect();

            //获取响应状态
            int responseCode = connection.getResponseCode();

            if (HttpURLConnection.HTTP_OK == responseCode) { //连接成功
                //当正确响应时处理数据
                StringBuffer buffer = new StringBuffer();
                String readLine;
                BufferedReader responseReader;
                //处理响应流
                responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((readLine = responseReader.readLine()) != null) {
                    buffer.append(readLine).append("\n");
                }
                responseReader.close();
                Log.d("HttpGET", buffer.toString());
                //JSONObject result = new JSONObject(buffer.toString());
                return buffer.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showDialog(final String user) {
        TelephonyManager telephonyManager = (TelephonyManager) SignUpActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
        //获取IMEI号
        @SuppressLint("MissingPermission") final String imei = telephonyManager.getDeviceId();

        final EditText et = new EditText(this);
        et.setHint("请输入卡密");
        new AlertDialog.Builder(this).
                setTitle("提示").setMessage("您没有激活软件无法正常使用请充值激活")
                .setView(et)
                .setNegativeButton("充值", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface p1, int p2) {
                        String response;
                        response = sendPost("http://yc.mmajd.com/system/?type=buy_kami", "user=" + user + "&kami=" + et.getText().toString());
                        Log.d(TAG, "BUY:   " + "response:" + response + "requesbody:" + "user=" + user + "&kami=" + et.getText().toString());
                        Log.d("充值结果", response);
                        if (response.contains("请输入充值卡密")) {
                            Message message = new Message();
                            message.what = nullkm;
                            handler.sendMessage(message);
                        } else if (response.contains("卡密不存在")) {
                            Message message = new Message();
                            message.what = nokm;
                            handler.sendMessage(message);
                        } else if (response.contains("充值成功")) {
                            Message message = new Message();
                            message.what = buysucced;
                            handler.sendMessage(message);
                        } else {
                            Message message = new Message();
                            message.what = elsess;
                            handler.sendMessage(message);
                        }

                    }
                })
                .setPositiveButton("购买", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface p1, int p2) {

                        String text = new HttpUtils().sendGet("http://yc.mmajd.com/system/online.txt");
                        Message message = new Message();
                        message.what = buy;
                        message.obj = text;
                        handler.sendMessage(message);
                    }
                }).setCancelable(false).create().show();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        mGoRegister.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoRegister.setVisibility(View.VISIBLE);
    }
}
