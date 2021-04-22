package com.example.newsapp.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.newsapp.network.CheckConnectionUntil;
import com.example.newsapp.utils.SaveLoadFileUntil;
import com.example.newsapp.utils.XMLDOMParserUntil;
import com.example.newsapp.variables.LinkBaoDauTu;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mahfa.dnswitch.DayNightSwitch;
import com.mahfa.dnswitch.DayNightSwitchListener;
import com.squareup.picasso.Picasso;

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

import static com.example.newsapp.utils.ListViewHeightBasedOnItemsUntil.setListViewHeightBasedOnItems;

public class InfographicNewsActivity extends AppCompatActivity {

    //kiểm tra tên báo
    private final String BAO_DAU_TU = "Báo Đầu Tư";

    // lưu SharedPreferences cho seekbar daynight
    private final String MY_PREFERENCES_DAYNIGHT = "nightModePrefs";
    private final String KEY_ISNIGHTMODE = "isNightMode";
    private SharedPreferences sharedPreferencesDayNight;

    // lưu SharedPreferences cho fontSize
    private SharedPreferences sharedPreferencesTextSize;
    private final String MY_PREFERENCES_TEXTSIZE = "fontSizePrefs";
    private final String KEY_HEADER_TITLE = "header_title";

    private TextView txt_dateTime;
    private TextView txt_xemBaiVietGoc, txt_chiaSe, txt_troVeTrangChu;
    private ToggleButton tb_danhDauReadNews;
    private ImageView img_aa, img_inforgraphic;
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

    private LinkBaoDauTu linkBaoDauTu;

    private Random randomNews;
    private Vector vectorNews;
    int iNew = 0;

    private BottomSheetDialog bottomSheetDialog;
    private SaveLoadFileUntil saveLoadFileUntil;

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
        setContentView(R.layout.activity_infographic_news);


        if (CheckConnectionUntil.haveNetworkConnection(this)) {
            init();
            initDataApp();
            actionBar();
            event();
        } else {
            CheckConnectionUntil.showDialogNoUpdateData(InfographicNewsActivity.this);
        }
    }

    private void init() {
        txt_dateTime = findViewById(R.id.txt_dateTime);
        img_inforgraphic = findViewById(R.id.img_inforgraphic);
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

        linkBaoDauTu = new LinkBaoDauTu();
        arrChiTietTinTuc = new ArrayList<>();
        saveLoadFileUntil = new SaveLoadFileUntil();

        // textSizeDialog
        dialog_box = new Dialog(InfographicNewsActivity.this);

        randomNews = new Random();
        vectorNews = new Vector();

        bottomSheetDialog = new BottomSheetDialog(InfographicNewsActivity.this, R.style.BottomSheetTheme);

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
        setTextSize(txt_dateTime);

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
                getAllDataHtmlNewsName(nameNewsIntent, linkNewsIntent, tabSelectedIntent);  // read news
            }
        });

        txt_troVeTrangChu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameNewsIntent.contains("Báo Đầu Tư")) {
                    startActivity(new Intent(InfographicNewsActivity.this, BaoDauTuActivity.class));
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
                Intent intent = new Intent(InfographicNewsActivity.this, XemBaiBaoGocActivity.class);
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
                    saveLoadFileUntil.saveFileNews(InfographicNewsActivity.this, "bookmarkNews.txt", nameNewsIntent,
                            titleNewsIntent, linkNewsIntent, imageNewsIntent, pubDateNewsIntent);
                    Toast.makeText(InfographicNewsActivity.this, "Đã thêm vào tin đánh dấu", Toast.LENGTH_SHORT).show();
                    tb_danhDauReadNews.setChecked(true);
                    bottomSheetDialog.dismiss();
                } else {
                    saveLoadFileUntil.removeExistsInFileBookmarNews(InfographicNewsActivity.this, nameNewsIntent,
                            titleNewsIntent, linkNewsIntent, imageNewsIntent, pubDateNewsIntent);
                    Toast.makeText(InfographicNewsActivity.this, "Đã bỏ khỏi tin đánh dấu", Toast.LENGTH_SHORT).show();
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

            case BAO_DAU_TU:
                new LoadHtmlBaoDauTuInforgraphic().execute(urlKenhBao);
                checkBaoDauTuLoadTinCungChuyenMuc(tabSelected);
                break;
        }
    }

    private void checkBaoDauTuLoadTinCungChuyenMuc(String tabSelected) {
        if (tabSelected.contains("Trang chủ"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_TRANG_CHU);
        else if (tabSelected.contains("Thời sự"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_THOI_SU);
        else if (tabSelected.contains("Đầu tư"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_DAU_TU);
        else if (tabSelected.contains("Bất động sản"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_BAT_DONG_SAN);
        else if (tabSelected.contains("Quốc tế"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_QUOC_TE);
        else if (tabSelected.contains("Doanh nghiệp"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_DOANH_NGHIEP);
        else if (tabSelected.contains("Doanh nhân"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_DOANH_NHAN);
        else if (tabSelected.contains("Ngân hàng"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_NGAN_HANG);
        else if (tabSelected.contains("Tài chính - Chứng khoán"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_TAI_CHINH_CHUNG_KHOAN);
        else if (tabSelected.contains("Tiêu dùng"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_TIEU_DUNG);
        else if (tabSelected.contains("Ô tô - Xe Máy"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_O_TO_XE_MAY);
        else if (tabSelected.contains("Viễn thông - Công nghệ"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_VIEN_THONG_CONG_NGHE);
        else if (tabSelected.contains("Đầu tư và Cuộc sống"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_DAU_TU_VA_CUOC_SONG);
        else if (tabSelected.contains("Dữ liệu"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_DU_LIEU);
        else if (tabSelected.contains("Điểm nóng"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_DIEM_NONG);
        else if (tabSelected.contains("Không gian khởi nghiệp"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_KHONG_GIAN_KHOI_NGHIEP);
        else if (tabSelected.contains("Thư viện ảnh"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_THU_VIEN_ANH);
        else if (tabSelected.contains("InfoMoney"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_INFOMONEY);
        else if (tabSelected.contains("Doanh nghiệp và Trách nhiệm xã hội"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_DOANH_NGHIEP_VA_TRACH_NHIEM_XA_HOI);
        else if (tabSelected.contains("Sức khỏe doanh nghiệp"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_SUC_KHOE_DOANH_NGHIEP);
        else if (tabSelected.contains("Du lịch"))
            new ReadDataRandomRSSInforgraphic().execute(linkBaoDauTu.LINK_DU_LICH);

    }

    private void showDialogTextSize() {

        askPermission(InfographicNewsActivity.this);
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

                txt_dateTime.setTextSize(pixelsToSp(InfographicNewsActivity.this, 48.0f)); // mặc đinh bên layout là 24sp
                // txt_aTru
                if (pixelsToSp(InfographicNewsActivity.this, txt_dateTime.getTextSize()) == pixelsToSp(InfographicNewsActivity.this, 24.0f)) { // 12sp
                    txt_aTru.setEnabled(true);
                    txt_aTru.setAlpha(1.0f);
                }

                // txt_aCong
                if (pixelsToSp(InfographicNewsActivity.this, txt_dateTime.getTextSize()) != pixelsToSp(InfographicNewsActivity.this, 90.0f)) { // 45sp
                    txt_aCong.setEnabled(true);
                    txt_aCong.setAlpha(1.0f);
                }

            }
        });

        txt_aTru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_dateTime.setTextSize(pixelsToSp(InfographicNewsActivity.this, txt_dateTime.getTextSize() - 6.0f));

                if (pixelsToSp(InfographicNewsActivity.this, txt_dateTime.getTextSize()) == pixelsToSp(InfographicNewsActivity.this, 24.0f)) { // 12sp
                    txt_aTru.setEnabled(false);
                    txt_aTru.setAlpha(0.3f);
                }

                // mở lại txtCong
                if (pixelsToSp(InfographicNewsActivity.this, txt_dateTime.getTextSize()) != pixelsToSp(InfographicNewsActivity.this, 90.0f)) { // 45sp
                    txt_aCong.setEnabled(true);
                    txt_aCong.setAlpha(1.0f);
                }

            }
        });

        txt_aCong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cứ 2.0f tương ứng với 1sp

                txt_dateTime.setTextSize(pixelsToSp(InfographicNewsActivity.this, txt_dateTime.getTextSize() + 6.0f));

                if (pixelsToSp(InfographicNewsActivity.this, txt_dateTime.getTextSize()) == pixelsToSp(InfographicNewsActivity.this, 90.0f)) { // 45sp
                    txt_aCong.setEnabled(false);
                    txt_aCong.setAlpha(0.3f);
                }

                // mở lại txt_aCong
                if (pixelsToSp(InfographicNewsActivity.this, txt_dateTime.getTextSize()) != pixelsToSp(InfographicNewsActivity.this, 24.0f)) { // 12sp
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
        AlertDialog.Builder builder = new AlertDialog.Builder(InfographicNewsActivity.this);
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
                askPermission(InfographicNewsActivity.this);

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

    private void setTextSize(TextView txt_dateTime) {
        txt_dateTime.setTextSize(pixelsToSp(InfographicNewsActivity.this, sharedPreferencesTextSize.getFloat(KEY_HEADER_TITLE, 48.0f)));

        if (pixelsToSp(InfographicNewsActivity.this, txt_dateTime.getTextSize()) == pixelsToSp(InfographicNewsActivity.this, 24.0f)) {
            txt_aTru.setEnabled(false);
            txt_aTru.setAlpha(0.3f);
        } else if (pixelsToSp(InfographicNewsActivity.this, txt_dateTime.getTextSize()) == pixelsToSp(InfographicNewsActivity.this, 90.0f)) {
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

    private class LoadHtmlBaoDauTuInforgraphic extends AsyncTask<String, Void, Void> {

        private String dateTimeHtml = "";
        private String dateTimeHtml2 = "";
        private String imageHtml = "";

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

                    //get data datetime
                    Elements sub22 = document.select("div.mr-auto > a.author");
                    if (sub22 != null) {
                        for (Element element : sub22) {
                            dateTimeHtml = element.text();
                        }
                    }
                    Elements sub2 = document.select("div.mr-auto > span.post-time");
                    if (sub2 != null) {
                        for (Element element : sub2) {
                            dateTimeHtml2 = element.text();
                        }
                    }

                    // get link image slide
                    Elements sub4 = document.select("body > p > img");
                    if (sub4 != null) {
                        for (Element element : sub4) {
                            String imageData = element.attr("src");
                            if (imageData != null) {
                                imageHtml = imageData;
                            }
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

            txt_dateTime.setText(dateTimeHtml + " " + dateTimeHtml2);

            Picasso.with(InfographicNewsActivity.this).load(Uri.parse(imageHtml))
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.errorimage)
                    .into(img_inforgraphic);

            hideShimmerLayout();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private class ReadDataRandomRSSInforgraphic extends AsyncTask<String, Integer, String> {

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

                XMLDOMParserUntil parser = new XMLDOMParserUntil();
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

                    String newsName = "";
                    if (link.contains("baodautu.vn/"))
                        newsName = BAO_DAU_TU;

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

                ChiTietTinTucNormalAdapter adapter = new ChiTietTinTucNormalAdapter(InfographicNewsActivity.this, arrTemp);
                linear_list.setAdapter(adapter);
                setListViewHeightBasedOnItems(linear_list);
                adapter.notifyDataSetChanged();
                setListViewHeightBasedOnItems(linear_list);

                linear_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        swipeRefreshLayout.setRefreshing(true);

                        Intent intent = null;
                        if (saveLoadFileUntil.loadFileTabSelect(InfographicNewsActivity.this).contains("Thư viện ảnh")) {
                            intent = new Intent(InfographicNewsActivity.this, SlideImageNewsActivity.class);

                        } else if (arrTemp.get(position).getTitle().contains("Infographic")) {
                            intent = new Intent(InfographicNewsActivity.this, InfographicNewsActivity.class);

                        } else if (arrTemp.get(position).getTitle().contains("Longform")) {
                            intent = new Intent(InfographicNewsActivity.this, LongformNewsActivity.class);

                        } else if (arrTemp.get(position).getLink().contains("www.bienphong.com.vn/videos/")) {
                            intent = new Intent(InfographicNewsActivity.this, VideoNewsActivity.class);

                        } else if (arrTemp.get(position).getLink().contains("www.bienphong.com.vn/tin-anh/")) {
                            intent = new Intent(InfographicNewsActivity.this, SlideImageNewsActivity.class);

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

}