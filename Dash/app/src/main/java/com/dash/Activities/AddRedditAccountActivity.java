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

package com.dash.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.dash.DashApp;
import com.dash.R;
import com.securepreferences.SecurePreferences;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import net.dean.jraw.RedditClient;
import net.dean.jraw.oauth.OAuthException;
import net.dean.jraw.oauth.StatefulAuthHelper;

import java.lang.ref.WeakReference;

/**
 * Adds a reddit account to your Dash account. It will open a new activity
 * containing a WebView that opens a Authentication URL provided by JRAW. With this authentication
 * URL you can sign in to your reddit account and link it to your Dash account. This activity will
 * be destroyed and will not be accessible again.
 */

public class AddRedditAccountActivity extends AppCompatActivity {
    private ProgressBar mProgressBar;
    private WebView mWebView;

    /**
     * Creates the view in the activity.
     *
     * @param savedInstanceState saved instance of this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reddit_account);

        // Initialize UI parts
        init();
    }

    /**
     * Shows the WebView when the activity is created.
     */
    @Override
    protected void onResume() {
        super.onResume();
        createWebView();
    }

    /**
     * Reports the success of authorizing the user and takes a final redirect URL as parameter.
     */
    class AuthenticateTask extends AsyncTask<String, Void, Boolean> {
        // Use a WeakReference so that we don't leak a Context
        private final StatefulAuthHelper mStatefulAuthHelper;
        private final WeakReference<Activity> mWeakReference;

        AuthenticateTask(Activity context, StatefulAuthHelper helper) {
            this.mStatefulAuthHelper = helper;
            this.mWeakReference = new WeakReference<>(context);
        }

        /**
         * Executes the mStatefulAuthHelper with the auth url provided by JRAW and reports an OAUTHException if it fails.
         *
         * @param urls the authentication URL provided by JRAW
         * @return success if authentication is succesfull and an OAUTHException if it isn't.
         */
        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                mStatefulAuthHelper.onUserChallenge(urls[0]);
                return true;
            } catch (OAuthException e) {
                // Report failure if an OAuthException occurs
                return false;
            }
        }

        /**
         * Reports the success of authorizing the user and takes a final redirect URL as parameter.      *
         *
         * @param success boolean describing the result of the authentication
         */
        @Override
        protected void onPostExecute(Boolean success) {
            if (mStatefulAuthHelper.getAuthStatus().equals(StatefulAuthHelper.Status.AUTHORIZED)) {
                addSharedPreferences();
            }

            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();

            // Finish the activity if it's still running
            Activity activity = this.mWeakReference.get();
            if (activity != null) {
                activity.setResult(success ? Activity.RESULT_OK : Activity.RESULT_CANCELED, new Intent());
                activity.finish();
            }
        }
    }

    /**
     * Adds the reddit username to the shared preferences so it can be used. It first encrypts the username, then saves it.
     */
    private void addSharedPreferences() {
        try {
            RedditClient redditClient = DashApp.getAccountHelper().getReddit();
            String redditUsername = redditClient.getAuthManager().currentUsername();

            SharedPreferences sharedPreferences = new SecurePreferences(getApplicationContext(),
                    "", DashboardActivity.getFilename());
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("Reddit", redditUsername);
            editor.apply();
        } catch (NullPointerException npe) {
            Log.w(getApplicationContext().toString(),
                    "Couldn't save preferences: " + npe.getMessage());
        }
    }

    /**
     * Creates the Reddit login WebView.
     */
    private void createWebView() {
        mWebView.clearCache(true);
        mWebView.clearHistory();

        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        // Get a StatefulAuthHelper instance to manage interactive authentication
        final StatefulAuthHelper statefulAuthHelper = DashApp.getAccountHelper().switchToNewUser();

        // Watch for pages loading
        mWebView.setWebViewClient(new WebViewClient() {

            /**
             * Stops loading the WebView when it has loaded the page and then executes the authentication URL.
             *
             * @param view The WebView
             * @param url The authentication URL
             * @param favicon The favicon
             */
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (statefulAuthHelper.isFinalRedirectUrl(url)) {
                    // No need to continue loading, we've already got all the required information
                    mWebView.stopLoading();
                    mWebView.setVisibility(View.GONE);

                    // Try to authenticate the user
                    new AuthenticateTask(AddRedditAccountActivity.this, statefulAuthHelper)
                            .execute(url);
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        // Generate an authentication URL
        String[] scopes = new String[]{"read", "identity"};
        String authorizationUrl = statefulAuthHelper
                .getAuthorizationUrl(true, true, scopes);

        // Finally, show the authorization URL to the user
        mWebView.loadUrl(authorizationUrl);
    }

    /**
     * Initializes the WebView and ProgressBar in the layout and links them to their corresponding
     * layout elements.
     */
    private void init() {
        mProgressBar = findViewById(R.id.addedAccount);
        mWebView = findViewById(R.id.webview);
    }
}
