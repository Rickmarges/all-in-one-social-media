package com.dash.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dash.BuildConfig;
import com.dash.Fragments.DashFragment;
import com.dash.Fragments.TwitterFragment;
import com.dash.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.TweetUi;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;

import static com.dash.Activities.DashboardActivity.getEncryptedEmail;

// Twitter API Docs: https://github.com/twitter-archive/twitter-kit-android/wiki
public class TwitterRepositoryActivity extends AppCompatActivity {
    public static TwitterRepositoryActivity twitterSingleton = null;

    public TwitterRepositoryActivity() {
    }

    //Creates a singlton of the twitterinstance
    public static TwitterRepositoryActivity GetSingleton() {
        //twitterSingleton = new TwitterRepositoryActivity();
        if (twitterSingleton == null) {
            synchronized (TwitterRepositoryActivity.class) {
                twitterSingleton = new TwitterRepositoryActivity();
            }
        }
        return twitterSingleton;
    }

    private void savePreferences(TwitterSession twitterSession) {
        try {
            Set<String> authTokenSet = new HashSet<>();
            String username = twitterSession.getUserName();
            long userId = twitterSession.getUserId();

            authTokenSet.add(twitterSession.getAuthToken().secret);
            authTokenSet.add(twitterSession.getAuthToken().token);

            SharedPreferences sharedPreferences = Objects.requireNonNull(TwitterFragment
                    .getInstance()
                    .getActivity())
                    .getSharedPreferences(getEncryptedEmail(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putStringSet("Twitter token", authTokenSet);
            editor.putString("Twitter username", username);
            editor.putLong("Twitter id", userId);
            editor.apply();
        } catch (NullPointerException npe) {
            Log.w("Warning", "Couldn't save preferences: " + npe.getMessage());
        }
    }

    public void clearSession() {
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
    }

    private TwitterSession getActiveSession() {
        return TwitterCore.getInstance().getSessionManager().getActiveSession();
    }

    public void GetHomeTimeline(int amount) {
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
                TwitterFragment.getInstance().createHomeTimelineView(t);
            }

            public void failure(TwitterException exception) {
                TwitterFragment.getInstance().setRefreshing(false);
                DashFragment.getInstance().setTwitterReady(false);
                DashFragment.getInstance().setRefreshing(false);
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
                TextView textView = ((Activity) context).findViewById(R.id.addTwitterAccount);
                twitterBtn.setVisibility(View.INVISIBLE);
                ((Activity) context).finish();

                //creates the instance
                TwitterSession session = TwitterRepositoryActivity.GetSingleton().getActiveSession();

                textView.setText(session.getUserName());

                CookieManager.getInstance().removeAllCookies(null);
                CookieManager.getInstance().flush();

                TwitterRepositoryActivity.GetSingleton().savePreferences(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                //clears the session
                Toast.makeText(context, "Authentication failed try again...", Toast.LENGTH_SHORT).show();
                TwitterRepositoryActivity.GetSingleton().clearSession();
            }
        });
    }
}