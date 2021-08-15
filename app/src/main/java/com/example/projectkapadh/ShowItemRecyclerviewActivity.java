package com.example.projectkapadh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toolbar;

import com.example.projectkapadh.Adapter.ShowItemRecyclerviewAdapter;
import com.example.projectkapadh.Model.ConsultancyModel;
import com.example.projectkapadh.Model.ShowItemRecyclerviewModel;
import com.example.projectkapadh.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Stack;

import static com.example.projectkapadh.DbQueries.ShowItemRecyclerviewQueries.LoadConsultancyList;

public class ShowItemRecyclerviewActivity extends AppCompatActivity {

    public static Toolbar toolbar;
    private String consultancyType;

    public static Stack<ArrayList<ConsultancyModel>> listStack;

    public static RecyclerView showItemRecyclerview;
    private DatabaseReference databaseReference;
    public static ShowItemRecyclerviewAdapter showItemRecyclerviewAdapter;
    public static ArrayList<ConsultancyModel> consultancyModelsList;

    public static DatabaseReference nodeRef = null;

    public static Context context;

    public static boolean LIST_1 = false, LIST_2 = false, LIST_3 = false;

    private boolean ONCE = true;

    @SuppressLint({"UseSupportActionBar", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_item_recyclerview);

        listStack = new Stack<>();

        context = getApplicationContext();

        consultancyType = getIntent().getStringExtra("consultancyType");

        consultancyModelsList = new ArrayList<>();

        toolbar = findViewById(R.id.ShowProductToolbar);
        toolbar.setTitle(consultancyType);
        setActionBar(toolbar);
        View view = toolbar.getChildAt(0);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform actions
                nodeRef = null;
                finish();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
        nodeRef = databaseReference.child("Consultancies").child(consultancyType);

        showItemRecyclerview = findViewById(R.id.showItemRecyclerview);
        showItemRecyclerview.setHasFixedSize(true);
        showItemRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        showItemRecyclerviewAdapter = new ShowItemRecyclerviewAdapter(getApplicationContext(), consultancyModelsList);
        showItemRecyclerview.setAdapter(showItemRecyclerviewAdapter);

        if(ONCE){
            ONCE = false;
            nodeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                    consultancyModelsList.clear();
                    for(DataSnapshot ds : snapshot.getChildren()){
                        if(!ds.getKey().equals("1ImageUrl")){
                            ConsultancyModel item = new ConsultancyModel(ds.getKey(), ds.child("1ImageUrl").getValue(String.class));
                            consultancyModelsList.add(item);
                            Log.d(item.getTitle(), item.getImageUrl());
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

            showItemRecyclerviewAdapter.notifyDataSetChanged();

        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){
            nodeRef = null;
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nodeRef = null;
    }
}