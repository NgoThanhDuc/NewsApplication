package com.example.newsapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.newsapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class DieuKhoanSuDungActivity extends AppCompatActivity {

    private final String GMAIL = "ngothanhduc1662000@gmail.com";
    private static String FACEBOOK_URL = "https://www.facebook.com/100048034022026";
    private static String FACEBOOK_PAGE_ID = "100048034022026";
    private final String LINK_SO_DIEN_THOAI = "tel:0333254167";
    private final String LINK_GOOGLE_MAP = "https://www.google.com/maps/place/80+%C4%90%C6%B0%E1%BB%9Dng+S%E1%BB%91+5,+Ph%C6%B0%E1%BB%9Dng+17,+G%C3%B2+V%E1%BA%A5p,+Th%C3%A0nh+ph%E1%BB%91+H%E1%BB%93+Ch%C3%AD+Minh,+Vi%E1%BB%87t+Nam/@10.8467553,106.675526,18z/data=!4m8!1m2!2m1!1zODAvMTIgxJDGsOG7nW5nIFPhu5EgNSwgR8OyIFbhuqVwLCBUaMOgbmggcGjhu5EgSOG7kyBDaMOtIE1pbmg!3m4!1s0x317529b2af348f85:0xaddcf022e4f7e075!8m2!3d10.8453325!4d106.6756586?hl=vi-VN";

    // appbar
    private ImageButton btn_menu;
    private Toolbar toolbar;
    private LinearLayout linear_dropdown;
    private TextView txt_lienHe, txt_trangChu, txt_veTrangTrucBottom;
    private ScrollView scrollView;
    private ToggleButton tb_twitter, tb_facebook, tb_tiktok;
    private BottomSheetDialog bottomSheetDialog;

    private SharedPreferences sharedPreferencesDayNight;
    private static final String MY_PREFERENCES_DAYNIGHT = "nightModePrefs";
    private static final String KEY_ISNIGHTMODE = "isNightMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dieu_khoan_su_dung);

        init();
        actionBar();
        checkNightModeActivated();
        envets();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        btn_menu = findViewById(R.id.btn_menu);
        linear_dropdown = findViewById(R.id.linear_dropdown);
        txt_lienHe = findViewById(R.id.txt_lienHe);
        txt_trangChu = findViewById(R.id.txt_trangChu);
        txt_veTrangTrucBottom = findViewById(R.id.txt_veTrangTrucBottom);
        tb_twitter = findViewById(R.id.tb_twitter);
        tb_facebook = findViewById(R.id.tb_facebook);
        tb_tiktok = findViewById(R.id.tb_tiktok);
        scrollView = findViewById(R.id.scrollView);
        bottomSheetDialog = new BottomSheetDialog(DieuKhoanSuDungActivity.this, R.style.BottomSheetTheme);

        sharedPreferencesDayNight = getSharedPreferences(MY_PREFERENCES_DAYNIGHT, Context.MODE_PRIVATE);
    }

    private void actionBar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void envets() {
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linear_dropdown.isShown()) {
                    linear_dropdown.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up));
                    linear_dropdown.setVisibility(View.GONE);
                } else {
                    linear_dropdown.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down));
                    linear_dropdown.setVisibility(View.VISIBLE);
                }

            }
        });

        txt_lienHe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = LayoutInflater.from(DieuKhoanSuDungActivity.this).inflate(R.layout.bottom_sheet_dieu_khoan_layout, null);

                TextView txt_facebook = view.findViewById(R.id.txt_facebook);
                TextView txt_std = view.findViewById(R.id.txt_sdt);
                TextView txt_gmail = view.findViewById(R.id.txt_gmail);
                TextView txt_ggmap = view.findViewById(R.id.txt_ggmap);

                txt_std.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callPhoneNumber();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });

                txt_facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                        String facebookUrl = getFacebookPageURL(DieuKhoanSuDungActivity.this);
                        facebookIntent.setData(Uri.parse(facebookUrl));
                        startActivity(facebookIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });

                txt_gmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        String[] recipients = {GMAIL};
                        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "");
                        intent.putExtra(Intent.EXTRA_TEXT, "");
                        intent.putExtra(Intent.EXTRA_CC, GMAIL);
                        intent.setType("text/html");
                        intent.setPackage("com.google.android.gm");
                        startActivity(Intent.createChooser(intent, "Send mail"));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });

                txt_ggmap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);//mở ra một view mới(cụ thể ở đây là trình duyệt)
                        intent.setData(Uri.parse(LINK_GOOGLE_MAP));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });

                bottomSheetDialog.setContentView(view);
                if (!bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.show();
                }

            }
        });

        txt_trangChu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txt_veTrangTrucBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tb_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        tb_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        tb_tiktok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
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

    private void checkNightModeActivated() {
        if (sharedPreferencesDayNight.getBoolean(KEY_ISNIGHTMODE, false)) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void callPhoneNumber() {
        try {
            if (Build.VERSION.SDK_INT > 22) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DieuKhoanSuDungActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 101);
                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(LINK_SO_DIEN_THOAI));
                startActivity(callIntent);
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(LINK_SO_DIEN_THOAI));
                startActivity(callIntent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 101 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhoneNumber();
            } else {
                Log.e("TAG", "Permission not Granted");
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkNightModeActivated();
    }

}