package com.example.hackathon;

public class InjuryReport {
    private String imageUrl;
    private String longitude;
    private String latitude;

    // Constructor
    public InjuryReport(String imageUrl, String longitude, String latitude) {
        this.imageUrl = imageUrl;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Getters
    public String getImageUrl() { return imageUrl; }
    public String getLongitude() { return longitude; }
    public String getLatitude() { return latitude; }
}
