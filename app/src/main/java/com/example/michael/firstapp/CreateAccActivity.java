package com.example.michael.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class CreateAccActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_MESSAGE = "com.example.michael.firstapp.MESSAGE";
    private static final String TAG = "CreateAccActivity";

    //Layout Elements
    private EditText mEmail;
    private EditText mUsername;
    private EditText mPassword;
    private ProgressBar mProgressBar;

    // [Start declare_auth]
    private FirebaseAuth mAuth;
    // [End declare_auth]

    /**
     * On create method initialises views, progress bar, FirebaseAuth and sets focus on input field
     *
     * @param savedInstanceState allows activity to restore to a previous state using data stored
     *                           in this Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_createaccount);

        // Views
        mEmail = findViewById(R.id.txtEmail);
        mUsername = findViewById(R.id.txtUsername);
        mPassword = findViewById(R.id.txtPassword);
        mProgressBar = findViewById(R.id.progressBar);

        // Set focus to Username, as first input to appear
        mUsername.requestFocus();

        // Buttons
        findViewById(R.id.btnCreateAcc).setOnClickListener(this);

        // Set progress bar to invisible by default
        mProgressBar.setVisibility(View.GONE);

        // [Start initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [End initialize_auth]
    }

    /**
     * Called when the user taps the Send button, calls createAccount
     *
     * @param view used to allow method to get values from input fields
     */
    @Override
    public void onClick(View view) {
        // Do something in response to button

        String email = mEmail.getText().toString().trim();
        String username = mUsername.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        // Validates user input - if invalid will abort account creation
        if (!validateForm(email, username, password)) {
            return;
        }
        checkUsernameTaken(email, password, username);

    }

    /**
     * Creates intent for MapsActivity and will open the activity
     */
    public void goToMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    /**
     * Method to add account to FirebaseAuth and FireStore
     * Calls validateForm, createUsername,sendEmailVerification, goToMap
     *
     * @param email    contains input for user's email, will be validated and added to FirebaseAuth
     *                 and FireStore for referencing username
     * @param password contains input for user's password, will be validated and added to
     *                 FirebaseAuth
     * @param username contains input for user's username, will be validated and added to
     *                 FireStore alongside email.
     */
    public void createAccount(final String email, String password, final String username) {
        Log.d(TAG, "createAccount: " + email);

        // Show progress bar to indicate to user that a task is being carried out
        mProgressBar.setVisibility(View.VISIBLE);

        // [Start create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Successful sign in
                            Log.d(TAG, "createUserWithEmailAndPassword:Success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            createUsername(email, username);
                            sendEmailVerification();
                            goToMap();
                        } else {
                            // Unsuccessful sign in
                            Log.w(TAG, "createUserWithEmailAndPassword:Failure",
                                    task.getException());
                            //Inform user of failure
                            Toast.makeText(CreateAccActivity.this,
                                    "Authentication Failed - Email may already be taken",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // Hide progress bar to indicate task has completed
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
        // [End create_user_with_email]
    }

    /**
     * Email validation using JavaMail Android (Official Java Email Package)
     * https://javaee.github.io/javamail/Android
     *
     * @param email contains user's email
     * @return boolean indicating if user's email is considered to be valid
     */
    public static boolean isValidEmailAddress(String email) {
        boolean valid = true;
        if (email == null) {
            valid = false;
        } else {
            try {
                InternetAddress emailAddress = new InternetAddress(email);
                // validates email
                emailAddress.validate();
            } catch (AddressException ex) {
                // If Email is invalid, returns false
                valid = false;
            }
        }
        // If Email is valid, returns true
        return valid;
    }

    /**
     * Validates user inputs, ensuring no input is left blank, and that password is at least
     * 6 characters long
     *
     * @param email    input email
     * @param username input username
     * @param password input password
     * @return boolean indicating validity of inputs
     */
    private boolean validateForm(String email, String username, String password) {
        boolean valid = true;
        // Check if email input is empty
        if (TextUtils.isEmpty(email) || email == null) {
            // Inform user field is compulsory
            mEmail.setError("Required.");
            valid = false;
        } else if (!isValidEmailAddress(email)) {
            // Inform user Email is not valid
            mEmail.setError("Invalid Email Address");
            valid = false;
        } else {
            mEmail.setError(null);
        }
        // Check if username input is empty
        if (TextUtils.isEmpty(username) || username == null) {
            // Inform user field is compulsory
            mUsername.setError("Required.");
            valid = false;
        } else if (username.length() > 20 || username.length() < 4) {
            mUsername.setError("Must be less than 20 characters");
            valid = false;
        } else {
            mUsername.setError(null);
        }
        // Check if password input is empty
        if (TextUtils.isEmpty(password) || password == null) {
            // Inform user field is compulsory
            mPassword.setError("Required.");
            valid = false;
        } else if (password.length() < 6 || password.length() > 20) {
            // Inform user password is not long enough or is too long
            // Authentication will fail if password is shorter than this value
            // as Firebase requires this minimum password length
            mPassword.setError("Password should be between 6 and 20 characters");
            valid = false;
        } else {
            mPassword.setError(null);
        }
        return valid;
    }

    /**
     * Checks if username is already taken
     * @param email input email
     * @param password input password
     * @param username input username
     */
    private void checkUsernameTaken(final String email, final String password,
                                    final String username){
        // Database connection
        FirebaseFirestore mDb = FirebaseFirestore.getInstance();
        // Collection to access
        CollectionReference users = mDb.collection("User");
        // Create query to check for username
        Query query = users.whereEqualTo("username", username);
        // Execute query
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                // Username exists
                if (task.getResult().size()!=0) {
                    Toast.makeText(CreateAccActivity.this,
                            "Username already taken",
                            Toast.LENGTH_SHORT).show();
                    mUsername.setError("Username taken");
                    // Username does not exist
                } else {
                    createAccount(email, password, username);
                }
            }
        });
    }

    /**
     * Will send a verification email to the email address provided by the user and inform
     * the user that the email has been sent
     */
    private void sendEmailVerification() {
        // Send Email verification
        // [Start send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        // Resolves NullPointerException
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        if (task.isSuccessful()) {
                            // On success, show Toast to indicate email was sent
                            Toast.makeText(CreateAccActivity.this,
                                    "Verification Email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // On failure, show Toast to indicate email was not sent
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(CreateAccActivity.this,
                                    "Failed to send verification Email",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [End send_email_verification]
    }

    /**
     * Creates a FireStore document containing the user's email and username, so that usernames can
     * be linked to email addresses for later reference
     *
     * @param email    contains user's email
     * @param username contain's user's desired username
     */
    private void createUsername(String email, String username) {
        Log.d(TAG, "createUsername:Start");
        // Database connection
        FirebaseFirestore mDb = FirebaseFirestore.getInstance();
        // Database path
        DocumentReference newUserRef = mDb.collection("User")
                .document();

        // Create user object to insert
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);

        // Insert user to Firestore Database
        Log.d(TAG, "createUsername:Inserting");
        newUserRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // If data insert successful write to log
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUsername:Success");
                } else { // If data insert fails write exception to log
                    Log.e(TAG, "createUsername:", task.getException());
                }
            }
        });
    }
}