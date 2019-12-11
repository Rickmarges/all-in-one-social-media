package com.example.dash.ui.register;

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

import com.example.dash.R;
import com.example.dash.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailTV, passwordTV, passwordTV2;
    private Button regBtn;
    private Button loginBtn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        mAuth = FirebaseAuth.getInstance();

        initializeUI();

        regBtn.setOnClickListener(view -> {
            if (passwordTV.getText().toString().equals(passwordTV2.getText().toString())) {
                registerNewUser();
            } else {
                Animation animShake = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.hshake);
                passwordTV.setError("Doesn't match");
                passwordTV2.setError("Doesn't match");
                passwordTV.startAnimation(animShake);
                passwordTV2.startAnimation(animShake);
                return;
            }
        });

        loginBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void sendEmailVerification(){
        mAuth.getCurrentUser().sendEmailVerification()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Verification sent",  Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to send verification", Toast.LENGTH_LONG).show();
                }
            }
        );
    }

    private void registerNewUser() {
        progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = emailTV.getText().toString();
        password = passwordTV.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailTV.setError("Required");
            Animation animShake = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.hshake);
            emailTV.startAnimation(animShake);
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordTV.setError("Required");
            Animation animShake = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.hshake);
            passwordTV.startAnimation(animShake);
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordTV2.setError("Required");
            Animation animShake = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.hshake);
            passwordTV2.startAnimation(animShake);
            progressBar.setVisibility(View.GONE);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    sendEmailVerification();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                    Animation animShake = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.hshake);
                    regBtn.startAnimation(animShake);
                    progressBar.setVisibility(View.GONE);
                }
            }
        );
    }

    private void initializeUI() {
        emailTV = findViewById(R.id.emailregister);
        passwordTV = findViewById(R.id.passwordregister);
        passwordTV2 = findViewById(R.id.passwordconfirm);
        regBtn = findViewById(R.id.register);
        loginBtn = findViewById(R.id.login_navigation);
        progressBar = findViewById(R.id.loading_register);
    }
}