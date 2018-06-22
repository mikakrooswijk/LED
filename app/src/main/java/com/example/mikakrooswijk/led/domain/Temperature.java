package com.example.mikakrooswijk.led.domain;

/**
 * Created by Mika Krooswijk on 22-6-2018.
 */

public class Temperature {
    private Double temperature;
    private String date;
    private String time;

    public Temperature(Double temperature, String date, String time){
        this.temperature = temperature;
        this.date = date;
        this.time = time;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
