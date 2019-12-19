package com.example.dash.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dash.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class AccountActivity extends AppCompatActivity {

    protected FirebaseAuth mAuth;
    protected TextView emailAccount;
    protected ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // Get user credentials
        init();
        mAuth = FirebaseAuth.getInstance();
        emailAccount.setText(mAuth.getCurrentUser().getEmail());
        imageButton.findViewById(R.id.addredditbtn);

        imageButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddRedditAccount.class);
            startActivity(intent);
        });
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        emailAccount.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void init() {
        imageButton = findViewById(R.id.addredditbtn);
        emailAccount = findViewById(R.id.emailAccount);
    }
}
