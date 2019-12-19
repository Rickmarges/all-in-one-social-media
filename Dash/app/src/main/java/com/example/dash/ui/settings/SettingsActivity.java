package com.example.dash.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dash.R;
import com.example.dash.ui.dashboard.DashboardActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity {

    private Spinner sortingSpinner;
    private Spinner countrySpinner;
    private SharedPreferences myPrefs;
    private SharedPreferences.Editor prefsEditor;
    private ArrayAdapter<String> countryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        try {
            myPrefs = getSharedPreferences(DashboardActivity.getEncryptedEmail(), Context.MODE_PRIVATE);
        } catch (Exception e){
            Log.w("Warning", "Couldn't load preferences!");
        }

        String[] countries = new String[]{
                "US",
                "GB",
                "NL"
        };

        countryAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, countries);
        createSpinners();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            SharedPreferences sharedPreferences = getSharedPreferences(DashboardActivity.getEncryptedEmail(), Context.MODE_PRIVATE);
            int sortsSavedValue = sharedPreferences.getInt("RedditSort", 0);

            String countrySavedValue = sharedPreferences.getString("Country", "US");

            int spinnerPosition = countryAdapter.getPosition(countrySavedValue);

            sortingSpinner.setSelection(sortsSavedValue);
            countrySpinner.setSelection(spinnerPosition);
        }catch (Exception e){
            sortingSpinner.setSelection(0);
            countrySpinner.setSelection(0);
        }
    }

    private void createSpinners() {
        prefsEditor = myPrefs.edit();

        sortingSpinner = findViewById(R.id.spinnerRedditSort);
        countrySpinner = findViewById(R.id.spinnerCountry);

        String[] sorts = new String[]{
                "Hot",
                "Top",
                "Best",
                "Controversial",
                "New",
                "Rising"
        };


        ArrayAdapter<String> sortingAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, sorts);
        sortingAdapter.setDropDownViewResource(R.layout.spinner_item);
        sortingSpinner.setAdapter(sortingAdapter);
        sortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    prefsEditor.putInt("RedditSort", sortingSpinner.getSelectedItemPosition());
                    prefsEditor.apply();
                } catch (Exception e){
                    Log.w("PREFWARNING", "Couldn't save preference: " + e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        countryAdapter.setDropDownViewResource(R.layout.spinner_item);
        countrySpinner.setAdapter(countryAdapter);
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    prefsEditor.putString("Country", parent.getSelectedItem().toString());
                    prefsEditor.apply();
                } catch (Exception e){
                    Log.w("PREFWARNING", "Couldn't save preference: " + e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
