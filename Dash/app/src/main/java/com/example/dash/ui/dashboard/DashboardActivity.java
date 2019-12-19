package com.example.dash.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

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

import net.dean.jraw.models.PersistedAuthData;
import net.dean.jraw.oauth.DeferredPersistentTokenStore;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;


@SuppressLint("ClickableViewAccessibility")
public class DashboardActivity extends AppCompatActivity {
    private Button menuBtn;
    private FirebaseUser user;
    private int backCounter;
    private long startTime;
    private static String encryptedEmail;
    private static AsyncTask testTask;
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

    public static String getEncryptedEmail() {
        return encryptedEmail;
    }

    private void initialize() {
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

    private void checkReddit() {
        try {
            DeferredPersistentTokenStore tokenStore = RedditApp.getTokenStore();
            TreeMap<String, PersistedAuthData> data = new TreeMap<>(tokenStore.data());
            List<String> usernames = new ArrayList<>(data.keySet());

            SharedPreferences sharedPref = getSharedPreferences(getEncryptedEmail(), Context.MODE_PRIVATE);
            String redditUsername = sharedPref.getString("Reddit", "");

            for (int i = 0; i < usernames.size(); i++) {
                if (usernames.get(i).equals(redditUsername)) {
                    testTask = new ReauthenticationTask().execute(usernames.get(i));
                    break;
                }
            }
            //usernames.forEach(System.out::println);
        } catch (RuntimeException re) {
            System.out.println("No such user found." + re.getMessage());
        }
    }

    public static AsyncTask getTestTask(){
        return testTask;
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
            // TODO return other encrypted string
            return "";
        }
    }
}
