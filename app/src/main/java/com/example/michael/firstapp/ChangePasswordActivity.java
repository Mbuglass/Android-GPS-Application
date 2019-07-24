package com.example.michael.firstapp;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ChangePasswordActivity";

    // Assign the input fields to variables
    private EditText mCurrentPassword;
    private EditText mNewPassword;

    // Assign progress bar to variable
    private ProgressBar mProgressBar;

    /**
     * On create method initialises views, progress bar and FireStore
     * @param savedInstanceState allows activity to restore to a previous state using data stored
     *                           in this Bundle
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_changepassword);

        // Views
        mCurrentPassword = findViewById(R.id.txtCurrentPassword);
        mNewPassword = findViewById(R.id.txtNewPassword);

        // Set progress bar to invisible by default
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * Validates inputs against criteria to prevent error
     * Inputs must not be null
     * Passwords must be longer than 6 characters
     * @return boolean value indicating validity of inputs
     * @param currentPassword current password to validate
     * @param newPassword new password to validate
     */
    public boolean validateForm(String currentPassword, String newPassword) {
        boolean valid = true;
        // Get input from text box
        // Check if password input is empty
        if (TextUtils.isEmpty(currentPassword)||currentPassword==null) {
            // Inform user field is compulsory
            mCurrentPassword.setError("Required.");
            valid = false;
        }
        else if (TextUtils.isEmpty(newPassword)||newPassword==null) {
            // Inform user field is compulsory
            mNewPassword.setError("Required.");
            valid = false;
        }
        else if (currentPassword.length() < 6 || currentPassword.length()>20) {
            // Inform user password is not long enough
            // Authentication will fail if password is shorter than this value
            // as Firebase requires this minimum password length
            mCurrentPassword.setError("Password should be between 6 and 20 characters");
            valid = false;
        }
        else if (newPassword.length() < 6 || newPassword.length()>20) {
            // Inform user password is not long enough
            // Authentication will fail if password is shorter than this value
            // as Firebase requires this minimum password length
            mNewPassword.setError("Password should be between 6 and 20 characters");
            valid = false;
        }
        else if (newPassword.equals(currentPassword)){
            mNewPassword.setError("New password can not equal current password");
            valid = false;
        }
        else {
            // Clear error messages
            mCurrentPassword.setError(null);
            mNewPassword.setError(null);
        }
        return valid;
    }

    /**
     * Verifies the input for currentPassword matches data stored in the Firebase Authentication
     * database by re-authenticating user's credentials
     * @param user FirebaseUser object for current user
     * @param currentPassword user input current password
     * @param newPassword user input new password
     */
    public void verifyCurrentPassword(final FirebaseUser user, String currentPassword,
                                         final String newPassword){
        AuthCredential authCredential = EmailAuthProvider
                .getCredential(user.getEmail(),currentPassword);
        user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updatePassword(user, newPassword);
                    Log.d(TAG, "verifyCurrentPassword: Password Verified.");
                }
                else {
                    Log.d(TAG, "verifyCurrentPassword: Password Incorrect.");
                    Toast.makeText(ChangePasswordActivity.this,
                            "Current Password Incorrect",
                            Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Updates the user's password in the Firebase Authorisation database
     * @param user FirebaseUser user object
     * @param newPassword user input new password
     */
    public void updatePassword(FirebaseUser user, String newPassword){
        if (user!=null){
            // Change password
            user.updatePassword(newPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG,
                                        "ChangePasswordActivity: Password Change successful");
                                Toast.makeText(ChangePasswordActivity.this,
                                        "Password Changed", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.w(TAG, "ChangePasswordActivity: Password Change failure",
                                        task.getException());
                                Toast.makeText(ChangePasswordActivity.this,
                                        "Error Occurred While Changing Password"
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        // Hide progress bar to indicate task has completed
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * Called when the user clicks the change password button
     * Will update the user's password to the value entered into the textbox
     * using FirebaseAuth to ensure password is not stored locally and is encrypted.
     * @param view allows method to get inputs from view text boxes
     */
    @Override
    public void onClick(View view) {
        // Show progress bar to indicate to user that a task is being carried out
        mProgressBar.setVisibility(View.VISIBLE);

        String currentPassword = mCurrentPassword.getText().toString().trim();
        String newPassword = mNewPassword.getText().toString().trim();

        // Check user input is valid
        if (!validateForm(currentPassword, newPassword)) {
            return;
        }
        // Get current user from FireBase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        verifyCurrentPassword(user, currentPassword, newPassword);
    }
}
