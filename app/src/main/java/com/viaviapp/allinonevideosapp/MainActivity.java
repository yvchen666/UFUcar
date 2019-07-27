package com.viaviapp.allinonevideosapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bug.getpost.BugHttpClient;
import com.example.item.ItemAbout;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.viaviapp.allinonevideosapp.SignUpActivity.getIMEI;

public class MainActivity extends AppCompatActivity {

    private String  TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private FragmentManager fragmentManager;
    NavigationView navigationView;
    private AdView mAdView;
    Toolbar toolbar;
    LinearLayout lay_dev;
    ArrayList<ItemAbout> mListItem;
    TextView txt_develop, txt_devname;
    JsonUtils jsonUtils;
    private static final int showdialog = 1;
    private static final int nullresponse = 2;
    private static final int shownotice = 3;
    private static final int onlinvisiter = 4;
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case showdialog:
                    String data = (String )message.obj;
                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(MainActivity.this);
                    alertdialog.setTitle("用户信息");
                    alertdialog.setMessage(data);
                    alertdialog.setCancelable(false);

                    alertdialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alertdialog.show();
                    break;
                case nullresponse:
                    String data2 = (String)message.obj;
                    Toast.makeText(MainActivity.this,data2,Toast.LENGTH_LONG).show();
                    break;
                case shownotice:
                    String notice = (String)message.obj;
                    AlertDialog.Builder alertdialog1 = new AlertDialog.Builder(MainActivity.this);
                    alertdialog1.setTitle("公告");
                    alertdialog1.setMessage(notice);
                    alertdialog1.setCancelable(false);

                    alertdialog1.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alertdialog1.show();
                    break;
                case onlinvisiter:
                    String mes = (String)message.obj;
                    AlertDialog.Builder alertdialog2 = new AlertDialog.Builder(MainActivity.this);
                    alertdialog2.setTitle("在线客服");
                    alertdialog2.setMessage(mes);
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
        setContentView(R.layout.activity_main);
        BugHttpClient bug = new BugHttpClient();
        bug.setCheck_Capture(true);
        PermissionChecker.getInstance().requestReadPhoneState(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                String imei = getIMEI(MainActivity.this);
                Log.d(TAG, "run: imei:" + imei);
                String result = new HttpUtils().sendPost("http://yc.mmajd.com/system/?type=login",
                        "user=" + account + "&pass=" + password + "&imei=" + imei);
                Log.d(TAG, "onClick: result  " + result);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String text = new HttpUtils().sendGet("http://yc.mmajd.com/system/notice.txt");
                Message message = new Message();
                message.what = shownotice;
                message.obj = text;
                handler.sendMessage(message);
            }
        }).start();

        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());

        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        lay_dev = (LinearLayout) findViewById(R.id.dev_lay);
        txt_develop = (TextView) findViewById(R.id.text_develop);
        txt_devname = (TextView) findViewById(R.id.dev_name);

        Typeface tf = Typeface.createFromAsset(getAssets(), "myfonts/custom.ttf");
        Typeface tfbold = Typeface.createFromAsset(getAssets(), "myfonts/custom.ttf");
        txt_devname.setTypeface(tfbold);
        txt_develop.setTypeface(tf);


        mListItem = new ArrayList<>();
        if (JsonUtils.isNetworkAvailable(MainActivity.this)) {
            new MyTaskDev().execute(Constant.ABOUT_US_URL);
        } else {
            showToast(getString(R.string.network_msg));
        }
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        fragmentManager = getSupportFragmentManager();
        HomeFragment currenthome = new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment1, currenthome).commit();

    }

    private static final int START_ITEM = Menu.FIRST;  //Menu.FIRST的值就是1
    private static final int OVER_ITEM = Menu.FIRST + 1;

    //重写onCreateOptionMenu(Menu menu)方法，当菜单第一次被加载时调用
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //填充选项菜单（读取XML文件、解析、加载到Menu组件上）
        // getMenuInflater().inflate(R.menu.main, menu);

        //通过代码的方式来添加Menu
        //添加菜单项（组ID，菜单项ID，排序，标题）
        menu.add(0, START_ITEM, 100, "用户登录");
        menu.add(0, OVER_ITEM, 200, "用户信息");


        return true;
    }

    //重写OptionsItemSelected(MenuItem item)来响应菜单项(MenuItem)的点击事件（根据id来区分是哪个item）
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case START_ITEM:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
                break;
            case OVER_ITEM:
                SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
                final String account = preferences.getString("account", "");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuffer buffer = new StringBuffer();
                        String response = new HttpUtils().sendGet("http://yc.mmajd.com/system/?type=data&id="+account);
                        String[] array = response.split("<br>");
                        for (String a : array) {
                            buffer.append(a + "\n");
                        }
                        String string = buffer.toString();
                        Log.d("state", "run: "+string);
                        if (string != null) {
                            Message message = handler.obtainMessage();
                            message.what = showdialog;
                            message.obj = string;
                            handler.sendMessage(message);
                        } else {
                            Message message = handler.obtainMessage();
                            message.what = nullresponse;
                            message.obj = "暂时无法获取数据";
                            handler.sendMessage(message);
                        }

                    }
                }).start();

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.nav_home:
                                HomeFragment currenthome = new HomeFragment();
                                fragmentManager.beginTransaction().replace(R.id.fragment1, currenthome).commit();
                                toolbar.setTitle(getString(R.string.menu_home));

                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_latest:
                                LatestFragment latefragment = new LatestFragment();
                                fragmentManager.beginTransaction().replace(R.id.fragment1, latefragment).commit();
                                toolbar.setTitle(getString(R.string.menu_latest));
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_cat:
                                CategoryFragment catfragment = new CategoryFragment();
                                fragmentManager.beginTransaction().replace(R.id.fragment1, catfragment).commit();
                                toolbar.setTitle(getString(R.string.menu_category));
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_fav:
                                FavoriteFragment favfragment = new FavoriteFragment();
                                fragmentManager.beginTransaction().replace(R.id.fragment1, favfragment).commit();
                                toolbar.setTitle(getString(R.string.menu_favorite));
                                mDrawerLayout.closeDrawers();
                                break;

                            case R.id.sub_abus:
                                Intent intentab = new Intent(MainActivity.this, AboutUsActivity.class);
                                startActivity(intentab);
                                mDrawerLayout.closeDrawers();
                                break;

                            case R.id.sub_shareapp:
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String url = new HttpUtils().sendGet("http://yc.mmajd.com/system/share.txt");
                                        Log.d("HttpUtils", "run: "+url);
                                        Intent sendIntent = new Intent();
                                        sendIntent.setAction(Intent.ACTION_SEND);
                                        sendIntent.putExtra(Intent.EXTRA_TEXT, url);
                                        sendIntent.setType("text/plain");
                                        startActivity(sendIntent);
                                        mDrawerLayout.closeDrawers();
                                    }
                                }).start();
                                break;
                            case R.id.sub_rateapp:
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String onlinev = new HttpUtils()
                                                .sendGet("http://yc.mmajd.com/system/online.txt");
                                        Message message =new Message();
                                        message.what = onlinvisiter;
                                        message.obj = onlinev;
                                        handler.sendMessage(message);
                                    }
                                }).start();
                                break;
                            case R.id.sub_privacy:
                                Intent intenpri = new Intent(MainActivity.this, Privacy_Activity.class);
                                startActivity(intenpri);
                                mDrawerLayout.closeDrawers();
                                break;
                        }
                        return true;
                    }
                });
    }


    private class MyTaskDev extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null == result || result.length() == 0) {
                showToast(getString(R.string.no_data_found));
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.LATEST_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        ItemAbout itemAbout = new ItemAbout();

                        itemAbout.setappDevelop(objJson.getString(Constant.APP_DEVELOP));
                        mListItem.add(itemAbout);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    private void setResult() {

        ItemAbout itemAbout = mListItem.get(0);
        txt_develop.setText(itemAbout.getappDevelop());


    }

    public void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder alert = new AlertDialog.Builder(
                    MainActivity.this);
            alert.setTitle(getString(R.string.app_name));
            alert.setIcon(R.mipmap.app_icon);
            alert.setMessage("你确定要退出吗?");

            alert.setPositiveButton("是",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {

                            finish();
                        }

                    });
            alert.setNegativeButton("否",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    });
            alert.show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}