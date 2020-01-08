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

import net.dean.jraw.RedditClient;
import net.dean.jraw.oauth.OAuthException;
import net.dean.jraw.oauth.StatefulAuthHelper;

import java.lang.ref.WeakReference;

public class AddRedditAccountActivity extends AppCompatActivity {
    private WebView mWebView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reddit_account);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        createWebView();
    }

    // An async task that takes a final redirect URL as a parameter and reports the success of authorizing the user.
    class AuthenticateTask extends AsyncTask<String, Void, Boolean> {
        // Use a WeakReference so that we don't leak a Context
        private final WeakReference<Activity> mWeakReference;
        private final StatefulAuthHelper mStatefulAuthHelper;

        AuthenticateTask(Activity context, StatefulAuthHelper helper) {
            this.mWeakReference = new WeakReference<>(context);
            this.mStatefulAuthHelper = helper;
        }

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

    private void addSharedPreferences() {
        try {
            RedditClient redditClient = DashApp.getAccountHelper().getReddit();
            String redditUsername = DashboardActivity.encryptString(redditClient.getAuthManager().currentUsername());
            SharedPreferences sharedPreferences = getSharedPreferences(DashboardActivity.getEncryptedEmail(), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Reddit", redditUsername);
            editor.apply();
        } catch (NullPointerException npe) {
            Log.w(getApplicationContext().toString(), "Couldn't save preferences: " + npe.getMessage());
        }
    }

    private void createWebView() {
        mWebView.clearCache(true);
        mWebView.clearHistory();

        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        // Get a StatefulAuthHelper instance to manage interactive authentication
        final StatefulAuthHelper statefulAuthHelper = DashApp.getAccountHelper().switchToNewUser();

        // Watch for pages loading
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (statefulAuthHelper.isFinalRedirectUrl(url)) {
                    // No need to continue loading, we've already got all the required information
                    mWebView.stopLoading();
                    mWebView.setVisibility(View.GONE);

                    // Try to authenticate the user
                    new AuthenticateTask(AddRedditAccountActivity.this, statefulAuthHelper).execute(url);
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        // Generate an authentication URL
        String[] scopes = new String[]{"read", "identity"};
        String authorizationUrl = statefulAuthHelper.getAuthorizationUrl(true, true, scopes);

        // Finally, show the authorization URL to the user
        mWebView.loadUrl(authorizationUrl);
    }

    private void init() {
        mProgressBar = findViewById(R.id.addedAccount);
        mWebView = findViewById(R.id.webview);
    }
}
