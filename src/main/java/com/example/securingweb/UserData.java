package com.example.securingweb;

import java.util.ArrayList;

public class UserData {
    private String username;
    private String password;
    private ArrayList<String> roles;

    // Constructor
    public UserData() {
        roles = new ArrayList<>();
    }

    // Getters y Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }
}
