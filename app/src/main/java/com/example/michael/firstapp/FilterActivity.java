package com.example.michael.firstapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FilterActivity extends AppCompatActivity {

    // Filter variables
    public static final String KEY_FILTER_PREF_MEMORIAl = "filter_memorial";
    public static final String KEY_FILTER_PREF_STATUE = "filter_statue";
    public static final String KEY_FILTER_PREF_BUILDING = "filter_building";
    public static final String KEY_FILTER_PREF_PARK = "filter_park";
    public static final String KEY_FILTER_PREF_PLAY_AREA = "filter_play_area";
    public static final String KEY_FILTER_PREF_STREET_ART = "filter_street_art";
    public static final String KEY_FILTER_PREF_TOILET = "filter_toilet";
    public static final String KEY_FILTER_PREF_BIN = "filter_bin";
    public static final String KEY_FILTER_PREF_MISC = "filter_misc";

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
                .replace(android.R.id.content, new FilterFragment())
                .commit();
    }
}
