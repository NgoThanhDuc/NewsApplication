package com.example.newsapp.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.newsapp.R;
import com.example.newsapp.models.ChiTietTinTuc;
import com.example.newsapp.utils.SaveLoadFileUntil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChiTietTinTucTwoViewTypeAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ChiTietTinTuc> arrChiTietTinTuc;
    private BottomSheetDialog bottomSheetDialog;
    private ToggleButton tb_danhDau, tb_shareFacebook, tb_copyLink;
    private SaveLoadFileUntil saveLoadFileUntil;

    public ChiTietTinTucTwoViewTypeAdapter(Context context, ArrayList<ChiTietTinTuc> arrChiTietTinTuc) {
        this.context = context;
        this.arrChiTietTinTuc = arrChiTietTinTuc;
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetTheme);
        saveLoadFileUntil = new SaveLoadFileUntil();
    }

    @Override
    public int getCount() {
        return arrChiTietTinTuc.size();
    }

    @Override
    public Object getItem(int position) {
        return arrChiTietTinTuc.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0)
            return 0;
        else
            return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private class ViewHolder {
        private TextView txt_title, txt_ngayDang, txt_newsName;
        private ImageView img_anhChiTiet;
        private View divider;
        private LinearLayout linearImageSave;
    }

    private class ViewHolderFirst {
        private TextView txt_title, txt_ngayDang;
        private ImageView img_anhChiTiet;
        private LinearLayout linearImageSave;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        int viewType = this.getItemViewType(position);

        switch (viewType) {
            case 0:
                return createAndHandleEventsForTheFirstLineOfRecyclerView(position, convertView, parent);
            case 1:
                return createAndEventHandlingForTheRestOfRecyclerView(position, convertView, parent);
            default:
                return convertView;
        }
    }

    private View createAndHandleEventsForTheFirstLineOfRecyclerView(final int position, View convertView, final ViewGroup parent){
        ViewHolderFirst holderFirst;
        View viewFirst = convertView;

        if (viewFirst == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewFirst = inflater.inflate(R.layout.custom_item_first_listview_layout_chung, parent, false);

            holderFirst = new ViewHolderFirst();
            holderFirst.txt_title = viewFirst.findViewById(R.id.txt_title);
            holderFirst.txt_ngayDang = viewFirst.findViewById(R.id.txt_ngayDang);
            holderFirst.img_anhChiTiet = viewFirst.findViewById(R.id.img_anhChiTiets);
            holderFirst.linearImageSave = viewFirst.findViewById(R.id.linearImageSave);

            viewFirst.setTag(holderFirst);
        } else {
            holderFirst = (ViewHolderFirst) viewFirst.getTag();
        }

        final ChiTietTinTuc chiTietTinTucFirst = arrChiTietTinTuc.get(position);

        // set up the list item
        if (chiTietTinTucFirst != null) {
            if (holderFirst.txt_title != null)
                holderFirst.txt_title.setText(chiTietTinTucFirst.getTitle());

            if (holderFirst.txt_ngayDang != null)
                holderFirst.txt_ngayDang.setText(chiTietTinTucFirst.getPubDate());

            if (holderFirst.img_anhChiTiet != null)
                Picasso.with(context).load(chiTietTinTucFirst.getImage())
                        .placeholder(R.drawable.progress_animation)
                        .error(R.drawable.errorimage)
                        .into(holderFirst.img_anhChiTiet);
        }

        holderFirst.linearImageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View sheetView = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.bottom_sheet_layout,
                        (ViewGroup) parent.findViewById(R.id.bottom_sheet));

                tb_danhDau = sheetView.findViewById(R.id.tb_danhDau);
                tb_shareFacebook = sheetView.findViewById(R.id.tb_shareFacebook);
                tb_copyLink = sheetView.findViewById(R.id.tb_copyLink);

                tb_danhDau.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (tb_danhDau.isChecked() == true) {
                            saveLoadFileUntil.saveFileNews(context, "bookmarkNews.txt", chiTietTinTucFirst.getNewsName(), chiTietTinTucFirst.getTitle(), chiTietTinTucFirst.getLink(), chiTietTinTucFirst.getImage(), chiTietTinTucFirst.getPubDate());
                            Toast.makeText(context, "Đã thêm vào tin đánh dấu", Toast.LENGTH_SHORT).show();
                            tb_danhDau.setChecked(true);
                            bottomSheetDialog.dismiss();
                        } else {
                            saveLoadFileUntil.removeExistsInFileBookmarNews(context, chiTietTinTucFirst.getNewsName(), chiTietTinTucFirst.getTitle(), chiTietTinTucFirst.getLink(), chiTietTinTucFirst.getImage(), chiTietTinTucFirst.getPubDate());
                            Toast.makeText(context, "Đã bỏ khỏi tin đánh dấu", Toast.LENGTH_SHORT).show();
                            tb_danhDau.setChecked(false);
                            bottomSheetDialog.dismiss();
                        }

                    }
                });

                tb_shareFacebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, chiTietTinTucFirst.getLink());
                        intent.setType("text/plain");

                        context.startActivity(Intent.createChooser(intent, "Share to: "));
                        bottomSheetDialog.dismiss();
                    }
                });

                tb_copyLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Copied", chiTietTinTucFirst.getLink());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });

                boolean checkToggleButtonDanhDau = saveLoadFileUntil.checkExistsInFileBookmarNews(context, chiTietTinTucFirst.getNewsName(), chiTietTinTucFirst.getTitle(), chiTietTinTucFirst.getLink(), chiTietTinTucFirst.getImage(), chiTietTinTucFirst.getPubDate());
                if (checkToggleButtonDanhDau)
                    tb_danhDau.setChecked(true);
                else
                    tb_danhDau.setChecked(false);

                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();
            }
        });

        return viewFirst;
    }

    private View createAndEventHandlingForTheRestOfRecyclerView(final int position, View convertView, final ViewGroup parent){
        ViewHolder holder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_item_listview_layout_chung, parent, false);

            holder = new ViewHolder();
            holder.txt_newsName = view.findViewById(R.id.txt_newsName);
            holder.txt_title = view.findViewById(R.id.txt_title);
            holder.txt_ngayDang = view.findViewById(R.id.txt_ngayDang);
            holder.img_anhChiTiet = view.findViewById(R.id.img_anhChiTiets);
            holder.linearImageSave = view.findViewById(R.id.linearImageSave);
            holder.divider = view.findViewById(R.id.divider);

            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        final ChiTietTinTuc chiTietTinTuc = arrChiTietTinTuc.get(position);

        // set up the list item
        if (chiTietTinTuc != null) {

            if (holder.txt_newsName != null)
                holder.txt_newsName.setText(chiTietTinTuc.getNewsName());

            if (holder.txt_title != null)
                holder.txt_title.setText(chiTietTinTuc.getTitle());

            if (holder.txt_ngayDang != null)
                holder.txt_ngayDang.setText(chiTietTinTuc.getPubDate());

            if (holder.img_anhChiTiet != null)
                Picasso.with(context).load(chiTietTinTuc.getImage())
                        .placeholder(R.drawable.progress_animation)
                        .error(R.drawable.errorimage)
                        .into(holder.img_anhChiTiet);

            if (holder.divider != null)
                holder.divider.setVisibility(View.GONE);
        }

        holder.linearImageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View sheetView = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.bottom_sheet_layout,
                        (ViewGroup) parent.findViewById(R.id.bottom_sheet));

                tb_danhDau = sheetView.findViewById(R.id.tb_danhDau);
                tb_shareFacebook = sheetView.findViewById(R.id.tb_shareFacebook);
                tb_copyLink = sheetView.findViewById(R.id.tb_copyLink);

                tb_danhDau.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (tb_danhDau.isChecked() == true) {
                            saveLoadFileUntil.saveFileNews(context, "bookmarkNews.txt", chiTietTinTuc.getNewsName(), chiTietTinTuc.getTitle(), chiTietTinTuc.getLink(), chiTietTinTuc.getImage(), chiTietTinTuc.getPubDate());
                            Toast.makeText(context, "Đã thêm vào tin đánh dấu", Toast.LENGTH_SHORT).show();
                            tb_danhDau.setChecked(true);
                            bottomSheetDialog.dismiss();
                        } else {
                            saveLoadFileUntil.removeExistsInFileBookmarNews(context, chiTietTinTuc.getNewsName(), chiTietTinTuc.getTitle(), chiTietTinTuc.getLink(), chiTietTinTuc.getImage(), chiTietTinTuc.getPubDate());
                            Toast.makeText(context, "Đã bỏ khỏi tin đánh dấu", Toast.LENGTH_SHORT).show();
                            tb_danhDau.setChecked(false);
                            bottomSheetDialog.dismiss();
                        }

                    }
                });

                tb_shareFacebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, chiTietTinTuc.getLink());
                        intent.setType("text/plain");

                        context.startActivity(Intent.createChooser(intent, "Share to: "));
                        bottomSheetDialog.dismiss();
                    }
                });

                tb_copyLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Copied", chiTietTinTuc.getLink());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });

                boolean checkToggleButtonDanhDau = saveLoadFileUntil.checkExistsInFileBookmarNews(context, chiTietTinTuc.getNewsName(), chiTietTinTuc.getTitle(), chiTietTinTuc.getLink(), chiTietTinTuc.getImage(), chiTietTinTuc.getPubDate());
                if (checkToggleButtonDanhDau)
                    tb_danhDau.setChecked(true);
                else
                    tb_danhDau.setChecked(false);

                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();
            }
        });

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
        view.setAnimation(animation);

        return view;
    }
}
