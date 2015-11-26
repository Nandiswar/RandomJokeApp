package com.nandi.randomjoke.network;

import com.nandi.randomjoke.model.JokeModel;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by nandi_000 on 25-11-2015.
 * API request which fetches the random joke
 */
public class RandomJokeApiRequest extends RetrofitSpiceRequest<JokeModel,RandomJokeApi> {

    public RandomJokeApiRequest(){
        super(JokeModel.class, RandomJokeApi.class);
    }

    @Override
    public JokeModel loadDataFromNetwork() throws Exception {
        return getService().getRandomJoke();
    }
}
