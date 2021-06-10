package com.example.project_messenger.model;

public class Users {
    String profilePicture;
    String userName;
    String mail;
    String password;
    String userId;
    String birthDay;
    String city;



    String phone;

    public Users() {
    }

    public Users(String userName, String mail, String password, String userId) {
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.userId = userId;
    }

    public Users(String profilePicture, String userName, String mail, String password, String userId,
                 String birthDay, String city, String phone) {
        this.profilePicture = profilePicture;
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.userId = userId;
        this.birthDay = birthDay;
        this.city = city;
        this.phone = phone;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
