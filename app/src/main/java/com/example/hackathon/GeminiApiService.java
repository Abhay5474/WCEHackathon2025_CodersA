package com.example.hackathon;

import java.lang.reflect.Parameter;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Query;
import retrofit2.http.POST;

public interface GeminiApiService {
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    Call<GeminiResponse> generateContent(
            @Query("key") String apiKey, // Pass the API key as a query parameter
            @Body GeminiRequest request // Request body
    );
}