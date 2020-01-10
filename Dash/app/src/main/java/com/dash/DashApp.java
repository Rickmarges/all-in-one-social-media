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
        sAccountHelper = AndroidHelper.accountHelper(appInfoProvider,
                deviceUuid, sharedPreferencesTokenStore);

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

    public static SharedPreferencesTokenStore getTokenStore() {
        return sharedPreferencesTokenStore;
    }
}