package com.dash.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dash.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class AccountActivity extends AppCompatActivity {
    private TextView mEmailAccountTV;
    private ImageButton mRedditIB;
    private Button mResetBtn;
    private boolean secondClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // Initialize UI parts
        init();

        secondClick = false;

        // Get user email and encrypt that email so it can be used for storage
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mEmailAccountTV.setText(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail());

        // Add an OnClickListener to the Reddit imagebutton
        // If the button is pressed it will send the user to the AddRedditAccountActivity
        mRedditIB.setOnClickListener(view -> startActivity(new Intent(this,
                AddRedditAccountActivity.class)));

        // Add an OnClickListener to the
        // If the button is pressed it will send the user to the AddRedditAccountActivity
        Animation animShake = AnimationUtils.loadAnimation(this, R.anim.hshake);

        mResetBtn.setOnClickListener(view -> {
            if (!secondClick) {
                mResetBtn.setText("Confirm");
                mResetBtn.setAnimation(animShake);
            }
            if (secondClick) {
                firebaseAuth.sendPasswordResetEmail(Objects.requireNonNull(firebaseAuth
                        .getCurrentUser()
                        .getEmail()))
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Sent reset email!",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error sending email.",
                                        Toast.LENGTH_LONG).show();
                            }
                            mResetBtn.setText(R.string.reset_password);
                            secondClick = false;
                        });
            }
            secondClick = true;
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void init() {
        mRedditIB = findViewById(R.id.add_reddit_btn);
        mEmailAccountTV = findViewById(R.id.emailAccount);
        mResetBtn = findViewById(R.id.resetpwd);
    }
}
