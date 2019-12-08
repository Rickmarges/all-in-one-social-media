package com.example.dash.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.dash.R;
import com.example.dash.ui.account.AccountActivity;
import com.example.dash.ui.login.LoginActivity;
import com.example.dash.ui.settings.SettingsActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.view.Gravity.FILL_HORIZONTAL;

public class DashboardActivity extends AppCompatActivity {
    private Button menuBtn;
    private FirebaseUser user;
    private int backCounter;
    private long startTime;

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

    private void initialize() {
        setContentView(R.layout.activity_dashboard);

        backCounter = 0;

        initializeUI();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
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
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void popupMenu(){
        PopupMenu popupMenu = new PopupMenu(DashboardActivity.this, menuBtn, FILL_HORIZONTAL);
        menuBtn.setOnTouchListener(popupMenu.getDragToOpenListener());
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());

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

}
