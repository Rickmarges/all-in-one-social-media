package com.example.dash.ui.login;

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

import com.example.dash.BuildConfig;
import com.example.dash.data.TwitterRepository;
import com.example.dash.R;
import com.example.dash.ui.account.ResetPasswordActivity;
import com.example.dash.ui.dashboard.DashboardActivity;
import com.example.dash.ui.register.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText emailTV, passwordTV;
    private Button loginBtn;
    private Button registerBtn;
    private Button forgotBtn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private int backCounter;
    private long startTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        mAuth = FirebaseAuth.getInstance();

        backCounter = 0;

        checkLoggedIn();

        initializeUI();

        loginBtn.setOnClickListener(view -> loginUserAccount());


        registerBtn.setOnClickListener(view -> startActivity(new Intent(this, RegisterActivity.class)));

        forgotBtn.setOnClickListener(view -> startActivity(new Intent(this, ResetPasswordActivity.class)));

    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeUI();
    }

    @Override
    public void onBackPressed(){
        if (backCounter < 1 || (System.currentTimeMillis() - startTime) / 1000 > 3) {
            startTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "Press again to exit", Toast.LENGTH_SHORT).show();
            backCounter++;
        } else {
            backCounter = 0;
            finishAffinity();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        backCounter = 0;
    }

    private void checkLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(this, DashboardActivity.class));
        }
    }

    private void loginUserAccount() {
        progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = emailTV.getText().toString();
        password = passwordTV.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailTV.setError("Required");
            Animation animShake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.hshake);
            emailTV.startAnimation(animShake);
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordTV.setError("Required");
            Animation animShake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.hshake);
            passwordTV.startAnimation(animShake);
            progressBar.setVisibility(View.GONE);
            return;
        }
        
        hideButtons();

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    if (Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified()) {
                        startActivity(new Intent(this, DashboardActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Email is not verified", Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                        showButtons();
                    }
                } else {
                    showButtons();
                    Toast.makeText(getApplicationContext(), "Login failed! Please try again", Toast.LENGTH_LONG).show();
                    Animation animShake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.hshake);
                    loginBtn.startAnimation(animShake);
                }
            }
        );
    }

    private void initializeUI() {
        emailTV = findViewById(R.id.email);
        passwordTV = findViewById(R.id.password);
        registerBtn = findViewById(R.id.register);
        loginBtn = findViewById(R.id.login);
        progressBar = findViewById(R.id.loading);
        forgotBtn = findViewById(R.id.forgotpwd);
        showButtons();
    }

    private void hideButtons(){
        loginBtn.setVisibility(View.GONE);
        registerBtn.setVisibility(View.GONE);
        forgotBtn.setVisibility(View.GONE);
    }

    private void showButtons(){
        loginBtn.setVisibility(View.VISIBLE);
        registerBtn.setVisibility(View.VISIBLE);
        forgotBtn.setVisibility(View.VISIBLE);
    }
}