package com.example.projectkapadh.Adapter;

import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.projectkapadh.Model.BannerModel;
import com.example.projectkapadh.Model.GridProductLayoutModel;
import com.example.projectkapadh.Model.HomepageModel;
import com.example.projectkapadh.R;
import com.example.projectkapadh.ShowItemRecyclerviewActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomepageAdapter extends RecyclerView.Adapter {

    private List<HomepageModel> homepageModelList;

    public HomepageAdapter(List<HomepageModel> homepageModelList) {
        this.homepageModelList = homepageModelList;
    }

    @Override
    public int getItemViewType(int position) {
        switch (homepageModelList.get(position).getType()) {
            case 0:
                return HomepageModel.BANNER_SLIDER;
            case 1:
                return HomepageModel.GRID_PRODUCT_VIEW;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HomepageModel.BANNER_SLIDER:
                View bannerSliderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliding_ad_banner_layout, parent, false);
                return new BannerSliderViewHolder(bannerSliderView);
            case HomepageModel.GRID_PRODUCT_VIEW:
                    View gridProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_product_layout, parent, false);
                    return  new GridProductViewHolder(gridProductView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (homepageModelList.get(position).getType()) {
            case HomepageModel.BANNER_SLIDER:
                List<BannerModel> bannerModelList = homepageModelList.get(position).getBannerModelList();
                ((BannerSliderViewHolder) holder).setBannerSliderViewPager(bannerModelList);
                break;
            case HomepageModel.GRID_PRODUCT_VIEW:
                String gridLayoutTitle = homepageModelList.get(position).getTitle();
                List<GridProductLayoutModel> gridProductLayoutModelList = homepageModelList.get(position).getGridProductLayoutModelList();
                ((GridProductViewHolder) holder).setGridProductLayout(gridProductLayoutModelList, gridLayoutTitle);
                break;
            default:
                return;
        }
    }

    @Override
    public int getItemCount() {
        return homepageModelList.size();
    }

    //////////////////Banner

    public class BannerSliderViewHolder extends RecyclerView.ViewHolder {

        private ViewPager bannerSliderViewpager;
        //private ImageView bannerImage;
        //private List<BannerModel> bannerModelList;
        private int currentPage;
        private Timer timer;
        final private long DELAY_TIME = 2000;
        final private long PERIOD_TIME = 2000;
        private List<BannerModel> arrangedList;

        public BannerSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerSliderViewpager = itemView.findViewById(R.id.ad_banner_viewpager);
            //bannerImage = itemView.findViewById(R.id.bannerImage);
        }

        private void setBannerSliderViewPager(final List<BannerModel> bannerModelList) {

            currentPage = 2;
            if(timer != null){
                timer.cancel();
            }
            arrangedList = new ArrayList<>();
            for (int x = 0; x < bannerModelList.size(); x++) {
                arrangedList.add(x, bannerModelList.get(x));
            }
            if(arrangedList.size() > 1){
                arrangedList.add(0, bannerModelList.get(bannerModelList.size() - 2));
                arrangedList.add(1, bannerModelList.get(bannerModelList.size() - 1));
                arrangedList.add(bannerModelList.get(0));
                arrangedList.add(bannerModelList.get(1));
            }
//            else{
//
//                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) bannerSliderViewpager.getLayoutParams();
//                params.leftMargin =8 ; params.rightMargin = 8;
//                params.topMargin = 16; params.bottomMargin = 16;
//                bannerSliderViewpager.setLayoutParams(params);
//            }

            BannerAdapter bannerAdapter = new BannerAdapter(arrangedList);
            bannerSliderViewpager.setAdapter(bannerAdapter);
            bannerSliderViewpager.setClipToPadding(false);
            bannerSliderViewpager.setPageMargin(20);

            bannerSliderViewpager.setCurrentItem(currentPage);

            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        pageLooper(arrangedList);
                    }
                }
            };
            bannerSliderViewpager.addOnPageChangeListener(onPageChangeListener);

            startBannerSlideShow(arrangedList);

            bannerSliderViewpager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    pageLooper(arrangedList);
                    stopBannerSlideShow();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        startBannerSlideShow(arrangedList);
                    }
                    return false;
                }
            });
        }

        private void pageLooper(List<BannerModel> bannerModelList) {
            if (currentPage == bannerModelList.size() - 2) {
                currentPage = 2;
                bannerSliderViewpager.setCurrentItem(currentPage, false);
            }
            if (currentPage == 1) {
                currentPage = bannerModelList.size() - 3;
                bannerSliderViewpager.setCurrentItem(currentPage, false);
            }
        }

        private void startBannerSlideShow(final List<BannerModel> bannerModelList) {
            Handler handler = new Handler();
            Runnable update = new Runnable() {
                @Override
                public void run() {
                    if (currentPage >= bannerModelList.size()) {
                        currentPage = 1;
                    }
                    bannerSliderViewpager.setCurrentItem(currentPage++, true);
                }
            };
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, DELAY_TIME, PERIOD_TIME);

        }

        private void stopBannerSlideShow() {
            timer.cancel();
        }
    }

    //////////////Banner











    //////////GridViewHolder

    public class GridProductViewHolder extends RecyclerView.ViewHolder{
        private TextView gridLayoutTitle;
        private Button gridLayoutViewAll;
        private GridView gridView;

        public GridProductViewHolder(@NonNull View itemView) {
            super(itemView);
            gridLayoutTitle = itemView.findViewById(R.id.gridLayoutTitle);
//            gridLayoutViewAll = itemView.findViewById(R.id.viewAllBtn);
            gridView = itemView.findViewById(R.id.gridProductLayoutGridview);
        }
        private void setGridProductLayout(List<GridProductLayoutModel> gridProductLayoutModelList, String title){
            gridLayoutTitle.setText(title);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String itemTv = gridProductLayoutModelList.get(position).getConsultancyTitle();
                    //Toast.makeText(parent.getContext(), itemTv, Toast.LENGTH_SHORT).show();
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), ShowItemRecyclerviewActivity.class)
                    .putExtra("consultancyType", itemTv));
                }
            });
//            gridView.setAdapter(new GridProductLayoutAdapter(gridProductLayoutModelList));

        }


    }

    //////////GridViewHolder
}
