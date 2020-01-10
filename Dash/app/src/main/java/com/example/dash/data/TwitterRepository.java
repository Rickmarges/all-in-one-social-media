package com.example.dash.data;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;

// Twitter API Docs: https://github.com/twitter-archive/twitter-kit-android/wiki
public class TwitterRepository extends AppCompatActivity {
    public static TwitterRepository TwitterSingleton = null;
    private Twitter instance;
    private String token;
    private String secret;
    public TwitterSession userSession;

    public TwitterRepository() {
        //TwitterAuthToken authToken = session.getAuthToken();
        //token = authToken.token;
        //secret = authToken.secret;
    }

    public static TwitterRepository GetSingleton(){
        //TwitterSingleton = new TwitterRepository();
        if (TwitterSingleton == null) {
            synchronized(TwitterRepository.class) {
                TwitterSingleton = new TwitterRepository();
            }
        }
        return TwitterSingleton;
    }

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
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();

        //For some reason a call with count 20 will get 18 results. So 22 is 20.
        Call<List<Tweet>> call = statusesService.homeTimeline(amount + 2, null, null, null, false, null, null);
        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                //Do something with result
                List<Tweet> t = result.data;

                fragment.createHomeTimelineView(t);
                //t.get(0).entities.media.get(0).url;
            }

            public void failure(TwitterException exception) {
                //Do something on failure
            }
        });
    }

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

    static public void setTwitterCallback(Context context, TwitterLoginButton twitterBtn) {
        twitterBtn.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Toast.makeText(context, "Authentication succesfull", Toast.LENGTH_SHORT).show();
                TextView textView = (TextView) ((Activity) context).findViewById(R.id.textView3);
                twitterBtn.setVisibility(View.INVISIBLE);
                ((Activity) context).finish();

                TwitterSession session = TwitterRepository.GetSingleton().getActiveSession();
                TwitterRepository.GetSingleton().createSession(session);
                textView.setText(session.getUserName());
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(context, "Authentication failed try again...", Toast.LENGTH_SHORT).show();
                TwitterRepository.TwitterSingleton.clearSession();
            }
        });
    }

    static public long getCurrentUserId(){
        return TwitterRepository.TwitterSingleton.getActiveSession().getUserId();
    }
}