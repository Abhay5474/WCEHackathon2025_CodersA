package com.example.hackathon;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hackathon.R;

import java.util.List;

public class Adoption_Fetch extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AnimalAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_fetch);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch images from the server
        new FetchAnimalsTask(animals -> {
            if (animals.isEmpty()) {
                Toast.makeText(Adoption_Fetch.this, "No images found!", Toast.LENGTH_SHORT).show();
            } else {
                adapter = new AnimalAdapter(Adoption_Fetch.this, animals);
                recyclerView.setAdapter(adapter);
                Toast.makeText(Adoption_Fetch.this, "images found!", Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }
}
