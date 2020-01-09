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
