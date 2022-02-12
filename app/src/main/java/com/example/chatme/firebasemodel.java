package com.example.chatme;

public class firebasemodel {

    String name;
    String Image;
    String UID;
    String Status;

    public firebasemodel(String name, String image, String UID, String status) {
        this.name = name;
        Image = image;
        this.UID = UID;
        Status = status;
    }

    public firebasemodel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
