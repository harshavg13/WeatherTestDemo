package com.harshavardhang.weathertestdemo.model;

public class Weather {

    private String main; // Weather main condition (e.g., Clear)
    private String description; // Weather description (e.g., clear sky)
    private String icon; // Weather icon code (e.g., 01d)

    // Getters and Setters
    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
