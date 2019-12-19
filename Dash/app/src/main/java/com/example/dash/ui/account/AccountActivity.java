package com.example.dash.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dash.R;
import com.example.dash.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class AccountActivity extends AppCompatActivity {

    private TextView emailAccount;
    private ImageButton imageButton;
    private Button resetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // Get user credentials
        init();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        emailAccount.setText(mAuth.getCurrentUser().getEmail());

        imageButton.setOnClickListener(view -> startActivity(new Intent(this, AddRedditAccount.class)));

        resetBtn.setOnClickListener(view -> {
            mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Sent reset email!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error sending email.", Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void init() {
        imageButton = findViewById(R.id.addredditbtn);
        emailAccount = findViewById(R.id.emailAccount);
        resetBtn = findViewById(R.id.resetpwd);
    }
}
