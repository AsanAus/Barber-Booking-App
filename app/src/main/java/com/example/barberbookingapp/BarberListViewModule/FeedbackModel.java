package com.example.barberbookingapp.BarberListViewModule;

public class FeedbackModel {
    private String barberID;
    private String userID;
    private String comment;
    public FeedbackModel(){
    }

    public FeedbackModel(String barberID, String userID, String comment) {
        this.barberID = barberID;
        this.userID = userID;
        this.comment = comment;
    }

    // Getters and Setters
    public String getBarberID() {
        return barberID;
    }

    public void setBarberID(String barberID) {
        this.barberID = barberID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
