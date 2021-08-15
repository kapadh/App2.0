package com.example.projectkapadh.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projectkapadh.Model.ConsultancyModel;
import com.example.projectkapadh.Model.GridProductLayoutModel;
import com.example.projectkapadh.R;
import com.example.projectkapadh.ShowItemRecyclerviewActivity;

import java.util.ArrayList;
import java.util.List;

public class GridProductLayoutAdapter extends BaseAdapter {

    Context context;
    ArrayList<ConsultancyModel> consultancyModelsList;

    public GridProductLayoutAdapter(Context context, ArrayList<ConsultancyModel> consultancyModelsList) {
        this.context = context;
        this.consultancyModelsList = consultancyModelsList;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView == null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_product_layout,null);
            view.setElevation(0);
            view.setBackgroundColor(Color.parseColor("#ffffff"));
            ImageView productImage = view.findViewById(R.id.productImageview);
            TextView productTitle = view.findViewById(R.id.productTitle);

            productTitle.setText(consultancyModelsList.get(position).getTitle());
            Glide.with(parent.getContext()).load(consultancyModelsList.get(position).getImageUrl()).apply(new RequestOptions().placeholder(R.drawable.imageplaceholder)).into(productImage);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    view.getContext().startActivity(new Intent(view.getContext(), ShowItemRecyclerviewActivity.class)
                            .putExtra("consultancyType", productTitle.getText().toString()));

//                    Toast.makeText(context, productTitle.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            view = convertView;
        }

        return view;
    }

}
