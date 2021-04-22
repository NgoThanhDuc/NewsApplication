package com.example.newsapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.newsapp.R;
import com.example.newsapp.models.ChiTietTinTuc;
import com.example.newsapp.utils.SaveLoadFileUntil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChiTietTinTucDanhDauAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ChiTietTinTuc> arrChiTietTinTuc;
    private SaveLoadFileUntil saveLoadFileUntil;

    public ChiTietTinTucDanhDauAdapter(Context context, ArrayList<ChiTietTinTuc> arrChiTietTinTuc) {
        this.context = context;
        this.arrChiTietTinTuc = arrChiTietTinTuc;
        saveLoadFileUntil = new SaveLoadFileUntil();
    }

    public void addListItemAdapter(ArrayList<ChiTietTinTuc> itemPlus) {
        arrChiTietTinTuc.addAll(itemPlus);
        this.notifyDataSetChanged();
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

    public class ViewHolder {
        private TextView txt_title, txt_ngayDang, txt_newsName;
        private ImageView img_anhChiTiet;
        private View divider;
        private LinearLayout linearImageSave;
    }

    @Override
    public View getView(final int position, View view, final ViewGroup parent) {

        final ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_item_listview_layout_chung, null);

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

        if (chiTietTinTuc != null) {
            holder.txt_newsName.setText(chiTietTinTuc.getNewsName());
            holder.txt_title.setText(chiTietTinTuc.getTitle());

            holder.txt_ngayDang.setText(chiTietTinTuc.getPubDate());
            Picasso.with(context).load(chiTietTinTuc.getImage())
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.errorimage)
                    .into(holder.img_anhChiTiet);
            holder.divider.setVisibility(View.GONE);
            holder.linearImageSave.setVisibility(View.GONE);
        }

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
        view.setAnimation(animation);

        return view;
    }
}
