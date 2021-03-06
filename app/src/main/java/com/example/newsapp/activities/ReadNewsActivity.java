package com.example.newsapp.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.newsapp.R;
import com.example.newsapp.adapters.ChiTietTinTucNormalAdapter;
import com.example.newsapp.adapters.SlideImageHolderAdapter;
import com.example.newsapp.models.ChiTietTinTuc;
import com.example.newsapp.models.ImagePlaceHolder;
import com.example.newsapp.network.CheckConnectionNetwork;
import com.example.newsapp.utils.SaveLoadFileUtil;
import com.example.newsapp.utils.XMLDOMParserUtil;
import com.example.newsapp.variables.LinkBaoBienPhong;
import com.example.newsapp.variables.LinkBaoDauTu;
import com.example.newsapp.variables.LinkBaoNgoiSao;
import com.example.newsapp.variables.LinkBaoNguoiLaoDong;
import com.example.newsapp.variables.LinkBaoPhapLuat;
import com.example.newsapp.variables.LinkBaoTuoiTre;
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

public class ReadNewsActivity extends AppCompatActivity {

    //ki???m tra t??n b??o
    private final String BAO_TUOI_TRE = "B??o Tu???i Tr???";
    private final String BAO_BIEN_PHONG = "B??o Bi??n Ph??ng";
    private final String BAO_DAU_TU = "B??o ?????u T??";
    private final String BAO_PHAP_LUAT = "B??o Ph??p Lu???t";
    private final String BAO_NGOI_SAO = "B??o Ng??i Sao";
    private final String BAO_NGUOI_LAO_DONG = "B??o Ng?????i Lao ?????ng";

    // l??u SharedPreferences cho seekbar daynight
    private final String MY_PREFERENCES_DAYNIGHT = "nightModePrefs";
    private final String KEY_ISNIGHTMODE = "isNightMode";
    private SharedPreferences sharedPreferencesDayNight;

    // l??u SharedPreferences cho fontSize
    private SharedPreferences sharedPreferencesTextSize;
    private final String MY_PREFERENCES_TEXTSIZE = "fontSizePrefs";
    private final String KEY_HEADER_TITLE = "header_title";
    private final String KEY_DATE_TIME = "date_time";
    private final String KEY_SUB_TITLE = "sub_title";
    private final String KEY_ALT_IMAGE = "alt_image";
    private final String KEY_NOI_DUNG_BAO = "noi_dung_bao";

    private TextView txt_headerTitle, txt_dateTime, txt_subTitle, txt_noiDungBao;
    private TextView txt_xemBaiVietGoc, txt_chiaSe, txt_troVeTrangChu;
    private ToggleButton tb_danhDauReadNews;
    private ImageView img_aa;
    private ImageView imageViewBao;
    private ViewPager2 viewPagerImageSlide;
    private Toolbar toolbar;
    private NestedScrollView nestedsv;
    private RelativeLayout relativeLayoutBottom;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout linear_imageList;
    private ListView linear_list;
    private ShimmerLayout shimmer_view_contain;

    // getIntent from Bao Activity
    private String titleNewsIntent = "";
    private String nameNewsIntent = "";
    private String linkNewsIntent = "";
    private String imageNewsIntent = "";
    private String pubDateNewsIntent = "";
    private String tabSelectedIntent = "";

    private ArrayList<String> arrNoiDungBaoHtml;
    private ArrayList<ChiTietTinTuc> arrChiTietTinTuc;

    private LinkBaoTuoiTre linkBaoTuoiTre;
    private LinkBaoBienPhong linkBaoBienPhong;
    private LinkBaoDauTu linkBaoDauTu;
    private LinkBaoPhapLuat linkBaoPhapLuat;
    private LinkBaoNgoiSao linkBaoNgoiSao;
    private LinkBaoNguoiLaoDong linkBaoNguoiLaoDong;

    private Random randomNews;
    private Vector vectorNews;
    int iNew = 0;

    private BottomSheetDialog bottomSheetDialog;
    private SaveLoadFileUtil saveLoadFileUntil;

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
        setContentView(R.layout.activity_read_news);

        if (CheckConnectionNetwork.haveNetworkConnection(this)) {
            init();
            actionBar();
            initDataApp();
            events();
        } else {
            CheckConnectionNetwork.showDialogNoUpdateData(ReadNewsActivity.this);
        }
    }

    private void init() {
        txt_headerTitle = findViewById(R.id.txt_headerTitle);
        txt_dateTime = findViewById(R.id.txt_dateTime);
        txt_subTitle = findViewById(R.id.txt_subTitle);
        txt_noiDungBao = findViewById(R.id.txt_noiDungBao);
        txt_xemBaiVietGoc = findViewById(R.id.txt_xemBaiVietGoc);
        txt_chiaSe = findViewById(R.id.txt_chiaSe);
        viewPagerImageSlide = findViewById(R.id.viewPagerImageSlide);
        txt_troVeTrangChu = findViewById(R.id.txt_troVeTrangChu);
        imageViewBao = findViewById(R.id.imageViewBao);
        img_aa = findViewById(R.id.img_aa);
        toolbar = findViewById(R.id.toolbar);
        nestedsv = findViewById(R.id.nestedsv);
        relativeLayoutBottom = findViewById(R.id.relativeLayoutBottom);
        linear_list = findViewById(R.id.linear_list);
        linear_imageList = findViewById(R.id.linear_imageList);
        tb_danhDauReadNews = findViewById(R.id.tb_danhDauReadNews);
        shimmer_view_contain = findViewById(R.id.shimmer_view_contain);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        linkBaoTuoiTre = new LinkBaoTuoiTre();
        linkBaoBienPhong = new LinkBaoBienPhong();
        linkBaoDauTu = new LinkBaoDauTu();
        linkBaoPhapLuat = new LinkBaoPhapLuat();
        linkBaoNgoiSao = new LinkBaoNgoiSao();
        linkBaoNguoiLaoDong = new LinkBaoNguoiLaoDong();

        arrChiTietTinTuc = new ArrayList<>();
        arrNoiDungBaoHtml = new ArrayList<>();
        saveLoadFileUntil = new SaveLoadFileUtil();

        // init textSizeDialog
        dialog_box = new Dialog(ReadNewsActivity.this);

        randomNews = new Random();
        vectorNews = new Vector();

        bottomSheetDialog = new BottomSheetDialog(ReadNewsActivity.this, R.style.BottomSheetTheme);


    }

    private void initDataApp() {
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.GREEN);

        //textSize dialog
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

        checkNightModeActivated(); // check dayNight
        setTextSize(txt_headerTitle, txt_dateTime, txt_subTitle, txt_noiDungBao);

        // Check ????nh d???u
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

    private void events() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recreate();  // read news
            }
        });

        txt_troVeTrangChu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameNewsIntent.contains("B??o Tu???i Tr???")) {
                    startActivity(new Intent(ReadNewsActivity.this, BaoTuoiTreActivity.class));
                    /*   overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);*/
                } else if (nameNewsIntent.contains("B??o Bi??n Ph??ng")) {
                    startActivity(new Intent(ReadNewsActivity.this, BaoBienPhongActivity.class));
                    /* overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);*/
                } else if (nameNewsIntent.contains("B??o ?????u T??")) {
                    startActivity(new Intent(ReadNewsActivity.this, BaoDauTuActivity.class));
                    /*  overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);*/
                } else if (nameNewsIntent.contains("B??o Ph??p Lu???t")) {
                    startActivity(new Intent(ReadNewsActivity.this, BaoPhapLuatActivity.class));
                    /*  overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);*/
                } else if (nameNewsIntent.contains("B??o Ng??i Sao")) {
                    startActivity(new Intent(ReadNewsActivity.this, BaoNgoiSaoActivity.class));
                    /*  overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);*/
                } else if (nameNewsIntent.contains("B??o Ng?????i Lao ?????ng")) {
                    startActivity(new Intent(ReadNewsActivity.this, BaoNguoiLaoDongActivity.class));
                    /*  overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);*/
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
                Intent intent = new Intent(ReadNewsActivity.this, XemBaiBaoGocActivity.class);
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
                    saveLoadFileUntil.saveFileNews(ReadNewsActivity.this, "bookmarkNews.txt", nameNewsIntent,
                            titleNewsIntent, linkNewsIntent, imageNewsIntent, pubDateNewsIntent);
                    Toast.makeText(ReadNewsActivity.this, "???? th??m v??o tin ????nh d???u", Toast.LENGTH_SHORT).show();
                    tb_danhDauReadNews.setChecked(true);
                    bottomSheetDialog.dismiss();
                } else {
                    saveLoadFileUntil.removeExistsInFileBookmarNews(ReadNewsActivity.this, nameNewsIntent,
                            titleNewsIntent, linkNewsIntent, imageNewsIntent, pubDateNewsIntent);
                    Toast.makeText(ReadNewsActivity.this, "???? b??? kh???i tin ????nh d???u", Toast.LENGTH_SHORT).show();
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
            case BAO_TUOI_TRE:
                new LoadHtmlBaoTuoiTre().execute(urlKenhBao);
                checkBaoTuoiTreLoadTinCungChuyenMuc(tabSelected);
                break;

            case BAO_BIEN_PHONG:
                new LoadHtmlBaoBienPhong().execute(urlKenhBao);
                checkBaoBienPhongLoadTinCungChuyenMuc(tabSelected);
                break;

            case BAO_DAU_TU:
                new LoadHtmlBaoDauTu().execute(urlKenhBao);
                checkBaoDauTuLoadTinCungChuyenMuc(tabSelected);
                break;

            case BAO_PHAP_LUAT:
                new LoadHtmlBaoPhapLuat().execute(urlKenhBao);
                checkBaoPhapLuatLoadTinCungChuyenMuc(tabSelected);
                break;

            case BAO_NGOI_SAO:
                new LoadHtmlBaoNgoiSao().execute(urlKenhBao);
                checkBaoNgoiSaoLoadTinCungChuyenMuc(tabSelected);
                break;

            case BAO_NGUOI_LAO_DONG:
                new LoadHtmlBaoNguoiLaoDong().execute(urlKenhBao);
                checkBaoNguoiLaoDongLoadTinCungChuyenMuc(tabSelected);
                break;
        }
    }

    private void checkBaoTuoiTreLoadTinCungChuyenMuc(String tabSelected) {
        if (tabSelected.contains("Trang ch???"))
            new ReadDataRandomRSS().execute(linkBaoTuoiTre.LINK_TRANG_CHU);
        else if (tabSelected.contains("Th??? gi???i"))
            new ReadDataRandomRSS().execute(linkBaoTuoiTre.LINK_THE_GIOI);
        else if (tabSelected.contains("Kinh doanh"))
            new ReadDataRandomRSS().execute(linkBaoTuoiTre.LINK_KINH_DOANH);
        else if (tabSelected.contains("Xe"))
            new ReadDataRandomRSS().execute(linkBaoTuoiTre.LINK_XE);
        else if (tabSelected.contains("V??n h??a"))
            new ReadDataRandomRSS().execute(linkBaoTuoiTre.LINK_VAN_HOA);
        else if (tabSelected.contains("Th??? thao"))
            new ReadDataRandomRSS().execute(linkBaoTuoiTre.LINK_THE_THAO);
        else if (tabSelected.contains("Khoa h???c"))
            new ReadDataRandomRSS().execute(linkBaoTuoiTre.LINK_KHOA_HOC);
        else if (tabSelected.contains("Gi??? th???t"))
            new ReadDataRandomRSS().execute(linkBaoTuoiTre.LINK_GIA_THAT);
        else if (tabSelected.contains("B???n ?????c l??m b??o"))
            new ReadDataRandomRSS().execute(linkBaoTuoiTre.LINK_BAN_DOC_LAM_BAO);
        else if (tabSelected.contains("Th???i s???"))
            new ReadDataRandomRSS().execute(linkBaoTuoiTre.LINK_THOI_SU);
        else if (tabSelected.contains("Ph??p lu???t"))
            new ReadDataRandomRSS().execute(linkBaoTuoiTre.LINK_PHAP_LUAT);
        else if (tabSelected.contains("C??ng ngh???"))
            new ReadDataRandomRSS().execute(linkBaoTuoiTre.LINK_CONG_NGHE);
        else if (tabSelected.contains("Nh???p s???ng tr???"))
            new ReadDataRandomRSS().execute(linkBaoTuoiTre.LINK_NHIP_SONG_TRE);
        else if (tabSelected.contains("Gi???i tr??"))
            new ReadDataRandomRSS().execute(linkBaoTuoiTre.LINK_GIAI_TRI);
        else if (tabSelected.contains("Gi??o d???c"))
            new ReadDataRandomRSS().execute(linkBaoTuoiTre.LINK_GIAO_DUC);
        else if (tabSelected.contains("S???c kh???e"))
            new ReadDataRandomRSS().execute(linkBaoTuoiTre.LINK_SUC_KHOE);
        else if (tabSelected.contains("Th?? gi???n"))
            new ReadDataRandomRSS().execute(linkBaoTuoiTre.LINK_THU_GIAN);
        else if (tabSelected.contains("Du l???ch"))
            new ReadDataRandomRSS().execute(linkBaoTuoiTre.LINK_DU_LICH);
    }

    private void checkBaoBienPhongLoadTinCungChuyenMuc(String tabSelected) {
        if (tabSelected.contains("Ch??nh tr???"))
            new ReadDataRandomRSS().execute(linkBaoBienPhong.LINK_CHINH_TRI);
        else if (tabSelected.contains("Theo g????ng B??c"))
            new ReadDataRandomRSS().execute(linkBaoBienPhong.LINK_THEO_GUONG_BAC);
        else if (tabSelected.contains("Qu??n s??? - Qu???c ph??ng"))
            new ReadDataRandomRSS().execute(linkBaoBienPhong.LINK_QUAN_SU_QUOC_PHONG);
        else if (tabSelected.contains("Bi??n ph??ng to??n d??n"))
            new ReadDataRandomRSS().execute(linkBaoBienPhong.LINK_BIEN_PHONG_TOAN_DAN);
        else if (tabSelected.contains("X?? h???i"))
            new ReadDataRandomRSS().execute(linkBaoBienPhong.LINK_XA_HOI);
        else if (tabSelected.contains("Kinh t???"))
            new ReadDataRandomRSS().execute(linkBaoBienPhong.LINK_KINH_TE);
        else if (tabSelected.contains("Ph??p lu???t"))
            new ReadDataRandomRSS().execute(linkBaoBienPhong.LINK_PHAP_LUAT);
        else if (tabSelected.contains("V??n h??a"))
            new ReadDataRandomRSS().execute(linkBaoBienPhong.LINK_VAN_HOA);
        else if (tabSelected.contains("Th??? thao"))
            new ReadDataRandomRSS().execute(linkBaoBienPhong.LINK_THE_THAO);
        else if (tabSelected.contains("Ph??ng s???"))
            new ReadDataRandomRSS().execute(linkBaoBienPhong.LINK_PHONG_SU);
        else if (tabSelected.contains("Qu???c t???"))
            new ReadDataRandomRSS().execute(linkBaoBienPhong.LINK_QUOC_TE);
        else if (tabSelected.contains("Ch??? quy???n bi??n gi???i, bi???n, ?????o Vi???t Nam"))
            new ReadDataRandomRSS().execute(linkBaoBienPhong.LINK_CHU_QUYEN_BIEN_GIOI_BIEN_DAO_VN);
        else if (tabSelected.contains("Ph??ng s??? ???nh"))
            new ReadDataRandomRSS().execute(linkBaoBienPhong.LINK_PHONG_SU_ANH);
        else if (tabSelected.contains("Video"))
            new ReadDataRandomRSS().execute(linkBaoBienPhong.LINK_VIDEO);

    }

    private void checkBaoDauTuLoadTinCungChuyenMuc(String tabSelected) {
        if (tabSelected.contains("Trang ch???"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_TRANG_CHU);
        else if (tabSelected.contains("Th???i s???"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_THOI_SU);
        else if (tabSelected.contains("?????u t??"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_DAU_TU);
        else if (tabSelected.contains("B???t ?????ng s???n"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_BAT_DONG_SAN);
        else if (tabSelected.contains("Qu???c t???"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_QUOC_TE);
        else if (tabSelected.contains("Doanh nghi???p"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_DOANH_NGHIEP);
        else if (tabSelected.contains("Doanh nh??n"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_DOANH_NHAN);
        else if (tabSelected.contains("Ng??n h??ng"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_NGAN_HANG);
        else if (tabSelected.contains("T??i ch??nh - Ch???ng kho??n"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_TAI_CHINH_CHUNG_KHOAN);
        else if (tabSelected.contains("Ti??u d??ng"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_TIEU_DUNG);
        else if (tabSelected.contains("?? t?? - Xe M??y"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_O_TO_XE_MAY);
        else if (tabSelected.contains("Vi???n th??ng - C??ng ngh???"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_VIEN_THONG_CONG_NGHE);
        else if (tabSelected.contains("?????u t?? v?? Cu???c s???ng"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_DAU_TU_VA_CUOC_SONG);
        else if (tabSelected.contains("D??? li???u"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_DU_LIEU);
        else if (tabSelected.contains("??i???m n??ng"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_DIEM_NONG);
        else if (tabSelected.contains("Kh??ng gian kh???i nghi???p"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_KHONG_GIAN_KHOI_NGHIEP);
        else if (tabSelected.contains("Th?? vi???n ???nh"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_THU_VIEN_ANH);
        else if (tabSelected.contains("InfoMoney"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_INFOMONEY);
        else if (tabSelected.contains("Doanh nghi???p v?? Tr??ch nhi???m x?? h???i"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_DOANH_NGHIEP_VA_TRACH_NHIEM_XA_HOI);
        else if (tabSelected.contains("S???c kh???e doanh nghi???p"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_SUC_KHOE_DOANH_NGHIEP);
        else if (tabSelected.contains("Du l???ch"))
            new ReadDataRandomRSS().execute(linkBaoDauTu.LINK_DU_LICH);

    }

    private void checkBaoPhapLuatLoadTinCungChuyenMuc(String tabSelected) {
        if (tabSelected.contains("Home"))
            new ReadDataRandomRSS().execute(linkBaoPhapLuat.LINK_HOME);
        else if (tabSelected.contains("Th???i s???"))
            new ReadDataRandomRSS().execute(linkBaoPhapLuat.LINK_THOI_SU);
        else if (tabSelected.contains("T?? ph??p"))
            new ReadDataRandomRSS().execute(linkBaoPhapLuat.LINK_TU_PHAP);
        else if (tabSelected.contains("Kinh t???"))
            new ReadDataRandomRSS().execute(linkBaoPhapLuat.LINK_KINH_TE);
        else if (tabSelected.contains("Ph??p lu???t"))
            new ReadDataRandomRSS().execute(linkBaoPhapLuat.LINK_PHAP_LUAT);
        else if (tabSelected.contains("S??? ki???n & B??n lu???n"))
            new ReadDataRandomRSS().execute(linkBaoPhapLuat.LINK_SU_KIEN_BAN_LUAN);
        else if (tabSelected.contains("D??n sinh"))
            new ReadDataRandomRSS().execute(linkBaoPhapLuat.LINK_DAN_SINH);
        else if (tabSelected.contains("B???n ?????c"))
            new ReadDataRandomRSS().execute(linkBaoPhapLuat.LINK_BAN_DOC);
        else if (tabSelected.contains("Ti??u d??ng & D?? lu???n"))
            new ReadDataRandomRSS().execute(linkBaoPhapLuat.LINK_TIEU_DUNG_DU_LUAN);
        else if (tabSelected.contains("B???t ?????ng s???n"))
            new ReadDataRandomRSS().execute(linkBaoPhapLuat.LINK_BAT_DONG_SAN);
        else if (tabSelected.contains("T?? v???n 365"))
            new ReadDataRandomRSS().execute(linkBaoPhapLuat.LINK_TU_VAN_365);
        else if (tabSelected.contains("S???ng kh???e"))
            new ReadDataRandomRSS().execute(linkBaoPhapLuat.LINK_SONG_KHOE);
        else if (tabSelected.contains("Th??? gi???i Sao"))
            new ReadDataRandomRSS().execute(linkBaoPhapLuat.LINK_THE_GIO_SAO);
        else if (tabSelected.contains("Xe"))
            new ReadDataRandomRSS().execute(linkBaoPhapLuat.LINK_XE);
    }

    private void checkBaoNgoiSaoLoadTinCungChuyenMuc(String tabSelected) {
        if (tabSelected.contains("Tin n???i b???t"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_TIN_NOI_BAT);
        else if (tabSelected.contains("Tin m???i nh???t"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_TIN_MOI_NHAT);
        else if (tabSelected.contains("H???u tr?????ng"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_HAU_TRUONG);
        else if (tabSelected.contains("Th???i cu???c"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_THOI_CUOC);
        else if (tabSelected.contains("Showbiz Vi???t"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_SHOWBIZ_VIET);
        else if (tabSelected.contains("??u M???"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_AU_MY);
        else if (tabSelected.contains("H??nh s???"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_HINH_SU);
        else if (tabSelected.contains("Th???i trang"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_THOI_TRANG);
        else if (tabSelected.contains("D??n ch??i"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_DAN_CHOI);
        else if (tabSelected.contains("C?? d??u"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_CO_DAU);
        else if (tabSelected.contains("???nh c?????i"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_ANH_CUOI);
        else if (tabSelected.contains("Th??? thao"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_THE_THAO);
        else if (tabSelected.contains("C?????i"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_CUOI);
        else if (tabSelected.contains("Ch??u ??"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_CHAU_A);
        else if (tabSelected.contains("Chuy???n l???"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_CHUYEN_LA);
        else if (tabSelected.contains("Th????ng tr?????ng"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_THUONG_TRUONG);
        else if (tabSelected.contains("L??m ?????p"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_LAM_DEP);
        else if (tabSelected.contains("??n ch??i"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_AN_CHOI);
        else if (tabSelected.contains("Bu??n chuy???n"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_BUON_CHUYEN);
        else if (tabSelected.contains("C???m nang"))
            new ReadDataRandomRSS().execute(linkBaoNgoiSao.LINK_CAM_NANG);

    }

    private void checkBaoNguoiLaoDongLoadTinCungChuyenMuc(String tabSelected) {
        if (tabSelected.contains("Tin m???i nh???t"))
            new ReadDataRandomRSS().execute(linkBaoNguoiLaoDong.LINK_TIN_MOI_NHAT);
        else if (tabSelected.contains("Th???i s???"))
            new ReadDataRandomRSS().execute(linkBaoNguoiLaoDong.LINK_THOI_SU);
        else if (tabSelected.contains("Th???i s??? qu???c t???"))
            new ReadDataRandomRSS().execute(linkBaoNguoiLaoDong.LINK_THOI_SU_QUOC_TE);
        else if (tabSelected.contains("C??ng ??o??n"))
            new ReadDataRandomRSS().execute(linkBaoNguoiLaoDong.LINK_CONG_DOAN);
        else if (tabSelected.contains("B???n ?????c"))
            new ReadDataRandomRSS().execute(linkBaoNguoiLaoDong.LINK_BAN_DOC);
        else if (tabSelected.contains("Kinh t???"))
            new ReadDataRandomRSS().execute(linkBaoNguoiLaoDong.LINK_KINH_TE);
        else if (tabSelected.contains("S???c kh???e"))
            new ReadDataRandomRSS().execute(linkBaoNguoiLaoDong.LINK_SUC_KHOE);
        else if (tabSelected.contains("Gi??o d???c"))
            new ReadDataRandomRSS().execute(linkBaoNguoiLaoDong.LINK_GIAO_DUC);
        else if (tabSelected.contains("Ph??p lu???t"))
            new ReadDataRandomRSS().execute(linkBaoNguoiLaoDong.LINK_PHAP_LUAT);
        else if (tabSelected.contains("Gi???i tr??"))
            new ReadDataRandomRSS().execute(linkBaoNguoiLaoDong.LINK_GIAI_TRI);
        else if (tabSelected.contains("Th??? thao"))
            new ReadDataRandomRSS().execute(linkBaoNguoiLaoDong.LINK_THE_THAO);
        else if (tabSelected.contains("C??ng ngh???"))
            new ReadDataRandomRSS().execute(linkBaoNguoiLaoDong.LINK_CONG_NGHE);
        else if (tabSelected.contains("??i???m ?????n"))
            new ReadDataRandomRSS().execute(linkBaoNguoiLaoDong.LINK_DIEM_DEN);
        else if (tabSelected.contains("L?? t?????ng s???ng"))
            new ReadDataRandomRSS().execute(linkBaoNguoiLaoDong.LINK_LY_TUONG_SONG);
        else if (tabSelected.contains("N??i th???ng"))
            new ReadDataRandomRSS().execute(linkBaoNguoiLaoDong.LINK_NOI_THANG);
        else if (tabSelected.contains("Tin ?????c quy???n"))
            new ReadDataRandomRSS().execute(linkBaoNguoiLaoDong.LINK_TIN_DOC_QUYEN);

    }

    private void showDialogTextSize() {

        askPermission(ReadNewsActivity.this);
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

                txt_headerTitle.setTextSize(pixelsToSp(ReadNewsActivity.this, 48.0f)); // m???c ??inh b??n layout l?? 24sp
                txt_dateTime.setTextSize(pixelsToSp(ReadNewsActivity.this, 28.0f)); // m???c ??inh b??n layout l?? 14sp
                txt_subTitle.setTextSize(pixelsToSp(ReadNewsActivity.this, 40.0f)); // m???c ??inh b??n layout l?? 20sp
                txt_noiDungBao.setTextSize(pixelsToSp(ReadNewsActivity.this, 40.0f)); // m???c ??inh b??n layout l?? 20sp

                // txt_aTru
                if (pixelsToSp(ReadNewsActivity.this, txt_headerTitle.getTextSize()) == pixelsToSp(ReadNewsActivity.this, 24.0f)) { // 12sp
                    txt_aTru.setEnabled(true);
                    txt_aTru.setAlpha(1.0f);
                }

                // txt_aCong
                if (pixelsToSp(ReadNewsActivity.this, txt_headerTitle.getTextSize()) != pixelsToSp(ReadNewsActivity.this, 90.0f)) { // 45sp
                    txt_aCong.setEnabled(true);
                    txt_aCong.setAlpha(1.0f);
                }

                //ghi file
                saveTextSize(txt_headerTitle.getTextSize(), txt_dateTime.getTextSize(),
                        txt_subTitle.getTextSize(), txt_noiDungBao.getTextSize());
            }
        });

        txt_aTru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_headerTitle.setTextSize(pixelsToSp(ReadNewsActivity.this, txt_headerTitle.getTextSize() - 6.0f));
                txt_dateTime.setTextSize(pixelsToSp(ReadNewsActivity.this, txt_dateTime.getTextSize() - 6.0f));
                txt_subTitle.setTextSize(pixelsToSp(ReadNewsActivity.this, txt_subTitle.getTextSize() - 6.0f));
                txt_noiDungBao.setTextSize(pixelsToSp(ReadNewsActivity.this, txt_noiDungBao.getTextSize() - 6.0f));

                if (pixelsToSp(ReadNewsActivity.this, txt_headerTitle.getTextSize()) == pixelsToSp(ReadNewsActivity.this, 24.0f)) { // 12sp
                    txt_aTru.setEnabled(false);
                    txt_aTru.setAlpha(0.3f);
                }

                // m??? l???i txtCong
                if (pixelsToSp(ReadNewsActivity.this, txt_headerTitle.getTextSize()) != pixelsToSp(ReadNewsActivity.this, 90.0f)) { // 45sp
                    txt_aCong.setEnabled(true);
                    txt_aCong.setAlpha(1.0f);
                }

                //ghi file
                saveTextSize(txt_headerTitle.getTextSize(), txt_dateTime.getTextSize(),
                        txt_subTitle.getTextSize(), txt_noiDungBao.getTextSize());
            }
        });

        txt_aCong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // c??? 2.0f t????ng ???ng v???i 1sp

                txt_headerTitle.setTextSize(pixelsToSp(ReadNewsActivity.this, txt_headerTitle.getTextSize() + 6.0f));
                txt_dateTime.setTextSize(pixelsToSp(ReadNewsActivity.this, txt_dateTime.getTextSize() + 6.0f));
                txt_subTitle.setTextSize(pixelsToSp(ReadNewsActivity.this, txt_subTitle.getTextSize() + 6.0f));
                txt_noiDungBao.setTextSize(pixelsToSp(ReadNewsActivity.this, txt_noiDungBao.getTextSize() + 6.0f));

                if (pixelsToSp(ReadNewsActivity.this, txt_headerTitle.getTextSize()) == pixelsToSp(ReadNewsActivity.this, 90.0f)) { // 45sp
                    txt_aCong.setEnabled(false);
                    txt_aCong.setAlpha(0.3f);
                }

                // m??? l???i txt_aCong
                if (pixelsToSp(ReadNewsActivity.this, txt_headerTitle.getTextSize()) != pixelsToSp(ReadNewsActivity.this, 24.0f)) { // 12sp
                    txt_aTru.setEnabled(true);
                    txt_aTru.setAlpha(1.0f);
                }

                //ghi file
                saveTextSize(txt_headerTitle.getTextSize(), txt_dateTime.getTextSize(),
                        txt_subTitle.getTextSize(), txt_noiDungBao.getTextSize());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ReadNewsActivity.this);
        builder.setTitle("L???i c???p quy???n");
        builder.setMessage("B???n c???n ph???i c???p quy???n cho ???ng d???ng cho vi???c thao t??c tr??n h??? th???ng?");
        builder.setNegativeButton(Html.fromHtml("<font color='#FF3D00'>Kh??ng</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        builder.setPositiveButton(Html.fromHtml("<font color='#03A9F4'>???? hi???u</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                askPermission(ReadNewsActivity.this);

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

    private void saveTextSize(float txt_headerTitle, float txt_dateTime, float txt_subTitle, float txt_noiDungBao) {
        SharedPreferences.Editor editor = sharedPreferencesTextSize.edit();
        editor.putFloat(KEY_HEADER_TITLE, txt_headerTitle);
        editor.putFloat(KEY_DATE_TIME, txt_dateTime);
        editor.putFloat(KEY_SUB_TITLE, txt_subTitle);
        editor.putFloat(KEY_NOI_DUNG_BAO, txt_noiDungBao);
        editor.apply();
    }

    private void setTextSize(TextView txt_headerTitle, TextView txt_dateTime, TextView txt_subTitle, TextView txt_noiDungBao) {
        txt_headerTitle.setTextSize(pixelsToSp(ReadNewsActivity.this, sharedPreferencesTextSize.getFloat(KEY_HEADER_TITLE, 48.0f)));
        txt_dateTime.setTextSize(pixelsToSp(ReadNewsActivity.this, sharedPreferencesTextSize.getFloat(KEY_DATE_TIME, 28.0f)));
        txt_subTitle.setTextSize(pixelsToSp(ReadNewsActivity.this, sharedPreferencesTextSize.getFloat(KEY_SUB_TITLE, 40.0f)));
        txt_noiDungBao.setTextSize(pixelsToSp(ReadNewsActivity.this, sharedPreferencesTextSize.getFloat(KEY_NOI_DUNG_BAO, 40.0f)));

        if (pixelsToSp(ReadNewsActivity.this, txt_headerTitle.getTextSize()) == pixelsToSp(ReadNewsActivity.this, 24.0f)) {
            txt_aTru.setEnabled(false);
            txt_aTru.setAlpha(0.3f);
        } else if (pixelsToSp(ReadNewsActivity.this, txt_headerTitle.getTextSize()) == pixelsToSp(ReadNewsActivity.this, 90.0f)) {
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

    private class LoadHtmlBaoTuoiTre extends AsyncTask<String, Void, Void> {

        private String titleHeaderHtml = "";
        private String dateTimeHtml = "";
        private String subTitleHtml = "";
        private String imageHtml = "";
        private String altImageHtml = "";

        // lisview image
        private String imageData = "", placeHolderImageData = "";
        private ArrayList<ImagePlaceHolder> arrImagePlaceHolder; // m???ng ch???a ???nh v?? desc image


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
                    //L???y  html c?? th??? nh?? sau: div#latest-news > div.row > div.col-md-6 ho???c ch??? c???n d??ng  div.col-md-6

                    //get datat titleHeader, datetime
                    Elements sub = document.select("div.content > div.content-detail > div.w980");
                    if (sub != null) {
                        for (Element element : sub) {
                            Element titleHeaderSubject = element.getElementsByTag("h1").first();
                            Element dateTimeSubject = element.getElementsByClass("date-time").first();

                            if (titleHeaderSubject != null)
                                titleHeaderHtml = titleHeaderSubject.text();

                            if (dateTimeSubject != null)
                                dateTimeHtml = dateTimeSubject.text();
                        }
                    }

                    //get data subTitle, image
                    Elements sub2 = document.select("div.main-content-body");
                    if (sub2 != null) {
                        for (Element element : sub2) {
                            Element subTitleSubject = element.getElementsByTag("h2").first();
                            if (subTitleSubject != null)
                                subTitleHtml = subTitleSubject.text();
                        }
                    }

                    // get noi dung
                    Elements elms = document.select("div.main-content-body > div#main-detail-body > p");
                    if (elms != null) {
                        for (int i = 0; i < elms.size(); i++) {
                            String noiDungBaoSubject = elms.get(i).text();
                            if (noiDungBaoSubject != null)
                                arrNoiDungBaoHtml.add(noiDungBaoSubject);
                        }
                    }

                    // lisview image
                    arrImagePlaceHolder = new ArrayList<>();
                    Elements subImagePlace = document.select("div#main-detail-body > div.VCSortableInPreviewMode");
                    if (subImagePlace != null) {
                        for (Element element : subImagePlace) {
                            Elements imgSubject = element.getElementsByTag("img");
                            Elements placeHolderImgSubject = element.getElementsByTag("p");
                            for (int j = 0; j < imgSubject.size(); j++) {
                                try {
                                    if (placeHolderImgSubject.get(j).text() != null) {
                                        imageData = imgSubject.get(j).attr("src");
                                        placeHolderImageData = placeHolderImgSubject.get(j).text();
                                        arrImagePlaceHolder.add(new ImagePlaceHolder(imageData, placeHolderImageData));
                                    } else {
                                        arrImagePlaceHolder.add(new ImagePlaceHolder(imageData, ""));
                                    }

                                } catch (IndexOutOfBoundsException e) {
                                    arrImagePlaceHolder.add(new ImagePlaceHolder(imageData, ""));
                                }

                            }

                        }
                    }

                }

            } catch (IOException e) {
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

            StringBuilder stringBuilder = new StringBuilder();
            if (arrNoiDungBaoHtml != null || arrNoiDungBaoHtml.size() > 0 || !arrNoiDungBaoHtml.isEmpty()) {
                for (int i = 0; i < arrNoiDungBaoHtml.size(); i++) {
                    stringBuilder.append(arrNoiDungBaoHtml.get(i) + "\n" + "\n");
                }
                txt_noiDungBao.setText(stringBuilder);
            }

            if (arrImagePlaceHolder.size() > 0 || arrImagePlaceHolder != null) {
                arrImagePlaceHolder.remove(arrImagePlaceHolder.get(arrImagePlaceHolder.size() - 1)); // b??? pah??n t??? cu???i
                viewPagerImageSlide.setAdapter(new SlideImageHolderAdapter(ReadNewsActivity.this, arrImagePlaceHolder, viewPagerImageSlide));
                viewPagerImageSlide.setClipToPadding(false);
                viewPagerImageSlide.setClipChildren(false);
                viewPagerImageSlide.setOffscreenPageLimit(3);
                viewPagerImageSlide.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

                CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                compositePageTransformer.addTransformer(new MarginPageTransformer(20));
                compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                    @Override
                    public void transformPage(@NonNull View page, float position) {
                        float r = 1 - Math.abs(position);
                        page.setScaleY(0.85f + r * 0.15f);
                    }
                });
                viewPagerImageSlide.setPageTransformer(compositePageTransformer);
            }

            hideShimmerLayout();
            swipeRefreshLayout.setRefreshing(false);

        }
    }

    private class LoadHtmlBaoBienPhong extends AsyncTask<String, Void, Void> {

        private String titleHeaderHtml = "";
        private String dateTimeHtml = "";
        private String subTitleHtml = "";

        // lisview image
        private String imageData = "", placeHolderImageData = "";
        private ArrayList<ImagePlaceHolder> arrImagePlaceHolder;

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
                    //L???y  html c?? th??? nh?? sau: div#latest-news > div.row > div.col-md-6 ho???c ch??? c???n d??ng  div.col-md-6

                    //get datat titleHeader,
                    Elements subTitleHeader = document.select("article.article-main > h2.title--big");
                    if (subTitleHeader != null) {
                        for (Element element : subTitleHeader) {
                            Element titleHeaderSubject = element.getElementsByTag("a").first();
                            if (titleHeaderSubject != null)
                                titleHeaderHtml = titleHeaderSubject.text();
                        }
                    }

                    //datetime
                    Elements subDate = document.select("article.article-main > div.row > div.col-6");
                    if (subDate != null) {
                        for (Element element : subDate) {
                            Element dateTimeSubject = element.getElementsByClass("article-date").first();
                            if (dateTimeSubject != null)
                                dateTimeHtml = dateTimeSubject.text();
                        }
                    }

                    //getSubTitle
                    Elements subSubTitle = document.select("div.article-content-intro");
                    if (subSubTitle != null) {
                        for (Element element : subSubTitle) {
                            Element subTitleSubject = element.getElementsByTag("p").first();
                            if (subTitleSubject != null)
                                subTitleHtml = subTitleSubject.text();
                        }
                    }

                    // n???i dung
                    Elements elms = document.select("div.article-content-main > p");
                    if (elms != null) {
                        for (int i = 0; i < elms.size() - 1; i++) {
                            String noiDungBaoSubject = elms.get(i).text();
                            if (noiDungBaoSubject != null)
                                arrNoiDungBaoHtml.add(noiDungBaoSubject);
                        }
                    }

                    // lisview image
                    arrImagePlaceHolder = new ArrayList<>();
                    Elements subImagePlace = document.select("div.article-grp-content > div.article-content-main > div > figure.image");
                    if (subImagePlace != null) {
                        for (Element element : subImagePlace) {
                            Elements imgSubject = element.getElementsByTag("img");
                            Elements placeHolderImgSubject = element.getElementsByTag("figcaption");
                            for (int j = 0; j < imgSubject.size(); j++) {
                                try {
                                    if (placeHolderImgSubject.get(j).text() != null) {
                                        imageData = imgSubject.get(j).attr("src");
                                        placeHolderImageData = placeHolderImgSubject.get(j).text();
                                        arrImagePlaceHolder.add(new ImagePlaceHolder(imageData, placeHolderImageData));
                                    } else {
                                        arrImagePlaceHolder.add(new ImagePlaceHolder(imageData, ""));
                                    }


                                } catch (IndexOutOfBoundsException e) {
                                    arrImagePlaceHolder.add(new ImagePlaceHolder(imageData, ""));
                                }

                            }

                        }
                    }

                    // list image <p>
                    Elements imageList = document.select("div.article-content-main > p > img");
                    if (imageList != null) {
                        for (Element element : imageList) {
                            if (element.attr("src") != null)
                                arrImagePlaceHolder.add(new ImagePlaceHolder(element.attr("src"), ""));
                        }
                    }
                }

            } catch (IOException e) {
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

            //noi dung
            StringBuilder stringBuilder = new StringBuilder();
            if (arrNoiDungBaoHtml.size() > 0 || arrNoiDungBaoHtml != null || !arrNoiDungBaoHtml.isEmpty()) {
                for (int i = 0; i < arrNoiDungBaoHtml.size(); i++) {
                    stringBuilder.append(arrNoiDungBaoHtml.get(i) + "\n" + "\n");
                }
                txt_noiDungBao.setText(stringBuilder);
            }

            if (arrImagePlaceHolder.size() > 0 || arrImagePlaceHolder != null) {
                viewPagerImageSlide.setAdapter(new SlideImageHolderAdapter(ReadNewsActivity.this, arrImagePlaceHolder, viewPagerImageSlide));
                viewPagerImageSlide.setClipToPadding(false);
                viewPagerImageSlide.setClipChildren(false);
                viewPagerImageSlide.setOffscreenPageLimit(3);
                viewPagerImageSlide.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

                CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                compositePageTransformer.addTransformer(new MarginPageTransformer(20));
                compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                    @Override
                    public void transformPage(@NonNull View page, float position) {
                        float r = 1 - Math.abs(position);
                        page.setScaleY(0.85f + r * 0.15f);
                    }
                });
                viewPagerImageSlide.setPageTransformer(compositePageTransformer);
            }

            hideShimmerLayout();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private class LoadHtmlBaoDauTu extends AsyncTask<String, Void, Void> {

        private String titleHeaderHtml = "";
        private String dateTimeHtml = "";
        private String dateTimeHtml2 = "";
        private String subTitleHtml = "";

        // lisview image
        private String imageData = "", placeHolderImageData = "";
        private ArrayList<ImagePlaceHolder> arrImagePlaceHolder; // m???ng ch???a ???nh v?? desc image

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
                    //L???y  html c?? th??? nh?? sau: div#latest-news > div.row > div.col-md-6 ho???c ch??? c???n d??ng  div.col-md-6

                    //get datat titleHeader
                    Elements sub = document.select("div.col630 > div.title-detail");
                    if (sub != null) {
                        for (Element element : sub) {
                            String titleHeaderSubject = element.text();
                            if (titleHeaderSubject != null)
                                titleHeaderHtml = titleHeaderSubject;
                        }
                    }

                    //get datat datetime
                    Elements sub2 = document.select("div.col630 > div.author-share-top > div.mr-auto > a.author");
                    if (sub2 != null) {
                        for (Element element : sub2) {
                            String dateTimeSubject = element.text();
                            if (dateTimeSubject != null)
                                dateTimeHtml = dateTimeSubject;
                        }
                    }
                    //get datat datetime2
                    Elements sub22 = document.select("div.col630 > div.author-share-top > div.mr-auto > span.post-time");
                    if (sub22 != null) {
                        for (Element element : sub22) {
                            String dateTimeSubject2 = element.text();
                            if (dateTimeSubject2 != null)
                                dateTimeHtml2 = dateTimeSubject2;
                        }
                    }

                    //get data subTitle
                    Elements sub3 = document.select("div.sapo_detail");
                    if (sub3 != null) {
                        for (Element element : sub3) {
                            String subTitleSubject = element.text();
                            if (subTitleSubject != null)
                                subTitleHtml = subTitleSubject;
                        }
                    }

                    // get noi dung
                    Elements elms = document.select("div#content_detail_news");
                    if (elms != null) {
                        for (Element element : elms) {
                            Elements elements = element.getElementsByTag("p");
                            for (Element element1 : elements) {
                                String noiDungBaoSubject = element1.text();
                                if (noiDungBaoSubject != null)
                                    arrNoiDungBaoHtml.add(noiDungBaoSubject);
                            }

                        }
                    }

                    // lisview image
                    arrImagePlaceHolder = new ArrayList<>();
                    Elements subImagePlace = document.select("div#content_detail_news");
                    if (subImagePlace != null) {
                        for (Element element : subImagePlace) {
                            Elements imgSubject = element.getElementsByTag("img");
                            for (int j = 0; j < imgSubject.size(); j++) {
                                try {

                                    imageData = imgSubject.get(j).attr("src");
                                    placeHolderImageData = imgSubject.get(j).attr("alt");

                                    if (placeHolderImageData != null) {
                                        arrImagePlaceHolder.add(new ImagePlaceHolder(imageData, placeHolderImageData));
                                    } else {
                                        arrImagePlaceHolder.add(new ImagePlaceHolder(imageData, ""));
                                    }

                                } catch (IndexOutOfBoundsException e) {
                                    arrImagePlaceHolder.add(new ImagePlaceHolder(imageData, ""));
                                }

                            }

                        }
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            txt_headerTitle.setText(titleHeaderHtml);
            txt_dateTime.setText(dateTimeHtml + dateTimeHtml2);
            txt_subTitle.setText(subTitleHtml);

            StringBuilder stringBuilder = new StringBuilder();
            if (arrNoiDungBaoHtml != null || arrNoiDungBaoHtml.size() > 0) {
                for (int i = 0; i < arrNoiDungBaoHtml.size(); i++) {
                    stringBuilder.append(arrNoiDungBaoHtml.get(i) + "\n" + "\n");
                }
                txt_noiDungBao.setText(stringBuilder);
            }

            if (arrImagePlaceHolder.size() > 0 || arrImagePlaceHolder != null) {
                viewPagerImageSlide.setAdapter(new SlideImageHolderAdapter(ReadNewsActivity.this, arrImagePlaceHolder, viewPagerImageSlide));
                viewPagerImageSlide.setClipToPadding(false);
                viewPagerImageSlide.setClipChildren(false);
                viewPagerImageSlide.setOffscreenPageLimit(3);
                viewPagerImageSlide.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

                CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                compositePageTransformer.addTransformer(new MarginPageTransformer(20));
                compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                    @Override
                    public void transformPage(@NonNull View page, float position) {
                        float r = 1 - Math.abs(position);
                        page.setScaleY(0.85f + r * 0.15f);
                    }
                });
                viewPagerImageSlide.setPageTransformer(compositePageTransformer);
            }

            hideShimmerLayout();
            swipeRefreshLayout.setRefreshing(false);

        }
    }

    private class LoadHtmlBaoPhapLuat extends AsyncTask<String, Void, Void> {

        private String titleHeaderHtml = "";
        private String dateTimeHtml = "";
        private String subTitleHtml = "";

        // lisview image

        private ArrayList<ImagePlaceHolder> arrImagePlaceHolder; // m???ng ch???a ???nh v?? desc image

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
                    //L???y  html c?? th??? nh?? sau: div#latest-news > div.row > div.col-md-6 ho???c ch??? c???n d??ng  div.col-md-6


                    //get datat titleHeader
                    Elements sub = document.select("header.article__header");
                    if (sub != null) {
                        for (Element element : sub) {
                            Element titleHeaderSubject = element.getElementsByTag("h1").first();
                            if (titleHeaderSubject != null)
                                titleHeaderHtml = titleHeaderSubject.text();
                        }
                    }

                    //get data datetime
                    Elements sub2 = document.select("header.article__header > div.article__meta");
                    if (sub2 != null) {
                        for (Element element : sub2) {
                            Element dateTimeSubject = element.getElementsByTag("time").first();
                            if (dateTimeSubject != null)
                                dateTimeHtml = dateTimeSubject.text();
                        }
                    }

                    //get data subTitle
                    Elements sub3 = document.select("div.article__summary");
                    if (sub3 != null) {
                        for (Element element : sub3) {
                            String subTitleSubject = element.text();
                            if (subTitleSubject != null)
                                subTitleHtml = subTitleSubject;
                        }
                    }

                    // get noi dung
                    Elements elms = document.select("div.cms-body > p");
                    if (elms != null) {
                        for (int i = 0; i < elms.size(); i++) {

                            String noiDungBaoSubject = elms.get(i).text();
                            if (noiDungBaoSubject != null)
                                arrNoiDungBaoHtml.add(noiDungBaoSubject);
                        }
                    }

                    //get data image
                    arrImagePlaceHolder = new ArrayList<>();
                    Elements sub4 = document.select("img.img-responsive");
                    if (sub4 != null) {
                        for (Element element : sub4) {
                            if (element != null)
                                arrImagePlaceHolder.add(new ImagePlaceHolder(element.attr("src"), ""));
                        }
                    }

                    /*imageList = new ArrayList<>();*/
                    //get data image
                    Elements sub6 = document.select("table > tbody > tr > td");
                    if (sub6 != null) {
                        for (Element element : sub6) {
                            Elements imgSubject = element.getElementsByTag("img");
                            for (Element element1 : imgSubject) {
                                if (element1 != null)
                                    arrImagePlaceHolder.add(new ImagePlaceHolder(element1.attr("src"), ""));
                            }

                        }
                    }

                }

            } catch (IOException e) {
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

            StringBuilder stringBuilder = new StringBuilder();
            if (arrNoiDungBaoHtml != null || arrNoiDungBaoHtml.size() > 0) {
                for (int i = 0; i < arrNoiDungBaoHtml.size(); i++) {
                    stringBuilder.append(arrNoiDungBaoHtml.get(i) + "\n" + "\n");
                }
                txt_noiDungBao.setText(stringBuilder);
            }

            if (arrImagePlaceHolder.size() > 0 || arrImagePlaceHolder != null) {
                viewPagerImageSlide.setAdapter(new SlideImageHolderAdapter(ReadNewsActivity.this, arrImagePlaceHolder, viewPagerImageSlide));
                viewPagerImageSlide.setClipToPadding(false);
                viewPagerImageSlide.setClipChildren(false);
                viewPagerImageSlide.setOffscreenPageLimit(3);
                viewPagerImageSlide.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

                CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                compositePageTransformer.addTransformer(new MarginPageTransformer(20));
                compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                    @Override
                    public void transformPage(@NonNull View page, float position) {
                        float r = 1 - Math.abs(position);
                        page.setScaleY(0.85f + r * 0.15f);
                    }
                });
                viewPagerImageSlide.setPageTransformer(compositePageTransformer);
            }

            hideShimmerLayout();
            swipeRefreshLayout.setRefreshing(false);

        }
    }

    private class LoadHtmlBaoNgoiSao extends AsyncTask<String, Void, Void> {

        private String titleHeaderHtml = "";
        private String dateTimeHtml = "";
        private String subTitleHtml = "";

        // lisview image
        private String imageData = "", placeHolderImageData = "";
        private ArrayList<ImagePlaceHolder> arrImagePlaceHolder; // m???ng ch???a ???nh v?? desc image

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
                    //L???y  html c?? th??? nh?? sau: div#latest-news > div.row > div.col-md-6 ho???c ch??? c???n d??ng  div.col-md-6

                    //get datat titleHeader, subTitle
                    Elements sub = document.select("header.the-article-header");
                    if (sub != null) {
                        for (Element element : sub) {
                            Element titleHeaderSubject = element.getElementsByTag("h1").first();
                            Element subTitleSubject = element.getElementsByTag("p").last();
                            if (titleHeaderSubject != null)
                                titleHeaderHtml = titleHeaderSubject.text();
                            if (subTitleSubject != null)
                                subTitleHtml = subTitleSubject.text();
                        }
                    }

                    //get datat datetime
                    Elements sub2 = document.select("p.metadate");
                    if (sub2 != null) {
                        for (Element element : sub2) {
                            String dateTimeSubject = element.text();
                            if (dateTimeSubject != null)
                                dateTimeHtml = dateTimeSubject;
                        }
                    }

                    // get noi dung
                    Elements elms = document.select("p.Normal");
                    if (elms != null) {
                        for (int i = 0; i < elms.size(); i++) {
                            String noiDungBaoSubject = elms.get(i).text();
                            if (noiDungBaoSubject != null)
                                arrNoiDungBaoHtml.add(noiDungBaoSubject);
                        }
                    }

                    //getdata image , placeholder
                    arrImagePlaceHolder = new ArrayList<>();
                    Elements subImagePlace = document.select("table.tplCaption > tbody > tr > td");
                    if (subImagePlace != null) {
                        for (Element element : subImagePlace) {
                            Elements imgSubject = element.getElementsByTag("img");
                            for (int j = 0; j < imgSubject.size(); j++) {
                                try {

                                    imageData = imgSubject.get(j).attr("src");
                                    placeHolderImageData = imgSubject.get(j).attr("alt");

                                    if (placeHolderImageData != null) {
                                        arrImagePlaceHolder.add(new ImagePlaceHolder(imageData, placeHolderImageData));
                                    } else {
                                        arrImagePlaceHolder.add(new ImagePlaceHolder(imageData, ""));
                                    }

                                } catch (IndexOutOfBoundsException e) {
                                    arrImagePlaceHolder.add(new ImagePlaceHolder(imageData, ""));
                                }

                            }

                        }
                    }

                }

            } catch (IOException e) {
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

            StringBuilder stringBuilder = new StringBuilder();
            if (arrNoiDungBaoHtml != null || arrNoiDungBaoHtml.size() > 0 || !arrNoiDungBaoHtml.isEmpty()) {
                for (int i = 0; i < arrNoiDungBaoHtml.size(); i++) {
                    stringBuilder.append(arrNoiDungBaoHtml.get(i) + "\n" + "\n");
                }
                txt_noiDungBao.setText(stringBuilder);
            }

            if (arrImagePlaceHolder.size() > 0 || arrImagePlaceHolder != null) {
                viewPagerImageSlide.setAdapter(new SlideImageHolderAdapter(ReadNewsActivity.this, arrImagePlaceHolder, viewPagerImageSlide));
                viewPagerImageSlide.setClipToPadding(false);
                viewPagerImageSlide.setClipChildren(false);
                viewPagerImageSlide.setOffscreenPageLimit(3);
                viewPagerImageSlide.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

                CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                compositePageTransformer.addTransformer(new MarginPageTransformer(20));
                compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                    @Override
                    public void transformPage(@NonNull View page, float position) {
                        float r = 1 - Math.abs(position);
                        page.setScaleY(0.85f + r * 0.15f);
                    }
                });
                viewPagerImageSlide.setPageTransformer(compositePageTransformer);
            }

            hideShimmerLayout();
            swipeRefreshLayout.setRefreshing(false);

        }
    }

    private class LoadHtmlBaoNguoiLaoDong extends AsyncTask<String, Void, Void> {

        private String titleHeaderHtml = "";
        private String dateTimeHtml = "";
        private String dateTimeHtml2 = "";
        private String subTitleHtml = "";

        // lisview image
        private String imageData = "", placeHolderImageData = "";
        private ArrayList<ImagePlaceHolder> arrImagePlaceHolder; // m???ng ch???a ???nh v?? desc image

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
                    //L???y  html c?? th??? nh?? sau: div#latest-news > div.row > div.col-md-6 ho???c ch??? c???n d??ng  div.col-md-6

                    //get datat titleHeader
                    Elements sub = document.select("h1.title-content");
                    if (sub != null) {
                        for (Element element : sub) {
                            String titleHeaderSubject = element.text();
                            if (titleHeaderSubject != null)
                                titleHeaderHtml = titleHeaderSubject.trim();
                        }
                    }

                    //get datat datetime
                    Elements sub2 = document.select("span.pdate");
                    if (sub2 != null) {
                        for (Element element : sub2) {
                            String dateTimeSubject = element.text();
                            if (dateTimeSubject != null)
                                dateTimeHtml = dateTimeSubject.trim();
                        }
                    }

                    //get data subTitle
                    Elements sub3 = document.select("h2.sapo-detail");
                    if (sub3 != null) {
                        for (Element element : sub3) {
                            String subTitleSubject = element.text();
                            if (subTitleSubject != null)
                                subTitleHtml = subTitleSubject.trim();
                        }
                    }

                    // get noi dung
                    Elements elms = document.select("div.content-news-detail");
                    if (elms != null) {
                        for (Element element : elms) {
                            Elements elements = element.getElementsByTag("p");
                            for (Element element1 : elements) {
                                String noiDungBaoSubject = element1.text();
                                if (noiDungBaoSubject != null)
                                    arrNoiDungBaoHtml.add(noiDungBaoSubject.trim());
                            }
                        }
                    }

                    // lisview image
                    arrImagePlaceHolder = new ArrayList<>();
                    Elements subImagePlace = document.select("div.content-news-detail > div.VCSortableInPreviewMode");
                    if (subImagePlace != null) {
                        for (Element element : subImagePlace) {
                            Elements imgSubject = element.getElementsByTag("img");
                            Elements placeHolderImgSubject = element.getElementsByTag("p");
                            for (int j = 0; j < imgSubject.size(); j++) {
                                try {
                                    if (placeHolderImgSubject.get(j).text() != null) {
                                        imageData = imgSubject.get(j).attr("src");
                                        placeHolderImageData = placeHolderImgSubject.get(j).text();
                                        arrImagePlaceHolder.add(new ImagePlaceHolder(imageData, placeHolderImageData));
                                    } else {
                                        arrImagePlaceHolder.add(new ImagePlaceHolder(imageData, ""));
                                    }


                                } catch (IndexOutOfBoundsException e) {
                                    arrImagePlaceHolder.add(new ImagePlaceHolder(imageData, ""));
                                }

                            }

                        }
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            txt_headerTitle.setText(titleHeaderHtml);
            txt_dateTime.setText(dateTimeHtml + dateTimeHtml2);
            txt_subTitle.setText(subTitleHtml);

            StringBuilder stringBuilder = new StringBuilder();
            if (arrNoiDungBaoHtml != null || arrNoiDungBaoHtml.size() > 0) {
                for (int i = 0; i < arrNoiDungBaoHtml.size(); i++) {
                    stringBuilder.append(arrNoiDungBaoHtml.get(i) + "\n" + "\n");
                }
                txt_noiDungBao.setText(stringBuilder);
            }

            if (arrImagePlaceHolder.size() > 0 || arrImagePlaceHolder != null) {
                viewPagerImageSlide.setAdapter(new SlideImageHolderAdapter(ReadNewsActivity.this, arrImagePlaceHolder, viewPagerImageSlide));
                viewPagerImageSlide.setClipToPadding(false);
                viewPagerImageSlide.setClipChildren(false);
                viewPagerImageSlide.setOffscreenPageLimit(3);
                viewPagerImageSlide.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

                CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                compositePageTransformer.addTransformer(new MarginPageTransformer(20));
                compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                    @Override
                    public void transformPage(@NonNull View page, float position) {
                        float r = 1 - Math.abs(position);
                        page.setScaleY(0.85f + r * 0.15f);
                    }
                });
                viewPagerImageSlide.setPageTransformer(compositePageTransformer);
            }

            hideShimmerLayout();
            swipeRefreshLayout.setRefreshing(false);

        }
    }

    private void saveFileHistoryNews(Context context, String newsName, String title, String link, String image, String pubDate) {

        boolean checkContain = false; // bi???n ki???m tra city c?? trong file hay kh??ng

        try {

            File file = new File(context.getFilesDir(), "historyNews.txt");
            if (file.exists()) {

                // ?????c file ????? l???y data trong file
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

                // L???y data trong file ???? ?????c l??u trong m???ng r???i ??em so tr??ng
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getNewsName().equals(newsName) && items.get(i).getTitle().equals(title) && items.get(i).getLink().equals(link)
                            && items.get(i).getImage().equals(image) && items.get(i).getPubDate().equals(pubDate)) { // tr??ng th?? tho??t v??ng l???p

                        checkContain = true; //tr??ng
                        break;

                    } else {
                        checkContain = false;
                    }
                }

                // checkContain == false t???c l?? city ch??a c?? trong file n??n ghi v??o
                if (checkContain == false) {

                    if (items.size() >= 10) { // ki???m tra m???ng items ?????c t??? file ???? ????? 10 item hay ch??a
                        items.remove(0); // x??a item ?????u

                        items.add(new ChiTietTinTuc(newsName, title, link, image, pubDate)); // l??u  v??o m???ng items

                        file.delete(); // x??a d??? li???u trong file

                        FileOutputStream fs_out = context.openFileOutput("historyNews.txt", Context.MODE_APPEND);
                        OutputStreamWriter os = new OutputStreamWriter(fs_out);

                        for (int i = 0; i < items.size(); i++) { // ghi d??? li???u m???i g???m 5 item v??o file
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

            } else { // l??n ?????u c??i app th?? s??? ch??a c?? file n??n t???o v?? l??u d??? li???u city l???y t??? GPS v??o file
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

    private class ReadDataRandomRSS extends AsyncTask<String, Integer, String> {

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

                // check xem cso ther description tr??n ?????u hay kh??ng
                boolean checkDesc = false; //k c??
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

                //?????c xml khi c?? c???m th??? <![CDATA[ ?????ng tr?????ng
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

                    //tr?????ng h???p 1: kh??ng c?? <![CDATA[ bao quanh
                    title = parser.getValue(element, "title");
                    link = parser.getValue(element, "link");
                    pubDate = parser.getValue(element, "pubDate");

                    //----------------tr?????ng h???p 2: c?? <![CDATA[ bao quanh-----//
                    if (title.isEmpty())
                        title = nodeListTitle.item(i + 1).getTextContent();

                    if (link.isEmpty())
                        link = nodeListLink.item(i + 1).getTextContent();

                    if (pubDate.isEmpty())
                        pubDate = nodeListPubDate.item(i + 1).getTextContent();

                    String cdata = "";
                    if (checkDesc == false) { //tr?????ng h???p <channel> k c?? description n??n ?????c t??? i
                        cdata = nodeListDescription.item(i).getTextContent();
                    } else {
                        cdata = nodeListDescription.item(i + 1).getTextContent();
                    }

                    Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
                    Matcher matcher = p.matcher(cdata);

                    if (matcher.find())
                        image = matcher.group(1);

                    //n???u ???nh r???ng th?? g??n m???c ?????nh cho c??i ???nh l???i
                    if (image.isEmpty())
                        image = String.valueOf(R.drawable.ic_no_news);


                    String newsName = "";
                    if (link.contains("tuoitre.vn"))
                        newsName = BAO_TUOI_TRE;
                    else if (link.contains("bienphong.com.vn"))
                        newsName = BAO_BIEN_PHONG;
                    else if (link.contains("baodautu.vn/"))
                        newsName = BAO_DAU_TU;
                    else if (link.contains("baophapluat.vn/"))
                        newsName = BAO_PHAP_LUAT;
                    else if (link.contains("ngoisao.net/"))
                        newsName = BAO_NGOI_SAO;
                    else if (link.contains("nld.com.vn/"))
                        newsName = BAO_NGUOI_LAO_DONG;

                    arrChiTietTinTuc.add(new ChiTietTinTuc(newsName, title, link, image, pubDate));

                }

                // l???y ng???u nhi??n 6 ph???n t??? kh??gn tr??ng trong m???ng arrChiTietTinTuc
                final ArrayList<ChiTietTinTuc> arrTemp = new ArrayList<>(); // ch???a 6 it??m randrom
                for (int i = 0; i < 6; ) {
                    iNew = randomNews.nextInt(arrChiTietTinTuc.size());
                    if (!vectorNews.contains(iNew)) {
                        i++;
                        vectorNews.add(iNew);
                        arrTemp.add(new ChiTietTinTuc(arrChiTietTinTuc.get(iNew).getNewsName(), arrChiTietTinTuc.get(iNew).getTitle(),
                                arrChiTietTinTuc.get(iNew).getLink(), arrChiTietTinTuc.get(iNew).getImage(), arrChiTietTinTuc.get(iNew).getPubDate()));
                    }
                }

                ChiTietTinTucNormalAdapter adapter = new ChiTietTinTucNormalAdapter(ReadNewsActivity.this, arrTemp);
                linear_list.setAdapter(adapter);
                setListViewHeightBasedOnItems(linear_list);
                adapter.notifyDataSetChanged();
                setListViewHeightBasedOnItems(linear_list);

                linear_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        swipeRefreshLayout.setRefreshing(true);

                        Intent intent = null;
                        if (saveLoadFileUntil.loadFileTabSelect(ReadNewsActivity.this).contains("Th?? vi???n ???nh")) {
                            intent = new Intent(ReadNewsActivity.this, SlideImageNewsActivity.class);

                        } else if (arrTemp.get(position).getTitle().contains("Infographic")) {
                            intent = new Intent(ReadNewsActivity.this, InfographicNewsActivity.class);

                        } else if (arrTemp.get(position).getTitle().contains("Longform")) {
                            intent = new Intent(ReadNewsActivity.this, LongformNewsActivity.class);

                        } else if (arrTemp.get(position).getLink().contains("www.bienphong.com.vn/videos/")) {
                            intent = new Intent(ReadNewsActivity.this, VideoNewsActivity.class);

                        } else if (arrTemp.get(position).getLink().contains("www.bienphong.com.vn/tin-anh/")) {
                            intent = new Intent(ReadNewsActivity.this, SlideImageNewsActivity.class);

                        } else {
                            intent = new Intent(getApplicationContext(), ReadNewsActivity.class);
                        }

                        intent.putExtra("news_name", arrTemp.get(position).getNewsName().trim());
                        intent.putExtra("title_news", arrTemp.get(position).getTitle().trim());
                        intent.putExtra("link_news", arrTemp.get(position).getLink().trim());
                        intent.putExtra("image_news", arrTemp.get(position).getImage().trim());
                        intent.putExtra("pubdate_news", arrTemp.get(position).getPubDate().trim());
                        intent.putExtra("tab_selected", saveLoadFileUntil.loadFileTabSelect(getApplicationContext()));

                        saveFileHistoryNews(getApplicationContext(),
                                arrTemp.get(position).getNewsName().trim(), arrTemp.get(position).getTitle().trim(),
                                arrTemp.get(position).getLink().trim(), arrTemp.get(position).getImage().trim(),
                                arrTemp.get(position).getPubDate().trim());
                        startActivity(intent);
                        swipeRefreshLayout.setRefreshing(false);

                    }
                });

            } catch (NullPointerException e) {
            }
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkNightModeActivated();
    }

}