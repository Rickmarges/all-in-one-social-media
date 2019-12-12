package com.example.dash.data;

import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;

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

    }
}