package com.example.dash.ui.account;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.dash.R;
import com.example.dash.ui.RedditApp;
import com.google.firebase.auth.FirebaseAuth;

import net.dean.jraw.RedditClient;
import net.dean.jraw.oauth.OAuthException;
import net.dean.jraw.oauth.StatefulAuthHelper;

import java.lang.ref.WeakReference;

import okhttp3.Cookie;

public class AddRedditAccount extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reddit_account);
        final ProgressBar progressBar = findViewById(R.id.addedAccount);

        final WebView webView = findViewById(R.id.webview);
        webView.clearCache(true);
        webView.clearHistory();

        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        // Get a StatefulAuthHelper instance to manage interactive authentication
        final StatefulAuthHelper helper = RedditApp.getAccountHelper().switchToNewUser();

        // Watch for pages loading
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (helper.isFinalRedirectUrl(url)) {
                    // No need to continue loading, we've already got all the required information
                    webView.stopLoading();
                    webView.setVisibility(View.GONE);

                    // Try to authenticate the user
                    new AuthenticateTask(AddRedditAccount.this, helper).execute(url);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        // Generate an authentication URL
        boolean requestRefreshToken = true;
        boolean useMobileSite = true;
        String[] scopes = new String[]{"read", "identity"};
        String authUrl = helper.getAuthorizationUrl(requestRefreshToken, useMobileSite, scopes);

        // Finally, show the authorization URL to the user
        webView.loadUrl(authUrl);
    }

    /**
     * An async task that takes a final redirect URL as a parameter and reports the success of
     * authorizing the user.
     */
    private final class AuthenticateTask extends AsyncTask<String, Void, Boolean> {
        // Use a WeakReference so that we don't leak a Context
        private final WeakReference<Activity> context;

        private final StatefulAuthHelper helper;

        AuthenticateTask(Activity context, StatefulAuthHelper helper) {
            this.context = new WeakReference<>(context);
            this.helper = helper;
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                helper.onUserChallenge(urls[0]);

                return true;
            } catch (OAuthException e) {
                // Report failure if an OAuthException occurs
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            // Finish the activity if it's still running
            Activity host = this.context.get();
            addSP();
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();


            if (host != null) {
                host.setResult(success ? Activity.RESULT_OK : Activity.RESULT_CANCELED, new Intent());
                host.finish();
            }
        }
    }
    private void addSP(){
        RedditClient redditClient = RedditApp.getAccountHelper().getReddit();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(FirebaseAuth.getInstance().getCurrentUser().getEmail(), redditClient.getAuthManager().currentUsername());
        editor.apply();
        editor.commit();
    }
}
