package com.example.barberbookingapp.GeneralModule;

public class HelperClass {
    String email, username, password, role;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role;}

    public HelperClass(String email, String username, String password, String role) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public HelperClass() {

    }
}
