package com.example.barberbookingapp.BarberManagementModule;

public class Upcoming {

    String name;
    String date;
    String time;
    String serviceType;
    String price;
    String contact;
    String upcomingId;

    public Upcoming() {
        // Default constructor required for calls to DataSnapshot.getValue(Upcoming.class)
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpcomingId() {
        return upcomingId;
    }

    public void setUpcomingId(String upcomingId) {
        this.upcomingId = upcomingId;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
