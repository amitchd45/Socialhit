package com.socialhit.app.modelsClass;

public class User {
    String name;
    String email;
    String phone;
    String onlineStatus;
    String image;
    String uid;
    String cover;
    String typingTo;

    public User(String name, String email, String phone, String onlineStatus, String image, String uid, String cover, String typingTo) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.onlineStatus = onlineStatus;
        this.image = image;
        this.uid = uid;
        this.cover = cover;
        this.typingTo = typingTo;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public User() {
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
