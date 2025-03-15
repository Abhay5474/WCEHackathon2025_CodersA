package com.example.hackathon;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchReportsTask extends AsyncTask<Void, Void, String> {
    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL url = new URL("http://192.168.110.1/Hackathon/fetch_injury.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            conn.disconnect();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            try {
                JSONObject jsonResponse = new JSONObject(result);
                JSONArray reportsArray = jsonResponse.getJSONArray("data");

                for (int i = 0; i < reportsArray.length(); i++) {
                    JSONObject report = reportsArray.getJSONObject(i);
                    String imageUrl = report.getString("image_url");
                    String longitude = report.getString("longitude");
                    String latitude = report.getString("latitude");

                    Log.d("ReportData", "Image: " + imageUrl + ", Long: " + longitude + ", Lat: " + latitude);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}