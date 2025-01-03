package com.example.barberbookingapp.BarberManagementModule;

public class Service {
    private String serviceId;
    private String barberId;   // CamelCase
    private String serviceName;  // CamelCase
    private String duration;
    private String price;

    // Default constructor required for Firebase
    public Service() {}

    // Parameterized constructor
    public Service(String serviceId, String BarberID, String ServiceName, String Duration, String Price) {
        this.serviceId = serviceId;
        this.barberId = BarberID;
        this.serviceName = ServiceName;
        this.duration = Duration + " hours";
        this.price = "RM " + Price;
    }

    // Getters and Setters
    public String getBarberId() {
        return barberId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getDuration() {
        return duration;
    }

    public String getPrice(){
        return price;
    }

    public String getServiceId(){
        return serviceId;
    }
}

