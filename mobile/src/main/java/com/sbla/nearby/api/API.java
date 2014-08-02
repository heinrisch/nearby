package com.sbla.nearby.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

public class API {

    private static RestAdapter restAdapter;
    private static APIService apiService;

    public static APIService getAPIService() {
        if (apiService == null) {
            apiService = getRestAdapter().create(APIService.class);
        }
        return apiService;
    }

    private static RestAdapter getRestAdapter() {
        if (restAdapter == null) {
            restAdapter = new RestAdapter.Builder()
                    .setEndpoint(APIConf.apiEndpoint2())
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            request.addHeader("X-STHLMTraveling-API-Key", APIConf.get(APIConf.KEY));
                        }
                    })
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setConverter(new JacksonConverter(new ObjectMapper()))
                    .build();
        }
        return restAdapter;
    }
}
