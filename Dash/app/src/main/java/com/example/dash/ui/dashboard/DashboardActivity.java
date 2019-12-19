package com.example.dash.ui.dashboard;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.dash.BuildConfig;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.dash.R;
import com.example.dash.ui.RedditApp;
import com.example.dash.ui.account.AccountActivity;
import com.example.dash.ui.login.LoginActivity;
import com.example.dash.ui.settings.SettingsActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.TweetUi;

import java.util.List;
import java.security.MessageDigest;

public class DashboardActivity extends AppCompatActivity {
    private Button menuBtn;
    private FirebaseUser user;
    private int backCounter;
    private long startTime;
    private static String encryptedEmail;
    private TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        checkLoggedIn();

        initialize();

        menuBtn.setOnClickListener(view -> popupMenu());
    }

    @Override
    public void onResume() {
        super.onResume();
        tabLayout.getTabAt(0).select();
        checkLoggedIn();
        encryptedEmail = encryptString(user.getEmail());
        checkReddit();
    }

    @Override
    public void onBackPressed() {
        if (backCounter < 1 || (System.currentTimeMillis() - startTime) / 1000 > 3) {
            startTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "Press again to exit", Toast.LENGTH_SHORT).show();
            backCounter++;
        } else {
            backCounter = 0;
            finishAffinity();
        }
    }

    public static String getEncryptedEmail() throws Exception{
        if (!encryptedEmail.equals("")) {
            return encryptedEmail;
        } else {
            throw new Exception();
        }
    }

    private void initialize() {
        TwitterConfig config = new TwitterConfig.Builder(this.getBaseContext())
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(BuildConfig.TWITTER_CONSUMER_ACCESS_TOKEN, BuildConfig.TWITTER_CONSUMER_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);
        TwitterCore.getInstance();
        TweetComposer.getInstance();
        TweetUi.getInstance();

        setContentView(R.layout.activity_dashboard);

        backCounter = 0;

        initializeUI();
    }

    private void checkLoggedIn() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void settings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void account() {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }

    private void signOut() {
        user = null;
        FirebaseAuth.getInstance().signOut();
        tabLayout.getTabAt(0).select();
        try {
            LinearLayout ll = findViewById(R.id.trendsLayout);
            ll.removeAllViews();
        } catch (Exception e) {
            System.out.println("No Trend views to delete." + e.getMessage());
        }

        try {
            LinearLayout ll = findViewById(R.id.redditLayout);
            ll.removeAllViews();
        } catch (Exception e) {
            System.out.println("No Views to delete." + e.getMessage());
        }

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void popupMenu() {
        PopupMenu popupMenu = new PopupMenu(DashboardActivity.this, menuBtn);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getTitle().toString()) {
                case "Settings":
                    settings();
                    break;
                case "Account":
                    account();
                    break;
                case "Sign Out":
                    signOut();
                    break;
                default:
                    break;
            }
            return true;
        });
        menuBtn.setOnTouchListener(popupMenu.getDragToOpenListener());
        popupMenu.show();
    }

    private void initializeUI() {
        LinearLayout myLayout = findViewById(R.id.linearlayout);
        myLayout.requestFocus();

        menuBtn = findViewById(R.id.menubtn);

        ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

        tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fragment = getSupportFragmentManager();
        if (fragment != null) {
            Fragment frag = fragment.findFragmentByTag("android:switcher:2131230896:2");
            frag.onActivityResult(requestCode, resultCode, data);
        }
        else Log.d("Twitter", "fragment is null");
    }
    
    private void checkReddit() {
        try {
            SharedPreferences sharedPref = getSharedPreferences(getEncryptedEmail(), Context.MODE_PRIVATE);
            String redditUsername = sharedPref.getString("Reddit", "");

            if (!redditUsername.equals("")) {
                new ReauthenticationTask().execute(redditUsername);
            }
        } catch (Exception e) {
            System.out.println("No such user found." + e.getMessage());
        }
    }

    private class ReauthenticationTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... usernames) {
            RedditApp.getAccountHelper().switchToUser(usernames[0]);
            return null;
        }
    }

    public String encryptString(String string) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(string.getBytes());
            return new String(encodedhash);
        } catch (Exception e) {
            return "";
        }
    }
}
