package com.example.dash.data;

import com.example.dash.ui.dashboard.TwitterFragment;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.List;

import retrofit2.Call;

// Twitter API Docs: https://github.com/twitter-archive/twitter-kit-android/wiki
public class TwitterRepository {
    private Twitter instance;
    private String token;
    private String secret;
    private TwitterSession userSession;

    public TwitterRepository(TwitterSession session) {
        TwitterAuthToken authToken = session.getAuthToken();
        token = authToken.token;
        secret = authToken.secret;
        userSession = session;
    }

    public void Login() {
        //Save the tokens encrypted to local storage

    }

    public void GetHomeTimeline(TwitterFragment twitterFragment, int amount){
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();

        //For some reason a call with count 20 will get 18 results. So 22 is 20.
        Call<List<Tweet>> call = statusesService.homeTimeline(amount + 2, null, null, null, false, null, null);
        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                //Do something with result
                List<Tweet> tweetList = result.data;
                twitterFragment.createHomeTimelineView(tweetList);
            }
            public void failure(TwitterException exception) {
                //Do something on failure
            }
        });
    }
}