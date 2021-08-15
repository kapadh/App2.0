package com.example.projectkapadh.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectkapadh.Model.HistoryRecyclerviewItemModel;
import com.example.projectkapadh.R;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class HistoryRecyclerviewItemAdapter extends RecyclerView.Adapter<HistoryRecyclerviewItemAdapter.MyViewHolder> {

    Context context;
    ArrayList<HistoryRecyclerviewItemModel> historyRecyclerviewItemModelArrayList;

    public HistoryRecyclerviewItemAdapter(Context context, ArrayList<HistoryRecyclerviewItemModel> historyRecyclerviewItemModelArrayList) {
        this.context = context;
        this.historyRecyclerviewItemModelArrayList = historyRecyclerviewItemModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.history_recyclerview_item_layout,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryRecyclerviewItemAdapter.MyViewHolder holder, int position) {

        HistoryRecyclerviewItemModel list = historyRecyclerviewItemModelArrayList.get(position);
        String OrderStatus, SpNAme, SpFee, SpTiming, OrderDateTime, SpRating, SpSubCategory;
        OrderStatus = list.getOrderStatus();
        SpNAme = list.getSpName();
        SpFee = list.getSpFee();
        SpTiming = list.getSpTiming();
        OrderDateTime = list.getOrderDateTime();
        SpRating = list.getSpRating();
        SpSubCategory = list.getSpSubCategory();

        holder.setItemData(OrderStatus, SpNAme, SpFee, SpTiming, OrderDateTime, SpRating, SpSubCategory);

    }

    @Override
    public int getItemCount() {
        return historyRecyclerviewItemModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView SpName, SpFee, OrderStatus, SpTiming, OrderDateTime, SpRating, SpSubCategory;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            OrderStatus = itemView.findViewById(R.id.OrderStatus);
            SpName = itemView.findViewById(R.id.SpName);
            SpFee = itemView.findViewById(R.id.SpFee);
            SpTiming = itemView.findViewById(R.id.SpTiming);
            OrderDateTime = itemView.findViewById(R.id.OrderDateTime);
            SpRating = itemView.findViewById(R.id.SpRAting);
            SpSubCategory = itemView.findViewById(R.id.SubCAtegory);


        }

        public void setItemData(String setOrderStatus, String setSpName, String setSpFee, String setSpTiming, String setOrderDateTime, String setSpRating, String setSpSubCategory) {

            OrderStatus.setText(setOrderStatus);
            SpName.setText(setSpName);
            SpFee.setText(setSpFee);
            SpTiming.setText(setSpTiming);
            OrderDateTime.setText(setOrderDateTime);
            SpRating.setText(setSpRating);
            SpSubCategory.setText(setSpSubCategory);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Opening Service Provider Detailed Profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
