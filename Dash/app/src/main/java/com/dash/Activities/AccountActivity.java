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

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.dash.DashApp;
import com.dash.Fragments.RedditFragment;
import com.dash.Fragments.TwitterFragment;
import com.dash.R;
import com.google.firebase.auth.FirebaseAuth;
import com.securepreferences.SecurePreferences;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import net.dean.jraw.oauth.AccountHelper;

import java.util.Objects;

/**
 * Shows which accounts are linked to the Dash account, it shows the
 * Email from the FirebaseUser and links to pages that let you add different social media accounts.
 */

public class AccountActivity extends AppCompatActivity {
    private boolean mSecondClick;
    private Button mResetBtn;
    private ScrollView mScrollView;
    private ImageButton mRedditIB;
    private TwitterLoginButton mAddTwitterBtn;
    private TextView mEmailAccountTV;
    private ImageButton mRemoveRedditIB;
    private ImageButton mRemoveTwitterBtn;
    private FirebaseAuth mFirebaseAuth;

    /**
     * Creates the view in the activity, fills textview with FirebaseUser email, and sets
     * onClickListeners for buttons that let you add social media accounts to your Dash account.
     *
     * @param savedInstanceState saved instance of this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // Initialize Twitter so we can retrieve twitter data
        TwitterRepositoryActivity.InitializeTwitter(getApplicationContext());

        // Initialize UI elements
        init();

        new OrientationEventListener(getApplicationContext(), SensorManager.SENSOR_DELAY_UI) {
            /**
             * Called when the orientation of the device has changed.
             * orientation parameter is in degrees, ranging from 0 to 359.
             * orientation is 0 degrees when the device is oriented in its natural position,
             * 90 degrees when its left side is at the top, 180 degrees when it is upside down,
             * and 270 degrees when its right side is to the top.
             * {@link #ORIENTATION_UNKNOWN} is returned when the device is close to flat
             * and the orientation cannot be determined.
             *
             * @param orientation The new orientation of the device.
             * @see #ORIENTATION_UNKNOWN
             */
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation < 60 || orientation > 300) {
                    mScrollView.setEnabled(false);
                } else {
                    mScrollView.setEnabled(true);
                }
            }
        }.enable();

        // Get user email and encrypt that email so it can be used for storage
        mFirebaseAuth = FirebaseAuth.getInstance();
        mEmailAccountTV.setText(Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getEmail());

        // Add an OnClickListener to the Reddit imagebutton
        // If the button is pressed it will send the user to the AddRedditAccountActivity
        mRedditIB.setOnClickListener(view -> startActivity(new Intent(this,
                AddRedditAccountActivity.class)));

        // Add an OnClickListener to the mResetBtn to send the user a reset password email and it shows a popup if it succeeds or fails
        mResetBtn.setOnClickListener(view -> resetPassword());
    }

    /**
     * Reinitialize when the user returns to this activity
     */
    @Override
    protected void onResume() {
        super.onResume();
        init();
        mSecondClick = false;
    }

    /**
     * Overrides the standard animation to show our custom animation when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * Initializes the TextViews and Buttons in the layout and links them to their corresponding
     * layout elements. And checks if there already is an authorized twitter session available,
     * if so, hide twitter related elements
     */
    private void init() {
        mRedditIB = findViewById(R.id.add_reddit_btn);
        mRemoveRedditIB = findViewById(R.id.removeRedditIB);
        mRemoveTwitterBtn = findViewById(R.id.removetwitterbtn);
        mEmailAccountTV = findViewById(R.id.emailAccount);
        mResetBtn = findViewById(R.id.resetpwd);
        mAddTwitterBtn = findViewById(R.id.addtwitterbtn);
        mAddTwitterBtn.setPadding(16, 0, 0, 0);

        mScrollView = findViewById(R.id.accountScroll);

        checkReddit();

        checkTwitter();
    }

    private void resetPassword() {
        Animation animShake = AnimationUtils.loadAnimation(this, R.anim.hshake);
        if (!mSecondClick) {
            mResetBtn.setText(R.string.confirm);
            mResetBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat
                    .getColor(this, R.color.tw__composer_red)));
            mResetBtn.setAnimation(animShake);
        }
        if (mSecondClick) {
            mFirebaseAuth.sendPasswordResetEmail(Objects.requireNonNull(
                    Objects.requireNonNull(mFirebaseAuth
                            .getCurrentUser())
                            .getEmail()))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Sent reset email!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error sending email.",
                                    Toast.LENGTH_LONG).show();
                        }
                        mResetBtn.setText(R.string.reset_password);
                        mResetBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat
                                .getColor(this, R.color.colorBackgroundSecondary)));
                        mSecondClick = false;
                    });
        }
        mSecondClick = true;
    }

    /**
     * Checks if there is an active Reddit account
     */
    private void checkReddit() {
        // Retrieve the Reddit accounthelper
        AccountHelper accountHelper = DashApp.getAccountHelper();

        // Check if there is an authenticated Reddit user
        if (accountHelper.isAuthenticated()) {
            // Switch button visibility
            mRedditIB.setVisibility(View.INVISIBLE);
            mRemoveRedditIB.setVisibility(View.VISIBLE);

            // Set active username in a textview
            TextView textView = findViewById(R.id.addRedditAccount);
            textView.setText(accountHelper.getReddit().getAuthManager().currentUsername());

            // Make "Reddit username:" textview visible
            TextView redditUserTextView = findViewById(R.id.redditUsername);
            redditUserTextView.setVisibility(View.VISIBLE);

            // Add an OnClickListener to the remove Reddit button
            mRemoveRedditIB.setOnClickListener(view -> {
                // Delete the user tokens
                DashApp.getTokenStore().deleteRefreshToken(accountHelper.getReddit().getAuthManager().currentUsername());
                DashApp.getTokenStore().deleteLatest(accountHelper.getReddit().getAuthManager().currentUsername());
                accountHelper.logout();

                // Clear the UI and make the right items visible
                RedditFragment.getInstance().clearUI();
                mRemoveRedditIB.setVisibility(View.INVISIBLE);
                redditUserTextView.setVisibility(View.INVISIBLE);
                mRedditIB.setVisibility(View.VISIBLE);
                textView.setText(R.string.add_reddit);
            });
        }
    }

    /**
     * Checks if there is an active Twitter account
     */
    private void checkTwitter() {
        // Retrieve the active Twitter session
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        // Check if there is an active session
        if (session != null) {
            // Switch button visibility
            mAddTwitterBtn.setVisibility(View.INVISIBLE);
            mRemoveTwitterBtn.setVisibility(View.VISIBLE);

            // Set active username in a textview
            TextView textView = findViewById(R.id.addTwitterAccount);
            textView.setText(session.getUserName());

            // Make "Twitter username:" textview visible
            TextView twitterUserTextView = findViewById(R.id.twitterUsername);
            twitterUserTextView.setVisibility(View.VISIBLE);

            // Add an OnClickListener to the remove Twitter button
            mRemoveTwitterBtn.setOnClickListener(view -> {
                // Delete the user tokens
                SharedPreferences sharedPreferences = new SecurePreferences(getApplicationContext(),
                        "", DashboardActivity.getFilename());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("Twitter token");
                editor.remove("Twitter username");
                editor.remove("Twitter id");
                editor.apply();
                TwitterRepositoryActivity.twitterSingleton.clearSession();
                TwitterRepositoryActivity.setTwitterCallback(this, mAddTwitterBtn);

                // Clear the UI and make the right items visible
                TwitterFragment.getInstance().clearUI();
                mRemoveTwitterBtn.setVisibility(View.INVISIBLE);
                twitterUserTextView.setVisibility(View.INVISIBLE);
                mAddTwitterBtn.setVisibility(View.VISIBLE);
                textView.setText(R.string.add_twitter);
            });
        } else {
            // If there is not an active session set the right visibility to the buttons
            mAddTwitterBtn.setVisibility(View.VISIBLE);
            mRemoveTwitterBtn.setVisibility(View.INVISIBLE);
            TwitterRepositoryActivity.setTwitterCallback(this, mAddTwitterBtn);
        }
    }

    /**
     * Call this method when {@link android.app.Activity#onActivityResult(int, int, Intent)}
     * is called to complete the authorization flow.
     *
     * @param requestCode the request code used for SSO
     * @param resultCode  the result code returned by the SSO activity
     * @param data        the result data returned by the SSO activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAddTwitterBtn.onActivityResult(requestCode, resultCode, data);
    }

}
