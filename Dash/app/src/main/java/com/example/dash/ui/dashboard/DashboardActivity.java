package com.example.dash.ui.dashboard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.dash.R;
import com.example.dash.ui.account.AccountActivity;
import com.example.dash.ui.login.LoginActivity;
import com.example.dash.ui.settings.SettingsActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class DashboardActivity extends AppCompatActivity {
    private Button menuBtn;
    private FirebaseUser user;
    private int backCounter;
    private long startTime;
    private String encryptedString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        initialize();

        menuBtn.setOnClickListener(view -> popupMenu());
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLoggedIn();
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

    @Nullable
    private FirebaseUser getUser() {
        return user;
    }

    public String getEncryptedString(){
        return encryptedString;
    }

    private void initialize() {
        setContentView(R.layout.activity_dashboard);

        backCounter = 0;

        initializeUI();
    }

    private void checkLoggedIn() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            FirebaseUser user = getUser();
            new Encrypt().execute(user.getEmail());
        }
    }

    public class Encrypt extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            encryptedString = encryptString(strings[0]);
            return null;
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
        LinearLayout ll = findViewById(R.id.trendsLayout);
        ll.removeAllViews();

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

        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private String encryptString(String string) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);

            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] input = string.getBytes();
            cipher.update(input);
            return cipher.doFinal().toString();
        } catch (Exception e) {
            // TODO return other encrypted string
            return "";
        }
    }
}
