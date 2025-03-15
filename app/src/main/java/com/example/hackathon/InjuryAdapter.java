package com.example.hackathon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class InjuryAdapter extends RecyclerView.Adapter<InjuryAdapter.InjuryViewHolder> {
    private List<InjuryReport> injuryList = new ArrayList<>();

    public void setInjuryList(List<InjuryReport> newList) {
        injuryList.clear();
        injuryList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InjuryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_injury, parent, false);
        return new InjuryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InjuryViewHolder holder, int position) {
        InjuryReport report = injuryList.get(position);

        holder.tvLongitude.setText("Longitude: " + (report.getLongitude()));
        holder.tvLatitude.setText("Latitude: " + (report.getLatitude()));

        Glide.with(holder.itemView.getContext())
                .load(report.getImageUrl())
//                .placeholder(R.drawable.placeholder_image)
//                .error(R.drawable.error_image)
                .into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return injuryList.size();
    }

    static class InjuryViewHolder extends RecyclerView.ViewHolder {
        TextView tvLongitude, tvLatitude;
        ImageView ivImage;

        InjuryViewHolder(View itemView) {
            super(itemView);
            tvLongitude = itemView.findViewById(R.id.tvLongitude);
            tvLatitude = itemView.findViewById(R.id.tvLatitude);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }
}
