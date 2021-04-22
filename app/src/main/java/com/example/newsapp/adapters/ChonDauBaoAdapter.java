package com.example.newsapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.R;
import com.example.newsapp.interfaces.RecyclerViewClickInterface;
import com.example.newsapp.models.ChonKenhBao;

import java.util.ArrayList;

public class ChonDauBaoAdapter extends RecyclerView.Adapter<ChonDauBaoAdapter.MyViewHoler> {

    private Context context;
    private ArrayList<ChonKenhBao> chonKenhBaoArrayList;
    private RecyclerViewClickInterface recyclerViewClickInterface;

    public ChonDauBaoAdapter(Context context, ArrayList<ChonKenhBao> chonKenhBaoArrayList, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.context = context;
        this.chonKenhBaoArrayList = chonKenhBaoArrayList;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }

    @NonNull
    @Override
    public MyViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.custom_item_chon_kenh_bao, parent, false);
        return new MyViewHoler(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoler holder, int position) {
        holder.txt_nameNews.setText(chonKenhBaoArrayList.get(position).getNameNews());
        holder.imageNews.setBackgroundResource(chonKenhBaoArrayList.get(position).getImageNews());
    }

    @Override
    public int getItemCount() {
        return chonKenhBaoArrayList.size();
    }

    public void filterList(ArrayList<ChonKenhBao> filterlist) {
        chonKenhBaoArrayList = filterlist;
        notifyDataSetChanged();
    }

    public class MyViewHoler extends RecyclerView.ViewHolder {

        private TextView txt_nameNews;
        private ImageView imageNews;

        public MyViewHoler(@NonNull final View itemView) {
            super(itemView);

            txt_nameNews = itemView.findViewById(R.id.txt_nameNews);
            imageNews = itemView.findViewById(R.id.imageNews);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickInterface.onItemClick(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    recyclerViewClickInterface.onItemClick(getAdapterPosition());
                    return true;
                }
            });

        }
    }
}
