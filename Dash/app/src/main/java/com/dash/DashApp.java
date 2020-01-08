package com.dash;

import android.app.Application;

import net.dean.jraw.android.AndroidHelper;
import net.dean.jraw.android.AppInfoProvider;
import net.dean.jraw.android.ManifestAppInfoProvider;
import net.dean.jraw.android.SharedPreferencesTokenStore;
import net.dean.jraw.http.NoopHttpLogger;
import net.dean.jraw.oauth.AccountHelper;

import java.util.UUID;

public final class DashApp extends Application {
    private static AccountHelper sAccountHelper;
    private static SharedPreferencesTokenStore sharedPreferencesTokenStore;

    @Override
    public void onCreate() {
        super.onCreate();

        // Get UserAgent and OAuth2 data from AndroidManifest.xml
        AppInfoProvider appInfoProvider = new ManifestAppInfoProvider(getApplicationContext());

        // Ideally, this should be unique to every device
        UUID deviceUuid = UUID.randomUUID();

        // Store our access tokens and refresh tokens in shared preferences
        sharedPreferencesTokenStore = new SharedPreferencesTokenStore(getApplicationContext());
        // Load stored tokens into memory
        sharedPreferencesTokenStore.load();
        // Automatically save new tokens as they arrive
        sharedPreferencesTokenStore.setAutoPersist(true);

        // An AccountHelper manages switching between accounts and into/out of userless mode.
        sAccountHelper = AndroidHelper.accountHelper(appInfoProvider, deviceUuid, sharedPreferencesTokenStore);

        // Every time we use the AccountHelper to switch between accounts (from one account to
        // another, or into/out of userless mode), call this function
        sAccountHelper.onSwitch(redditClient -> {
            // If you want to disable logging, use a NoopHttpLogger instead:
             redditClient.setLogger(new NoopHttpLogger());
            return null;
        });
    }

    public static AccountHelper getAccountHelper() {
        return sAccountHelper;
    }

    public static SharedPreferencesTokenStore getTokenStore(){
        return sharedPreferencesTokenStore;
    }
}