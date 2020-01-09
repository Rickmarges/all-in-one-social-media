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


public class LoginActivity extends AppCompatActivity {

    private EditText mEmailET, mPasswordET;
    private Button mLoginBtn, mRegisterBtn, mForgotBtn;
    private ProgressBar mProgressBar;
    private FirebaseAuth mFirebaseAuth;
    private int mBackCounter;
    private long mStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        // Obtain the Firebase Authentication instance
        mFirebaseAuth = FirebaseAuth.getInstance();

        mBackCounter = 0;

        checkLoggedIn();

        init();

        mLoginBtn.setOnClickListener(view -> loginUserAccount());

        mRegisterBtn.setOnClickListener(view -> startActivity(new Intent(this,
                RegisterActivity.class)));

        mForgotBtn.setOnClickListener(view -> startActivity(new Intent(this,
                ResetPasswordActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBackCounter = 0;
    }

    private void checkLoggedIn() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && checkVerified()) {
            startActivity(new Intent(this, DashboardActivity.class));
        }
    }

    // TODO comments en aanpassen
    private void loginUserAccount() {
        mProgressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = mEmailET.getText().toString();
        password = mPasswordET.getText().toString();

        if (checkValidFields(email, password)) {
            return;
        }

        hideButtons();

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    mProgressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        if (checkVerified()) {
                            startActivity(new Intent(this, DashboardActivity.class));
                        } else {
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

    private boolean checkVerified() {
        return Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).isEmailVerified();
    }

    private boolean checkValidFields(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            mEmailET.setError("Required");
            Animation animShake = AnimationUtils.loadAnimation(this, R.anim.hshake);
            mEmailET.startAnimation(animShake);
            mProgressBar.setVisibility(View.GONE);
            return true;
        }
        if (!email.contains("@") && !email.contains(".")) {
            mEmailET.setError("Please enter a valid email");
            Animation animShake = AnimationUtils.loadAnimation(this, R.anim.hshake);
            mEmailET.startAnimation(animShake);
            mProgressBar.setVisibility(View.GONE);
            return true;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordET.setError("Required");
            Animation animShake = AnimationUtils.loadAnimation(this, R.anim.hshake);
            mPasswordET.startAnimation(animShake);
            mProgressBar.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    private void init() {
        mEmailET = findViewById(R.id.email);
        mPasswordET = findViewById(R.id.password);
        mRegisterBtn = findViewById(R.id.register);
        mLoginBtn = findViewById(R.id.login);
        mProgressBar = findViewById(R.id.loading);
        mForgotBtn = findViewById(R.id.forgotpwd);
        showButtons();
    }

    private void hideButtons() {
        mLoginBtn.setVisibility(View.GONE);
        mRegisterBtn.setVisibility(View.GONE);
        mForgotBtn.setVisibility(View.GONE);
    }

    private void showButtons() {
        mLoginBtn.setVisibility(View.VISIBLE);
        mRegisterBtn.setVisibility(View.VISIBLE);
        mForgotBtn.setVisibility(View.VISIBLE);
    }
}