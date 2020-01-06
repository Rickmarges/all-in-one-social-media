package com.example.dash.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.dash.BuildConfig;
import com.example.dash.R;
import com.example.dash.data.TwitterRepository;
import com.example.dash.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.TweetUi;

import java.util.Objects;

public class AccountActivity extends AppCompatActivity {

    private TextView emailAccount;
    private ImageButton imageButton;
    private TwitterLoginButton addTwitterBtn;
    private Button resetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterConfig config = new TwitterConfig.Builder(this.getBaseContext())
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(BuildConfig.TWITTER_CONSUMER_ACCESS_TOKEN, BuildConfig.TWITTER_CONSUMER_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);
        TwitterCore.getInstance();
        TweetComposer.getInstance();
        TweetUi.getInstance();

        setContentView(R.layout.activity_account);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // Get user credentials
        init();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        emailAccount.setText(mAuth.getCurrentUser().getEmail());

        imageButton.setOnClickListener(view -> startActivity(new Intent(this, AddRedditAccount.class)));
        TwitterRepository.setTwitterCallback(this.getApplicationContext(), addTwitterBtn);

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
        addTwitterBtn = findViewById(R.id.addtwitterbtn);
        emailAccount = findViewById(R.id.emailAccount);
        resetBtn = findViewById(R.id.resetpwd);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        addTwitterBtn.onActivityResult(requestCode, resultCode, data);
    }
}
