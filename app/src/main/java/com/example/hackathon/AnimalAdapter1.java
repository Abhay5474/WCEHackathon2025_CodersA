package com.example.hackathon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class AnimalAdapter1 extends RecyclerView.Adapter<AnimalAdapter1.AnimalViewHolder> {
    private Context context;
    private List<Animal1> animalList;

    public AnimalAdapter1(Context context, List<Animal1> animalList) {
        this.context = context;
        this.animalList = animalList;
    }

    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_injury, parent, false);
        return new AnimalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, int position) {
        Animal1 animal = animalList.get(position);
        holder.textViewLocation.setText("Lat: " + animal.getLatitude() + ", Lng: " + animal.getLongitude());

        // Load image using Glide
        Glide.with(context)
                .load(animal.getImageUrl())
                .into(holder.imageViewAnimal);
    }

    @Override
    public int getItemCount() {
        return animalList.size();
    }

    public static class AnimalViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewAnimal;
        TextView textViewLocation;

        public AnimalViewHolder(@NonNull View itemView) {
            super(itemView);
            //imageViewAnimal = itemView.findViewById(R.id.imageViewAnimal);
            //textViewLocation = itemView.findViewById(R.id.textViewLocation);
        }
    }
}
