package com.example.chatme;

public class UserProfileDetails {

    public String userName, userUID;

    public UserProfileDetails() {
    }

    public UserProfileDetails(String userName, String userUID) {
        this.userName = userName;
        this.userUID = userUID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }
}
