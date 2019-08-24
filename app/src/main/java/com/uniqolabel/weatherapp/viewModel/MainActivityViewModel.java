package com.uniqolabel.weatherapp.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.uniqolabel.weatherapp.service.repo.Events;
import com.uniqolabel.weatherapp.service.repo.Repository;


public class MainActivityViewModel extends AndroidViewModel {

    private MutableLiveData<Events.ForecastResponseEvent> forecastResponseEventMutableLiveData;
    private Repository repository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        forecastResponseEventMutableLiveData = new MutableLiveData<>();
        repository = Repository.getInstance();
    }

    public void sendRequest(String city) {
        repository.getForecast(city, forecastResponseEventMutableLiveData);
    }


    public LiveData<Events.ForecastResponseEvent> getForecast() {
        return forecastResponseEventMutableLiveData;
    }

}
