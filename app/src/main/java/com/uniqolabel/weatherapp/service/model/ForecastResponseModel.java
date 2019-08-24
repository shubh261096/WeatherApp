package com.uniqolabel.weatherapp.service.model;

import com.google.gson.annotations.SerializedName;

public class ForecastResponseModel {

    @SerializedName("current")
    private Current current;

    @SerializedName("alert")
    private Alert alert;

    @SerializedName("location")
    private Location location;

    @SerializedName("forecast")
    private Forecast forecast;

    public void setCurrent(Current current) {
        this.current = current;
    }

    public Current getCurrent() {
        return current;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    public Forecast getForecast() {
        return forecast;
    }

    @Override
    public String toString() {
        return
                "ForecastResponseModel{" +
                        "current = '" + current + '\'' +
                        ",alert = '" + alert + '\'' +
                        ",location = '" + location + '\'' +
                        ",forecast = '" + forecast + '\'' +
                        "}";
    }
}