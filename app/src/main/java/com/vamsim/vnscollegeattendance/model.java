package com.vamsim.vnscollegeattendance;

public class model {
    String Date,Status,Time;

    model(){

    }

    public model(String date, String status, String time) {
        Date = date;
        Status = status;
        Time = time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
