package com.example.michael.firstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends AppCompatActivity {
    /**
     * On create method initialises views, progress bar and FireStore
     *
     * @param savedInstanceState allows activity to restore to a previous state using data stored
     *                           in this Bundle
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_start);
    }

    /**
     * Called when the user clicks the Log In button
     * Creates intent for LoginActivity and begins the activity
     *
     * @param view allows source to be identified
     **/
    public void goToLogin(View view) {
        // Create intent
        Intent intent = new Intent(this, LoginActivity.class);
        // Opens new screen
        startActivity(intent);
    }

    /**
     * Called when the user clicks the Create Account button
     * Creates intent for CreateAccActivity and begins the activity
     *
     * @param view allows source to be identified
     **/
    public void goToCreateAccount(View view) {
        // Create intent
        Intent intent = new Intent(this, CreateAccActivity.class);
        // Opens new screen
        startActivity(intent);
    }
}
