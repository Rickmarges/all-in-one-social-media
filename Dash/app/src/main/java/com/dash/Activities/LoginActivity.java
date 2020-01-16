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
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dash.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

/**
 * Creates the login Activity you see when starting the application.
 * It shows the link to the register Activity.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText mEmailET, mPasswordET;
    private Button mLoginBtn, mRegisterBtn, mForgotBtn;
    private ProgressBar mProgressBar;
    private FirebaseAuth mFirebaseAuth;
    private int mBackCounter;
    private long mStartTime;

    /**
     * Creates this activity, the login page.
     * It checks if the login data is correct and then logs the user in.
     *
     * @param savedInstanceState saved instance of this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        // Obtain the Firebase Authentication instance
        mFirebaseAuth = FirebaseAuth.getInstance();

        mBackCounter = 0;

        // Check if a user was already logged in.
        checkLoggedIn();

        // Initialize the UI
        init();

        // Add an onClickListener to the login button which executes the login method
        mLoginBtn.setOnClickListener(view -> loginUserAccount());

        // Add an onClickListener to the register button which redirects the user to the register page
        mRegisterBtn.setOnClickListener(view -> startActivity(new Intent(this,
                RegisterActivity.class)));

        // Add an onClickListener to the forgot password button which redirects the user to the forgot password page
        mForgotBtn.setOnClickListener(view -> startActivity(new Intent(this,
                ResetPasswordActivity.class)));
    }

    /**
     * Initializes the activity when started.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Initialize the UI
        init();
    }

    /**
     * Closes the application if the back button is pressed twice in three seconds.
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
     * Resets the back presses when the application is closed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBackCounter = 0;
    }

    /**
     * Checks if a user was already logged in.
     */
    private void checkLoggedIn() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && checkVerified()) {
            startActivity(new Intent(this, DashboardActivity.class));
        }
    }

    /**
     * Logs the user in if the correct login data has been filled in.
     */
    private void loginUserAccount() {
        mProgressBar.setVisibility(View.VISIBLE);

        // Set email and password from the input fields
        String email, password;
        email = mEmailET.getText().toString();
        password = mPasswordET.getText().toString();

        //Check if the fields are valid
        if (checkValidFields(email, password)) {
            return;
        }

        // Hide the buttons for login, register and forgot password so they can't be clicked during loading
        hideButtons();

        //Use the Firebase authentication to sign in and login to the Dashboard
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    mProgressBar.setVisibility(View.GONE);
                    // Check if the task is successful. If it is the user is logged in
                    if (task.isSuccessful()) {
                        // Check if the user has verified his/her email account
                        if (checkVerified()) {
                            // If
                            startActivity(new Intent(this, DashboardActivity.class));
                        } else {
                            Objects.requireNonNull(mFirebaseAuth.getCurrentUser())
                                    .sendEmailVerification();
                            Toast.makeText(getApplicationContext(), "Email is not verified",
                                    Toast.LENGTH_LONG)
                                    .show();
                            mFirebaseAuth.signOut();
                            showButtons();
                        }
                    } else {
                        showButtons();
                        Toast.makeText(getApplicationContext(), "Login failed! Please try again",
                                Toast.LENGTH_LONG)
                                .show();
                        Animation animShake = AnimationUtils.loadAnimation(this, R.anim.hshake);
                        mLoginBtn.startAnimation(animShake);
                    }
                });
    }

    /**
     * Checks if the email from the user has been verified
     *
     * @return Returns the boolean, true if the email has been verified and false if the mail has not been verified.
     */
    private boolean checkVerified() {
        return Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).isEmailVerified();
    }

    /**
     * Checks if the login fields are correctly entered.
     *
     * @param email    The email which the user wants to login with
     * @param password The password which the user wants to login with
     * @return Returns the boolean true if the fields are valid and false if the fields are not correct
     */
    private boolean checkValidFields(String email, String password) {
        //Checks if the email field is not left empty
        if (TextUtils.isEmpty(email)) {
            mEmailET.setError("Required");
            Animation animShake = AnimationUtils.loadAnimation(this, R.anim.hshake);
            mEmailET.startAnimation(animShake);
            mProgressBar.setVisibility(View.GONE);
            return true;
        }
        //Checks if the email contains an at sign and if it contains a dot
        if (!email.contains("@") && !email.contains(".")) {
            mEmailET.setError("Please enter a valid email");
            Animation animShake = AnimationUtils.loadAnimation(this, R.anim.hshake);
            mEmailET.startAnimation(animShake);
            mProgressBar.setVisibility(View.GONE);
            return true;
        }
        //Checks if the password field is not left empty
        if (TextUtils.isEmpty(password)) {
            mPasswordET.setError("Required");
            Animation animShake = AnimationUtils.loadAnimation(this, R.anim.hshake);
            mPasswordET.startAnimation(animShake);
            mProgressBar.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    /**
     * Initializes the input fields and the buttons in the layout and links them to their
     * corresponding layout elements.
     */
    private void init() {
        mEmailET = findViewById(R.id.email);
        mPasswordET = findViewById(R.id.password);
        mRegisterBtn = findViewById(R.id.register);
        mLoginBtn = findViewById(R.id.login);
        mProgressBar = findViewById(R.id.loading);
        mForgotBtn = findViewById(R.id.forgotpwd);
        showButtons();
    }

    /**
     * Hides the login, register and forgot password buttons.
     */
    private void hideButtons() {
        mLoginBtn.setVisibility(View.GONE);
        mRegisterBtn.setVisibility(View.GONE);
        mForgotBtn.setVisibility(View.GONE);
    }

    /**
     * Shows the login, register and forgot password buttons.
     */
    private void showButtons() {
        mLoginBtn.setVisibility(View.VISIBLE);
        mRegisterBtn.setVisibility(View.VISIBLE);
        mForgotBtn.setVisibility(View.VISIBLE);
    }
}