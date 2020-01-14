/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Furthermore this project is licensed under the firebase.google.com/terms and
 * firebase.google.com/terms/crashlytics.
 *
 */

package com.dash.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dash.BuildConfig;
import com.dash.Fragments.TwitterFragment;
import com.dash.R;
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
import java.util.Objects;

import retrofit2.Call;

/**
 * Twitter API Docs: https://github.com/twitter-archive/twitter-kit-android/wiki
 */
public class TwitterRepository extends AppCompatActivity {
    public static TwitterRepository twitterSingleton = null;
    private Twitter instance;
    private String token;
    private String secret;
    public TwitterSession userSession;

    /**
     *
     */
    public TwitterRepository() {
    }

    /**
     * Creates a singlton of the twitterinstance
     *
     * @return a twitterSingleton
     */
    public static TwitterRepository GetSingleton() {
        //twitterSingleton = new TwitterRepository();
        if (twitterSingleton == null) {
            synchronized (TwitterRepository.class) {
                twitterSingleton = new TwitterRepository();
            }
        }
        return twitterSingleton;
    }

    /**
     * Creates a twitter session in the singleton
     *
     * @param session the twitter session
     */
    public void createSession(TwitterSession session) {
        twitterSingleton.userSession = session;
        TwitterAuthToken authToken = session.getAuthToken();
        twitterSingleton.token = authToken.token;
        twitterSingleton.secret = authToken.secret;
    }

    public TwitterCore getSession() {
        return TwitterCore.getInstance();
    }

    /**
     * Clears the currenctly active session
     */
    public void clearSession() {
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
    }

    public TwitterSession getActiveSession() {
        return TwitterCore.getInstance().getSessionManager().getActiveSession();
    }

    /**
     * Retrieves the hometimeline of the currenctly authenticated WwitterUser
     *
     * @param amount   the amount of tweets to be retrieved
     * @param fragment the fragment the cards will be placed in
     */
    public void GetHomeTimeline(int amount, TwitterFragment fragment) {
        //Gets the twitter api and calls the api to get the hometimeline
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();

        //For some reason a call with count 20 will get 18 results. So 22 is 20.
        Call<List<Tweet>> call = statusesService.homeTimeline(amount + 2, null, null, null, false, null, null);
        call.enqueue(new Callback<List<Tweet>>() {
            /**
             * Fills fragment with the result of the call
             *
             * @param result the result of the call
             */
            @Override
            public void success(Result<List<Tweet>> result) {
                //Creates the timeline in the twitter fragment
                List<Tweet> t = result.data;
                fragment.createHomeTimelineView(t);
            }

            /**
             * Reports failure
             *
             * @param te the exception thrown
             */
            public void failure(TwitterException te) {
                Log.w(Objects.requireNonNull(getApplicationContext()).toString(), "Unable to retrieve Timeline: " + te.getMessage());
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

    /**
     * Sets the twittercallback for the login button of twitter.
     *
     * @param context    the view
     * @param twitterBtn the button used to authenticate
     */
    static public void setTwitterCallback(Context context, TwitterLoginButton twitterBtn) {
        twitterBtn.setCallback(new Callback<TwitterSession>() {
            /**
             *Reports if authentication was succesfull
             *
             * @param result the result of the session
             */
            @Override
            public void success(Result<TwitterSession> result) {
                //resets the buttons
                Toast.makeText(context, "Authentication succesfull", Toast.LENGTH_SHORT).show();
                TextView textView = (TextView) ((Activity) context).findViewById(R.id.addTwitterAccount);
                twitterBtn.setVisibility(View.INVISIBLE);
                ((Activity) context).finish();

                //creates the instance
                TwitterSession session = TwitterRepository.GetSingleton().getActiveSession();
                TwitterRepository.GetSingleton().createSession(session);
                textView.setText(session.getUserName());

                // Clear all cookies of webview
                CookieManager.getInstance().removeAllCookies(null);
                CookieManager.getInstance().flush();
            }

            /**
             * Reports if authentication failed
             *
             * @param te the exception thrown
             */
            @Override
            public void failure(TwitterException te) {
                //clears the session
                Toast.makeText(context, "Authentication failed try again...", Toast.LENGTH_SHORT).show();
                TwitterRepository.GetSingleton().clearSession();
            }
        });
    }
}