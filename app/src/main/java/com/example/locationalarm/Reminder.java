package com.example.locationalarm;

public class Reminder {
    public String id;
    public String title;
    public String date;
    public String time;
    public double latitude;
    public double longitude;
    public Reminder() {}
    public Reminder(String id, String title, String date, String time, double latitude, double longitude) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}