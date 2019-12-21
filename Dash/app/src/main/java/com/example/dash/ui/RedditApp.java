package com.example.dash.ui;

import android.app.Application;

import net.dean.jraw.android.AndroidHelper;
import net.dean.jraw.android.AppInfoProvider;
import net.dean.jraw.android.ManifestAppInfoProvider;
import net.dean.jraw.android.SharedPreferencesTokenStore;
import net.dean.jraw.http.NoopHttpLogger;
import net.dean.jraw.oauth.AccountHelper;

import java.util.UUID;

public final class RedditApp extends Application {
    private static AccountHelper accountHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        // Get UserAgent and OAuth2 data from AndroidManifest.xml
        AppInfoProvider provider = new ManifestAppInfoProvider(getApplicationContext());

        // Ideally, this should be unique to every device
        UUID deviceUuid = UUID.randomUUID();

        // Store our access tokens and refresh tokens in shared preferences
        SharedPreferencesTokenStore tokenStore = new SharedPreferencesTokenStore(getApplicationContext());
        // Load stored tokens into memory
        tokenStore.load();
        // Automatically save new tokens as they arrive
        tokenStore.setAutoPersist(true);

        // An AccountHelper manages switching between accounts and into/out of userless mode.
        accountHelper = AndroidHelper.accountHelper(provider, deviceUuid, tokenStore);

        // Every time we use the AccountHelper to switch between accounts (from one account to
        // another, or into/out of userless mode), call this function
        accountHelper.onSwitch(redditClient -> {
            // By default, JRAW logs HTTP activity to System.out. We're going to use Log.i()
            // instead.
//            LogAdapter logAdapter = new SimpleAndroidLogAdapter(Log.INFO);

            // We're going to use the LogAdapter to write down the summaries produced by
            // SimpleHttpLogger
//            redditClient.setLogger(
//                    new SimpleHttpLogger(SimpleHttpLogger.DEFAULT_LINE_LENGTH, logAdapter));

            // If you want to disable logging, use a NoopHttpLogger instead:
             redditClient.setLogger(new NoopHttpLogger());

            return null;
        });
    }

    public static AccountHelper getAccountHelper() {
        return accountHelper;
    }
}