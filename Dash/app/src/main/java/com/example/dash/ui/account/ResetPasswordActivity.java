package com.example.dash.ui.account;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dash.R;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        Button resetPwdBtn = findViewById(R.id.resetPassword);
        EditText emailField = findViewById(R.id.emailReset);

        resetPwdBtn.setOnClickListener(view -> resetPassword(emailField.getText().toString()));
    }

    private void resetPassword(String email){
        auth.sendPasswordResetEmail(email);
    }
}
