package com.example.hackathon;

import java.lang.String;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.*;

public class Animal_Injury extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private Uri imageUri;
    private ImageView imageView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private TextView txtResult;
    private static final String API_URL = "https://detect.roboflow.com";
    private static final String API_KEY = "ikZWrzr1w0LHe0TdhI7p";
    private static final String MODEL_ID = "injured-animal-detector-6zzbu/3";
    private static final String CONFIDENCE_THRESHOLD = "0.04";
    private String latitude, longitude;
    private LinearLayout severityContainer;
    private static final String SERVER_URL = "http://192.168.110.1/Hackathon/injury.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_injury);

        imageView = findViewById(R.id.imageView);
        severityContainer = findViewById(R.id.severityContainer);
        Button btnSelectImage = findViewById(R.id.btnSelectImage);
        Button btnDetect = findViewById(R.id.btnDetect);
        Button btnSend = findViewById(R.id.btnSend);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();

        btnSelectImage.setOnClickListener(v -> selectImage());
        btnDetect.setOnClickListener(v -> detectInjury());
        btnSend.setOnClickListener(v -> {
            if (imageUri != null && latitude != null && longitude != null) {
                sendImageNameToServer(imageUri.toString(), latitude, longitude);
            } else {
                txtResult.setText("Please select an image and ensure location is enabled.");
            }
        });
    }

    private void sendImageNameToServer(String string, String latitude, String longitude) {

            if (imageUri == null) {
                Toast.makeText(this, "Select an image first!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                String encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT);

                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image_url", "data:image/jpeg;base64," + encodedImage)
                        .addFormDataPart("latitude", latitude)
                        .addFormDataPart("longitude", longitude)
                        .build();

                Request request = new Request.Builder()
                        .url(SERVER_URL)
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> txtResult.setText("Upload Failed: " + e.getMessage()));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            runOnUiThread(() -> txtResult.setText("Server Error: " + response.message()));
                            return;
                        }

                        String responseData = response.body().string();
                        runOnUiThread(() -> txtResult.setText("Response: " + responseData));
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                txtResult.setText("Error encoding image: " + e.getMessage());
            }

    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            try {
                imageUri = data.getData();
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap resizedBitmap = getResizedBitmap(bitmap, 720, 1600);
                imageView.setImageBitmap(resizedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                txtResult.setText("Error: " + e.getMessage());
            }
        }
    }

    private void detectInjury() {
        if (imageUri == null) {
            txtResult.setText("Select an image first!");
            return;
        }
        // Detection logic here
    }

    private String extractFileName(String uriString) {
        Uri uri = Uri.parse(uriString);
        String fileName = uri.getLastPathSegment();
        if (fileName != null && fileName.contains("/")) {
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        }
        return fileName;
    }

    private Bitmap getResizedBitmap(Bitmap bitmap, int reqWidth, int reqHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) reqWidth) / width;
        float scaleHeight = ((float) reqHeight) / height;
        float scaleFactor = Math.min(scaleWidth, scaleHeight);
        return Bitmap.createScaledBitmap(bitmap, Math.round(width * scaleFactor), Math.round(height * scaleFactor), true);
    }
}
