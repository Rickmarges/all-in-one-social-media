package com.dash.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.dash.Adapters.ViewPagerAdapter;
import com.dash.DashApp;
import com.dash.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;


@SuppressLint("ClickableViewAccessibility")
public class DashboardActivity extends AppCompatActivity {
    private Button mMenuBtn;
    private FirebaseUser mFirebaseUser;
    private int mBackCounter;
    private long mStartTime;
    private static String sEncryptedEmail;
    private TabLayout mTabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        checkLoggedIn();

        init();

        mMenuBtn.setOnClickListener(view -> popupMenu());
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(mTabLayout.getTabAt(0)).select();
        checkLoggedIn();
        sEncryptedEmail = encryptString(mFirebaseUser.getEmail());
        checkReddit();
    }

    @Override
    public void onBackPressed() {
        if (mBackCounter < 1 || (System.currentTimeMillis() - mStartTime) / 1000 > 3) {
            mStartTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "Press again to exit", Toast.LENGTH_SHORT).show();
            mBackCounter++;
        } else {
            mBackCounter = 0;
            finishAffinity();
        }
    }

    public static String getEncryptedEmail() throws IllegalStateException {
        if (!sEncryptedEmail.equals("")) {
            return sEncryptedEmail;
        } else {
            throw new IllegalStateException();
        }
    }

    private void init() {
        mBackCounter = 0;

        LinearLayout linearLayout = findViewById(R.id.linearlayout);
        linearLayout.requestFocus();

        mMenuBtn = findViewById(R.id.menubtn);

        ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

        mTabLayout = findViewById(R.id.tablayout);
        mTabLayout.setupWithViewPager(viewPager);
    }

    private void checkLoggedIn() {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void settings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void account() {
        startActivity(new Intent(this, AccountActivity.class));
    }

    private void signOut() {
        mFirebaseUser = null;
        FirebaseAuth.getInstance().signOut();
        Objects.requireNonNull(mTabLayout.getTabAt(0)).select();
        try {
            LinearLayout linearLayout = findViewById(R.id.trendsLayout);
            linearLayout.removeAllViews();
        } catch (NullPointerException npe) {
            Log.w(getApplicationContext().toString(), "No Trend views to delete." + npe.getMessage());
        }

        try {
            LinearLayout linearLayout = findViewById(R.id.redditLayout);
            linearLayout.removeAllViews();
        } catch (NullPointerException npe) {
            Log.w(getApplicationContext().toString(), "No Reddit views to delete." + npe.getMessage());
        }

        startActivity(new Intent(this, LoginActivity.class));
    }

    private void popupMenu() {
        PopupMenu popupMenu = new PopupMenu(DashboardActivity.this, mMenuBtn);
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
                    return false;
            }
            return true;
        });
        mMenuBtn.setOnTouchListener(popupMenu.getDragToOpenListener());
        popupMenu.show();
    }

    private void checkReddit() {
        try {
            //TODO loop through usernames, encrypt them and then reauthenticate
         //   DashApp.getTokenStore().getUsernames()

            SharedPreferences sharedPreferences = getSharedPreferences(getEncryptedEmail(), Context.MODE_PRIVATE);
            String redditUsername = sharedPreferences.getString("Reddit", "");

            if (!redditUsername.equals("")) {
                new ReauthenticationTask().execute(redditUsername);
            }
        } catch (NullPointerException npe) {
            Log.w(getApplicationContext().toString(), "No such user found." + npe.getMessage());
        }
    }

    private class ReauthenticationTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... usernames) {
            DashApp.getAccountHelper().switchToUser(usernames[0]);
            return null;
        }
    }

    public static String encryptString(String string) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = messageDigest.digest(string.getBytes());
            return new String(encodedhash);
        } catch (NullPointerException | NoSuchAlgorithmException e) {
            Log.w("Warinng", "Error encrypting string: " + e.getMessage());
            return "";
        }
    }
}
