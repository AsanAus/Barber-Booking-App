package com.example.barberbookingapp.BarberManagementModule;

public class Upcoming {

    String name;
    String date;
    String time;
    String serviceType;
    String price;
    String contact;
    String UpcomingId;


    public String getUpcomingId() {
        return UpcomingId;
    }

    public void setUpcomingId(String upcomingId) {
        UpcomingId = upcomingId;
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

    public Upcoming (){

    }
}
