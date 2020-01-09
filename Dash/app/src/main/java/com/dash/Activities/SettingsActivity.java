package com.dash.Activities;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.dash.R;

public class SettingsActivity extends AppCompatActivity {

    private Spinner mSortingSpinner, mCountrySpinner;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private ArrayAdapter<String> mCountryAdapter;
    private static final String[] sCountries = new String[]{"US", "GB", "NL"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSharedPreferences = getSharedPreferences(DashboardActivity.getEncryptedEmail(),
                Context.MODE_PRIVATE);
        mCountryAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_item, sCountries);
        try {
            int sortsSavedValue = mSharedPreferences.getInt("RedditSort", 0);

            String countrySavedValue = mSharedPreferences.getString("Country", "NL");

            int spinnerPosition = mCountryAdapter.getPosition(countrySavedValue);

            mSortingSpinner.setSelection(sortsSavedValue);
            mCountrySpinner.setSelection(spinnerPosition);
        } catch (IllegalStateException ise) {
            mSortingSpinner.setSelection(0);
            mCountrySpinner.setSelection(0);
            Log.w(getApplicationContext().toString(),
                    "Couldn't load preferences: " + ise.getMessage());
        }

        createSpinners();
    }

    private void createSpinners() {
        mEditor = mSharedPreferences.edit();

        String[] sortings = new String[]{
                "Hot",
                "Top",
                "Best",
                "Controversial",
                "New",
                "Rising"
        };

        ArrayAdapter<String> sortingAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_item, sortings);
        sortingAdapter.setDropDownViewResource(R.layout.spinner_item);
        mSortingSpinner.setAdapter(sortingAdapter);
        mSortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mEditor.putInt("RedditSort", mSortingSpinner.getSelectedItemPosition());
                    mEditor.apply();
                } catch (NullPointerException npe) {
                    Log.w(getApplicationContext().toString(),
                            "Couldn't save preference: " + npe.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCountryAdapter.setDropDownViewResource(R.layout.spinner_item);
        mCountrySpinner.setAdapter(mCountryAdapter);
        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mEditor.putString("Country", parent.getSelectedItem().toString());
                    mEditor.apply();
                } catch (NullPointerException npe) {
                    Log.w(getApplicationContext().toString(),
                            "Couldn't save preference: " + npe);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void init() {
        mSortingSpinner = findViewById(R.id.spinnerRedditSort);
        mCountrySpinner = findViewById(R.id.spinnerCountry);
    }
}
