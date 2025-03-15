package com.example.hackathon;

public class Clinic {
    private String name, phone, address;

    public Clinic(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
}
