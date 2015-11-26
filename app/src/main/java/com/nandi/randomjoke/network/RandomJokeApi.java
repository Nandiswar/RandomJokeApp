package com.nandi.randomjoke.network;

import com.nandi.randomjoke.model.JokeModel;

import retrofit.http.GET;
import retrofit.http.Headers;

/**
 * Created by nandi_000 on 25-11-2015.
 * Interface containing all the API's associated with the app
 */
public interface RandomJokeApi {

    @GET("/random")
    JokeModel getRandomJoke();
}
