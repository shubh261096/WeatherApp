package com.uniqolabel.weatherapp.service.repo;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uniqolabel.weatherapp.service.model.Error;
import com.uniqolabel.weatherapp.service.model.ErrorModel;
import com.uniqolabel.weatherapp.service.model.ForecastResponseModel;
import com.uniqolabel.weatherapp.service.rest.ApiClient;
import com.uniqolabel.weatherapp.service.rest.ApiInterface;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.uniqolabel.weatherapp.utils.AppConstants.ErrorConstants.BAD_REQUEST_ERROR;
import static com.uniqolabel.weatherapp.utils.AppConstants.ErrorConstants.SERVER_ERROR;
import static com.uniqolabel.weatherapp.utils.AppConstants.ErrorConstants.SOCKET_ERROR;
import static com.uniqolabel.weatherapp.utils.AppConstants.ErrorConstants.UNKNOWN_ERROR;


public class Repository {
    private static final String API_KEY = "8539a6c8ecf24baf83a90721192308";
    private static final int days = 6; // Making it static as asked for 6 since it is returning for the current day also
    private static ApiInterface apiService;

    private static class SingletonHelper {
        private static final Repository INSTANCE = new Repository();
    }

    public static Repository getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private Repository() {
        apiService = ApiClient.getClient().create(ApiInterface.class);
    }

    /* Forecast Request */
    public void getForecast(String city, final MutableLiveData<Events.ForecastResponseEvent> forecastResponseEventMutableLiveData) {
        apiService.getForecast(API_KEY, city, days)
                .enqueue(new Callback<ForecastResponseModel>() {
                    @Override
                    public void onResponse(@NonNull Call<ForecastResponseModel> call, @Nullable Response<ForecastResponseModel> response) {
                        if (response != null) {
                            if (response.isSuccessful()) {
                                forecastResponseEventMutableLiveData.
                                        postValue(new Events.ForecastResponseEvent(null, true, response.body()));
                            } else {
                                forecastResponseEventMutableLiveData.
                                        postValue(new Events.ForecastResponseEvent(buildErrorModel(response.code(), response.errorBody()), false, null));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ForecastResponseModel> call, @NonNull Throwable t) {
                        String errorMsg = handleFailureResponse(t);
                        ErrorModel errorModel = new ErrorModel();
                        Error error = new Error();
                        error.setMessage(errorMsg);
                        errorModel.setError(error);
                        forecastResponseEventMutableLiveData.
                                postValue(new Events.ForecastResponseEvent(errorModel, false, null));
                    }
                });
    }

    /* Handle Retrofit Response Failure */
    private String handleFailureResponse(Throwable throwable) {
        if (throwable instanceof IOException) {
            return SOCKET_ERROR;
        } else {
            return UNKNOWN_ERROR;
        }
    }

    private ErrorModel buildErrorModel(int responseCode, ResponseBody body) {
        Gson gson = new GsonBuilder().create();
        ErrorModel mError = new ErrorModel();
        Error error = new Error();
        try {
            mError = gson.fromJson(body.string(), ErrorModel.class);
        } catch (Exception e) {
            if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST && responseCode < HttpURLConnection.HTTP_INTERNAL_ERROR) {
                error.setMessage(BAD_REQUEST_ERROR);
                mError.setError(error);
            } else if (responseCode >= HttpURLConnection.HTTP_INTERNAL_ERROR && responseCode < (HttpURLConnection.HTTP_INTERNAL_ERROR + 100)) {
                error.setMessage(SERVER_ERROR);
                mError.setError(error);
            } else {
                error.setMessage(UNKNOWN_ERROR);
                mError.setError(error);
            }
        }
        return mError;
    }

}
