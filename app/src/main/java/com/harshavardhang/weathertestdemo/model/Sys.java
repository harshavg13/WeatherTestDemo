package com.harshavardhang.weathertestdemo.model;

public class Sys {

    private String country; // Country code
    private long sunrise; // Sunrise time in UNIX timestamp
    private long sunset; // Sunset time in UNIX timestamp

    // Getters and Setters
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getSunrise() {
        return sunrise;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }

    // Helper method to format sunrise time
    public String getFormattedSunrise() {
        return new java.text.SimpleDateFormat("hh:mm a")
                .format(new java.util.Date(sunrise * 1000));
    }

    // Helper method to format sunset time
    public String getFormattedSunset() {
        return new java.text.SimpleDateFormat("hh:mm a")
                .format(new java.util.Date(sunset * 1000));
    }
}
