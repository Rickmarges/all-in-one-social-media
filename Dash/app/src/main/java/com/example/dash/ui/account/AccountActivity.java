package com.example.dash.ui.account;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dash.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class AccountActivity extends AppCompatActivity {

    private TextView emailAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // Get user credentials
        init();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        emailAccount.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void init() {
        emailAccount = findViewById(R.id.emailAccount);
    }
}
