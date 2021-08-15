package com.example.projectkapadh.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projectkapadh.Model.ServiceProviderListItemModel;
import com.example.projectkapadh.R;
import com.example.projectkapadh.RecordingActivity;
import com.example.projectkapadh.ServiceProviderListActivity;
import com.example.projectkapadh.ServiceProviderProfileActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ServiceProviderListItemAdapter extends RecyclerView.Adapter<ServiceProviderListItemAdapter.MyViewHolder> {

    Context context;
    ArrayList<ServiceProviderListItemModel> list;

    public ServiceProviderListItemAdapter(Context context, ArrayList<ServiceProviderListItemModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.service_provider_list_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        ServiceProviderListItemModel item = list.get(position);
        String SplImage, SplName, SplRatings, SplFee, Description, SpUID;
        SplImage = item.getSpImage();
        SplName = item.getSpName();
        SplRatings = item.getSpRatings();
        SplFee = item.getSpFee();
        Description = item.getDescription();
        SpUID = item.getSpUID();

        holder.setItemData(SplImage, SplName, SplRatings,SplFee, Description, SpUID);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView SplImage;
        TextView SplName, SplRatings, SplFee, SeeProfileBtn, bookNowBtn, SpDescription;
        String SpUID;


        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            SplImage = itemView.findViewById(R.id.profileImage);
            SplName = itemView.findViewById(R.id.consName);
            SplRatings = itemView.findViewById(R.id.consRatings);
            SplFee = itemView.findViewById(R.id.consFee);
            SpDescription = itemView.findViewById(R.id.consDescription);

            SeeProfileBtn = itemView.findViewById(R.id.seeProfileBtn);
            bookNowBtn = itemView.findViewById(R.id.bookNowBtn);

            SeeProfileBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), ServiceProviderProfileActivity.class)
                            .putExtra("SpID", "serviceProviderId"));
                }
            });

            bookNowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), RecordingActivity.class)
                            .putExtra("SpName", SplName.getText().toString())
                            .putExtra("SpUID", SpUID));
                }
            });

        }

        public void setItemData(String setSplImage, String setSplName, String setSplRatings, String setSplFee, String Description, String uid){
            Glide.with(itemView.getContext()).load(setSplImage).apply(new RequestOptions().placeholder(R.drawable.ic_baseline_person_24)).into(SplImage);
            SplName.setText(setSplName);
            SplRatings.setText(setSplRatings);
            SplFee.setText(setSplFee);
            SpDescription.setText(Description);
            SpUID = uid;
        }
    }

}
