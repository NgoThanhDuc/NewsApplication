package com.example.newsapp.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.newsapp.activities.BaoBienPhongActivity;
import com.example.newsapp.activities.BaoDauTuActivity;
import com.example.newsapp.activities.BaoNgoiSaoActivity;
import com.example.newsapp.activities.BaoNguoiLaoDongActivity;
import com.example.newsapp.activities.BaoPhapLuatActivity;
import com.example.newsapp.activities.BaoTuoiTreActivity;
import com.example.newsapp.activities.MainActivity;

public class SharedPreferencesUntil {

    // newsName
    private SharedPreferences sharedPreferencesNewsName;
    private static final String MY_PREFERENCES_NEWSNAME = "newsNamePrefs";
    private static final String KEY_NEWSNAME = "news_name";

    public SharedPreferencesUntil(Context context) {
        sharedPreferencesNewsName = context.getSharedPreferences(MY_PREFERENCES_NEWSNAME, Context.MODE_PRIVATE);
    }

    public void saveNewsNameModeState(String newsName) {
        SharedPreferences.Editor editor = sharedPreferencesNewsName.edit();
        editor.putString(KEY_NEWSNAME, newsName);
        editor.apply();
    }

    public void checkNewsNameModeActivated(Context context) {
        if (sharedPreferencesNewsName.getString(KEY_NEWSNAME, "").equals("")) {
            context.startActivity(new Intent(context, MainActivity.class));

        } else {
            if (sharedPreferencesNewsName.getString(KEY_NEWSNAME, "").equals("Báo Tuổi Trẻ")) {
                context.startActivity(new Intent(context, BaoTuoiTreActivity.class));
            } else if (sharedPreferencesNewsName.getString(KEY_NEWSNAME, "").equals("Báo Biên Phòng")) {
                context.startActivity(new Intent(context, BaoBienPhongActivity.class));
            } else if (sharedPreferencesNewsName.getString(KEY_NEWSNAME, "").equals("Báo Đầu Tư")) {
                context.startActivity(new Intent(context, BaoDauTuActivity.class));
            } else if (sharedPreferencesNewsName.getString(KEY_NEWSNAME, "").equals("Báo Pháp Luật")) {
                context.startActivity(new Intent(context, BaoPhapLuatActivity.class));
            } else if (sharedPreferencesNewsName.getString(KEY_NEWSNAME, "").equals("Báo Ngôi Sao")) {
                context.startActivity(new Intent(context, BaoNgoiSaoActivity.class));
            } else if (sharedPreferencesNewsName.getString(KEY_NEWSNAME, "").equals("Báo Người Lao Động")) {
                context.startActivity(new Intent(context, BaoNguoiLaoDongActivity.class));
            }

        }
    }

}
