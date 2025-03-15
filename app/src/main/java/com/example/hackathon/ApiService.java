package com.example.hackathon;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("get_animals.php")  // Change this to your API endpoint
    Call<List<Animal1>> getAnimals();
}
