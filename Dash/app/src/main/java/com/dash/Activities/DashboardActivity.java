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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.dash.Adapters.ViewPagerAdapter;
import com.dash.DashApp;
import com.dash.R;
import com.dash.Utils.TwitterRepository;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Creates the main Activity you see when you are currently authenticated as a FireBaseUser
 * It shows a tablayout of currently 4 tabs: Dash, Reddit, Twitter, Trends.
 * It displays a dropdown menu in the top right.
 * In this activity the authentication of the different social media account is checked.
 */
@SuppressLint("ClickableViewAccessibility")
public class DashboardActivity extends AppCompatActivity {
    private Button mMenuBtn;
    private FirebaseUser mFirebaseUser;
    private TabLayout mTabLayout;
    private int mBackCounter;
    private long mStartTime;
    private static String sEncryptedEmail;

    /**
     * Creates this activity, The main Tab of the Dash app.
     * It checks if you are authenticated. if you are not authenticated you will be redirected to the LoginActivity
     * If you are authenticated you will stay on this activity to navigate through the tabs and the dropdown menu in the top right.
     *
     * @param savedInstanceState saved instance of this activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        checkLoggedIn();

        init();

        mMenuBtn.setOnClickListener(view -> popupMenu());
    }

    /**
     * Gives focus to the leftmost tab (Dash)
     * Checks if there is a FireBaseUser that is authenticated currenctly
     * Sets an ecnrypted email for comparison
     * Checks if there is a RedditUser that is authenticated currently
     */
    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(mTabLayout.getTabAt(0)).select();
        checkLoggedIn();
        sEncryptedEmail = encryptString(mFirebaseUser.getEmail());
        checkReddit();
    }

    /**
     * Closes the app if the back button is pressed twice in the span of 3 seconds.
     */
    @Override
    public void onBackPressed() {
        if (mBackCounter < 1 || (System.currentTimeMillis() - mStartTime) / 1000 > 3) {
            mStartTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "Press again to exit", Toast.LENGTH_SHORT)
                    .show();
            mBackCounter++;
        } else {
            mBackCounter = 0;
            finishAffinity();
        }
    }

    /**
     * Returns the encrypted email from the currently authenticated FireBaseUser.
     *
     * @return returns the encrypted email from the currently authenticated FireBaseUser
     * @throws IllegalStateException if the encyptedEmailString returns empty
     */
    public static String getEncryptedEmail() throws IllegalStateException {
        if (!sEncryptedEmail.equals("")) {
            return sEncryptedEmail;
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Initialize the backcounter regarding double pressing back to close app,
     * initializes the LinearLayout, Buttons, ViewPager(Adapter) and Tablayout
     * and links them to the corresponding layout elements
     */
    private void init() {
        mBackCounter = 0;

        LinearLayout linearLayout = findViewById(R.id.linearlayout);
        linearLayout.requestFocus();

        mMenuBtn = findViewById(R.id.menubtn);

        ViewPager viewPager = findViewById(R.id.pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount());

        mTabLayout = findViewById(R.id.tablayout);
        mTabLayout.setupWithViewPager(viewPager);
    }

    /**
     * Checks if there is a authenticated FireBaseUser if there isn't redirect to LoginActivity.
     */
    private void checkLoggedIn() {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    /**
     * Redirects to the Settings page when the button is clicked in the popup menu.
     */
    private void settings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    /**
     * Redirects to the Account page when the button is clicked in the popup menu.
     */
    private void account() {
        startActivity(new Intent(this, AccountActivity.class));
    }

    /**
     * Signs the currently authenticated FireBaseUser out when the button is clicked in the popupmenu.
     * This also destroys the saved data from Reddit, Twitter and Trends
     */
    private void signOut() {
        // Sets current FireBaseUser to null to ensure sign out
        mFirebaseUser = null;
        FirebaseAuth.getInstance().signOut();

        // Sets current tab in the Dashboard tablayout to the leftmost tab to reset
        Objects.requireNonNull(mTabLayout.getTabAt(0)).select();

        // Clear the different LinearLayouts from Trends, Reddit, Twitter and Dash respectively or throws an NPE if there is nothing to delete
        try {
            LinearLayout linearLayout = findViewById(R.id.trendsLayout);
            linearLayout.removeAllViews();
        } catch (NullPointerException npe) {
            Log.w(getApplicationContext().toString(),
                    "No Trend views to delete." + npe.getMessage());
        }

        try {
            LinearLayout linearLayout = findViewById(R.id.redditLayout);
            linearLayout.removeAllViews();
        } catch (NullPointerException npe) {
            Log.w(getApplicationContext().toString(),
                    "No Reddit views to delete." + npe.getMessage());
        }

        try {
            LinearLayout linearLayout = findViewById(R.id.dashLayout);
            linearLayout.removeAllViews();
        } catch (NullPointerException npe) {
            Log.w(getApplicationContext().toString(),
                    "No views in dashboard to delete." + npe.getMessage());
        }

        // Logs the currently authenticated RedditUser out.
        DashApp.getAccountHelper().logout();

        // Logs the currently authenticated TwitterUser out.
        TwitterRepository.GetSingleton().clearSession();

        // Redirects the user to LoginActivity
        startActivity(new Intent(this, LoginActivity.class));
    }

    /**
     * Displays a dropdownmenu in the top right to display a Settings, Account and Sign Out
     * and redirects the user to the pages correspoding to the input
     */
    private void popupMenu() {
        // Creates a new PopupMennu and inflates it according to the style declared in R.menu.menu.
        PopupMenu popupMenu = new PopupMenu(this, mMenuBtn);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());

        // If the android version is equal or greater than Q (10.0) it shows additional icons in the popupmenu.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true);
        }

        // Sets an onMenuItemClickListener on the different menu items, so corresponding methods are being called when clicked
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getTitle().toString()) {
                case "Settings":
                    settings();
                    break;
                case "Account":
                    account();
                    break;
                case "Sign Out":
                    signOut();
                    break;
                default:
                    return false;
            }
            return true;
        });

        //Sets an onTouchListener that enables an getDragToOpenListener so you can drag the menu with your finger to open it
        mMenuBtn.setOnTouchListener(popupMenu.getDragToOpenListener());
        popupMenu.show();
    }

    /**
     * Checks if the currently authenticated FireBaseUser is authenticated for reddit and when the user is,
     * executes ReauthenticationTask
     */
    private void checkReddit() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(getEncryptedEmail(),
                    Context.MODE_PRIVATE);

            String redditUsername = sharedPreferences.getString("Reddit", "");

            for (int i = 0; i < DashApp.getTokenStore().getUsernames().size(); i++) {
                String tempUser = DashApp.getTokenStore().getUsernames().get(i);
                String tempString = encryptString(tempUser);

                if (tempString.equals(redditUsername) && !tempUser.equals("")) {
                    new ReauthenticationTask().execute(tempUser);
                    return;
                }
            }
        } catch (NullPointerException npe) {
            Log.w(getApplicationContext().toString(),
                    "No such user found." + npe.getMessage());
        }
    }

    /**
     * Retrieves the frontpage of the currently authenticated RedditUser to show the Reddit Frontpage on Dash.
     */
    private static class ReauthenticationTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... usernames) {
            DashApp.getAccountHelper().switchToUser(usernames[0]);
            return null;
        }
    }

    /**
     * Encrypts a custom string.
     *
     * @param string The string thats requires encrypting
     * @return Returns the string but encrypted, unless there is no string to encypt, or when this
     * particular cryptographic algorithm is requested but is not available in the environment
     */
    public static String encryptString(String string) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = messageDigest.digest(string.getBytes());
            return new String(encodedhash);
        } catch (NullPointerException | NoSuchAlgorithmException e) {
            Log.w("Warinng", "Error encrypting string: " + e.getMessage());
            return "";
        }
    }
}
