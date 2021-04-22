package com.example.newsapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.newsapp.R;
import com.example.newsapp.models.ImagePlaceHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SlideImageHolderAdapter extends RecyclerView.Adapter<SlideImageHolderAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ImagePlaceHolder> arrSlideImage;
    private ViewPager2 viewPager2;

    public SlideImageHolderAdapter(Context context, ArrayList<ImagePlaceHolder> arrSlideImage, ViewPager2 viewPager2) {
        this.context = context;
        this.arrSlideImage = arrSlideImage;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_slide_image_news, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.txt_descImage.setText(arrSlideImage.get(position).getPlaceHolder());

        Picasso.with(context).load(Uri.parse(arrSlideImage.get(position).getImage()))
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.errorimage)
                .into(holder.imageSlide);
    }

    @Override
    public int getItemCount() {
        return arrSlideImage.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView imageSlide;
        private TextView txt_descImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageSlide = itemView.findViewById(R.id.imageSlide);
            txt_descImage = itemView.findViewById(R.id.txt_descImage);
        }
    }
}
