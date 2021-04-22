package com.example.newsapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.newsapp.R;
import com.example.newsapp.adapters.ChiTietTinTucTwoViewTypeAdapter;
import com.example.newsapp.models.ChiTietTinTuc;
import com.example.newsapp.network.CheckConnectionUntil;
import com.example.newsapp.utils.SaveLoadFileUntil;
import com.example.newsapp.utils.ShowHideUntil;
import com.example.newsapp.utils.XMLDOMParserUntil;
import com.example.newsapp.variables.LinkBaoNguoiLaoDong;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.mahfa.dnswitch.DayNightSwitch;
import com.mahfa.dnswitch.DayNightSwitchListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class BaoNguoiLaoDongActivity extends AppCompatActivity {

    private static String FACEBOOK_URL = "https://www.facebook.com/100048034022026";
    private static String FACEBOOK_PAGE_ID = "100048034022026";
    private final String GMAIL = "ngothanhduc1662000@gmail.com";

    private SharedPreferences sharedPreferencesDayNight;
    private static final String MY_PREFERENCES_DAYNIGHT = "nightModePrefs";
    private static final String KEY_ISNIGHTMODE = "isNightMode";

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private NavigationView navigationView;
    private DrawerLayout drawer_layout;
    private ListView lst_bao;
    private TextView txt_error, txt_newsName;
    private ShimmerLayout shimmer_view_contain;
    private FrameLayout frameLayout_contain;
    private ImageView img_logoBao;

    //navigation
    private TextView txt_tinDaDoc, txt_tinDanhDau, txt_facebookBaoThanhDuc, txt_dieuKhoanSuDung, txt_danhGiaUngDung, txt_guiMailGopY, txt_tienIch;
    private DayNightSwitch day_night_switch;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String[] arrTitleTab = {"Tin mới nhất", "Thời sự", "Thời sự quốc tế", "Công đoàn", "Bạn đọc", "Kinh tế", "Sức khỏe",
            "Giáo dục", "Pháp luật", "Giải trí", "Thể thao", "Công nghệ", "Điểm đến", "Lý tưởng sống",
            "Nói thẳng", "Tin độc quyền"};

    private ArrayList<ChiTietTinTuc> arrChiTietTinTuc;
    private ChiTietTinTucTwoViewTypeAdapter chiTietTinTucAdapter;
    private LinkBaoNguoiLaoDong linkBaoNguoiLaoDong;

    private SaveLoadFileUntil saveLoadFileUntil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chung);

        if (CheckConnectionUntil.haveNetworkConnection(this)) {
            init();
            actionBar();
            initDataApp();
            events();
        } else {
            CheckConnectionUntil.showDialogNoUpdateData(BaoNguoiLaoDongActivity.this);
        }

    }

    //sửa logo vs newsName
    @SuppressLint("ResourceAsColor")
    private void init() {
        lst_bao = findViewById(R.id.lst_baoTuoiTre);
        txt_error = findViewById(R.id.txt_error);
        shimmer_view_contain = findViewById(R.id.shimmer_view_contain);
        frameLayout_contain = findViewById(R.id.frameLayout_contain);

        txt_newsName = findViewById(R.id.txt_newsName);
        img_logoBao = findViewById(R.id.img_logoBao);

        //navigation
        txt_tinDaDoc = findViewById(R.id.txt_tinDaDoc);
        txt_tinDanhDau = findViewById(R.id.txt_tinDanhDau);
        txt_facebookBaoThanhDuc = findViewById(R.id.txt_facebookBaoThanhDuc);
        txt_dieuKhoanSuDung = findViewById(R.id.txt_dieuKhoanSuDung);
        txt_danhGiaUngDung = findViewById(R.id.txt_danhGiaUngDung);
        txt_danhGiaUngDung = findViewById(R.id.txt_danhGiaUngDung);
        txt_guiMailGopY = findViewById(R.id.txt_guiMailGopY);
        txt_guiMailGopY = findViewById(R.id.txt_guiMailGopY);
        txt_tienIch = findViewById(R.id.txt_tienIch);

        day_night_switch = findViewById(R.id.day_night_switch);
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        navigationView = findViewById(R.id.navigationView);
        drawer_layout = findViewById(R.id.drawer_layout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        arrChiTietTinTuc = new ArrayList<>();
        linkBaoNguoiLaoDong = new LinkBaoNguoiLaoDong();
        saveLoadFileUntil = new SaveLoadFileUntil();

    }

    private void initDataApp() {

        txt_newsName.setText(getResources().getString(R.string.bao_nguoi_lao_dong)); // setText title toolbar
        img_logoBao.setImageResource(R.drawable.ic_bao_nguoilaodong); // setImage title toolbar

        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.GREEN); // set color swipeRefreshLayout

        // init tab data
        for (int i = 0; i < arrTitleTab.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(arrTitleTab[i]));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));

        // save file tab data
        saveLoadFileUntil.saveFileTabSelect(getApplicationContext(), arrTitleTab[0]);

        // init sharedPreferencesDayNight
        sharedPreferencesDayNight = getSharedPreferences(MY_PREFERENCES_DAYNIGHT, Context.MODE_PRIVATE);

        //trước khi đọc dữ liệu thì check DayNight trước
        checkNightModeActivated();
        // mặc định chạu trang chủ
        new ReadDataRSS().execute(linkBaoNguoiLaoDong.LINK_TIN_MOI_NHAT);

    }

    private void actionBar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_apps);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BaoNguoiLaoDongActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    private void events() {

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (CheckConnectionUntil.haveNetworkConnection(BaoNguoiLaoDongActivity.this)) {
                    switch (tab.getPosition()) {
                        case 0:
                            saveLoadFileUntil.saveFileTabSelect(getApplicationContext(), tab.getText().toString());
                            new ReadDataRSS().execute(linkBaoNguoiLaoDong.LINK_TIN_MOI_NHAT);
                            break;

                        case 1:
                            saveLoadFileUntil.saveFileTabSelect(getApplicationContext(), tab.getText().toString());
                            new ReadDataRSS().execute(linkBaoNguoiLaoDong.LINK_THOI_SU);
                            break;

                        case 2:
                            saveLoadFileUntil.saveFileTabSelect(getApplicationContext(), tab.getText().toString());
                            new ReadDataRSS().execute(linkBaoNguoiLaoDong.LINK_THOI_SU_QUOC_TE);
                            break;

                        case 3:
                            saveLoadFileUntil.saveFileTabSelect(getApplicationContext(), tab.getText().toString());
                            new ReadDataRSS().execute(linkBaoNguoiLaoDong.LINK_CONG_DOAN);
                            break;

                        case 4:
                            saveLoadFileUntil.saveFileTabSelect(getApplicationContext(), tab.getText().toString());
                            new ReadDataRSS().execute(linkBaoNguoiLaoDong.LINK_BAN_DOC);
                            break;

                        case 5:
                            saveLoadFileUntil.saveFileTabSelect(getApplicationContext(), tab.getText().toString());
                            new ReadDataRSS().execute(linkBaoNguoiLaoDong.LINK_KINH_TE);
                            break;

                        case 6:
                            saveLoadFileUntil.saveFileTabSelect(getApplicationContext(), tab.getText().toString());
                            new ReadDataRSS().execute(linkBaoNguoiLaoDong.LINK_SUC_KHOE);
                            break;

                        case 7:
                            saveLoadFileUntil.saveFileTabSelect(getApplicationContext(), tab.getText().toString());
                            new ReadDataRSS().execute(linkBaoNguoiLaoDong.LINK_GIAO_DUC);
                            break;

                        case 8:
                            saveLoadFileUntil.saveFileTabSelect(getApplicationContext(), tab.getText().toString());
                            new ReadDataRSS().execute(linkBaoNguoiLaoDong.LINK_PHAP_LUAT);
                            break;

                        case 9:
                            saveLoadFileUntil.saveFileTabSelect(getApplicationContext(), tab.getText().toString());
                            new ReadDataRSS().execute(linkBaoNguoiLaoDong.LINK_GIAI_TRI);
                            break;

                        case 10:
                            saveLoadFileUntil.saveFileTabSelect(getApplicationContext(), tab.getText().toString());
                            new ReadDataRSS().execute(linkBaoNguoiLaoDong.LINK_THE_THAO);
                            break;

                        case 11:
                            saveLoadFileUntil.saveFileTabSelect(getApplicationContext(), tab.getText().toString());
                            new ReadDataRSS().execute(linkBaoNguoiLaoDong.LINK_CONG_NGHE);
                            break;

                        case 12:
                            saveLoadFileUntil.saveFileTabSelect(getApplicationContext(), tab.getText().toString());
                            new ReadDataRSS().execute(linkBaoNguoiLaoDong.LINK_DIEM_DEN);
                            break;

                        case 13:
                            saveLoadFileUntil.saveFileTabSelect(getApplicationContext(), tab.getText().toString());
                            new ReadDataRSS().execute(linkBaoNguoiLaoDong.LINK_LY_TUONG_SONG);
                            break;

                        case 14:
                            saveLoadFileUntil.saveFileTabSelect(getApplicationContext(), tab.getText().toString());
                            new ReadDataRSS().execute(linkBaoNguoiLaoDong.LINK_NOI_THANG);
                            break;

                        case 15:
                            saveLoadFileUntil.saveFileTabSelect(getApplicationContext(), tab.getText().toString());
                            new ReadDataRSS().execute(linkBaoNguoiLaoDong.LINK_TIN_DOC_QUYEN);
                            break;

                        default:
                            saveLoadFileUntil.saveFileTabSelect(getApplicationContext(), tab.getText().toString());
                            new ReadDataRSS().execute(linkBaoNguoiLaoDong.LINK_TIN_MOI_NHAT);
                            break;
                    }
                } else {
                    CheckConnectionUntil.showDialogNoUpdateData(BaoNguoiLaoDongActivity.this);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        day_night_switch.setListener(new DayNightSwitchListener() {
            @Override
            public void onSwitch(boolean is_night) {
                if (is_night) {
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    saveNightModeState(true);
                } else {
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    saveNightModeState(false);

                }
            }
        });

        lst_bao.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BaoNguoiLaoDongActivity.this, ReadNewsActivity.class);
                intent.putExtra("news_name", arrChiTietTinTuc.get(position).getNewsName());
                intent.putExtra("title_news", arrChiTietTinTuc.get(position).getTitle());
                intent.putExtra("link_news", arrChiTietTinTuc.get(position).getLink());
                intent.putExtra("image_news", arrChiTietTinTuc.get(position).getImage());
                intent.putExtra("pubdate_news", arrChiTietTinTuc.get(position).getPubDate());
                intent.putExtra("tab_selected", saveLoadFileUntil.loadFileTabSelect(getApplicationContext()));

                saveLoadFileUntil.saveFileNews(getApplicationContext(),
                        "historyNews.txt",
                        arrChiTietTinTuc.get(position).getNewsName().trim(),
                        arrChiTietTinTuc.get(position).getTitle().trim(),
                        arrChiTietTinTuc.get(position).getLink().trim(),
                        arrChiTietTinTuc.get(position).getImage().trim(),
                        arrChiTietTinTuc.get(position).getPubDate().trim());

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(BaoNguoiLaoDongActivity.this, "Không có tin mới!", Toast.LENGTH_SHORT).show();
                    }
                }, 1500);
            }
        });

        txt_tinDaDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BaoNguoiLaoDongActivity.this, NewsHistoryActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        txt_tinDanhDau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BaoNguoiLaoDongActivity.this, TinDaDanhDauActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        txt_facebookBaoThanhDuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(BaoNguoiLaoDongActivity.this);
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        txt_dieuKhoanSuDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BaoNguoiLaoDongActivity.this, DieuKhoanSuDungActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        txt_danhGiaUngDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowHideUntil.showDialogRating(BaoNguoiLaoDongActivity.this);
            }
        });

        txt_guiMailGopY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                String[] recipients = {GMAIL};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Góp ý về ứng dụng Báo Thanh Đức");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                intent.putExtra(Intent.EXTRA_CC, GMAIL);
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");
                startActivity(Intent.createChooser(intent, "Send mail"));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        txt_tienIch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    private String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
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

    private class ReadDataRSS extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            ShowHideUntil.showShimmerLayout(frameLayout_contain, shimmer_view_contain);
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
                Document document = parser.getDocument(s, lst_bao, txt_error, shimmer_view_contain, frameLayout_contain);

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

                    Element element = (Element) nodeList.item(i);

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

                    String newsName = (String) txt_newsName.getText();
                    arrChiTietTinTuc.add(new ChiTietTinTuc(newsName.trim(), title.trim(), link.trim(), image.trim(), pubDate.trim()));

                    lst_bao.setVisibility(View.VISIBLE);
                    txt_error.setVisibility(View.GONE);

                }

                chiTietTinTucAdapter = new ChiTietTinTucTwoViewTypeAdapter(BaoNguoiLaoDongActivity.this, arrChiTietTinTuc);
                lst_bao.setAdapter(chiTietTinTucAdapter);

                if (BaoNguoiLaoDongActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                    return;
                }

                ShowHideUntil.hideShimmerLayout(frameLayout_contain, shimmer_view_contain);

            } catch (NullPointerException e) {
                lst_bao.setVisibility(View.GONE);
                txt_error.setVisibility(View.VISIBLE);
                ShowHideUntil.hideShimmerLayout(frameLayout_contain, shimmer_view_contain);
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                drawer_layout.openDrawer(navigationView);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {

        checkNightModeActivated();
        super.onRestart();
    }

    @Override
    public void finish() {
        super.finish();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}