package com.example.projectkapadh.Fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.projectkapadh.Adapter.BannerAdapter;
import com.example.projectkapadh.Adapter.GridProductLayoutAdapter;
import com.example.projectkapadh.Adapter.HistoryRecyclerviewItemAdapter;
import com.example.projectkapadh.Adapter.HomepageAdapter;
import com.example.projectkapadh.Model.BannerModel;
import com.example.projectkapadh.Model.GridProductLayoutModel;
import com.example.projectkapadh.Model.HomepageModel;
import com.example.projectkapadh.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static SwipeRefreshLayout swipeRefreshLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    //private BannerAdapter bannerAdapter;
    private int currentPage = 2;
    private Timer timer;
    final private long DELAY_TIME = 3000;
    final private long PERIOD_TIME = 3000;

    ////////////////////////////////////////////////////

    private DatabaseReference databaseReference;
    List<BannerModel> bannerModelList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        final ArrayList<String> url = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        /////////////Banner

        bannerSliderViewpager = view.findViewById(R.id.ad_banner_viewpager);
        bannerModelList = new ArrayList<>();

        BannerAdapter bannerAdapter = new BannerAdapter(bannerModelList);
        bannerSliderViewpager.setAdapter(bannerAdapter);
        bannerSliderViewpager.setClipToPadding(false);
        bannerSliderViewpager.setClipToPadding(false);
        bannerSliderViewpager.setCurrentItem(currentPage);


        databaseReference.child("HomepageFeed").child("Banner")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for(DataSnapshot data : snapshot.getChildren()){
                            bannerModelList.add(new BannerModel(data.child("BannerImage").getValue().toString()));
                            url.add(data.child("BannerImage").getValue().toString());
                            Log.d("Banner Url", data.child("BannerImage").getValue().toString());
                        }
                        bannerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

//        bannerAdapter.notifyDataSetChanged();

        bannerModelList.add(new BannerModel("https://firebasestorage.googleapis.com/v0/b/projectkapadh.appspot.com/o/banners%2Fbanner3.jpg?alt=media&token=e8e67a7f-0910-4b91-8867-21778d97dcfd"));
        bannerModelList.add(new BannerModel("https://firebasestorage.googleapis.com/v0/b/projectkapadh.appspot.com/o/banners%2Fbanner2.jpg?alt=media&token=e0b07357-9f8b-4255-9aea-ebfabf756a28"));
        bannerModelList.add(new BannerModel("https://firebasestorage.googleapis.com/v0/b/projectkapadh.appspot.com/o/banners%2Fbanner1.jpg?alt=media&token=5a0ca84a-9dd9-4817-ac83-1f519668726d"));

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
                    pageLooper();
                }
            }
        };
        bannerSliderViewpager.addOnPageChangeListener(onPageChangeListener);

        startBannerSlideShow();

        bannerSliderViewpager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pageLooper();
                stopBannerSlideShow();
                if(event.getAction() == MotionEvent.ACTION_UP){
                    startBannerSlideShow();
                }
                return false;
            }
        });
        /////////////Banner

        ////////////Grid Product Layout

        List<GridProductLayoutModel> gridProductLayoutModelList = new ArrayList<>();
        gridProductLayoutModelList.add(new GridProductLayoutModel(R.drawable.defence,"Defence"));
        gridProductLayoutModelList.add(new GridProductLayoutModel(R.drawable.education,"Education"));
        gridProductLayoutModelList.add(new GridProductLayoutModel(R.drawable.placement,"Placement"));
        gridProductLayoutModelList.add(new GridProductLayoutModel(R.drawable.travel,"Travel"));

        TextView gridLayoutTitle = view.findViewById(R.id.gridLayoutTitle);
//        Button gridLayoutViewAllBtn = view.findViewById(R.id.viewAllBtn);
        GridView gridView = view.findViewById(R.id.gridProductLayoutGridview);

        gridView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }

        });

//        gridView.setAdapter(new GridProductLayoutAdapter(gridProductLayoutModelList));

        ////////////Grid Product Layout

        /////////////////////////////////////////////////////////Homepage Recyclerview

        RecyclerView homepageRecyclerview = view.findViewById(R.id.homepageRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        homepageRecyclerview.setLayoutManager(linearLayoutManager);

        List<HomepageModel> homepageModelList = new ArrayList<>();
        homepageModelList.add(new HomepageModel(0,bannerModelList));
        homepageModelList.add(new HomepageModel(1,"Consultancy", gridProductLayoutModelList));

        HomepageAdapter adapter = new HomepageAdapter(homepageModelList);
        homepageRecyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        /////////////////////////////////////////////////////////Homepage Recyclerview



        return view;
    }


    ////////////////////////banner
    private void pageLooper(){
        if(currentPage == bannerModelList.size() - 2){
            currentPage = 2;
            bannerSliderViewpager.setCurrentItem(currentPage,false);
        }
        if(currentPage == 1){
            currentPage = bannerModelList.size() - 3;
            bannerSliderViewpager.setCurrentItem(currentPage,false);
        }
    }

    private void startBannerSlideShow(){
        Handler handler = new Handler();
        Runnable update = new Runnable() {
            @Override
            public void run() {
                if(currentPage >= bannerModelList.size()){
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
        },DELAY_TIME, PERIOD_TIME);

    }

    private void stopBannerSlideShow(){
        timer.cancel();
    }
    ////////////////////////banner
}