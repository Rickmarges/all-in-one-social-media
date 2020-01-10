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

public class RegisterActivity extends AppCompatActivity {

    private EditText mEmailET, mPasswordET, mPasswordCheckET;
    private Button mRegBtn, mLoginBtn;
    private ProgressBar mProgressBar;
    private FirebaseAuth mFirebaseAuth;
    private String mEmail, mPassword, mPasswordCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        mFirebaseAuth = FirebaseAuth.getInstance();

        init();

        mRegBtn.setOnClickListener(view -> registerNewUser());

        mLoginBtn.setOnClickListener(view ->
                startActivity(new Intent(this, LoginActivity.class)));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
    }

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

    private void registerNewUser() {
        mProgressBar.setVisibility(View.VISIBLE);

        mEmail = mEmailET.getText().toString();
        mPassword = mPasswordET.getText().toString();
        mPasswordCheck = mPasswordCheckET.getText().toString();

        if (checkValidFields()) {
            mProgressBar.setVisibility(View.GONE);
            return;
        }

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

    private boolean checkValidFields() {
        Animation animShake = AnimationUtils.loadAnimation(this, R.anim.hshake);
        if (TextUtils.isEmpty(mEmail)) {
            mEmailET.setError("Required");
            mEmailET.startAnimation(animShake);
            return true;
        }
        if (!mEmail.contains("@") && !mEmail.contains(".")) {
            mEmailET.setError("Please enter a valid email");
            mEmailET.startAnimation(animShake);
            return true;
        }
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordET.setError("Required");
            mPasswordET.startAnimation(animShake);
            return true;
        }
        if (mPassword.length() < 6) {
            mPasswordET.setError("Minimum password length is 6");
            mPasswordET.startAnimation(animShake);
            return true;
        }
        if (TextUtils.isEmpty(mPasswordCheck)) {
            mPasswordCheckET.setError("Required");
            mPasswordCheckET.startAnimation(animShake);
            return true;
        }
        if (!mPassword.equals(mPasswordCheck)) {
            mPasswordET.setError("Doesn't match");
            mPasswordCheckET.setError("Doesn't match");
            mPasswordET.startAnimation(animShake);
            mPasswordCheckET.startAnimation(animShake);
            return true;
        }
        return false;
    }

    private void init() {
        mEmailET = findViewById(R.id.emailregister);
        mPasswordET = findViewById(R.id.passwordregister);
        mPasswordCheckET = findViewById(R.id.passwordconfirm);
        mRegBtn = findViewById(R.id.register);
        mLoginBtn = findViewById(R.id.login_navigation);
        mProgressBar = findViewById(R.id.loading_register);
    }
}