package com.example.barberbookingapp.BarberManagementModule;

public class Pending {
    String name;
    String date;
    String time;
    String serviceType;
    String price;
    String contact;
    String pendingId;


    public String getPendingId() {
        return pendingId;
    }

    public void setPendingId(String pendingId) {
        this.pendingId = pendingId;
    }


    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getPrice() {
        return price;
    }

    public String getContact() {
        return contact;
    }

    public Pending(){

    }
}
