package com.example.projectkapadh.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projectkapadh.Model.ConsultancyModel;
import com.example.projectkapadh.Model.ShowItemRecyclerviewModel;
import com.example.projectkapadh.R;
import com.example.projectkapadh.ServiceProviderListActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.projectkapadh.DbQueries.ShowItemRecyclerviewQueries.LoadConsultancyList;
import static com.example.projectkapadh.ServiceProviderListActivity.consultantListRef;
import static com.example.projectkapadh.ShowItemRecyclerviewActivity.LIST_1;
import static com.example.projectkapadh.ShowItemRecyclerviewActivity.LIST_2;
import static com.example.projectkapadh.ShowItemRecyclerviewActivity.LIST_3;
import static com.example.projectkapadh.ShowItemRecyclerviewActivity.consultancyModelsList;
import static com.example.projectkapadh.ShowItemRecyclerviewActivity.listStack;
import static com.example.projectkapadh.ShowItemRecyclerviewActivity.nodeRef;
import static com.example.projectkapadh.ShowItemRecyclerviewActivity.showItemRecyclerview;
import static com.example.projectkapadh.ShowItemRecyclerviewActivity.showItemRecyclerviewAdapter;
import static com.example.projectkapadh.ShowItemRecyclerviewActivity.toolbar;


public class ShowItemRecyclerviewAdapter extends RecyclerView.Adapter<ShowItemRecyclerviewAdapter.MyViewHolder> {

    Context context;
    ArrayList<ConsultancyModel> consultancyList;

    public ShowItemRecyclerviewAdapter(Context context, ArrayList<ConsultancyModel> consultancyModelsList) {
        this.context = context;
        this.consultancyList = consultancyModelsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_item_recyclerview_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowItemRecyclerviewAdapter.MyViewHolder holder, int position) {

        ConsultancyModel showItemRecyclerviewModel = consultancyList.get(position);
        String itemImageLink = showItemRecyclerviewModel.getImageUrl();
        String title = showItemRecyclerviewModel.getTitle();
        holder.setItemData(itemImageLink, title);

    }

    @Override
    public int getItemCount() {
        return consultancyList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.consultancyRecyclerviewItemImage);
            productTitle = itemView.findViewById(R.id.consultancyRecyclerviewItemTitle);

        }


        public void setItemData(String itemImageLink, String title) {

            productTitle.setText(title);
            Glide.with(itemView.getContext()).load(itemImageLink).apply(new RequestOptions().placeholder(R.drawable.imageplaceholder)).into(productImage);

            nodeRef.child(title).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                    if (snapshot.hasChild("ConsultantList")) {

                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(v.getContext(), "Consultant List", Toast.LENGTH_SHORT).show();

                                consultantListRef = nodeRef.child(title);

                                Intent intent = new Intent(itemView.getContext(), ServiceProviderListActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("SplTitle", title);
                                itemView.getContext().startActivity(intent);

                            }
                        });

                    } else {

                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                toolbar.setTitle(title);
                                consultancyModelsList.clear();
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    if (!ds.getKey().equals("1ImageUrl")) {
                                        ConsultancyModel item = new ConsultancyModel(ds.getKey(), ds.child("1ImageUrl").getValue(String.class));
                                        consultancyModelsList.add(item);
                                        Log.d(item.getTitle(), item.getImageUrl());
                                    }
                                }

                                nodeRef = nodeRef.child(title);
                                showItemRecyclerviewAdapter.notifyDataSetChanged();

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }

    }

}
