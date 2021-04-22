package com.example.newsapp.utils;

import android.view.View;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentActivity;

import com.example.newsapp.R;
import com.stepstone.apprating.AppRatingDialog;

import java.util.Arrays;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class ShowHideUntil {

    public static void showDialogRating(FragmentActivity activity) {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Gửi")
                .setNegativeButtonText("Thoát")
                .setNeutralButtonText("Để sau")
                .setNoteDescriptions(Arrays.asList("Rất tệ", "Không tốt", "Khá ổn", "Rất tốt", "Tuyệt vời !!!"))
                .setDefaultRating(2)
                .setTitle("Đánh giá ứng dụng")
                .setDescription("Vui lòng chọn một số ngôi sao và đưa ra phản hồi của bạn")
                .setStarColor(R.color.yellow)
                .setNoteDescriptionTextColor(R.color.greendark)
                .setTitleTextColor(R.color.greendark)
                .setDescriptionTextColor(R.color.greendark)
                .setCommentBackgroundColor(R.color.gray)
                .setWindowAnimation(R.style.MyAlertDialogStyle)
                .setHint("Vui lòng viết bình luận của bạn ở đây ...")
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(activity)
                .show();

    }

    public static void showShimmerLayout(FrameLayout frameLayout_contain, ShimmerLayout shimmer_view_contain) {
        frameLayout_contain.setVisibility(View.GONE);
        shimmer_view_contain.setVisibility(View.VISIBLE);
        shimmer_view_contain.startShimmerAnimation();
    }

    public static void hideShimmerLayout(FrameLayout frameLayout_contain, ShimmerLayout shimmer_view_contain) {
        shimmer_view_contain.stopShimmerAnimation();
        shimmer_view_contain.setVisibility(View.GONE);
        frameLayout_contain.setVisibility(View.VISIBLE);
    }

}
