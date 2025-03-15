package com.example.hackathon;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchAnimalsTask extends AsyncTask<Void, Void, List<Animal>> {
    private static final String API_URL ="http://192.168.110.1/Hackathon/fetch.php";
    ;
    private OnFetchCompleteListener listener;

    public interface OnFetchCompleteListener {
        void onFetchComplete(List<Animal> animals);
    }

    public FetchAnimalsTask(OnFetchCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    protected List<Animal> doInBackground(Void... voids) {
        List<Animal> animalList = new ArrayList<>();
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder jsonString = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();

            JSONArray jsonArray = new JSONArray(jsonString.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String imageUrl = obj.getString("image_url");
                animalList.add(new Animal(imageUrl));
            }

        } catch (Exception e) {
            Log.e("FetchAnimalsTask", "Error fetching images", e);
        }

        return animalList;
    }

    @Override
    protected void onPostExecute(List<Animal> animals) {
        if (listener != null) {
            listener.onFetchComplete(animals);
        }
    }
}

