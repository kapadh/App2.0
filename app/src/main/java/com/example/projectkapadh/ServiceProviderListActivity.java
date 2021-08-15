package com.example.projectkapadh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import com.example.projectkapadh.Adapter.ServiceProviderListItemAdapter;
import com.example.projectkapadh.Adapter.ShowItemRecyclerviewAdapter;
import com.example.projectkapadh.Model.ServiceProviderListItemModel;
import com.example.projectkapadh.Model.ShowItemRecyclerviewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.projectkapadh.ShowItemRecyclerviewActivity.nodeRef;


public class ServiceProviderListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String title;

    private RecyclerView recyclerView;
    public static DatabaseReference consultantListRef;
    private ServiceProviderListItemAdapter adapter;
    private ArrayList<ServiceProviderListItemModel> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_list);

        if (getIntent() != null) {
            title = getIntent().getStringExtra("SplTitle");
        }

        toolbar = findViewById(R.id.splToolbar);
        toolbar.setTitle(title);
        setActionBar(toolbar);
        View view = toolbar.getChildAt(1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform actions
                finish();
            }
        });

        recyclerView = findViewById(R.id.serviceProviderRecyclerview);
        recyclerView.setHasFixedSize(true);
        consultantListRef = consultantListRef.child("ConsultantList");

        Log.d("ConsultantListRef", consultantListRef.toString());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new ServiceProviderListItemAdapter(this, list);
        recyclerView.setAdapter(adapter);

        consultantListRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ServiceProviderListItemModel item = dataSnapshot.getValue(ServiceProviderListItemModel.class);
                    if (item.isVisibility()) {
                        list.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}