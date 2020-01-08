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

        mRegBtn.setOnClickListener(view -> {
            mEmail = mEmailET.getText().toString();
            mPassword = mPasswordET.getText().toString();
            mPasswordCheck = mPasswordCheckET.getText().toString();

            if (mPassword.equals(mPasswordCheck)) {
                registerNewUser();
            } else {
                Animation animShake = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.hshake);
                mPasswordET.setError("Doesn't match");
                mPasswordCheckET.setError("Doesn't match");
                mPasswordET.startAnimation(animShake);
                mPasswordCheckET.startAnimation(animShake);
            }
        });

        mLoginBtn.setOnClickListener(view -> startActivity(new Intent(this, LoginActivity.class)));
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
                        Toast.makeText(getApplicationContext(), "Verification sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to send verification", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void registerNewUser() {
        mProgressBar.setVisibility(View.VISIBLE);

        if (checkValidFields()) {
            return;
        }

        mFirebaseAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mProgressBar.setVisibility(View.GONE);
                        sendEmailVerification();
                        startActivity(new Intent(this, LoginActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                        Animation animShake = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.hshake);
                        mRegBtn.startAnimation(animShake);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    private boolean checkValidFields() {
        if (TextUtils.isEmpty(mEmailET.getText().toString())) {
            mEmailET.setError("Required");
            Animation animShake = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.hshake);
            mEmailET.startAnimation(animShake);
            mProgressBar.setVisibility(View.GONE);
            return true;
        }
        if (!mEmailET.getText().toString().contains("@") && !mEmailET.getText().toString().contains(".")) {
            mEmailET.setError("Please enter a valid email");
            Animation animShake = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.hshake);
            mEmailET.startAnimation(animShake);
            mProgressBar.setVisibility(View.GONE);
            return true;
        }
        if (TextUtils.isEmpty(mPasswordET.getText().toString())) {
            mPasswordET.setError("Required");
            Animation animShake = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.hshake);
            mPasswordET.startAnimation(animShake);
            mProgressBar.setVisibility(View.GONE);
            return true;
        }
        if (mPasswordET.getText().toString().length() < 6) {
            mPasswordET.setError("Minimum password length is 6");
            Animation animShake = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.hshake);
            mPasswordET.startAnimation(animShake);
            mProgressBar.setVisibility(View.GONE);
            return true;
        }
        if (TextUtils.isEmpty(mPasswordCheckET.getText().toString())) {
            mPasswordCheckET.setError("Required");
            Animation animShake = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.hshake);
            mPasswordCheckET.startAnimation(animShake);
            mProgressBar.setVisibility(View.GONE);
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