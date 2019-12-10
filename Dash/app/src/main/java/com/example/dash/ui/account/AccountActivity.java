package com.example.dash.ui.account;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dash.R;
import com.google.firebase.auth.FirebaseAuth;

public class AccountActivity extends AppCompatActivity {

    protected FirebaseAuth mAuth;
    protected TextView emailAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // Get user credentials
        init();
        mAuth = FirebaseAuth.getInstance();
        emailAccount.setText(mAuth.getCurrentUser().getEmail());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void init() {
        emailAccount = findViewById(R.id.emailAccount);
    }
}
