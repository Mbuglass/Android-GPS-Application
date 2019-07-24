package com.example.michael.firstapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    // Filter variables
    public static final String KEY_SETTING_DARK_MODE = "settings_dark_mode";
    public static final String KEY_SETTINGS_LABELS = "settings_labels";

    /**
     * Creates FilterFragment with relevant keys
     *
     * @param savedInstanceState allows activity to restore to a previous state using data stored
     *                           in this Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}