package com.nandi.randomjoke.service;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

/**
 * Created by nandi_000 on 25-11-2015.
 * Service using the Robospice-Retrofit library to make an api call
 */
public class RandomJokeService extends RetrofitGsonSpiceService {

    private final String BASE_URL = "http://api.icndb.com/jokes";

    @Override
    protected String getServerUrl() {
        return BASE_URL;
    }
}
