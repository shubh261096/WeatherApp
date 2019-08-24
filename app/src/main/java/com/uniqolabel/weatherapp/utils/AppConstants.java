package com.uniqolabel.weatherapp.utils;

public class AppConstants {

    public final static int ACCESS_LOCATION_CODE = 101;
    public final static int GPS_REQUEST_CODE = 901;
    public final static int LOCATION_REQUEST_CODE = 900;


    /* ERROR CONSTANTS USED IN REPOSITORY */
    public interface ErrorConstants {
        String SERVER_ERROR = "Unable to process your request. \nPlease try again later.";
        String BAD_REQUEST_ERROR = "Unable to process your request. \nPlease try again later.";
        String UNKNOWN_ERROR = "Unable to process your request. \nPlease try again later.";
        String SOCKET_ERROR = "Unable to connect with the server.\nPlease try again later.";
    }
}
