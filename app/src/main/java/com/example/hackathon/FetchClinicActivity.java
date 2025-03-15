package com.example.hackathon;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchClinicActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ClinicAdapter adapter;
    private List<Clinic> clinicList;
    private static final String FETCH_URL = "http://192.168.110.1/Hackathon/fetchClinic.php"; // Change to your API URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_clinic);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        clinicList = new ArrayList<>();
        adapter = new ClinicAdapter(clinicList);
        recyclerView.setAdapter(adapter);


         new FetchClinicsTask().execute();
    }

    private class FetchClinicsTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(FETCH_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                conn.disconnect();
                return result.toString();
            } catch (Exception e) {
                Log.e("FetchError", "Error fetching data", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray clinicsArray = jsonResponse.getJSONArray("clinics");
                        clinicList.clear();
                        for (int i = 0; i < clinicsArray.length(); i++) {
                            JSONObject clinicObj = clinicsArray.getJSONObject(i);
                            String name = clinicObj.getString("name");
                            String phone = clinicObj.getString("phone");
                            String address = clinicObj.getString("address");
                            clinicList.add(new Clinic(name, phone, address));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), "No clinics found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("ParseError", "Error parsing JSON", e);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
