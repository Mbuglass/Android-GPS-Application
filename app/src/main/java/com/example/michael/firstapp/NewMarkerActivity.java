package com.example.michael.firstapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;


public class NewMarkerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "NewMarkerActivity";

    // Layout elements
    private EditText mTitle;
    private Spinner mCategory;
    private EditText mDescription;
    private ProgressBar mProgressBar;

    /**
     * On create method initialises views, progress bar, FirebaseAuth and sets focus on input field
     *
     * @param savedInstanceState allows activity to restore to a previous state using data stored
     *                           in this Bundle
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_newmarker);

        //Views
        mTitle = findViewById(R.id.txtTitle);
        mCategory = findViewById(R.id.spinner);
        mDescription = findViewById(R.id.txtDescription);
        mProgressBar = findViewById(R.id.progressBar);

        // Create an array adapter using string array and default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply adapter to the spinner
        mCategory.setAdapter(adapter);

        // Set progress bar to invisible by default
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * Called when place marker button is pressed, validates form and will create the marker if
     * input is valid
     *
     * @param view allows input values to be fetched and for progress bar to be manipulated
     */
    @Override
    public void onClick(View view) {

        // Show progress bar to indicate to user that a task is being carried out
        mProgressBar.setVisibility(View.VISIBLE);
        // Validates user input - if invalid will abort marker creation

        //Get input from title text box
        String title = mTitle.getText().toString().trim();

        //Get input from description text box
        String desc = mDescription.getText().toString().trim();

        // Validates inputs, will abort creation of marker if invalid
        if (!validateForm(title, desc)) {
            // Hide progress bar to indicate task has completed
            mProgressBar.setVisibility(View.GONE);
            return;
        }

        // Get values from category spinner
        String category = mCategory.getSelectedItem().toString().trim();

        // Pass values to createMarker class to add marker to database
        createMarker(title, category, desc);
    }

    /**
     * Validates user inputs, ensuring no input is left blank
     *
     * @param title marker title input
     * @param desc  marker description input
     * @return boolean indicating validity of inputs
     */
    private boolean validateForm(String title, String desc) {
        Log.d(TAG, "validateForm:Start");
        boolean valid = true;

        // Check if title input is empty
        if (TextUtils.isEmpty(title) || title == null) {
            Log.d(TAG, "validateForm:Invalid");
            // Inform user field is compulsory
            mTitle.setError("Required.");
            valid = false;
        }

        // Check if title input is empty
        if (TextUtils.isEmpty(desc) || desc == null) {
            Log.d(TAG, "validateForm:Invalid");
            // Inform user field is compulsory
            mDescription.setError("Required.");
            valid = false;
        }
        Log.d(TAG, "validateForm:Valid");
        return valid;
    }

    /**
     * Add marker information to FireStore cloud database
     *
     * @param title       marker title
     * @param category    marker category
     * @param description marker description
     */
    private void createMarker(String title, String category, String description) {
        Log.d(TAG, "createMarker:Start");

        // Database connection
        FirebaseFirestore mDB = FirebaseFirestore.getInstance();
        // Database path
        DocumentReference newMarkerRef = mDB.collection("Location")
                .document();

        // Get latitude and longitude from intent
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        Double lat = extras.getDouble("MARKER_LAT");
        Double lng = extras.getDouble("MARKER_LNG");

        // Create geo point for Location class
        GeoPoint geoPoint = new GeoPoint(lat, lng);

        // Create Location object to insert
        Location location = new Location();
        location.setCoordinates(geoPoint);
        location.setLocationName(title);
        location.setCategory(category);
        location.setDescription(description);
        location.setScore(0);
        location.setPlacedBy(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        // Insert into Firestore database
        Log.d(TAG, "createMarker:Inserting");
        newMarkerRef.set(location).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // If data insert successful write to log
                if (task.isSuccessful()) {
                    Log.d(TAG, "createMarker:Success");
                    Toast.makeText(NewMarkerActivity.this,
                            "Marker added successfully",
                            Toast.LENGTH_SHORT).show();
                } else { // If data insert fails write exception to log
                    Log.e(TAG, "createMarker:", task.getException());
                }
            }
        });
        // Hide progress bar to indicate task has completed
        mProgressBar.setVisibility(View.GONE);
    }
}