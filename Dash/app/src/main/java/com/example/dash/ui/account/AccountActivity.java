package com.example.dash.ui.account;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.dash.R;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        overridePendingTransition(R.anim.slideleft, R.anim.slideright);
    }
}
