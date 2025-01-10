package com.example.barberbookingapp.BarberListViewModule;

import android.os.Parcel;
import android.os.Parcelable;

public class Barber implements Parcelable{

    private String barberId;  // Add barberId field
    private String username;
    private String location;
    private double rating;
    private String profileImage;

    private String details;


    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getRating() {
        return (float) rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    // Getter and Setter for barberId
    public String getBarberId() {
        return barberId;
    }

    public void setBarberId(String barberId) {
        this.barberId = barberId;
    }

    public Barber(String barberId, String username, String location, double rating, String profileImage) {
        this.barberId = barberId;
        this.username = username;
        this.location = location;
        this.rating = rating;
        this.profileImage = profileImage;

    }

    public Barber(String barberId, String username, String location, double rating, String profileImage, String details) {
        this.barberId = barberId;
        this.username = username;
        this.location = location;
        this.rating = rating;
        this.profileImage = profileImage;
        this.details = details;

    }

    public Barber() {

    }

    // Parcelable constructor
    protected Barber(Parcel in) {
        barberId = in.readString();
        username = in.readString();
        location = in.readString();
        rating = in.readDouble();
        profileImage = in.readString();
        details = in.readString();
    }

    public static final Creator<Barber> CREATOR = new Creator<Barber>() {
        @Override
        public Barber createFromParcel(Parcel in) {
            return new Barber(in);
        }

        @Override
        public Barber[] newArray(int size) {
            return new Barber[size];
        }
    };


    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(barberId);
        parcel.writeString(username);
        parcel.writeString(location);
        parcel.writeDouble(rating);
        parcel.writeString(profileImage);
        parcel.writeString(details);
    }
}
