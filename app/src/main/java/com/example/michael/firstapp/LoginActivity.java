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

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_MESSAGE = "com.example.michael.firstapp.MESSAGE";
    private static final String TAG = "LoginActivity";

    //Layout Elements
    private EditText mEmail;
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
        setContentView(R.layout.act_login);

        // Views
        mEmail = findViewById(R.id.txtEmail);
        mPassword = findViewById(R.id.txtPassword);
        mProgressBar = findViewById(R.id.progressBar);

        // Set focus to Email, as first input to appear
        mEmail.requestFocus();

        // Buttons
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.txtResetPassword).setOnClickListener(this);

        // Set progress bar to invisible by default
        mProgressBar.setVisibility(View.GONE);

        // [Start initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [End initialize_auth]
    }

    /**
     * Called when the user taps a button button
     * If the login button was pressed, @see login method is called
     * if the reset passwrod button was pressed, the @see resetPassword method is called
     *
     * @param view Allows method to check origin of onClick call
     */
    @Override
    public void onClick(View view) {
        // Do something in response to button
        switch (view.getId()) {
            // If  the login button was pressed
            case R.id.btnLogin:
                login(mEmail.getText().toString().trim(),
                        mPassword.getText().toString().trim());
                break;
            case R.id.txtResetPassword: // if the reset password button was pressed
                resetPassword(mEmail.getText().toString().trim());
        }
    }

    /**
     * Creates intent for MapsActivity and will open the activity
     */
    public void goToMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    /**
     * Attempts to log the user in using email and password
     *
     * @param email    contains input email
     * @param password contains input password
     */
    public void login(String email, String password) {
        Log.d(TAG, "login:" + email);

        // Show progress bar to indicate to user that a task is being carried out
        mProgressBar.setVisibility(View.VISIBLE);

        // Validate user input - if invalid will abort account creation
        if (!validateForm(email, password)) {
            // Hide progress bar to indicate task has completed
            mProgressBar.setVisibility(View.GONE);
            return;
        }

        // [Start sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in successful
                            Log.d(TAG, "signInWithEmailAndPassword:Success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            goToMap();
                        } else {
                            // Sign in unsuccessful
                            Log.w(TAG, "signInWithEmailAndPassword:failed",
                                    task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // Hide progress bar to indicate task has completed
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
        // [End sign_in_with_email]
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
        // TODO: Add to testing null value produced error without below statement
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
     * 6 characters long as well as validating email address
     *
     * @return boolean indicating validity of inputs
     */
    public boolean validateForm(String email, String password) {
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
        // Check if password input is empty
        if (TextUtils.isEmpty(password) || password == null) {
            // Inform user field is compulsory
            mPassword.setError("Required.");
            valid = false;
        } else if (password.length() < 6 || password.length() > 20) {
            // Inform user password is not long enough
            // Authentication will fail if password is shorter than this value
            // as Firebase requires this minimum password length
            mPassword.setError("Password should be between 6 and 20 characters");
            //TODO: Add to test plan that following line was missing causing error
            valid = false;
        } else {
            mPassword.setError(null);
        }
        return valid;
    }

    /**
     * Check email is valid and, if so, send password reset to provided email address
     *
     * @param email contains input email, used to send password reset instructions to
     */
    private void resetPassword(final String email) {
        Boolean valid = true;
        // Check if contents of email text box is valid
        if (!isValidEmailAddress(email)) {
            // Inform user Email is not valid
            Log.w(TAG, "resetPassword: Email not valid.");
            Toast.makeText(LoginActivity.this,
                    "Invalid Email Address",
                    Toast.LENGTH_SHORT).show();
            valid = false;
        }
        // If entered email is valid, a password reset email will be sent
        // to the entered email
        if (valid) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "resetPassword: Email sent.");
                                Toast.makeText(LoginActivity.this,
                                        "Verification Email sent to " + email,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
}