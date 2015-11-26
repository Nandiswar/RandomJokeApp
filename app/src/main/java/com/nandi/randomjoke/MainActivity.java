package com.nandi.randomjoke;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

import com.nandi.randomjoke.model.JokeDataModel;
import com.nandi.randomjoke.model.JokeModel;
import com.nandi.randomjoke.network.RandomJokeApi;
import com.nandi.randomjoke.network.RandomJokeApiRequest;
import com.nandi.randomjoke.service.RandomJokeService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    // refresh time in milli seconds related to joke api call
    private static final long UPDATE_TIME = 15000;

    // file name of the shared preference xml to be created
    private static final String PREFS_NAME = "MyPrefsFile";

    // Service using which api calls are made
    private SpiceManager spiceManager = new SpiceManager(RandomJokeService.class);

    // The api request with the information about random joke api call
    private RandomJokeApiRequest request;

    // The text view that is displayed & updated with joke fetched
    private TextView jokeTextView;

    // Handler to make api call every 15 seconds
    private Handler jokesHandler;

    // stores the last api call timestamp in milli seconds
    private long apiCallTimeStamp;

    // shared preference to retain last joke settings - lastJokeText and prevTimeStamp
    private SharedPreferences jokeSettings;

    /*
    * First method called upon creating activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews(); // initialize the layout views
        initJokeRequest(); // create api request
    }

    /*
    Initializing the views associated with layout
     */
    private void bindViews() {
        jokeTextView = (TextView) findViewById(R.id.jokeText);
    }

    private void initJokeRequest() {
        request = new RandomJokeApiRequest();
        jokesHandler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        readJokeSettings();
    }

    /*
    Check settings for making a new joke api call and updating joke text view
     */
    private void readJokeSettings() {
        jokeSettings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); // get the preference file
        String prevJoke = jokeSettings.getString("lastJokeText", null); // fetch value linked to lastJokeText key

        // check for previous joke in preferences and update jokeTextView accordingly
        if (prevJoke != null) {
            jokeTextView.setText(prevJoke);
        }

        long prevTimeStamp = jokeSettings.getLong("prevTimeStamp", 0); // fetch value linked to prevTimeStamp key
        apiCallTimeStamp = prevTimeStamp;
        long elapsedTime = System.currentTimeMillis() - prevTimeStamp; // calculate elapsed time since last api call

        // make an immediate api request upon first time opening app notified by value 0 or
        // make api call if elapsed time is more than 15 seconds
        if (prevTimeStamp == 0 || elapsedTime > UPDATE_TIME) {
            jokesHandler.post(jokesRunnable);
        } else {
            jokesHandler.postDelayed(jokesRunnable, UPDATE_TIME - elapsedTime);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        jokesHandler.removeCallbacks(jokesRunnable); // remove callbacks when activity is paused
        storeJokeSettings(); // store the joke values into shared preference
    }

    private void storeJokeSettings() {
        SharedPreferences.Editor editor = jokeSettings.edit();
        editor.putString("lastJokeText", jokeTextView.getText().toString());
        editor.putLong("prevTimeStamp", apiCallTimeStamp);
        // Commit the edits
        editor.commit();
    }

    /*
    Runnable to execute the api call
     */
    private Runnable jokesRunnable = new Runnable() {
        @Override
        public void run() {
            apiCallTimeStamp = System.currentTimeMillis();
            spiceManager.execute(request, "Random Joke", DurationInMillis.ALWAYS_EXPIRED, new ApiRequestListener());
        }
    };

    /*
    Api Request Listener implementing the success/failure callbacks
     */
    private class ApiRequestListener implements RequestListener<JokeModel> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(MainActivity.this, "Please check your data connection!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(JokeModel jokeModel) {
            if (jokeModel != null && jokeModel.type.equalsIgnoreCase("success")) {
                updateJokeText(jokeModel);
            }
            jokesHandler.postDelayed(jokesRunnable, UPDATE_TIME); // callback to make an api call after 15 secs
        }
    }

    /*
    Updating the textview with fetched values
     */
    private void updateJokeText(JokeModel jokeModel) {
        JokeDataModel data = jokeModel.value;
        String joke = data.joke;
        if (joke != null && !joke.equals("")) {
            jokeTextView.setText(Html.fromHtml(joke)); // replace special characters like &quot; appropriately
        }
    }

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }
}
