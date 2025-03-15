package com.example.hackathon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Collections;

import okhttp3.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainChat2 extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private Uri imageUri;
    private ImageView imageView;
    private TextView txtResult;
    private static final String API_URL = "https://detect.roboflow.com";
    private static final String API_KEY = "ikZWrzr1w0LHe0TdhI7p";
    private static final String MODEL_ID = "injured-animal-detector-6zzbu/3";
    private static final String CONFIDENCE_THRESHOLD = "0.04";
    private com.example.hackathon.GeminiApiService geminiApiService;
    private static final String GEMINI_API_KEY = "AIzaSyDgsrTya7QBVnWkiZxn5564ZwmVJYMeKX8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat2);

        imageView = findViewById(R.id.imageView);
        txtResult = findViewById(R.id.txtResult);
        Button btnSelectImage = findViewById(R.id.btnSelectImage);
        Button btnDetect = findViewById(R.id.btnDetect);

        geminiApiService = com.example.hackathon.RetrofitClient.getInstance();

        btnSelectImage.setOnClickListener(v -> selectImage());
        btnDetect.setOnClickListener(v -> detectInjury());

        Button btnChatWithAI = findViewById(R.id.btnChatWithAI);
        btnChatWithAI.setOnClickListener(v -> {
            Intent intent = new Intent(MainChat2.this, MainChat.class);
            startActivity(intent);
        });

    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void detectInjury() {
        if (imageUri == null) {
            txtResult.setText("Select an image first!");
            return;
        }

        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            File file = new File(getCacheDir(), "upload.jpg");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "upload.jpg",
                            RequestBody.create(file, MediaType.parse("image/jpeg")))
                    .build();

            String apiUrl = API_URL + "/" + MODEL_ID +
                    "?api_key=" + API_KEY +
                    "&confidence=" + CONFIDENCE_THRESHOLD;

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    runOnUiThread(() -> txtResult.setText("Error: " + e.getMessage()));
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String detectionResult = response.body().string();
                        sendToChatbot(detectionResult);
                    } else {
                        runOnUiThread(() -> txtResult.setText("Failed to get response"));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            txtResult.setText("Error: " + e.getMessage());
        }
    }

    private void sendToChatbot(String detectionResult) {
        String query = detectionResult + " Give first aid help instructions for animal based on this result.";
        com.example.hackathon.GeminiRequest.Part part = new com.example.hackathon.GeminiRequest.Part(query);
        com.example.hackathon.GeminiRequest.Content content = new com.example.hackathon.GeminiRequest.Content("user", Collections.singletonList(part));
        com.example.hackathon.GeminiRequest request = new com.example.hackathon.GeminiRequest(Collections.singletonList(content));

        Call<com.example.hackathon.GeminiResponse> call = geminiApiService.generateContent(GEMINI_API_KEY, request);
        call.enqueue(new Callback<com.example.hackathon.GeminiResponse>() {
            @Override
            public void onResponse(Call<com.example.hackathon.GeminiResponse> call, Response<com.example.hackathon.GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String chatbotResponse = response.body().getCandidates().get(0).getContent().getParts().get(0).getText();
                    runOnUiThread(() -> txtResult.setText(chatbotResponse));
                } else {
                    runOnUiThread(() -> txtResult.setText("Failed to get first aid info."));
                }
            }

            @Override
            public void onFailure(Call<com.example.hackathon.GeminiResponse> call, Throwable t) {
                runOnUiThread(() -> txtResult.setText("Error: " + t.getMessage()));
            }
        });
    }
}
