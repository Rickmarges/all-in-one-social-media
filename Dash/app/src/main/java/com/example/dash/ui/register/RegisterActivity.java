package com.example.dash.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dash.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.example.dash.R;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.*;

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

        mAuth = FirebaseAuth.getInstance();

        initializeUI();

        regBtn.setOnClickListener(view -> {
                if (passwordTV.getText().toString().equals(passwordTV2.getText().toString())) {
                    registerNewUser();
                    Log.d("Success", "Registered");
                } else {
                    Log.d("Failed", "Password doesn't match");
                    Toast.makeText(getApplicationContext(), "Password doesn't match", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        );

        loginBtn.setOnClickListener(view -> {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        );
    }

    private void registerNewUser() {
        progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = emailTV.getText().toString();
        password = passwordTV.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Message", "Succesful");
                            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Log.d("Message", "FUCKED");
                            Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                )
        .addOnFailureListener(exception -> {
                Log.d("Fail", exception.getMessage());
            }
        );
    }

    private void initializeUI() {
        emailTV = findViewById(R.id.emailregister);
        passwordTV = findViewById(R.id.passwordregister);
        passwordTV2 = findViewById(R.id.passwordconfirm);
        regBtn = findViewById(R.id.signup);
        loginBtn = findViewById(R.id.login_navigation);
        progressBar = findViewById(R.id.loading);
    }
}