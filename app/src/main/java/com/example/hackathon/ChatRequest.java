package com.example.hackathon;

public class ChatRequest {
    private String message;

    public ChatRequest(String message) {
        System.out.print(message);
        this.message = message + " in a concise manner in max 5 lines";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
