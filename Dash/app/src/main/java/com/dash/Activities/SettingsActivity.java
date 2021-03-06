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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dash.R;
import com.securepreferences.SecurePreferences;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * This activity is for showing the current settings of the account and for changing these settings.
 */

public class SettingsActivity extends AppCompatActivity {

    private Spinner mSortingSpinner, mCountrySpinner;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private ArrayAdapter<mCountries> mCountryAdapter;
    private enum mCountries {
        US,
        GB,
        NL
    }
    private enum mSortings {
        HOT,
        TOP,
        BEST,
        CONTROVERSIAL,
        NEW,
        RISING
    }

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

        //Initialize UI elements
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
    @Override
    protected void onResume() {
        super.onResume();
        // Retrieve the encrypted email from the currenctly authenticated FireBaseUser
        mSharedPreferences = new SecurePreferences(getApplicationContext(),
                "", DashboardActivity.getFilename());
        try {
            // Create the spinners
            createSpinners();
            // Retrieve the preference regarding the sorting of the Reddit Frontpage
            String sortsSavedValue = mSharedPreferences.getString("RedditSort", "BEST");
            // Retrieve the preference regarding the Country for Google Trends
            String countrySavedValue = mSharedPreferences.getString("Country", "NL");
            // Set spinner to the retrieved preference
            mSortingSpinner.setSelection(mSortings.valueOf(sortsSavedValue).ordinal());
            mCountrySpinner.setSelection(mCountries.valueOf(countrySavedValue).ordinal());
        } catch (IllegalStateException ise) {
            // If preference could not be loaded set spinner to default selection and log warning
            mSortingSpinner.setSelection(0);
            mCountrySpinner.setSelection(0);
            Log.w(getApplicationContext().toString(),
                    "Couldn't load preferences: " + ise.getMessage());
        }
    }

    /**
     * Creates the options which you can choose in the dropdown menus.
     */
    private void createSpinners() {
        // Open an editor to save to sharedpreferences
        mEditor = mSharedPreferences.edit();
        // Apply style and set the adapter to the Spinner
        ArrayAdapter<mSortings> sortingAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_item, mSortings.values());
        sortingAdapter.setDropDownViewResource(R.layout.spinner_item);
        mSortingSpinner.setAdapter(sortingAdapter);
        mSortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Sets the shared preference to the one the user selects in the dropdownmenu of Reddit
             *
             * @param parent The AdapterView where the selection happened
             * @param view The view within the AdapterView that was clicked
             * @param position The position of the view in the adapter
             * @param id The row id of the item that is selected
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mEditor.putString("RedditSort", parent.getSelectedItem().toString());
                    mEditor.apply();
                    Toast.makeText(getApplicationContext(),
                            "Saved Reddit sort preference!", LENGTH_SHORT).show();
                } catch (NullPointerException npe) {
                    Log.w(getApplicationContext().toString(),
                            "Couldn't save preference: " + npe.getMessage());
                }
            }

            /**
             * Invokes callback method when the selection disappears from this view.
             * The selection can disappear for instance when touch is activated or when the adapter becomes empty.
             *
             * @param parent The AdapterView that now contains no selected item.
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Create an adapter for the spinners
        mCountryAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_item, mCountries.values());
        // Apply style and set the adapter to the Spinner
        mCountryAdapter.setDropDownViewResource(R.layout.spinner_item);
        mCountrySpinner.setAdapter(mCountryAdapter);
        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Sets the shared preference to the one the user selects in the dropdownmenu of Trends
             *
             * @param parent The AdapterView where the selection happened
             * @param view The view within the AdapterView that was clicked
             * @param position The position of the view in the adapter
             * @param id The row id of the item that is selected
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mEditor.putString("Country", parent.getSelectedItem().toString());
                    mEditor.apply();
                    Toast.makeText(getApplicationContext(),
                            "Saved language preference!", LENGTH_SHORT).show();
                } catch (NullPointerException npe) {
                    Log.w(getApplicationContext().toString(),
                            "Couldn't save preference: " + npe);
                }
            }

            /**
             * Invokes callback method when the selection disappears from this view.
             * The selection can disappear for instance when touch is activated or when the adapter becomes empty.
             *
             * @param parent The AdapterView that now contains no selected item.
             */
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
