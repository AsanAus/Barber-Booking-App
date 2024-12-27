package com.example.barberbookingapp.UserManagementModule;

public class userProfileDataClass {

    private String userName;
    private String userEmail;
    private String userPhoneNumber;
    private String userPassword;
    private String dataImage;

    public userProfileDataClass(String userPassword, String userName, String userEmail, String userPhoneNumber, String dataImage) {
        this.userPassword = userPassword;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.dataImage = dataImage;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getDataImage(){
        return dataImage;
    }
}
