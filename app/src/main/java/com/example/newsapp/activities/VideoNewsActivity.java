package com.example.newsapp.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.newsapp.R;
import com.example.newsapp.adapters.ChiTietTinTucNormalAdapter;
import com.example.newsapp.models.ChiTietTinTuc;
import com.example.newsapp.network.CheckConnectionNetwork;
import com.example.newsapp.utils.SaveLoadFileUtil;
import com.example.newsapp.utils.XMLDOMParserUtil;
import com.example.newsapp.variables.LinkBaoBienPhong;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mahfa.dnswitch.DayNightSwitch;
import com.mahfa.dnswitch.DayNightSwitchListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.supercharge.shimmerlayout.ShimmerLayout;

import static com.example.newsapp.utils.ListViewHeightBasedOnItemsUtil.setListViewHeightBasedOnItems;

public class VideoNewsActivity extends AppCompatActivity {

    //kiểm tra tên báo
    private final String BAO_BIEN_PHONG = "Báo Biên Phòng";

    // lưu SharedPreferences cho seekbar daynight
    private final String MY_PREFERENCES_DAYNIGHT = "nightModePrefs";
    private final String KEY_ISNIGHTMODE = "isNightMode";
    private SharedPreferences sharedPreferencesDayNight;

    // lưu SharedPreferences cho fontSize
    private SharedPreferences sharedPreferencesTextSize;
    private final String MY_PREFERENCES_TEXTSIZE = "fontSizePrefs";
    private final String KEY_HEADER_TITLE = "header_title";
    private final String KEY_DATE_TIME = "date_time";
    private final String KEY_SUB_TITLE = "sub_title";

    private TextView txt_headerTitle, txt_dateTime, txt_subTitle;
    private PlayerView playerViewBao;
    private ProgressBar progressBarBao;
    private SimpleExoPlayer simpleExoPlayer;
    private TextView txt_xemBaiVietGoc, txt_chiaSe, txt_troVeTrangChu;
    private ToggleButton tb_danhDauReadNews;
    private ImageView img_aa, img_fullscreen;
    private Toolbar toolbar;
    private NestedScrollView nestedsv;
    private RelativeLayout relativeLayoutBottom;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView linear_list;
    private ShimmerLayout shimmer_view_contain;

    // getIntent from Bao Activity
    private String titleNewsIntent = "";
    private String nameNewsIntent = "";
    private String linkNewsIntent = "";
    private String imageNewsIntent = "";
    private String pubDateNewsIntent = "";
    private String tabSelectedIntent = "";

    private ArrayList<ChiTietTinTuc> arrChiTietTinTuc;

    private LinkBaoBienPhong linkBaoBienPhong;

    private Random randomNews;
    private Vector vectorNews;
    int iNew = 0;

    private BottomSheetDialog bottomSheetDialog;
    private SaveLoadFileUtil saveLoadFileUntil;

    private boolean flag = false;

    //show dialog textSize
    private Dialog dialog_box;
    private DayNightSwitch day_night_switch;
    private TextView txt_macDinh;
    private TextView txt_aTru;
    private TextView txt_aCong;
    private SeekBar seekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_news);


        if (CheckConnectionNetwork.haveNetworkConnection(this)) {
            init();
            initDataApp();
            actionBar();
            event();
        } else {
            CheckConnectionNetwork.showDialogNoUpdateData(VideoNewsActivity.this);
        }

    }

    private void init() {
        txt_headerTitle = findViewById(R.id.txt_headerTitle);
        txt_dateTime = findViewById(R.id.txt_dateTime);
        txt_subTitle = findViewById(R.id.txt_subTitle);
        playerViewBao = findViewById(R.id.player_view);
        progressBarBao = findViewById(R.id.progress_bar);
        img_fullscreen = findViewById(R.id.bt_fullscreen);
        txt_xemBaiVietGoc = findViewById(R.id.txt_xemBaiVietGoc);
        txt_chiaSe = findViewById(R.id.txt_chiaSe);
        txt_troVeTrangChu = findViewById(R.id.txt_troVeTrangChu);
        img_aa = findViewById(R.id.img_aa);
        toolbar = findViewById(R.id.toolbar);
        nestedsv = findViewById(R.id.nestedsv);
        relativeLayoutBottom = findViewById(R.id.relativeLayoutBottom);
        linear_list = findViewById(R.id.linear_list);
        tb_danhDauReadNews = findViewById(R.id.tb_danhDauReadNews);
        shimmer_view_contain = findViewById(R.id.shimmer_view_contain);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        linkBaoBienPhong = new LinkBaoBienPhong();
        arrChiTietTinTuc = new ArrayList<>();
        saveLoadFileUntil = new SaveLoadFileUtil();

        // textSizeDialog
        dialog_box = new Dialog(VideoNewsActivity.this);
        randomNews = new Random();
        vectorNews = new Vector();

        bottomSheetDialog = new BottomSheetDialog(VideoNewsActivity.this, R.style.BottomSheetTheme);

    }

    private void initDataApp() {

        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.GREEN);

        dialog_box.setContentView(R.layout.custom_text_size_layout);
        day_night_switch = dialog_box.findViewById(R.id.day_night_switch);
        txt_macDinh = dialog_box.findViewById(R.id.txt_macDinh);
        txt_aTru = dialog_box.findViewById(R.id.txt_aTru);
        txt_aCong = dialog_box.findViewById(R.id.txt_aCong);
        seekBar = dialog_box.findViewById(R.id.seekBar);
        seekBar.setMax(255);

        sharedPreferencesDayNight = getSharedPreferences(MY_PREFERENCES_DAYNIGHT, Context.MODE_PRIVATE);
        sharedPreferencesTextSize = getSharedPreferences(MY_PREFERENCES_TEXTSIZE, Context.MODE_PRIVATE);

        Intent intent = getIntent();
        nameNewsIntent = intent.getStringExtra("news_name");
        titleNewsIntent = intent.getStringExtra("title_news");
        linkNewsIntent = intent.getStringExtra("link_news");
        imageNewsIntent = intent.getStringExtra("image_news");
        pubDateNewsIntent = intent.getStringExtra("pubdate_news");
        tabSelectedIntent = intent.getStringExtra("tab_selected");

        checkNightModeActivated();
        setTextSize(txt_headerTitle, txt_dateTime, txt_subTitle);

        // Check đánh dấu
        if (saveLoadFileUntil.checkExistsInFileBookmarNews(this, nameNewsIntent, titleNewsIntent, linkNewsIntent,
                imageNewsIntent, pubDateNewsIntent) == true) {

            tb_danhDauReadNews.setChecked(true);
        } else {
            tb_danhDauReadNews.setChecked(false);

        }

        getAllDataHtmlNewsName(nameNewsIntent, linkNewsIntent, tabSelectedIntent);  // read news
    }

    private void actionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void event() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                simpleExoPlayer.setPlayWhenReady(true);
                getAllDataHtmlNewsName(nameNewsIntent, linkNewsIntent, tabSelectedIntent);  // read news
            }
        });

        txt_troVeTrangChu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameNewsIntent.contains("Báo Biên Phòng")) {
                    startActivity(new Intent(VideoNewsActivity.this, BaoBienPhongActivity.class));
                }
            }
        });

        txt_chiaSe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, linkNewsIntent);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share to: "));

            }
        });

        txt_xemBaiVietGoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideoNewsActivity.this, XemBaiBaoGocActivity.class);
                intent.putExtra("link_news_read_activity", linkNewsIntent);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        nestedsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY > oldScrollY) { // scroll dow
                    relativeLayoutBottom.setVisibility(View.GONE);
                } else {
                    relativeLayoutBottom.setVisibility(View.VISIBLE);
                }
            }
        });

        tb_danhDauReadNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tb_danhDauReadNews.isChecked() == true) {
                    saveLoadFileUntil.saveFileNews(VideoNewsActivity.this, "bookmarkNews.txt", nameNewsIntent,
                            titleNewsIntent, linkNewsIntent, imageNewsIntent, pubDateNewsIntent);
                    Toast.makeText(VideoNewsActivity.this, "Đã thêm vào tin đánh dấu", Toast.LENGTH_SHORT).show();
                    tb_danhDauReadNews.setChecked(true);
                    bottomSheetDialog.dismiss();
                } else {
                    saveLoadFileUntil.removeExistsInFileBookmarNews(VideoNewsActivity.this, nameNewsIntent,
                            titleNewsIntent, linkNewsIntent, imageNewsIntent, pubDateNewsIntent);
                    Toast.makeText(VideoNewsActivity.this, "Đã bỏ khỏi tin đánh dấu", Toast.LENGTH_SHORT).show();
                    tb_danhDauReadNews.setChecked(false);
                    bottomSheetDialog.dismiss();
                }

            }
        });

        img_aa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogTextSize();
            }
        });

    }

    private void getAllDataHtmlNewsName(String newsName, String urlKenhBao, String tabSelected) {
        switch (newsName) {

            case BAO_BIEN_PHONG:
                new LoadHtmlBaoBienPhongVideoView().execute(urlKenhBao);
                new ReadDataRandomRSSVideoView().execute(linkBaoBienPhong.LINK_VIDEO);
                break;
        }
    }

    private void showDialogTextSize() {

        askPermission(VideoNewsActivity.this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    android.provider.Settings.System.putInt(
                            getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS, progress);

                } catch (SecurityException e) {
                    showDialogCheckPermission();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        txt_macDinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txt_headerTitle.setTextSize(pixelsToSp(VideoNewsActivity.this, 48.0f)); // mặc đinh bên layout là 24sp
                txt_dateTime.setTextSize(pixelsToSp(VideoNewsActivity.this, 28.0f)); // mặc đinh bên layout là 14sp
                txt_subTitle.setTextSize(pixelsToSp(VideoNewsActivity.this, 40.0f)); // mặc đinh bên layout là 20sp

                // txt_aTru
                if (pixelsToSp(VideoNewsActivity.this, txt_headerTitle.getTextSize()) == pixelsToSp(VideoNewsActivity.this, 24.0f)) { // 12sp
                    txt_aTru.setEnabled(true);
                    txt_aTru.setAlpha(1.0f);
                }

                // txt_aCong
                if (pixelsToSp(VideoNewsActivity.this, txt_headerTitle.getTextSize()) != pixelsToSp(VideoNewsActivity.this, 90.0f)) { // 45sp
                    txt_aCong.setEnabled(true);
                    txt_aCong.setAlpha(1.0f);
                }

            }
        });

        txt_aTru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_headerTitle.setTextSize(pixelsToSp(VideoNewsActivity.this, txt_headerTitle.getTextSize() - 6.0f));
                txt_dateTime.setTextSize(pixelsToSp(VideoNewsActivity.this, txt_dateTime.getTextSize() - 6.0f));
                txt_subTitle.setTextSize(pixelsToSp(VideoNewsActivity.this, txt_subTitle.getTextSize() - 6.0f));

                if (pixelsToSp(VideoNewsActivity.this, txt_headerTitle.getTextSize()) == pixelsToSp(VideoNewsActivity.this, 24.0f)) { // 12sp
                    txt_aTru.setEnabled(false);
                    txt_aTru.setAlpha(0.3f);
                }

                // mở lại txtCong
                if (pixelsToSp(VideoNewsActivity.this, txt_headerTitle.getTextSize()) != pixelsToSp(VideoNewsActivity.this, 90.0f)) { // 45sp
                    txt_aCong.setEnabled(true);
                    txt_aCong.setAlpha(1.0f);
                }

            }
        });

        txt_aCong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cứ 2.0f tương ứng với 1sp

                txt_headerTitle.setTextSize(pixelsToSp(VideoNewsActivity.this, txt_headerTitle.getTextSize() + 6.0f));
                txt_dateTime.setTextSize(pixelsToSp(VideoNewsActivity.this, txt_dateTime.getTextSize() + 6.0f));
                txt_subTitle.setTextSize(pixelsToSp(VideoNewsActivity.this, txt_subTitle.getTextSize() + 6.0f));

                if (pixelsToSp(VideoNewsActivity.this, txt_headerTitle.getTextSize()) == pixelsToSp(VideoNewsActivity.this, 90.0f)) { // 45sp
                    txt_aCong.setEnabled(false);
                    txt_aCong.setAlpha(0.3f);
                }

                // mở lại txt_aCong
                if (pixelsToSp(VideoNewsActivity.this, txt_headerTitle.getTextSize()) != pixelsToSp(VideoNewsActivity.this, 24.0f)) { // 12sp
                    txt_aTru.setEnabled(true);
                    txt_aTru.setAlpha(1.0f);
                }

            }
        });

        day_night_switch.setListener(new DayNightSwitchListener() {
            @Override
            public void onSwitch(boolean is_night) {
                if (is_night) {
                    dialog_box.dismiss();
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    saveNightModeState(true);
                } else {
                    dialog_box.dismiss();
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    saveNightModeState(false);

                }
            }
        });

        Window window = dialog_box.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP | Gravity.RIGHT;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.y = 100;   //if you want give margin from top
        wlp.x = 100;     //if you want give margin from left
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialog_box.show();
    }

    private void askPermission(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            try {
                if (Settings.System.canWrite(context)) {

                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    context.startActivity(intent);
                }
            } catch (SecurityException e) {

            }

        }
    }

    private void showDialogCheckPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoNewsActivity.this);
        builder.setTitle("Lỗi cấp quyền");
        builder.setMessage("Bạn cần phải cấp quyền cho ứng dụng cho việc thao tác trên hệ thống?");
        builder.setNegativeButton(Html.fromHtml("<font color='#FF3D00'>Không</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        builder.setPositiveButton(Html.fromHtml("<font color='#03A9F4'>Đã hiểu</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                askPermission(VideoNewsActivity.this);

            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    private void setTextSize(TextView txt_headerTitle, TextView txt_dateTime, TextView txt_subTitle) {
        txt_headerTitle.setTextSize(pixelsToSp(VideoNewsActivity.this, sharedPreferencesTextSize.getFloat(KEY_HEADER_TITLE, 48.0f)));
        txt_dateTime.setTextSize(pixelsToSp(VideoNewsActivity.this, sharedPreferencesTextSize.getFloat(KEY_DATE_TIME, 28.0f)));
        txt_subTitle.setTextSize(pixelsToSp(VideoNewsActivity.this, sharedPreferencesTextSize.getFloat(KEY_SUB_TITLE, 40.0f)));

        if (pixelsToSp(VideoNewsActivity.this, txt_headerTitle.getTextSize()) == pixelsToSp(VideoNewsActivity.this, 24.0f)) {
            txt_aTru.setEnabled(false);
            txt_aTru.setAlpha(0.3f);
        } else if (pixelsToSp(VideoNewsActivity.this, txt_headerTitle.getTextSize()) == pixelsToSp(VideoNewsActivity.this, 90.0f)) {
            txt_aCong.setEnabled(false);
            txt_aCong.setAlpha(0.3f);
        }

    }

    private void saveNightModeState(boolean nightMode) {
        SharedPreferences.Editor editor = sharedPreferencesDayNight.edit();
        editor.putBoolean(KEY_ISNIGHTMODE, nightMode);
        editor.apply();
    }

    private void checkNightModeActivated() {
        if (sharedPreferencesDayNight.getBoolean(KEY_ISNIGHTMODE, false)) {
            day_night_switch.setIsNight(true);
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            day_night_switch.setIsNight(false);
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void showShimmerLayout() {
        swipeRefreshLayout.setVisibility(View.GONE);
        shimmer_view_contain.setVisibility(View.VISIBLE);
        shimmer_view_contain.startShimmerAnimation();
    }

    private void hideShimmerLayout() {
        shimmer_view_contain.stopShimmerAnimation();
        shimmer_view_contain.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    public void saveFileHistoryNews(Context context, String newsName, String title, String link, String image, String pubDate) {

        boolean checkContain = false; // biến kiểm tra city có trong file hay không

        try {

            File file = new File(context.getFilesDir(), "historyNews.txt");
            if (file.exists()) {

                // Đọc file để lấy data trong file
                ArrayList<ChiTietTinTuc> items = new ArrayList<ChiTietTinTuc>();
                FileInputStream fs_in = context.openFileInput("historyNews.txt");
                InputStreamReader is = new InputStreamReader(fs_in);
                BufferedReader br = new BufferedReader(is);
                String lineRead = br.readLine();
                String newsNameFile = "", titleFile = "", imageFile = "", linkFile = "", pubDateFile = "";
                while (lineRead != null) {
                    if (!lineRead.equals("#")) {

                        if (lineRead.equals("[newsName]")) {
                            lineRead = br.readLine();
                            newsNameFile = lineRead;

                        } else if (lineRead.equals("[title]")) {
                            lineRead = br.readLine();
                            titleFile = lineRead;

                        } else if (lineRead.equals("[link]")) {
                            lineRead = br.readLine();
                            linkFile = lineRead;

                        } else if (lineRead.equals("[image]")) {
                            lineRead = br.readLine();
                            imageFile = lineRead;

                        } else if (lineRead.equals("[pubDate]")) {
                            lineRead = br.readLine();
                            pubDateFile = lineRead;
                        }

                        if (!newsNameFile.equals("") && !titleFile.equals("") && !linkFile.equals("") && !imageFile.equals("") && !pubDateFile.equals("")) {
                            items.add(new ChiTietTinTuc(newsNameFile, titleFile, linkFile, imageFile, pubDateFile));
                        }

                    } else {
                        lineRead = "";
                        newsNameFile = "";
                        titleFile = "";
                        imageFile = "";
                        linkFile = "";
                        pubDateFile = "";
                    }
                    lineRead = br.readLine();
                }
                br.close();

                // Lấy data trong file đã đọc lưu trong mảng rồi đem so trùng
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getNewsName().equals(newsName) && items.get(i).getTitle().equals(title) && items.get(i).getLink().equals(link)
                            && items.get(i).getImage().equals(image) && items.get(i).getPubDate().equals(pubDate)) { // trùng thì thoát vòng lặp

                        checkContain = true; //trùng
                        break;

                    } else {
                        checkContain = false;
                    }
                }

                // checkContain == false tức là city chưa có trong file nên ghi vào
                if (checkContain == false) {

                    if (items.size() >= 10) { // kiểm tra mảng items đọc từ file đã đủ 10 item hay chưa
                        items.remove(0); // xóa item đầu

                        items.add(new ChiTietTinTuc(newsName, title, link, image, pubDate)); // lưu  vào mảng items

                        file.delete(); // xóa dữ liệu trong file

                        FileOutputStream fs_out = context.openFileOutput("historyNews.txt", Context.MODE_APPEND);
                        OutputStreamWriter os = new OutputStreamWriter(fs_out);

                        for (int i = 0; i < items.size(); i++) { // ghi dữ liệu mới gồm 5 item vào file
                            os.write("[newsName]" + "\n" + items.get(i).getNewsName() + "\n");
                            os.write("[title]" + "\n" + items.get(i).getTitle() + "\n");
                            os.write("[link]" + "\n" + items.get(i).getLink() + "\n");
                            os.write("[image]" + "\n" + items.get(i).getImage() + "\n");
                            os.write("[pubDate]" + "\n" + items.get(i).getPubDate() + "\n");
                            os.write("#\n");
                        }

                        os.close();

                    } else {
                        FileOutputStream fs_out = context.openFileOutput("historyNews.txt", Context.MODE_APPEND);
                        OutputStreamWriter os = new OutputStreamWriter(fs_out);
                        os.write("[newsName]" + "\n" + newsName + "\n");
                        os.write("[title]" + "\n" + title + "\n");
                        os.write("[link]" + "\n" + link + "\n");
                        os.write("[image]" + "\n" + image + "\n");
                        os.write("[pubDate]" + "\n" + pubDate + "\n");
                        os.write("#\n");
                        os.close();

                    }
                }

            } else { // làn đầu cài app thì sẽ chưa có file nên tạo và lưu dữ liệu city lấy từ GPS vào file
                FileOutputStream fs_out = context.openFileOutput("historyNews.txt", Context.MODE_APPEND);
                OutputStreamWriter os = new OutputStreamWriter(fs_out);
                os.write("[newsName]" + "\n" + newsName + "\n");
                os.write("[title]" + "\n" + title + "\n");
                os.write("[link]" + "\n" + link + "\n");
                os.write("[image]" + "\n" + image + "\n");
                os.write("[pubDate]" + "\n" + pubDate + "\n");
                os.write("#\n");
                os.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String docNoiDung_Tu_URL(String theUrl) {
        StringBuilder content = new StringBuilder();
        try {
            // create a url object
            URL url = new URL(theUrl);

            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();

            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line = "";

            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private class LoadHtmlBaoBienPhongVideoView extends AsyncTask<String, Void, Void> {

        private String titleHeaderHtml = "";
        private String dateTimeHtml = "";
        private String subTitleHtml = "";
        private String linkVideoVewHtml = "";

        @Override
        protected void onPreExecute() {
            showShimmerLayout();
        }

        @Override
        protected Void doInBackground(String... strings) {

            Document document = null;

            try {
                document = (Document) Jsoup.connect(strings[0]).get();

                if (document != null) {
                    //Lấy  html có thẻ như sau: div#latest-news > div.row > div.col-md-6 hoặc chỉ cần dùng  div.col-md-6

                    //get data titleHeader
                    Elements sub = document.select("div.ft-info-media");
                    if (sub != null) {
                        for (Element element : sub) {
                            Element titleHeaderSubject = element.getElementsByTag("h1").first();

                            if (titleHeaderSubject != null)
                                titleHeaderHtml = titleHeaderSubject.text();
                        }
                    }

                    //get data datetime
                    Elements sub2 = document.select("div.ft-info-media > div.ft-time-media");
                    if (sub2 != null) {
                        for (Element element : sub2) {
                            dateTimeHtml = element.text();
                        }
                    }

                    //get subTitleHtml
                    Elements sub3 = document.select("div.ft-info-media > div.ft-text-info-media");
                    if (sub3 != null) {
                        for (Element element : sub3) {
                            Element subtitleSubject = element.getElementsByTag("p").first();

                            if (subtitleSubject != null)
                                subTitleHtml = subtitleSubject.text();
                        }
                    }

                    // get linkVideoView
                    Elements sub4 = document.select("div.ft-ct-media");
                    if (sub4 != null) {
                        for (Element element : sub4) {
                            Element videoSubject = element.getElementsByTag("source").first();

                            if (videoSubject != null)
                                linkVideoVewHtml = videoSubject.attr("src");
                        }
                    }

                }

            } catch (
                    IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            txt_headerTitle.setText(titleHeaderHtml);
            txt_dateTime.setText(dateTimeHtml);
            txt_subTitle.setText(subTitleHtml);

            LoadControl loadControl = new DefaultLoadControl(); // init load controler
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(); // init band width meter
            TrackSelector trackSelector = new DefaultTrackSelector( // init track selector
                    new AdaptiveTrackSelection.Factory(bandwidthMeter)
            );

            //init simple exo player
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(VideoNewsActivity.this, trackSelector, loadControl);
            // init data source factory
            DefaultHttpDataSourceFactory factory = new DefaultHttpDataSourceFactory("exoplayer_video");
            //init extractors factory
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            //int media source
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(linkVideoVewHtml), factory, extractorsFactory, null, null);
            playerViewBao.setPlayer(simpleExoPlayer); // set player
            playerViewBao.setKeepScreenOn(true);
            simpleExoPlayer.prepare(mediaSource);
            simpleExoPlayer.setPlayWhenReady(false);
            simpleExoPlayer.addListener(new Player.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                }

                @Override
                public void onLoadingChanged(boolean isLoading) {
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    // check codition
                    if (playbackState == Player.STATE_BUFFERING) { // when buffering show progress bar
                        progressBarBao.setVisibility(View.VISIBLE);
                    } else if (playbackState == Player.STATE_READY) { // when ready hide progress bar
                        progressBarBao.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {
                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                }

                @Override
                public void onPositionDiscontinuity(int reason) {
                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                }

                @Override
                public void onSeekProcessed() {
                }
            });

            img_fullscreen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (flag) {
                        img_fullscreen.setImageResource(R.drawable.ic_fullscreen);
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().show();
                            relativeLayoutBottom.setVisibility(View.VISIBLE);
                        }
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerViewBao.getLayoutParams();
                        params.width = params.MATCH_PARENT;
                        params.height = (int) (280 * getApplicationContext().getResources().getDisplayMetrics().density);
                        params.bottomMargin = (int) (0 * getApplicationContext().getResources().getDisplayMetrics().density);
                        playerViewBao.setLayoutParams(params);

                        flag = false;

                    } else { // fullscreen
                        img_fullscreen.setImageResource(R.drawable.ic_fullscreen_exit);
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                        if (getSupportActionBar() != null) {
                            getSupportActionBar().hide();
                            relativeLayoutBottom.setVisibility(View.INVISIBLE);

                        }
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerViewBao.getLayoutParams();
                        params.width = params.MATCH_PARENT;
                        params.height = params.MATCH_PARENT;
                        params.bottomMargin = (int) (55 * getApplicationContext().getResources().getDisplayMetrics().density);
                        playerViewBao.setLayoutParams(params);

                        flag = true;
                    }
                }
            });

            if (VideoNewsActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            hideShimmerLayout();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private class ReadDataRandomRSSVideoView extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            return docNoiDung_Tu_URL(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                XMLDOMParserUtil parser = new XMLDOMParserUtil();
                org.w3c.dom.Document document = parser.getDocument(s);

                // check xem cso ther description trên đầu hay không
                boolean checkDesc = false; //k có
                NodeList nodeListChannel = document.getElementsByTagName("channel");
                for (int i = 0; i < nodeListChannel.getLength(); i++) {
                    NodeList nodeListDescription = document.getElementsByTagName("description");
                    String dataDesc = nodeListDescription.item(i).getTextContent();
                    if (dataDesc == null || dataDesc.contains("<a href=")) {
                        checkDesc = false;
                    } else {
                        checkDesc = true;
                    }
                }

                NodeList nodeList = document.getElementsByTagName("item");
                NodeList nodeListDescription = document.getElementsByTagName("description");

                //đọc xml khi có cụm thẻ <![CDATA[ đứng trướng
                NodeList nodeListTitle = document.getElementsByTagName("title");
                NodeList nodeListLink = document.getElementsByTagName("link");
                NodeList nodeListPubDate = document.getElementsByTagName("pubDate");

                String title = "";
                String link = "";
                String image = "";
                String pubDate = "";

                arrChiTietTinTuc.removeAll(arrChiTietTinTuc);
                for (int i = 0; i < nodeList.getLength(); i++) {

                    org.w3c.dom.Element element = (org.w3c.dom.Element) nodeList.item(i);

                    //trường hợp 1: không có <![CDATA[ bao quanh
                    title = parser.getValue(element, "title");
                    link = parser.getValue(element, "link");
                    pubDate = parser.getValue(element, "pubDate");

                    //----------------trường hợp 2: có <![CDATA[ bao quanh-----//
                    if (title.isEmpty())
                        title = nodeListTitle.item(i + 1).getTextContent();

                    if (link.isEmpty())
                        link = nodeListLink.item(i + 1).getTextContent();

                    if (pubDate.isEmpty())
                        pubDate = nodeListPubDate.item(i + 1).getTextContent();

                    String cdata = "";
                    if (checkDesc == false) { //trường hợp <channel> k có description nên đọc từ i
                        cdata = nodeListDescription.item(i).getTextContent();
                    } else {
                        cdata = nodeListDescription.item(i + 1).getTextContent();
                    }

                    Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
                    Matcher matcher = p.matcher(cdata);

                    if (matcher.find())
                        image = matcher.group(1);

                    //nếu ảnh rỗng thì gán mặc định cho cái ảnh lỗi
                    if (image.isEmpty())
                        image = String.valueOf(R.drawable.ic_no_news);

                    String newsName = "Báo Biên Phòng";
                    arrChiTietTinTuc.add(new ChiTietTinTuc(newsName, title, link, image, pubDate));

                }

                // lấy ngẫu nhiên 6 phần tử khôgn trùng trong mảng arrChiTietTinTuc
                final ArrayList<ChiTietTinTuc> arrTemp = new ArrayList<>(); // chứa 6 itém randrom
                for (int i = 0; i < 6; ) {
                    iNew = randomNews.nextInt(arrChiTietTinTuc.size());
                    if (!vectorNews.contains(iNew)) {
                        i++;
                        vectorNews.add(iNew);
                        arrTemp.add(new ChiTietTinTuc(arrChiTietTinTuc.get(iNew).getNewsName(), arrChiTietTinTuc.get(iNew).getTitle(),
                                arrChiTietTinTuc.get(iNew).getLink(), arrChiTietTinTuc.get(iNew).getImage(), arrChiTietTinTuc.get(iNew).getPubDate()));
                    }
                }

                ChiTietTinTucNormalAdapter adapter = new ChiTietTinTucNormalAdapter(VideoNewsActivity.this, arrTemp);
                linear_list.setAdapter(adapter);
                setListViewHeightBasedOnItems(linear_list);
                adapter.notifyDataSetChanged();
                setListViewHeightBasedOnItems(linear_list);

                linear_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        swipeRefreshLayout.setRefreshing(true);
                        simpleExoPlayer.stop();
                        simpleExoPlayer.release();

                        Intent intent = null;
                        if (saveLoadFileUntil.loadFileTabSelect(VideoNewsActivity.this).contains("Thư viện ảnh")) {
                            intent = new Intent(VideoNewsActivity.this, SlideImageNewsActivity.class);

                        } else if (arrTemp.get(position).getTitle().contains("Infographic")) {
                            intent = new Intent(VideoNewsActivity.this, InfographicNewsActivity.class);

                        } else if (arrTemp.get(position).getTitle().contains("Longform")) {
                            intent = new Intent(VideoNewsActivity.this, LongformNewsActivity.class);

                        } else if (arrTemp.get(position).getLink().contains("www.bienphong.com.vn/videos/")) {
                            intent = new Intent(VideoNewsActivity.this, VideoNewsActivity.class);

                        } else if (arrTemp.get(position).getLink().contains("www.bienphong.com.vn/tin-anh/")) {
                            intent = new Intent(VideoNewsActivity.this, SlideImageNewsActivity.class);

                        } else {
                            intent = new Intent(getApplicationContext(), ReadNewsActivity.class);
                        }

                        intent.putExtra("news_name", arrTemp.get(position).getNewsName());
                        intent.putExtra("title_news", arrTemp.get(position).getTitle());
                        intent.putExtra("link_news", arrTemp.get(position).getLink());
                        intent.putExtra("image_news", arrTemp.get(position).getImage());
                        intent.putExtra("pubdate_news", arrTemp.get(position).getPubDate());
                        intent.putExtra("tab_selected", saveLoadFileUntil.loadFileTabSelect(getApplicationContext()));

                        saveFileHistoryNews(getApplicationContext(),
                                arrTemp.get(position).getNewsName(), arrTemp.get(position).getTitle(),
                                arrTemp.get(position).getLink(), arrTemp.get(position).getImage(),
                                arrTemp.get(position).getPubDate());
                        startActivity(intent);
                        swipeRefreshLayout.setRefreshing(false);

                    }
                });

            } catch (NullPointerException e) {
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (simpleExoPlayer != null) {
            //stop video when ready
            simpleExoPlayer.setPlayWhenReady(false);
            //get playback state
            simpleExoPlayer.getPlaybackState();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (simpleExoPlayer != null) {
            // play video when ready
            simpleExoPlayer.setPlayWhenReady(false);
            //get playback state
            simpleExoPlayer.getPlaybackState();
        }
    }


}