package com.example.dash.data;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dash.BuildConfig;
import com.example.dash.R;
import com.example.dash.ui.dashboard.TwitterFragment;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.TweetUi;

import java.util.List;

import retrofit2.Call;

// Twitter API Docs: https://github.com/twitter-archive/twitter-kit-android/wiki
public class TwitterRepository extends AppCompatActivity {
    public static TwitterRepository TwitterSingleton = null;
    private Twitter instance;
    private String token;
    private String secret;
    public TwitterSession userSession;

    public TwitterRepository() {
    }

    //Creates a singlton of the twitterinstance
    public static TwitterRepository GetSingleton() {
        //TwitterSingleton = new TwitterRepository();
        if (TwitterSingleton == null) {
            synchronized (TwitterRepository.class) {
                TwitterSingleton = new TwitterRepository();
            }
        }
        return TwitterSingleton;
    }

    //Creates a twitter session in the singleton
    public void createSession(TwitterSession session) {
        TwitterSingleton.userSession = session;
        TwitterAuthToken authToken = session.getAuthToken();
        TwitterSingleton.token = authToken.token;
        TwitterSingleton.secret = authToken.secret;
    }

    public TwitterCore getSession() {
        return TwitterCore.getInstance();
    }

    public void clearSession() {
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
    }

    public TwitterSession getActiveSession() {
        return TwitterCore.getInstance().getSessionManager().getActiveSession();
    }

    public void GetHomeTimeline(int amount, TwitterFragment fragment) throws InterruptedException {
        //Gets the twitter api and calls the api to get the hometimeline
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();

        //For some reason a call with count 20 will get 18 results. So 22 is 20.
        Call<List<Tweet>> call = statusesService.homeTimeline(amount + 2, null, null, null, false, null, null);
        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                //Creates the timeline in the twitter fragment
                List<Tweet> t = result.data;
                fragment.createHomeTimelineView(t);
            }

            public void failure(TwitterException exception) {
            }
        });
    }

    //Initialize the twitter config
    static public void InitializeTwitter(Context context) {
        TwitterConfig config = new TwitterConfig.Builder(context)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(BuildConfig.TWITTER_CONSUMER_ACCESS_TOKEN, BuildConfig.TWITTER_CONSUMER_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);
        TwitterCore.getInstance();
        TweetComposer.getInstance();
        TweetUi.getInstance();
    }

    //Sets the twittercallback for the login button of twitter.
    static public void setTwitterCallback(Context context, TwitterLoginButton twitterBtn) {
        twitterBtn.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                //resets the buttons
                Toast.makeText(context, "Authentication succesfull", Toast.LENGTH_SHORT).show();
                TextView textView = (TextView) ((Activity) context).findViewById(R.id.textView3);
                twitterBtn.setVisibility(View.INVISIBLE);
                ((Activity) context).finish();

                //creates the instance
                TwitterSession session = TwitterRepository.GetSingleton().getActiveSession();
                TwitterRepository.GetSingleton().createSession(session);
                textView.setText(session.getUserName());
            }

            @Override
            public void failure(TwitterException exception) {
                //clears the session
                Toast.makeText(context, "Authentication failed try again...", Toast.LENGTH_SHORT).show();
                TwitterRepository.TwitterSingleton.clearSession();
            }
        });
    }

    static public long getCurrentUserId() {
        return TwitterRepository.TwitterSingleton.getActiveSession().getUserId();
    }
}