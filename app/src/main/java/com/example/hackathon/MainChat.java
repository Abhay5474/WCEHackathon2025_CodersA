package com.example.hackathon;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainChat extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private Button sendButton;
    private com.example.hackathon.ChatAdapter chatAdapter;
    private List<com.example.hackathon.ChatMessage> chatMessages;
    private com.example.hackathon.GeminiApiService geminiApiService;
    private static final String API_KEY = "AIzaSyDgsrTya7QBVnWkiZxn5564ZwmVJYMeKX8"; // Replace with your Gemini API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        chatMessages = new ArrayList<>();
        chatAdapter = new com.example.hackathon.ChatAdapter(chatMessages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        geminiApiService = com.example.hackathon.RetrofitClient.getInstance();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = messageEditText.getText().toString().trim();
                if (!userMessage.isEmpty()) {
                    // Add user message to the chat
                    chatMessages.add(new com.example.hackathon.ChatMessage(userMessage, true));
                    chatAdapter.notifyDataSetChanged();
                    messageEditText.setText("");

                    // Send message to Gemini API
                    sendMessageToGemini(userMessage);
                }
            }
        });
    }



    private void sendMessageToGemini(String message) {
        // Create the request body
        com.example.hackathon.GeminiRequest.Part part = new com.example.hackathon.GeminiRequest.Part(message);
        com.example.hackathon.GeminiRequest.Content content = new com.example.hackathon.GeminiRequest.Content("user", Collections.singletonList(part));
        com.example.hackathon.GeminiRequest request = new com.example.hackathon.GeminiRequest(Collections.singletonList(content));

        // Make the Retrofit call with the API key as a query parameter
        Call<com.example.hackathon.GeminiResponse> call = geminiApiService.generateContent(API_KEY, request);

        call.enqueue(new Callback<com.example.hackathon.GeminiResponse>() {
            @Override
            public void onResponse(Call<com.example.hackathon.GeminiResponse> call, Response<com.example.hackathon.GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Extract the chatbot response
                    String chatbotResponse = response.body().getCandidates().get(0).getContent().getParts().get(0).getText();

                    // Add chatbot response to the chat
                    chatMessages.add(new com.example.hackathon.ChatMessage(chatbotResponse, false));
                    chatAdapter.notifyDataSetChanged();
                    chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                } else {
                    // Handle API error
                    chatMessages.add(new com.example.hackathon.ChatMessage("Failed to get response from chatbot.", false));
                    chatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<com.example.hackathon.GeminiResponse> call, Throwable t) {
                // Handle network failure
                chatMessages.add(new com.example.hackathon.ChatMessage("Network error. Please try again.", false));
                chatAdapter.notifyDataSetChanged();
            }
        });
    }
}