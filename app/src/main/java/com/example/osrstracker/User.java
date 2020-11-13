package com.example.osrstracker;

public class User {
    private int userID;
    private String userName;

    public User(int userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }

    @Override
    public String toString() {
        return userName;
    }

    public int getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

}
