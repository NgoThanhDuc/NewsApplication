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
import com.example.newsapp.adapters.ChiTietTinTucNormalAdapter;
import com.example.newsapp.models.ChiTietTinTuc;
import com.example.newsapp.utils.SaveLoadFileUntil;

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

public class NewsHistoryActivity extends AppCompatActivity {

    private SwipeMenuListView listViewNewsHitory;
    private Toolbar toolbarNewsHistory;
    private TextView txt_noNewsHistory;
    private ImageView img_deleteAll;
    private ShimmerLayout shimmer_view_contain;
    private FrameLayout frameLayout_contain;

    private ArrayList<ChiTietTinTuc> arrNewsHistory;
    private ChiTietTinTucNormalAdapter chiTietTinTucNormalAdapter;
    private AlertDialog.Builder builder;

    private SaveLoadFileUntil saveLoadFileUntil;

    private SharedPreferences sharedPreferencesDayNight;
    private static final String MY_PREFERENCES_DAYNIGHT = "nightModePrefs";
    private static final String KEY_ISNIGHTMODE = "isNightMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_history);

        init();
        checkNightModeActivated();
        actionBar();
        loadFileNewsHistory();
        envent();

    }

    private void init() {
        listViewNewsHitory = findViewById(R.id.listView);
        toolbarNewsHistory = findViewById(R.id.toolbarNewsHistory);
        img_deleteAll = findViewById(R.id.img_deleteAll);
        txt_noNewsHistory = findViewById(R.id.txt_noNewsHitory);
        shimmer_view_contain = findViewById(R.id.shimmer_view_contain);
        frameLayout_contain = findViewById(R.id.frameLayout_contain);

        builder = new AlertDialog.Builder(NewsHistoryActivity.this);
        saveLoadFileUntil = new SaveLoadFileUntil();

        sharedPreferencesDayNight = getSharedPreferences(MY_PREFERENCES_DAYNIGHT, Context.MODE_PRIVATE);


    }

    private void actionBar() {
        setSupportActionBar(toolbarNewsHistory);
        toolbarNewsHistory.setNavigationIcon(R.drawable.ic_baseline_arrow);
        toolbarNewsHistory.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
        listViewNewsHitory.setMenuCreator(creator);

        listViewNewsHitory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                deleteOneItemLongClick(position);
                return true;
            }
        });

        listViewNewsHitory.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
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

        listViewNewsHitory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = null;
                // kiểm tra đường link có chứa video hay không
                if (arrNewsHistory.get(position).getLink().contains("www.bienphong.com.vn/videos/")) {
                    intent = new Intent(NewsHistoryActivity.this, VideoNewsActivity.class);

                } else if (arrNewsHistory.get(position).getLink().contains("www.bienphong.com.vn/tin-anh/")) {
                    intent = new Intent(NewsHistoryActivity.this, SlideImageNewsActivity.class);

                } else if (arrNewsHistory.get(position).getTitle().contains("Infographic")) {
                    intent = new Intent(NewsHistoryActivity.this, InfographicNewsActivity.class);

                } else if (arrNewsHistory.get(position).getTitle().contains("Longform")) {
                    intent = new Intent(NewsHistoryActivity.this, LongformNewsActivity.class);

                } else if (arrNewsHistory.get(position).getNewsName().contains("_TVA")) {
                    intent = new Intent(NewsHistoryActivity.this, SlideImageNewsActivity.class);

                } else {
                    intent = new Intent(NewsHistoryActivity.this, ReadNewsActivity.class);
                }

                intent.putExtra("news_name", arrNewsHistory.get(position).getNewsName());
                intent.putExtra("title_news", arrNewsHistory.get(position).getTitle());
                intent.putExtra("link_news", arrNewsHistory.get(position).getLink());
                intent.putExtra("image_news", arrNewsHistory.get(position).getImage());
                intent.putExtra("pubdate_news", arrNewsHistory.get(position).getPubDate());
                intent.putExtra("tab_selected", saveLoadFileUntil.loadFileTabSelect(getApplicationContext()));

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
        arrNewsHistory.remove(position);
        chiTietTinTucNormalAdapter.notifyDataSetChanged();
        Toast.makeText(NewsHistoryActivity.this, Html.fromHtml("<font color='#ffffff'>Xoá thành công!</font>"), Toast.LENGTH_SHORT).show();

        try {

            File file = new File(getFilesDir(), "historyNews.txt");
            if (file.exists()) {

                file.delete(); // xóa dữ liệu trong file

                FileOutputStream fs_out = openFileOutput("historyNews.txt", Context.MODE_APPEND);
                OutputStreamWriter os = new OutputStreamWriter(fs_out);

                for (int i = 0; i < arrNewsHistory.size(); i++) { // ghi dữ liệu mới gồm 5 item vào file
                    os.write("[newsName]" + "\n" + arrNewsHistory.get(i).getNewsName() + "\n");
                    os.write("[title]" + "\n" + arrNewsHistory.get(i).getTitle() + "\n");
                    os.write("[link]" + "\n" + arrNewsHistory.get(i).getLink() + "\n");
                    os.write("[image]" + "\n" + arrNewsHistory.get(i).getImage() + "\n");
                    os.write("[pubDate]" + "\n" + arrNewsHistory.get(i).getPubDate() + "\n");
                    os.write("#\n");
                }
                os.close();

                // xóa từng cái nhưng hết
                FileInputStream fs_in = openFileInput("historyNews.txt");
                InputStreamReader is = new InputStreamReader(fs_in);
                BufferedReader br = new BufferedReader(is);
                String lineRead = br.readLine();
                if (lineRead == null) {
                    img_deleteAll.setEnabled(false);
                    img_deleteAll.setAlpha(0.5f);
                    listViewNewsHitory.setVisibility(View.GONE);
                    txt_noNewsHistory.setVisibility(View.VISIBLE);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void deleteOneItemLongClick(final int position) {
        builder.setTitle("Xóa bài đã đọc");
        builder.setMessage("Bạn có chắc chắn Bài này không?");
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
        builder.setTitle("Xóa bài đã đọc");
        builder.setMessage("Bạn có chắc chắn XÓA TẤT CẢ bài đã đọc gần đây không?");
        builder.setNegativeButton(Html.fromHtml("<font color='#03A9F4'>Không</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(Html.fromHtml("<font color='#FF3D00'>Xóa tất cả</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                File file = new File(getFilesDir(), "historyNews.txt");
                if (file.exists()) {
                    arrNewsHistory.removeAll(arrNewsHistory);
                    chiTietTinTucNormalAdapter.notifyDataSetChanged();

                    file.delete(); // xóa dữ liệu trong file
                    Toast.makeText(NewsHistoryActivity.this, Html.fromHtml("<font color='#ffffff'>Xoá thành công!</font>"), Toast.LENGTH_SHORT).show();

                    img_deleteAll.setEnabled(false);
                    img_deleteAll.setAlpha(0.5f);

                    dialog.dismiss();
                    listViewNewsHitory.setVisibility(View.GONE);
                    txt_noNewsHistory.setVisibility(View.VISIBLE);
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

    public void checkNightModeActivated() {
        if (sharedPreferencesDayNight.getBoolean(KEY_ISNIGHTMODE, false)) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void loadFileNewsHistory() {

        showShimmerLayout();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ArrayList<ChiTietTinTuc> items = new ArrayList<ChiTietTinTuc>();
                try {

                    File file = new File(getFilesDir(), "historyNews.txt");
                    if (file.exists()) {

                        FileInputStream fs_in = openFileInput("historyNews.txt");
                        InputStreamReader is = new InputStreamReader(fs_in);
                        BufferedReader br = new BufferedReader(is);
                        String lineRead = br.readLine();
                        String newsNameFile = "", titleFile = "", imageFile = "", linkFile = "", pubDateFile = "";

                        if (lineRead != null) {
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

                            arrNewsHistory = new ArrayList<>();

                            for (int i = items.size() - 1; i >= 0; i--) {

                                arrNewsHistory.add(new ChiTietTinTuc(items.get(i).getNewsName(),
                                        items.get(i).getTitle(), items.get(i).getLink(),
                                        items.get(i).getImage(), items.get(i).getPubDate()));

                            }

                            if (arrNewsHistory == null) {
                                listViewNewsHitory.setVisibility(View.GONE);
                                txt_noNewsHistory.setVisibility(View.VISIBLE);
                                img_deleteAll.setEnabled(false);
                                img_deleteAll.setAlpha(0.5f);
                                hideShimmerLayout();

                            } else {
                                listViewNewsHitory.setVisibility(View.VISIBLE);
                                txt_noNewsHistory.setVisibility(View.GONE);
                                img_deleteAll.setEnabled(true);
                                img_deleteAll.setAlpha(1f);

                                chiTietTinTucNormalAdapter = new ChiTietTinTucNormalAdapter(NewsHistoryActivity.this, arrNewsHistory);
                                listViewNewsHitory.setAdapter(chiTietTinTucNormalAdapter);
                                chiTietTinTucNormalAdapter.notifyDataSetChanged();
                                hideShimmerLayout();
                            }

                        } else { // data trống
                            listViewNewsHitory.setVisibility(View.GONE);
                            txt_noNewsHistory.setVisibility(View.VISIBLE);
                            img_deleteAll.setEnabled(false);
                            img_deleteAll.setAlpha(0.5f);
                            hideShimmerLayout();
                        }


                    } else { // file không tồn tại
                        img_deleteAll.setEnabled(false);
                        img_deleteAll.setAlpha(0.5f);
                        listViewNewsHitory.setVisibility(View.GONE);
                        txt_noNewsHistory.setVisibility(View.VISIBLE);
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
        super.onRestart();

    }
}