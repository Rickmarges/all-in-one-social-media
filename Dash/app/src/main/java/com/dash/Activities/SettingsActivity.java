/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Furthermore this project is licensed under the firebase.google.com/terms and
 * firebase.google.com/terms/crashlytics.
 *
 */

package com.dash.Activities;

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

/**
 * This activity is for showing the current settings of the account and for changing these settings.
 */

public class SettingsActivity extends AppCompatActivity {

    private Spinner mSortingSpinner, mCountrySpinner;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private ArrayAdapter<String> mCountryAdapter;
    private static final String[] sCountries = new String[]{"US", "GB", "NL"};

    /**
     * Creates the view in the activity.
     *
     * @param savedInstanceState saved instance of this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        //Initialize UI parts
        init();
    }

    /**
     * Returns to the previous activity if the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * Sets the dropdown menus to a default selection if none was saved before.
     * Saves the selection to the users account.
     */
    //TODO comments in the method
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

    /**
     * Creates the options which you can choose in the dropdown menus.
     */
    //TODO comments in the method
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

    /**
     * Initializes the Reddit sorting dropdown menu and the Country dropdown menu
     * and links them to their corresponding layout elements.
     */
    private void init() {
        mSortingSpinner = findViewById(R.id.spinnerRedditSort);
        mCountrySpinner = findViewById(R.id.spinnerCountry);
    }
}
