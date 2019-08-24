package com.uniqolabel.weatherapp.service.model;

import com.google.gson.annotations.SerializedName;

public class ErrorModel {

    @SerializedName("error")
    private Error error;

    public void setError(Error error) {
        this.error = error;
    }

    public Error getError() {
        return error;
    }

    @Override
    public String toString() {
        return
                "ErrorModel{" +
                        "error = '" + error + '\'' +
                        "}";
    }
}