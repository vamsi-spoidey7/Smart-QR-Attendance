package com.vamsim.vnscollegeattendance;

public class UserHelperClass {
    String username,rollnumber,phonenumber,email,password,uid;

    public UserHelperClass() {
    }

    public UserHelperClass(String username, String rollnumber, String phonenumber, String email, String password, String uid) {
        this.username = username;
        this.rollnumber = rollnumber;
        this.phonenumber = phonenumber;
        this.email = email;
        this.password = password;
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRollnumber() {
        return rollnumber;
    }

    public void setRollnumber(String rollnumber) {
        this.rollnumber = rollnumber;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
