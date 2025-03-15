package com.example.hackathon;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ClinicInfoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PersonAdapter adapter;
    private ArrayList<Person> personList;
    private ConstraintLayout formContainer;
    private EditText etName, etPhoneNumber, etAddress;
    private Button btnAdd, btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_info);

        recyclerView = findViewById(R.id.recyclerView);
        formContainer = findViewById(R.id.formContainer);
        etName = findViewById(R.id.etName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etAddress = findViewById(R.id.etAddress);
        btnAdd = findViewById(R.id.btnAdd);
        btnSubmit = findViewById(R.id.btnSubmit);

        personList = new ArrayList<>();
        adapter = new PersonAdapter(personList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> formContainer.setVisibility(View.VISIBLE));

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhoneNumber.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (!name.isEmpty() && !phone.isEmpty() && !address.isEmpty()) {
                personList.add(new Person(name, phone, address));
                adapter.notifyItemInserted(personList.size() - 1);
                recyclerView.smoothScrollToPosition(personList.size() - 1);

                // Send data to the server
                new AddPersonTask().execute(name, phone, address);

                etName.setText("");
                etPhoneNumber.setText("");
                etAddress.setText("");
                formContainer.setVisibility(View.GONE);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Person person = personList.get(position);



                personList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // AsyncTask to Add a Person
    private static class AddPersonTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String phone = params[1];
            String address = params[2];
            HttpURLConnection conn = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://192.168.110.1/Hackathon/insertClinic.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);  // 5 seconds timeout
                conn.setReadTimeout(5000);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Encode and send data
                String data = "name=" + URLEncoder.encode(name, "UTF-8") +
                        "&phone=" + URLEncoder.encode(phone, "UTF-8") +
                        "&address=" + URLEncoder.encode(address, "UTF-8");

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(data.getBytes("UTF-8"));
                    os.flush();
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString(); // Return server response
                } else {
                    return "Error: Server responded with code " + responseCode;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception ignored) {}
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Log or handle result
            System.out.println("Server Response: " + result);
        }
    }
    // AsyncTask to Delete a Person

}
