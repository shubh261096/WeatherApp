package com.uniqolabel.weatherapp.service.repo;

import com.uniqolabel.weatherapp.service.model.ErrorModel;
import com.uniqolabel.weatherapp.service.model.ForecastResponseModel;

public class Events {

    private Events() {
    }

    public static class BaseEvent {
        private ErrorModel errorModel;
        private boolean success;

        BaseEvent(ErrorModel errorModel, boolean success) {
            this.errorModel = errorModel;
            this.success = success;
        }

        public ErrorModel getErrorModel() {
            return errorModel;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public static class ForecastResponseEvent extends BaseEvent {
        private ForecastResponseModel forecastResponseModel;

        ForecastResponseEvent(ErrorModel errorModel, boolean success, ForecastResponseModel forecastResponseModel) {
            super(errorModel, success);
            this.forecastResponseModel = forecastResponseModel;
        }

        public ForecastResponseModel getForecastResponseModel() {
            return forecastResponseModel;
        }
    }


}
