package com.example.dash.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dash.R;
import com.example.dash.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText emailField;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_reset_password);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        auth = FirebaseAuth.getInstance();

        Button resetPwdBtn = findViewById(R.id.resetPassword);
        emailField = findViewById(R.id.emailReset);

        resetPwdBtn.setOnClickListener(view -> resetPassword());
    }

    private void resetPassword() {
        if (TextUtils.isEmpty(emailField.getText().toString())) {
            emailField.setError("Required");
            emailField.startAnimation(AnimationUtils.loadAnimation(this, R.anim.hshake));
        } else {
            auth.sendPasswordResetEmail(emailField.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Sent reset email!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Error sending email.", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}
