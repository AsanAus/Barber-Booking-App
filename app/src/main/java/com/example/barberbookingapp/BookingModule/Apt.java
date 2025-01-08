package com.example.barberbookingapp.BookingModule;

public class Apt {
    private String barberID;
    private String barberLocation;
    private String serviceName;
    private String servicePrice;
    private String date;
    private String time;
    private String status; // New field for status
    private String userID; // New field for user ID


    // Default constructor for Firebase
    public Apt() {
    }

    public Apt(String barberID, String barberLocation, String serviceName, String servicePrice, String date, String time, String status, String userID) {
        this.barberID = barberID;
        this.barberLocation = barberLocation;
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
        this.date = date;
        this.time = time;
        this.status = status; // Initialize status
        this.userID = userID; // Initialize userID
    }

    // Getters and Setters
    public String getBarberID() {
        return barberID;
    }

    public void setBarberID(String barberID) {
        this.barberID = barberID;
    }

    public String getBarberLocation() {
        return barberLocation;
    }

    public void setBarberLocation(String barberLocation) {
        this.barberLocation = barberLocation;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServicePrice() {
        return servicePrice;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setServicePrice(String servicePrice) {
        this.servicePrice = servicePrice;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() { // Getter for status
        return status;
    }

    public void setStatus(String status) { // Setter for status
        this.status = status;
    }
}
