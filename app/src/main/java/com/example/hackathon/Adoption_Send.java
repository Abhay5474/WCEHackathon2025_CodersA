package com.example.hackathon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Adoption_Send extends AppCompatActivity {

    private Button btnUpload, btnSelectImage;
    private ImageView ivAnimalImage;
    private Uri imageUri;
    private static final int IMAGE_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_send);

        btnUpload = findViewById(R.id.btnUpload);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        ivAnimalImage = findViewById(R.id.ivAnimalImage);

        btnSelectImage.setOnClickListener(v -> selectImage());
        btnUpload.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadAnimal(imageUri);
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            ivAnimalImage.setImageURI(imageUri);
        }
    }

    private void uploadAnimal(Uri imageUri) {
        String uploadUrl = "http://192.168.110.1/Hackathon/image.php";

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            String image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            StringRequest request = new StringRequest(Request.Method.POST, uploadUrl,
                    response -> Toast.makeText(this, "Animal Uploaded", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("image",image);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}