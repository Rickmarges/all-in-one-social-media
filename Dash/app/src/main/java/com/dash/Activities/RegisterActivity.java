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

import java.util.Objects;

/**
 * Creates the register activity to register an account for Dash.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText mEmailET, mPasswordET, mPasswordCheckET;
    private Button mRegBtn, mLoginBtn;
    private ProgressBar mProgressBar;
    private FirebaseAuth mFirebaseAuth;
    private String mEmail, mPassword, mPasswordCheck;

    /**
     * Creates the activity, checks if there is a currenctly authenticated FireBaseUser
     * Sets onClickListeners on the register button and the back(login) button
     *
     * @param savedInstanceState saved instance of this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        mFirebaseAuth = FirebaseAuth.getInstance();

        // Initializes the UI elements
        init();

        // Set onClickListeners to Register and Login Buttons
        mRegBtn.setOnClickListener(view -> registerNewUser());

        mLoginBtn.setOnClickListener(view ->
                startActivity(new Intent(this, LoginActivity.class)));
    }

    /**
     * Redirects the user to the Loginactivity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
    }

    /**
     * Sends a verification email to the registered mail.
     */
    private void sendEmailVerification() {
        Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(),
                                "Verification sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Failed to send verification", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Registers the new user with the credentials from the EditTexts.
     */
    private void registerNewUser() {
        mProgressBar.setVisibility(View.VISIBLE);

        mEmail = mEmailET.getText().toString();
        mPassword = mPasswordET.getText().toString();
        mPasswordCheck = mPasswordCheckET.getText().toString();

        if (checkValidFields()) {
            mProgressBar.setVisibility(View.GONE);
            return;
        }

        //Creates the user in the Firebase Authentication
        mFirebaseAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mProgressBar.setVisibility(View.GONE);
                        sendEmailVerification();
                        startActivity(new Intent(this, LoginActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Registration failed! Please try again later", Toast.LENGTH_LONG)
                                .show();
                        Animation animShake = AnimationUtils.loadAnimation(this, R.anim.hshake);
                        mRegBtn.startAnimation(animShake);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * Checks if the register fields are valid.
     *
     * @return Returns true if all the fields are valid and false if one of the fields does not
     * meet the requirements.
     */
    private boolean checkValidFields() {
        Animation animShake = AnimationUtils.loadAnimation(this, R.anim.hshake);
        //Check if the email field is not empty
        if (TextUtils.isEmpty(mEmail)) {
            mEmailET.setError("Required");
            mEmailET.startAnimation(animShake);
            return true;
        }
        //Check if the email contains an at sign and a dot
        if (!mEmail.contains("@") && !mEmail.contains(".")) {
            mEmailET.setError("Please enter a valid email");
            mEmailET.startAnimation(animShake);
            return true;
        }
        //Check if the password field is not empty
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordET.setError("Required");
            mPasswordET.startAnimation(animShake);
            return true;
        }
        //Check if the password is more than six characters long
        if (mPassword.length() < 6) {
            mPasswordET.setError("Minimum password length is 6");
            mPasswordET.startAnimation(animShake);
            return true;
        }
        //Check if the password check field is not empty
        if (TextUtils.isEmpty(mPasswordCheck)) {
            mPasswordCheckET.setError("Required");
            mPasswordCheckET.startAnimation(animShake);
            return true;
        }
        //Check is the password check field is the same as the password field
        if (!mPassword.equals(mPasswordCheck)) {
            mPasswordET.setError("Doesn't match");
            mPasswordCheckET.setError("Doesn't match");
            mPasswordET.startAnimation(animShake);
            mPasswordCheckET.startAnimation(animShake);
            return true;
        }
        return false;
    }

    /**
     * Initializes the EditTexts and Buttons and links them to the corresponding layout elements
     */
    private void init() {
        mEmailET = findViewById(R.id.emailregister);
        mPasswordET = findViewById(R.id.passwordregister);
        mPasswordCheckET = findViewById(R.id.passwordconfirm);
        mRegBtn = findViewById(R.id.register);
        mLoginBtn = findViewById(R.id.login_navigation);
        mProgressBar = findViewById(R.id.loading_register);
    }
}