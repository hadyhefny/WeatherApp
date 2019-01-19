package com.example.hodhod.weatherapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;
import java.util.zip.Inflater;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Info> mInfoList;

    public MyAdapter() {
    }

    public MyAdapter(List<Info> infoList) {
        mInfoList = infoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item,viewGroup,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Info info = mInfoList.get(i);
        viewHolder.timeForecast.setText(info.getLocalTime());
        viewHolder.degreeForecast.setText(info.getDegree());
        viewHolder.conditionCurrent.setText(info.getCondition());
    }

    @Override
    public int getItemCount() {
        return mInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView timeForecast;
        public TextView conditionCurrent;
        public TextView degreeForecast;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            timeForecast = itemView.findViewById(R.id.time_forecast);
            conditionCurrent = itemView.findViewById(R.id.condition_current);
            degreeForecast = itemView.findViewById(R.id.degree_forecast);

        }
    }
}
