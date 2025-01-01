package com.example.barberbookingapp.BookingModule;

public class Apt {
    private String barberName;
    private String barberLocation;
    private String serviceName;
    private String servicePrice;
    private String date;
    private String time;

    // Default constructor for Firebase
    public Apt() {
    }

    public Apt(String barberName, String barberLocation, String serviceName, String servicePrice, String date, String time) {
        this.barberName = barberName;
        this.barberLocation = barberLocation;
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
        this.date = date;
        this.time = time;
    }

    // Getters and Setters
    public String getBarberName() {
        return barberName;
    }

    public void setBarberName(String barberName) {
        this.barberName = barberName;
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
}
