package com.uniqolabel.weatherapp.service.rest;

import com.uniqolabel.weatherapp.service.model.ForecastResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiInterface {
    @GET("forecast.json")
    Call<ForecastResponseModel> getForecast(@Query("key") String apiKey, @Query("q") String city, @Query("days") int days);
}