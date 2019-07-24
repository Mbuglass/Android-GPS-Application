package com.example.michael.firstapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class ViewMarkerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ViewMarkerActivity";

    private FirebaseFirestore mDb;

    // Assign the input fields to variables
    private TextView mTitle;
    private TextView mCategory;
    private TextView mDescription;
    private TextView mPlacedBy;

    private Button mDeleteMarker;

    // Assign progress bar to variable
    private ProgressBar mProgressBar;

    /**
     * On create method initialises views, progress bar and FireStore
     *
     * @param savedInstanceState allows activity to restore to a previous state using data stored
     *                           in this Bundle
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_viewmarker);

        // Database Connection
        mDb = FirebaseFirestore.getInstance();

        // Set progress bar to invisible by default
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        // Views
        mTitle = findViewById(R.id.txtTitle);
        mCategory = findViewById(R.id.txtCategory);
        mDescription = findViewById(R.id.txtDescription);
        mPlacedBy = findViewById(R.id.txtPlacedBy);

        // Buttons
        mDeleteMarker = findViewById(R.id.btnDelete);
        mDeleteMarker.setVisibility(View.GONE);

        populateInfo();
    }

    /**
     * Populates layout elements with relevant info from FireStore database
     */
    public void populateInfo() {
        // Show progress bar to indicate to user that a task is being carried out
        mProgressBar.setVisibility(View.VISIBLE);

        // Get marker ID from intent extras
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String ref = extras.getString("MARKER_ID");

        assert ref != null;
        // Document path where ref is marker ID
        DocumentReference marker = mDb.collection("Location")
                .document(ref);

        // Get marker info
        marker.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // Avoid null pointer exception
                    assert document != null;
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        // Populate text fields with data
                        mTitle.setText(document.getString("locationName"));
                        mCategory.setText(document.getString("category"));
                        mDescription.setText(document.getString("description"));
                        // Get username from database
                        // Cannot display user email for privacy concerns
                        String userPlacedBy = document.getString("placedBy");
                        getUsername(userPlacedBy);
                        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail()
                                .toString();
                        if (currentUser.equals(userPlacedBy)) {
                            mDeleteMarker.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        // Hide progress bar to indicate task has completed
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * Populates placedBy text view with username of the user who placed the marker
     *
     * @param email contains email of user who placed the marker
     */
    private void getUsername(String email) {
        Log.d(TAG, "getUsername:Getting");
        FirebaseFirestore mDb = FirebaseFirestore.getInstance();
        CollectionReference ref = mDb.collection("User");
        String userEmail = email;
        Log.d(TAG, "getUsername: current user: "
                + userEmail);
        Query query = ref.whereEqualTo("email", userEmail);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("TAG", "Document Id: " + document.getId());
                        mPlacedBy.setText(document.getString("username"));
                        Log.d(TAG, "getUsername: Found");
                    }
                }
            }
        });
    }

    /**
     * Called when delete button is pressed (Button only visible to user who placed the marker
     * originally)
     * Will delete the marker from the database.
     *
     * @param view allows source of click to be identified and view components to be manipulated
     */
    @Override
    public void onClick(View view) {
        // Dialog interface used to confirm deletion in case of accidental button press
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    // onClick event for the confirmation dialogue
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            // YES button pressed
                            case DialogInterface.BUTTON_POSITIVE:

                                // Get marker ID from intent extras
                                Bundle extras = getIntent().getExtras();
                                assert extras != null;
                                String ref = extras.getString("MARKER_ID");

                                // Document path where ref is marker ID
                                DocumentReference marker = mDb.collection("Location")
                                        .document(ref);

                                // Delete document from FireStore database
                                marker.delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "Marker deleted successfully");
                                                Toast.makeText(ViewMarkerActivity.this,
                                                        "Marker deleted successfully",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                                Toast.makeText(ViewMarkerActivity.this,
                                                        "Error deleting document",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                // Hide progress bar to indicate task has completed
                                mProgressBar.setVisibility(View.GONE);
                                break;
                            // NO button pressed
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
        // Creates confirmation dialogue to ensure user is certain they want to delete the marker
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewMarkerActivity.this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}