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

public class ResetPasswordActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private EditText mEmailET;
    private Button mResetPasswordBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_reset_password);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        mFirebaseAuth = FirebaseAuth.getInstance();

        init();

        mResetPasswordBtn.setOnClickListener(view -> resetPassword());
    }

    private void resetPassword() {
        if (TextUtils.isEmpty(mEmailET.getText().toString())) {
            mEmailET.setError("Required");
            mEmailET.startAnimation(AnimationUtils.loadAnimation(this, R.anim.hshake));
        } else {
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

    private void init() {
        mResetPasswordBtn = findViewById(R.id.resetPassword);
        mEmailET = findViewById(R.id.emailReset);
    }
}
