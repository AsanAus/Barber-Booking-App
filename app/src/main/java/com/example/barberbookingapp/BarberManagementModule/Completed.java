package com.example.barberbookingapp.BarberManagementModule;

public class Completed {
    String name;
    String date;
    String time;
    String serviceType;
    String price;
    String contact;
    String completedId;

    public String getCompletedId() {
        return completedId;
    }

    public void setCompletedId(String completedId) {
        this.completedId = completedId;
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

    public Completed(){

    }
}
