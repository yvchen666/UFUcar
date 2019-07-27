package com.viaviapp.allinonevideosapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.RelatedGridAdapter;
import com.example.dailymotion.DailyMotionPlay;
import com.example.favorite.DatabaseHandler;
import com.example.favorite.ItemDb;
import com.example.item.ItemLatest;
import com.example.item.ItemRelated;
import com.example.play.OpenYouTubePlayerActivity;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.example.vimeo.Vimeo;
import com.example.youtube.YoutubePlay;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class VideoPlay extends AppCompatActivity {

    public DatabaseHandler db;
    private Menu menu;
    String vid, video_cat_name, video_type, video_title, video_url, video_playid, video_thumbnail_b, video_thumbnail_s, video_duration, video_description;
    private AdView mAdView;
    Toolbar toolbar;
    List<ItemLatest> arrayOfLatestVideo;
    List<ItemRelated> arrayOfRelated;
    private ItemLatest objAllBean;
    private ItemRelated objAllBeanrelated;
    ImageView vp_imageview, img_play;
    TextView txt_name;
    WebView webdesc;
    LinearLayout linearContent;
    GridView gridViewrela;
    RelatedGridAdapter objAdapterrelated;
    private int columnWidth;
    private String TAG = "VideoPlay";
    JsonUtils jsonUtils;
    public static final int nullvip = 1;
    public static final int nulluse = 2;
    public static final int elsess = 3;
    public static final int nullkm = 4;
    public static final int nokm = 5;
    public static final int buysucced = 6;
    public static final int buy = 7;

    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case nulluse:
                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(VideoPlay.this);
                    alertdialog.setTitle("提示");
                    alertdialog.setMessage("您还未登录，请先登录");
                    alertdialog.setCancelable(false);

                    alertdialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setClass(VideoPlay.this, SignUpActivity.class);
                            startActivity(intent);
                        }
                    });
                    alertdialog.show();
                    break;

                case elsess:
                    Toast.makeText(VideoPlay.this, "未知错误", Toast.LENGTH_SHORT).show();
                    break;
                case nullkm:
                    Toast.makeText(VideoPlay.this, "请输入卡密！", Toast.LENGTH_SHORT).show();
                    break;
                case nokm:
                    Toast.makeText(VideoPlay.this, "卡密不存在！", Toast.LENGTH_SHORT).show();
                    break;
                case buysucced:
                    Toast.makeText(VideoPlay.this, "充值成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(VideoPlay.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case buy:
                    String text = (String) message.obj;
                    AlertDialog.Builder alertdialog2 = new AlertDialog.Builder(VideoPlay.this);
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
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());

        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        db = new DatabaseHandler(this);
        arrayOfLatestVideo = new ArrayList<>();
        arrayOfRelated = new ArrayList<>();

        vp_imageview = (ImageView) findViewById(R.id.img_gmain);
        txt_name = (TextView) findViewById(R.id.text_title);
        img_play = (ImageView) findViewById(R.id.img_play);
        webdesc = (WebView) findViewById(R.id.desweb);
        gridViewrela = (GridView) findViewById(R.id.gridcat_related);
        linearContent = (LinearLayout) findViewById(R.id.rel_c_content);

        if (JsonUtils.isNetworkAvailable(VideoPlay.this)) {
            new MyTask().execute(Constant.SINGLE_VIDEO_URL + Constant.VIDEO_IDD);
        } else {
            showToast(getString(R.string.network_msg));
        }
    }

    private class MyTask extends AsyncTask<String, Void, String> {

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
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        ItemLatest objItem = new ItemLatest();

                        objItem.setId(objJson.getString(Constant.LATEST_ID));
                        objItem.setCategoryId(objJson.getString(Constant.LATEST_CATID));
                        objItem.setCategoryName(objJson.getString(Constant.LATEST_CAT_NAME));
                        objItem.setVideoUrl(objJson.getString(Constant.LATEST_VIDEO_URL));
                        objItem.setVideoId(objJson.getString(Constant.LATEST_VIDEO_ID));
                        objItem.setVideoName(objJson.getString(Constant.LATEST_VIDEO_NAME));
                        objItem.setDuration(objJson.getString(Constant.LATEST_VIDEO_DURATION));
                        objItem.setDescription(objJson.getString(Constant.LATEST_VIDEO_DESCRIPTION));
                        objItem.setImageUrl(objJson.getString(Constant.LATEST_IMAGE_URL));
                        objItem.setVideoType(objJson.getString(Constant.LATEST_VIDEOTYPE));
                        objItem.setVideoImgBig(objJson.getString(Constant.LATEST_IMAGE_URL_BIG));

                        arrayOfLatestVideo.add(objItem);

                        JSONArray jsonArraychild = objJson.getJSONArray(Constant.RELATED_ARRAY);
                        if (jsonArraychild.length() == 0) {

                        } else {
                            for (int j = 0; j < jsonArraychild.length(); j++) {
                                JSONObject objChild = jsonArraychild.getJSONObject(j);
                                ItemRelated item = new ItemRelated();
                                item.setRId(objChild.getString(Constant.RELATED_ID));
                                item.setRVideoName(objChild.getString(Constant.RELATED_NAME));
                                item.setRVideoType(objChild.getString(Constant.RELATED_TYPE));
                                item.setRCategoryName(objChild.getString(Constant.RELATED_CNAME));
                                item.setRVideoId(objChild.getString(Constant.RELATED_PID));
                                item.setRImageUrl(objChild.getString(Constant.RELATED_IMG));
                                item.setRDuration(objChild.getString(Constant.RELATED_TIME));
                                arrayOfRelated.add(item);
                            }
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setAdapterToListview();
            }

        }

        public void setAdapterToListview() {

            objAllBean = arrayOfLatestVideo.get(0);

            vid = objAllBean.getId();
            video_cat_name = objAllBean.getCategoryName();
            video_type = objAllBean.getVideoType();
            video_title = objAllBean.getVideoName();
            video_url = objAllBean.getVideoUrl();
            video_playid = objAllBean.getVideoId();
            video_thumbnail_b = objAllBean.getVideoImgBig();
            video_thumbnail_s = objAllBean.getImageUrl();
            video_duration = objAllBean.getDuration();
            video_description = objAllBean.getDescription();

            if (video_type.equals("local")) {
                Picasso.with(VideoPlay.this).load(video_thumbnail_b).placeholder(R.drawable.placeholder).into(vp_imageview);
            } else if (video_type.equals("server_url")) {
                Picasso.with(VideoPlay.this).load(video_thumbnail_b).placeholder(R.drawable.placeholder).into(vp_imageview);
            } else if (video_type.equals("youtube")) {
                Picasso.with(VideoPlay.this).load(Constant.YOUTUBE_IMAGE_FRONT + video_playid + Constant.YOUTUBE_SMALL_IMAGE_BACK).placeholder(R.drawable.placeholder).into(vp_imageview);
            } else if (video_type.equals("dailymotion")) {
                Picasso.with(VideoPlay.this).load(Constant.DAILYMOTION_IMAGE_PATH + video_playid).placeholder(R.drawable.placeholder).into(vp_imageview);
            } else if (video_type.equals("vimeo")) {
                Picasso.with(VideoPlay.this).load(video_thumbnail_b).placeholder(R.drawable.placeholder).into(vp_imageview);
            }

            txt_name.setText(video_title);

            webdesc.setBackgroundColor(0);
            webdesc.setFocusableInTouchMode(false);
            webdesc.setFocusable(false);
            webdesc.getSettings().setDefaultTextEncodingName("UTF-8");

            String mimeType = "text/html;charset=UTF-8";
            String encoding = "utf-8";
            String htmlText = video_description;

            String text = "<html><head>"
                    + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/myfonts/custom.ttf\")}body{font-family: MyFont;color: #545454;text-align:justify}"
                    + "</style></head>"
                    + "<body>"
                    + htmlText
                    + "</body></html>";

            webdesc.loadDataWithBaseURL(null, text, mimeType, encoding, null);
            img_play.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                    final String account = preferences.getString("account", "");
                    String password = preferences.getString("password", "");
                    Log.d("SharedPreferences", "account:" + account + "password" + password);
                    if (account == "" | password == "") {
                        Message message = new Message();
                        message.what = nulluse;
                        handler.sendMessage(message);
                    } else {
                        new Thread(new Runnable() {
                            String response;

                            @Override
                            public void run() {
                                HttpUtils httpUtils = new HttpUtils();
                                response = httpUtils.sendGet("http://yc.mmajd.com/system/?type=data&id=" + account);
                                if (response.contains("已授权") && response.contains("正常")) {
                                    if (video_type.equals("local")) {
                                        Intent lVideoIntent = new Intent(null, Uri.parse("file://" + video_url), VideoPlay.this, OpenYouTubePlayerActivity.class);
                                        startActivity(lVideoIntent);
                                    } else if (video_type.equals("server_url")) {
                                        Intent lVideoIntent = new Intent(null, Uri.parse("file://" + video_url), VideoPlay.this, OpenYouTubePlayerActivity.class);
                                        startActivity(lVideoIntent);
                                    } else if (video_type.equals("youtube")) {
                                        Intent i = new Intent(VideoPlay.this, YoutubePlay.class);
                                        i.putExtra("id", video_playid);
                                        startActivity(i);
                                    } else if (video_type.equals("dailymotion")) {
                                        Intent i = new Intent(VideoPlay.this, DailyMotionPlay.class);
                                        i.putExtra("id", video_playid);
                                        startActivity(i);
                                    } else if (video_type.equals("vimeo")) {
                                        Intent i = new Intent(VideoPlay.this, Vimeo.class);
                                        i.putExtra("id", video_playid);
                                        startActivity(i);
                                    }
                                } else {
                                    Looper.prepare();
                                    showDialog(account);
                                    Looper.loop();
                                }
                            }
                        }).start();

                    }
                }
            });

            RelatedVideoContent();

        }
    }

    public void RelatedVideoContent() {

        objAdapterrelated = new RelatedGridAdapter(VideoPlay.this, R.layout.latest_row_item,
                arrayOfRelated, columnWidth);
        gridViewrela.setAdapter(objAdapterrelated);

        gridViewrela.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                objAllBeanrelated = arrayOfRelated.get(position);
                Constant.VIDEO_IDD = objAllBeanrelated.getRvid();

                Intent intplay = new Intent(VideoPlay.this, VideoPlay.class);
                intplay.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intplay);
            }
        });

    }

    public void showToast(String msg) {
        Toast.makeText(VideoPlay.this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);
        this.menu = menu;
        FirstFav();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;


            case R.id.menu_fav:
                List<ItemDb> pojolist = db.getFavRow(Constant.VIDEO_IDD);
                if (pojolist.size() == 0) {
                    AddtoFav();//if size is zero i.e means that record not in database show add to favorite
                } else {
                    if (pojolist.get(0).getvid().equals(Constant.VIDEO_IDD)) ;
                    {
                        RemoveFav();
                    }

                }

                return true;

            case R.id.menu_share:

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_video_msg) + "\n" + video_title + "\n" + video_url + "\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }

    }

    public void AddtoFav() {

        db.AddtoFavorite(new ItemDb(Constant.VIDEO_IDD, video_cat_name, video_type, video_playid, video_title, video_thumbnail_s, video_duration));
        Toast.makeText(getApplicationContext(), getString(R.string.add_favorite_msg), Toast.LENGTH_SHORT).show();
        menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.fav_hover));
    }

    public void RemoveFav() {

        db.RemoveFav(new ItemDb(Constant.VIDEO_IDD));
        Toast.makeText(getApplicationContext(), getString(R.string.remove_favorite_msg), Toast.LENGTH_SHORT).show();
        menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.fav));

    }

    public void FirstFav() {

        List<ItemDb> pojolist = db.getFavRow(Constant.VIDEO_IDD);
        if (pojolist.size() == 0) {
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.fav));

        } else {
            if (pojolist.get(0).getvid().equals(Constant.VIDEO_IDD)) {
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.fav_hover));

            }

        }
    }

    private void showDialog(final String user) {
        TelephonyManager telephonyManager = (TelephonyManager) VideoPlay.this.getSystemService(Context.TELEPHONY_SERVICE);
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
                        HttpUtils httpUtils = new HttpUtils();
                        response = httpUtils.sendPost("http://yc.mmajd.com/system/?type=buy_kami", "user=" + user + "&kami=" + et.getText().toString());
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
}
