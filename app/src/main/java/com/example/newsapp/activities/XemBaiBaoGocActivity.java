package com.example.newsapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.newsapp.R;
import com.example.newsapp.network.CheckConnectionUntil;

public class XemBaiBaoGocActivity extends AppCompatActivity {

    private WebView webView;
    private Toolbar toolbarReadNews;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String link = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_bai_bao_goc);

        if (CheckConnectionUntil.haveNetworkConnection(this)) {
            init();
            actionBar();
            events();
        } else {
            CheckConnectionUntil.showDialogNoUpdateData(XemBaiBaoGocActivity.this);
        }

    }

    private void init() {

        Intent intent = getIntent();
        link = intent.getStringExtra("link_news_read_activity");

        toolbarReadNews = findViewById(R.id.toolbarReadNews);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.GREEN);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Please Wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        Drawable drawable = new ProgressBar(this).getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.orange), PorterDuff.Mode.SRC_IN);
        progressDialog.setIndeterminateDrawable(drawable);

        webView = findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true); // Enable responsive layout
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);

        webView.addJavascriptInterface(new WebAppInterface(this), "Android"); // Bind mã JavaScript vào mã Android

        webView.setWebViewClient(new MyWebViewClient()); // Xử lý Điều hướng Trang
        webView.loadUrl(link);
    }

    private void actionBar() {
        setSupportActionBar(toolbarReadNews);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarReadNews.setNavigationIcon(R.drawable.ic_baseline_arrow);
        toolbarReadNews.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.destroy();
                finish();
            }
        });
    }

    private void events() {

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                progressDialog.show();
                if (newProgress >= 80) {
                    progressDialog.dismiss();
                }

                super.onProgressChanged(view, newProgress);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            swipeRefreshLayout.setRefreshing(false);
            progressDialog.dismiss();
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);

            if (Uri.parse(url).getHost().equals("www.example.com")) {
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);

            return true;
        }
    }

    private class WebAppInterface {
        Context context;

        public WebAppInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();

        } else {
            webView.destroy();
            super.onBackPressed();
        }
    }

}