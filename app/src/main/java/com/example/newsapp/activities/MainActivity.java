package com.example.newsapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.R;
import com.example.newsapp.interfaces.RecyclerViewClickInterface;
import com.example.newsapp.adapters.ChonDauBaoAdapter;
import com.example.newsapp.models.ChonKenhBao;
import com.example.newsapp.utils.SharedPreferencesUtil;
import com.example.newsapp.utils.VNCharacterUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickInterface {

    private EditText ed_search;
    private ImageView img_menu, img_cancel;
    private RecyclerView recyclerView;
    private ArrayList<ChonKenhBao> arrayListKenhBao;
    private ChonDauBaoAdapter adapterKenhBao;

    private GridLayoutManager gridLayoutManager;

    private SharedPreferences sharedPreferencesDayNight;
    private static final String MY_PREFERENCES_DAYNIGHT = "nightModePrefs";
    private static final String KEY_ISNIGHTMODE = "isNightMode";

    private SharedPreferencesUtil sharedPreferencesUntil;

    private VNCharacterUtil vnCharacterUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initDataApp();
        events();

    }

    private void init() {

        recyclerView = findViewById(R.id.recyclerView);
        img_menu = findViewById(R.id.img_menu);
        img_cancel = findViewById(R.id.img_cancel);
        ed_search = findViewById(R.id.ed_search);

        arrayListKenhBao = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3, LinearLayoutManager.VERTICAL, false);
        sharedPreferencesUntil = new SharedPreferencesUtil(MainActivity.this);
        vnCharacterUtil = new VNCharacterUtil();
    }

    private void initDataApp() {

        arrayListKenhBao.add(new ChonKenhBao(R.drawable.ic_bao_tuoitre, "B??o Tu???i Tr???"));
        arrayListKenhBao.add(new ChonKenhBao(R.drawable.ic_bao_bienphong, "B??o Bi??n Ph??ng"));
        arrayListKenhBao.add(new ChonKenhBao(R.drawable.ic_bao_dautu, "B??o ?????u T??"));
        arrayListKenhBao.add(new ChonKenhBao(R.drawable.ic_bao_phapluat, "B??o Ph??p Lu???t"));
        arrayListKenhBao.add(new ChonKenhBao(R.drawable.ic_bao_ngoisao, "B??o Ng??i Sao"));
        arrayListKenhBao.add(new ChonKenhBao(R.drawable.ic_bao_nguoilaodong, "B??o Ng?????i Lao ?????ng"));

        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        adapterKenhBao = new ChonDauBaoAdapter(MainActivity.this, arrayListKenhBao, this);
        recyclerView.setAdapter(adapterKenhBao);

        sharedPreferencesDayNight = getSharedPreferences(MY_PREFERENCES_DAYNIGHT, Context.MODE_PRIVATE);

        checkNightModeActivated();
    }

    private void events() {

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count <= 0) {
                    img_cancel.setVisibility(View.GONE);
                } else {
                    img_cancel.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_search.setText("");
                img_cancel.setVisibility(View.GONE);
            }
        });

    }

    private void filter(String text) {
        ArrayList<ChonKenhBao> filterlist = new ArrayList<>();
        for (ChonKenhBao item : arrayListKenhBao) {
            if (vnCharacterUtil.removeAccent(item.getNameNews().toLowerCase()).contains(vnCharacterUtil.removeAccent(text.toLowerCase()))) {
                filterlist.add(item);
            }
        }
        adapterKenhBao.filterList(filterlist);
    }

    public void checkNightModeActivated() {
        if (sharedPreferencesDayNight.getBoolean(KEY_ISNIGHTMODE, false)) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    protected void onRestart() {
        checkNightModeActivated();
        super.onRestart();
    }

    @Override
    public void onItemClick(int posittion) {

        switch (arrayListKenhBao.get(posittion).getNameNews()) {
            case "B??o Tu???i Tr???":
                startActivity(new Intent(MainActivity.this, BaoTuoiTreActivity.class));
                sharedPreferencesUntil.saveNewsNameModeState("B??o Tu???i Tr???");
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "B??o Bi??n Ph??ng":
                startActivity(new Intent(MainActivity.this, BaoBienPhongActivity.class));
                sharedPreferencesUntil.saveNewsNameModeState("B??o Bi??n Ph??ng");
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "B??o ?????u T??":
                startActivity(new Intent(MainActivity.this, BaoDauTuActivity.class));
                sharedPreferencesUntil.saveNewsNameModeState("B??o ?????u T??");
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "B??o Ph??p Lu???t":
                startActivity(new Intent(MainActivity.this, BaoPhapLuatActivity.class));
                sharedPreferencesUntil.saveNewsNameModeState("B??o Ph??p Lu???t");
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "B??o Ng??i Sao":
                startActivity(new Intent(MainActivity.this, BaoNgoiSaoActivity.class));
                sharedPreferencesUntil.saveNewsNameModeState("B??o Ng??i Sao");
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "B??o Ng?????i Lao ?????ng":
                startActivity(new Intent(MainActivity.this, BaoNguoiLaoDongActivity.class));
                sharedPreferencesUntil.saveNewsNameModeState("B??o Ng?????i Lao ?????ng");
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

        }
    }

    @Override
    public void onItemLongClick(int posittion) {
    }

}