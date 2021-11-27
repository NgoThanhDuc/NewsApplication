package com.example.newsapp.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.newsapp.R;
import com.example.newsapp.adapters.ChiTietTinTucDanhDauAdapter;
import com.example.newsapp.models.ChiTietTinTuc;
import com.example.newsapp.utils.SaveLoadFileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class TinDaDanhDauActivity extends AppCompatActivity {

    private SwipeMenuListView listViewNewsDanhDau;
    private Toolbar toolbarTinDanhDau;
    private TextView txt_noDanhDau;
    private ImageView img_deleteAll;
    private ShimmerLayout shimmer_view_contain;
    private FrameLayout frameLayout_contain;

    private ArrayList<ChiTietTinTuc> arrNewsDanhDau;
    private ChiTietTinTucDanhDauAdapter chiTietTinTucDanhDauAdapter;
    private AlertDialog.Builder builder;

    private SaveLoadFileUtil saveLoadFileUntil;

    private SharedPreferences sharedPreferencesDayNight;
    private static final String MY_PREFERENCES_DAYNIGHT = "nightModePrefs";
    private static final String KEY_ISNIGHTMODE = "isNightMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tin_da_danh_dau);

        init();
        checkNightModeActivated();
        actionBar();
        loadFileNewsDanhDau();
        envent();

    }

    private void init() {
        listViewNewsDanhDau = findViewById(R.id.listView);
        toolbarTinDanhDau = findViewById(R.id.toolbarTinDanhDau);
        img_deleteAll = findViewById(R.id.img_deleteAll);
        txt_noDanhDau = findViewById(R.id.txt_noDanhDau);
        shimmer_view_contain = findViewById(R.id.shimmer_view_contain);
        frameLayout_contain = findViewById(R.id.frameLayout_contain);

        builder = new AlertDialog.Builder(TinDaDanhDauActivity.this);
        saveLoadFileUntil = new SaveLoadFileUtil();

        sharedPreferencesDayNight = getSharedPreferences(MY_PREFERENCES_DAYNIGHT, Context.MODE_PRIVATE);

    }

    private void actionBar() {
        setSupportActionBar(toolbarTinDanhDau);
        toolbarTinDanhDau.setNavigationIcon(R.drawable.ic_baseline_arrow);
        toolbarTinDanhDau.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void checkNightModeActivated() {
        if (sharedPreferencesDayNight.getBoolean(KEY_ISNIGHTMODE, false)) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void envent() {

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(180);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete_sweep);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        listViewNewsDanhDau.setMenuCreator(creator);

        listViewNewsDanhDau.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0: // delete
                        deleteOneItem(position);
                        break;
                }
                // false : close the menu; true : not close the menu
                return true;
            }
        });

        listViewNewsDanhDau.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteOneItemLongClick(position);
                return true;
            }
        });

        listViewNewsDanhDau.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                // kiểm tra đường link có chứa video hay không
                if (arrNewsDanhDau.get(position).getLink().contains("www.bienphong.com.vn/videos/")) {
                    intent = new Intent(TinDaDanhDauActivity.this, VideoNewsActivity.class);

                } else if (arrNewsDanhDau.get(position).getLink().contains("www.bienphong.com.vn/tin-anh/")) {
                    intent = new Intent(TinDaDanhDauActivity.this, SlideImageNewsActivity.class);

                } else if (arrNewsDanhDau.get(position).getTitle().contains("Infographic")) {
                    intent = new Intent(TinDaDanhDauActivity.this, InfographicNewsActivity.class);

                } else if (arrNewsDanhDau.get(position).getTitle().contains("Longform")) {
                    intent = new Intent(TinDaDanhDauActivity.this, LongformNewsActivity.class);

                } else if (arrNewsDanhDau.get(position).getNewsName().contains("_TVA")) {
                    intent = new Intent(TinDaDanhDauActivity.this, SlideImageNewsActivity.class);

                } else {
                    intent = new Intent(TinDaDanhDauActivity.this, ReadNewsActivity.class);
                }

                intent.putExtra("news_name", arrNewsDanhDau.get(position).getNewsName());
                intent.putExtra("title_news", arrNewsDanhDau.get(position).getTitle());
                intent.putExtra("link_news", arrNewsDanhDau.get(position).getLink());
                intent.putExtra("image_news", arrNewsDanhDau.get(position).getImage());
                intent.putExtra("pubdate_news", arrNewsDanhDau.get(position).getPubDate());
                intent.putExtra("tab_selected", saveLoadFileUntil.loadFileTabSelect(getApplicationContext()));

                saveLoadFileUntil.saveFileNews(getApplicationContext(),
                        "bookmarkNews.txt",
                        arrNewsDanhDau.get(position).getNewsName(),
                        arrNewsDanhDau.get(position).getTitle(),
                        arrNewsDanhDau.get(position).getLink(),
                        arrNewsDanhDau.get(position).getImage(),
                        arrNewsDanhDau.get(position).getPubDate());

                startActivity(intent);
            }
        });

        img_deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllItem();
            }
        });

    }

    private void deleteOneItem(int position) {
        arrNewsDanhDau.remove(position);
        chiTietTinTucDanhDauAdapter.notifyDataSetChanged();
        Toast.makeText(TinDaDanhDauActivity.this, Html.fromHtml("<font color='#ffffff'>Xoá thành công!</font>"), Toast.LENGTH_SHORT).show();

        try {

            File file = new File(getFilesDir(), "bookmarkNews.txt");
            if (file.exists()) {

                file.delete(); // xóa dữ liệu trong file

                FileOutputStream fs_out = openFileOutput("bookmarkNews.txt", Context.MODE_APPEND);
                OutputStreamWriter os = new OutputStreamWriter(fs_out);

                for (int i = 0; i < arrNewsDanhDau.size(); i++) { // ghi dữ liệu mới gồm 5 item vào file
                    os.write("[newsName]" + "\n" + arrNewsDanhDau.get(i).getNewsName() + "\n");
                    os.write("[title]" + "\n" + arrNewsDanhDau.get(i).getTitle() + "\n");
                    os.write("[link]" + "\n" + arrNewsDanhDau.get(i).getLink() + "\n");
                    os.write("[image]" + "\n" + arrNewsDanhDau.get(i).getImage() + "\n");
                    os.write("[pubDate]" + "\n" + arrNewsDanhDau.get(i).getPubDate() + "\n");
                    os.write("#\n");
                }
                os.close();

                // xóa từng cái nhưng hết
                FileInputStream fs_in = openFileInput("bookmarkNews.txt");
                InputStreamReader is = new InputStreamReader(fs_in);
                BufferedReader br = new BufferedReader(is);
                String lineRead = br.readLine();
                if (lineRead == null) {
                    img_deleteAll.setEnabled(false);
                    img_deleteAll.setAlpha(0.5f);
                    listViewNewsDanhDau.setVisibility(View.GONE);
                    txt_noDanhDau.setVisibility(View.VISIBLE);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void deleteOneItemLongClick(final int position) {
        builder.setTitle("Xóa bài đã đánh dấu");
        builder.setMessage("Bạn có chắc chắn bỏ đánh dấu Bài này không?");
        builder.setNegativeButton(Html.fromHtml("<font color='#03A9F4'>Không</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(Html.fromHtml("<font color='#FF3D00'>Có</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteOneItem(position);

            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void deleteAllItem() {
        builder.setTitle("Xóa bài đánh dấu");
        builder.setMessage("Bạn có chắc chắn XÓA TẤT CẢ bài đã dánh dấu không?");
        builder.setNegativeButton(Html.fromHtml("<font color='#03A9F4'>Không</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(Html.fromHtml("<font color='#FF3D00'>Xóa tất cả</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                File file = new File(getFilesDir(), "bookmarkNews.txt");
                if (file.exists()) {
                    arrNewsDanhDau.removeAll(arrNewsDanhDau);
                    chiTietTinTucDanhDauAdapter.notifyDataSetChanged();

                    file.delete(); // xóa dữ liệu trong file
                    Toast.makeText(TinDaDanhDauActivity.this, Html.fromHtml("<font color='#ffffff'>Xoá thành công!</font>"), Toast.LENGTH_SHORT).show();

                    img_deleteAll.setEnabled(false);
                    img_deleteAll.setAlpha(0.5f);

                    dialog.dismiss();
                    listViewNewsDanhDau.setVisibility(View.GONE);
                    txt_noDanhDau.setVisibility(View.VISIBLE);
                }

            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showShimmerLayout() {
        frameLayout_contain.setVisibility(View.GONE);
        shimmer_view_contain.setVisibility(View.VISIBLE);
        shimmer_view_contain.startShimmerAnimation();
    }

    private void hideShimmerLayout() {
        shimmer_view_contain.stopShimmerAnimation();
        shimmer_view_contain.setVisibility(View.GONE);
        frameLayout_contain.setVisibility(View.VISIBLE);
    }

    private void loadFileNewsDanhDau() {

        showShimmerLayout();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ArrayList<ChiTietTinTuc> items = new ArrayList<ChiTietTinTuc>();
                try {

                    File file = new File(getFilesDir(), "bookmarkNews.txt");
                    if (file.exists()) {

                        FileInputStream fs_in = openFileInput("bookmarkNews.txt");
                        InputStreamReader is = new InputStreamReader(fs_in);
                        BufferedReader br = new BufferedReader(is);
                        String lineRead = br.readLine();
                        String newsNameFile = "", titleFile = "", imageFile = "", linkFile = "", pubDateFile = "";

                        if (lineRead != null) { // xóa lần lượt từng item sẽ k delete file, file vẫn cần nhưng dữ liệu rỗng

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

                            arrNewsDanhDau = new ArrayList<>();
                            for (int i = items.size() - 1; i >= 0; i--) {
                                arrNewsDanhDau.add(new ChiTietTinTuc(items.get(i).getNewsName(),
                                        items.get(i).getTitle(), items.get(i).getLink(),
                                        items.get(i).getImage(), items.get(i).getPubDate()));
                            }

                            if (arrNewsDanhDau == null) {

                                listViewNewsDanhDau.setVisibility(View.GONE);
                                txt_noDanhDau.setVisibility(View.VISIBLE);
                                img_deleteAll.setEnabled(false);
                                img_deleteAll.setAlpha(0.5f);
                                hideShimmerLayout();

                            } else {
                                listViewNewsDanhDau.setVisibility(View.VISIBLE);
                                txt_noDanhDau.setVisibility(View.GONE);
                                img_deleteAll.setEnabled(true);
                                img_deleteAll.setAlpha(1f);

                                chiTietTinTucDanhDauAdapter = new ChiTietTinTucDanhDauAdapter(TinDaDanhDauActivity.this, arrNewsDanhDau);
                                listViewNewsDanhDau.setAdapter(chiTietTinTucDanhDauAdapter);
                                chiTietTinTucDanhDauAdapter.notifyDataSetChanged();
                                hideShimmerLayout();
                            }

                        } else {
                            listViewNewsDanhDau.setVisibility(View.GONE);
                            txt_noDanhDau.setVisibility(View.VISIBLE);
                            img_deleteAll.setEnabled(false);
                            img_deleteAll.setAlpha(0.5f);
                            hideShimmerLayout();
                        }

                    } else { // file không tồn tại
                        img_deleteAll.setEnabled(false);
                        img_deleteAll.setAlpha(0.5f);
                        listViewNewsDanhDau.setVisibility(View.GONE);
                        txt_noDanhDau.setVisibility(View.VISIBLE);
                        hideShimmerLayout();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, 500);


    }

    @Override
    protected void onRestart() {
        checkNightModeActivated();
        loadFileNewsDanhDau();
        super.onRestart();
    }

}