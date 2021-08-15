package com.example.projectkapadh.DbQueries;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectkapadh.Model.ConsultancyModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.projectkapadh.ShowItemRecyclerviewActivity.showItemRecyclerviewAdapter;

public class ShowItemRecyclerviewQueries {

    public static ArrayList<ConsultancyModel> LoadConsultancyList(ArrayList<ConsultancyModel> list, DatabaseReference listRef){

        list.clear();
        listRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(!ds.getKey().equals("1ImageUrl")){
                        ConsultancyModel item = new ConsultancyModel(ds.getKey(), ds.child("1ImageUrl").getValue(String.class));
                        list.add(item);
                        Log.d(item.getTitle(), item.getImageUrl());
                    }

                }
                showItemRecyclerviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        return list;
    }


}
