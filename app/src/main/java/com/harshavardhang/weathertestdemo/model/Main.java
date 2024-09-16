package com.harshavardhang.weathertestdemo.model;

public class Main {

    private double temp; // Current temperature in Kelvin
    private double feels_like; // Feels like temperature in Kelvin
    private double temp_min; // Minimum temperature in Kelvin
    private double temp_max; // Maximum temperature in Kelvin
    private int pressure; // Pressure in hPa
    private int humidity; // Humidity percentage

    // Getters and Setters
    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getFeels_like() {
        return feels_like;
    }

    public void setFeels_like(double feels_like) {
        this.feels_like = feels_like;
    }

    public double getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(double temp_min) {
        this.temp_min = temp_min;
    }

    public double getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(double temp_max) {
        this.temp_max = temp_max;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    // Helper methods for temperature conversion
    public double getTempInCelsius() {
        return temp - 273.15;
    }

    public double getFeelsLikeInCelsius() {
        return feels_like - 273.15;
    }

    public double getTempMinInCelsius() {
        return temp_min - 273.15;
    }

    public double getTempMaxInCelsius() {
        return temp_max - 273.15;
    }
}
