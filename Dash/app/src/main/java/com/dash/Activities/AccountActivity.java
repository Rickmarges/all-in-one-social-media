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
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dash.R;
import com.dash.Utils.TwitterRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.Objects;

/**
 * Shows which accounts are linked to the Dash account, it shows the
 * Email from the FirebaseUser and links to pages that let you add different social media accounts.
 */

public class AccountActivity extends AppCompatActivity {
    private boolean mSecondClick;
    private Button mResetBtn;
    private ImageButton mRedditIB;
    private TwitterLoginButton addTwitterBtn;
    private ImageButton removeTwitterBtn;
    private TextView mEmailAccountTV;

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

        // Initialize UI parts
        init();

        mSecondClick = false;

        TwitterRepository.InitializeTwitter(getApplicationContext());

        // Get user email and encrypt that email so it can be used for storage
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mEmailAccountTV.setText(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail());

        // Add an OnClickListener to the Reddit imagebutton
        // If the button is pressed it will send the user to the AddRedditAccountActivity
        mRedditIB.setOnClickListener(view -> startActivity(new Intent(this,
                AddRedditAccountActivity.class)));

        // Add an OnClickListener to the sendPasswordRessetEmail
        // If the button is pressed it will send the user to a reset email and it shows a popup if
        // it succeeds and another popup if it fails
        Animation animShake = AnimationUtils.loadAnimation(this, R.anim.hshake);

        mResetBtn.setOnClickListener(view -> {
            if (!mSecondClick) {
                mResetBtn.setText(R.string.confirm);
                mResetBtn.setAnimation(animShake);
            }
            if (mSecondClick) {
                firebaseAuth.sendPasswordResetEmail(Objects.requireNonNull(firebaseAuth
                        .getCurrentUser()
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
                            mSecondClick = false;
                        });
            }
            mSecondClick = true;
        });
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
        mEmailAccountTV = findViewById(R.id.emailAccount);
        mResetBtn = findViewById(R.id.resetpwd);
        addTwitterBtn = findViewById(R.id.addtwitterbtn);
        removeTwitterBtn = findViewById(R.id.removetwitterbtn);

        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if(session != null){
            addTwitterBtn.setVisibility(View.INVISIBLE);
            removeTwitterBtn.setVisibility(View.VISIBLE);
            TextView textView = findViewById(R.id.textView3);
            textView.setText(session.getUserName());
            removeTwitterBtn.setOnClickListener(view -> {
                TwitterRepository.TwitterSingleton.clearSession();
                finish();
            });
        }else{
            addTwitterBtn.setVisibility(View.VISIBLE);
            removeTwitterBtn.setVisibility(View.INVISIBLE);
            TwitterRepository.setTwitterCallback(this, addTwitterBtn);
        }
    }

    /**
     * Call this method when {@link android.app.Activity#onActivityResult(int, int, Intent)}
     * is called to complete the authorization flow.
     *
     * @param requestCode the request code used for SSO
     * @param resultCode the result code returned by the SSO activity
     * @param data the result data returned by the SSO activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        addTwitterBtn.onActivityResult(requestCode, resultCode, data);
    }
}
