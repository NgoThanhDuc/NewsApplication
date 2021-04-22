package com.example.newsapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newsapp.R;
import com.example.newsapp.utils.SharedPreferencesUntil;

public class SplashScreenActivity extends AppCompatActivity {

    private final int SPLASH_TIME_OUT = 4000;
    private Animation top_splash_screen, bottom_splash_screen;
    private ImageView imageView;
    private TextView textView, textView2;

    private SharedPreferencesUntil sharedPreferencesUntil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        sharedPreferencesUntil = new SharedPreferencesUntil(SplashScreenActivity.this);

        top_splash_screen = AnimationUtils.loadAnimation(this, R.anim.top_splash_screen);
        bottom_splash_screen = AnimationUtils.loadAnimation(this, R.anim.bottom_splash_screen);

        imageView.setAnimation(top_splash_screen);
        textView.setAnimation(bottom_splash_screen);
        textView2.setAnimation(bottom_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sharedPreferencesUntil.checkNewsNameModeActivated(SplashScreenActivity.this);
            }
        }, SPLASH_TIME_OUT);
    }

    // hide navigation bar
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

}