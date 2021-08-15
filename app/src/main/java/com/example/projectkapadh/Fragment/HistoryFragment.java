package com.example.projectkapadh.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.projectkapadh.Adapter.HistoryRecyclerviewItemAdapter;
import com.example.projectkapadh.Adapter.ShowItemRecyclerviewAdapter;
import com.example.projectkapadh.Model.HistoryRecyclerviewItemModel;
import com.example.projectkapadh.Model.ShowItemRecyclerviewModel;
import com.example.projectkapadh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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


    //////////////////////////////////////////////////////////////////////////////////////////////

    private FirebaseAuth mAuth;
    private RecyclerView historyOrderRecyclerview;
    private DatabaseReference databaseReference;
    private HistoryRecyclerviewItemAdapter historyRecyclerviewItemAdapter;
    private ArrayList<HistoryRecyclerviewItemModel> historyRecyclerviewItemModelArrayList;
    private TextView historyNoticeTv;

    //////////////////////////////////////////////////////////////////////////////////////////////



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        mAuth = FirebaseAuth.getInstance();
        String UID = mAuth.getCurrentUser().getUid();

        historyNoticeTv = view.findViewById(R.id.historyNoticeTV);

        historyOrderRecyclerview = view.findViewById(R.id.historyRecyclerview);
        historyOrderRecyclerview.setHasFixedSize(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("UserOrderHistory");

        historyOrderRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        historyRecyclerviewItemModelArrayList = new ArrayList<>();
        historyRecyclerviewItemAdapter  = new HistoryRecyclerviewItemAdapter(getContext(), historyRecyclerviewItemModelArrayList);
        historyOrderRecyclerview.setAdapter(historyRecyclerviewItemAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HistoryRecyclerviewItemModel item = dataSnapshot.getValue(HistoryRecyclerviewItemModel.class);
                    historyRecyclerviewItemModelArrayList.add(item);
                }
                if(historyRecyclerviewItemModelArrayList.isEmpty()){
                    historyNoticeTv.setVisibility(View.VISIBLE);
                }
                historyRecyclerviewItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;

    }
}