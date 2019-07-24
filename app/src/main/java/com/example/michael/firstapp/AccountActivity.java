package com.example.michael.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AccountActivity";

    private FirebaseFirestore mDb;

    // Assign the input fields to variables
    private TextView mEmail;
    private TextView mUsername;


    /**
     * On create method initialises views, progress bar and FireStore
     *
     * @param savedInstanceState allows activity to restore to a previous state using data stored
     *                           in this Bundle
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_account);

        // Database connection
        mDb = FirebaseFirestore.getInstance();

        // Views
        mEmail = findViewById(R.id.txtEmail);
        mUsername = findViewById(R.id.txtUsername);

        // Populate text boxes
        populateInfo();
    }

    /**
     * Populates text field with user email by getting the current user from FirebaseAuth
     */
    public void populateInfo() {
        // Get email address from FirebaseAuth and put value into text box
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mEmail.setText(userEmail);

        CollectionReference ref = mDb.collection("User");
        Log.d(TAG, "getUsername: current user: "
                + userEmail);
        Query query = ref.whereEqualTo("email", userEmail);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("TAG", "Document Id: " + document.getId());
                        mUsername.setText(document.getString("username"));
                        Log.d(TAG, "getUsername: Found");
                    }
                }
            }
        });
    }

    /**
     * Called when the user clicks the change password button
     * Will update the user's password to the value entered into the textbox
     * using FirebaseAuth to ensure password is not stored locally and is encrypted.
     *
     * @param view allows identification of onclick origin
     */
    @Override
    public void onClick(View view) {
        // Create intent
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        // Opens new screen
        startActivity(intent);
    }
}