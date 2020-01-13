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
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dash.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Creates the reset password activity to reset the password for an account if the user forgot its password
 */
public class ResetPasswordActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private EditText mEmailET;
    private Button mResetPasswordBtn;

    /**
     * Creates this activity, the reset password activity.
     *
     * @param savedInstanceState saved instance of this activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_reset_password);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        mFirebaseAuth = FirebaseAuth.getInstance();

        init();

        mResetPasswordBtn.setOnClickListener(view -> resetPassword());
    }

    /**
     * Reset the password and replaces the old password in Firebase with the new password
     */
    private void resetPassword() {
        //Check if the email field is left empty
        if (TextUtils.isEmpty(mEmailET.getText().toString())) {
            mEmailET.setError("Required");
            mEmailET.startAnimation(AnimationUtils.loadAnimation(this, R.anim.hshake));
        } else {
            //Send the password reset email from Firebase
            mFirebaseAuth.sendPasswordResetEmail(mEmailET.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    "Sent reset email!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(this, LoginActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Error sending email.", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    /**
     * Initializes the input field and the button in the layout and links them to their
     * corresponding layout elements.
     */
    private void init() {
        mResetPasswordBtn = findViewById(R.id.resetPassword);
        mEmailET = findViewById(R.id.emailReset);
    }
}
