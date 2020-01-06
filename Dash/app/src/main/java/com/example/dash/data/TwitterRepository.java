package com.example.dash.data;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dash.R;
import com.example.dash.ui.dashboard.TwitterFragment;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.io.File;
import java.util.List;

import retrofit2.Call;

// Twitter API Docs: https://github.com/twitter-archive/twitter-kit-android/wiki
public class TwitterRepository extends AppCompatActivity {
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

    static public void setTwitterCallback(Context context, TwitterLoginButton twitterBtn) {
        twitterBtn.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Toast.makeText(context, "Authentication succesfull", Toast.LENGTH_SHORT).show();
                FirstTimeAuthenticated();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(context, "Authentication failed try again...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    static private void FirstTimeAuthenticated(){
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        TwitterRepository twitterRepository = new TwitterRepository(session);
        twitterRepository.Login();
        //twitterRepository.GetHomeTimeline(twitterFragment, 20);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        TwitterLoginButton twitterBtn = findViewById(R.id.addtwitterbtn);
        twitterBtn.onActivityResult(requestCode, resultCode, data);
    }
}