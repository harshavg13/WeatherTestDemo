package com.harshavardhang.weathertestdemo.model;

public class WeatherResponse {

    private String name; // City name
    private Sys sys; // Sys info (country, sunrise, sunset)
    private Main main; // Main weather details (temperature, pressure, humidity)
    private Wind wind; // Wind info
    private Clouds clouds; // Cloud coverage info
    private Weather[] weather; // Weather condition array
    private int visibility; // Visibility in meters
    private int timezone; // Timezone offset in seconds from UTC

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }
}

