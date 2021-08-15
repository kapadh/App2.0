package com.example.projectkapadh.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.projectkapadh.Adapter.BannerAdapter;
import com.example.projectkapadh.Adapter.GridProductLayoutAdapter;
import com.example.projectkapadh.Model.BannerModel;
import com.example.projectkapadh.Model.ConsultancyModel;
import com.example.projectkapadh.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomepageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomepageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomepageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomepageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomepageFragment newInstance(String param1, String param2) {
        HomepageFragment fragment = new HomepageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /////////////////////////////////////////////////////
    private ViewPager bannerSliderViewpager;
    private BannerAdapter adapter;
    private List<BannerModel> bannerModelList;
    private int currentPage;
    private Timer timer;
    final private long DELAY_TIME = 3000;
    final private long PERIOD_TIME = 3000;
    private List<BannerModel> arrangedList;

    private TabLayout viewpagerIndicator;

    ArrayList<ConsultancyModel> consultancyModelsList;

    ////////////////////////////////////////////////////

    DatabaseReference databaseReference, bannerRef;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        bannerRef = databaseReference.child("HomepageFeed").child("Banner");

        bannerSliderViewpager = view.findViewById(R.id.ad_banner_viewpager);
        bannerModelList = new ArrayList<>();

        viewpagerIndicator = view.findViewById(R.id.viewpager_indicator);

        bannerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    BannerModel item = ds.getValue(BannerModel.class);
                    bannerModelList.add(item);
                    Log.d("imageUrl", item.getBannerImage());
                }

                bannerAutoSlide();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        viewpagerIndicator.setupWithViewPager(bannerSliderViewpager,true);

        loadConsultancies(view);

        return  view;
    }

    private void loadConsultancies(View view) {

        consultancyModelsList = new ArrayList<>();

        DatabaseReference consultancyRef = databaseReference.child("Consultancies");

                consultancyRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            ConsultancyModel item = new ConsultancyModel(ds.getKey(), ds.child("1ImageUrl").getValue(String.class));
                            consultancyModelsList.add(item);
                            Log.d(item.getTitle(), item.getImageUrl());
                        }

                        GridProductLayoutAdapter adapter = new GridProductLayoutAdapter(getContext(), consultancyModelsList);
                        GridView gridView = view.findViewById(R.id.gridProductLayoutGridview);
                        gridView.setAdapter(adapter);

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });



    }

    @SuppressLint("ClickableViewAccessibility")
    private void bannerAutoSlide(){

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

        adapter = new BannerAdapter(arrangedList);
        bannerSliderViewpager.setAdapter(adapter);
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
                if(state == ViewPager.SCROLL_STATE_IDLE){
                    pageLooper(arrangedList);
                }
            }
        };
        bannerSliderViewpager.addOnPageChangeListener(onPageChangeListener);

        startBannerSlideShow(arrangedList);

        bannerSliderViewpager.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pageLooper(arrangedList);
                stopBannerSlideShow();
                if(event.getAction() == MotionEvent.ACTION_UP){
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