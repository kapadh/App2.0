package com.example.projectkapadh.Adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projectkapadh.Model.BannerModel;
import com.example.projectkapadh.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BannerAdapter extends PagerAdapter {

    private List<BannerModel> bannerModelList;

    public BannerAdapter(List<BannerModel> bannerModelList) {
        this.bannerModelList = bannerModelList;

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.banner_layout, container, false);

        CardView bannerContainer = view.findViewById(R.id.banner_container);
        ImageView bannerImage = view.findViewById(R.id.bannerImage);
        Glide.with(container.getContext()).load(bannerModelList.get(position).getBannerImage()).apply(new RequestOptions().placeholder(R.drawable.imageplaceholder)).into(bannerImage);
        //Picasso.get().load(bannerModelList.get(position).getBannerImage()).placeholder(R.drawable.imageplaceholder).into(bannerImage);

        container.addView(view,0);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return bannerModelList.size();
    }
}
