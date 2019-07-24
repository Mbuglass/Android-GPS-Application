package com.example.michael.firstapp;

import android.text.TextUtils;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class ChangePasswordTest {

    @Test
    public void validCurrentPasswordValidNewPasswordPasses() {
        // First passwords contain mixed case, number and special characters
        assertTrue(validateForm("@PA55w0rd!", "@PA55w0rd!2"));
        // Second passwords contain only letters, and are the minimum length
        assertTrue(validateForm("passw1", "passw2"));
        // Third passwords contain only emoji
        assertTrue(validateForm("\uDD8A \uD83E\uDD9D \uD83D\uDC3B \uD83D\uDC3C",
                "\uDD8A \uD83E\uDD9D \uD83D\uDC3B \uD83D"));
        // Fourth password contains quotation marks
        assertTrue(validateForm("\"\"password\"\"", "\"\"password2\"\""));
    }

    @Test
    public void invalidCurrentPasswordValidNewPasswordFails() {
        // First current password is null
        assertTrue(!validateForm(null, "@PA55w0rd!2"));
        // Second current password is empty string
        assertTrue(!validateForm("", "@PA55w0rd!2"));
        // Third current password is one character too short
        assertTrue(!validateForm("12345", "@PA55w0rd!2"));
        // Fourth current password is one character too long
        assertTrue(!validateForm("123456789123456789123", "@PA55w0rd!2"));
    }

    @Test
    public void validCurrentPasswordInvalidNewPasswordFails() {
        // First new password is null
        assertTrue(!validateForm("@PA55w0rd!", null));
        // Second new password is empty string
        assertTrue(!validateForm("@PA55w0rd!", ""));
        // Third new password is one character too short
        assertTrue(!validateForm("@PA55w0rd!", "12345"));
        // Fourth new password is one character too long
        assertTrue(!validateForm("@PA55w0rd!",
                "123456789123456789123"));
    }

    @Test
    public void invalidCurrentPasswordInvalidNewPasswordFails() {
        // First current password is null, new password is empty
        assertTrue(!validateForm(null, ""));
        // Second
        assertTrue(!validateForm("", null));
        // Third
        assertTrue(!validateForm("12345", "12345"));
        /// Fourth
        assertTrue(!validateForm("123456789123456789123",
                "123456789123456789123"));
    }

    @Test
    public void newPasswordMustNotEqualCurrentPassword() {
        // Passwords are both valid, but are equal
        assertTrue(!validateForm("@PA55w0rd!", "@PA55w0rd!"));
    }

    /**
     * The following method is extracted from ChangePasswordActivity and edited to remove references
     * to text fields, setError() methods in the original produce nullPointerExceptions when called
     * from test class
     *
     * @param currentPassword current password to validate
     * @param newPassword     new password to validate
     * @return boolean value indicating validity of inputs
     */
    private boolean validateForm(String currentPassword, String newPassword) {
        boolean valid = true;
        // Get input from text box
        // Check if password input is empty
        if (TextUtils.isEmpty(currentPassword) || currentPassword == null) {
            // Inform user field is compulsory
            valid = false;
        } else if (TextUtils.isEmpty(newPassword) || newPassword == null) {
            // Inform user field is compulsory
            valid = false;
        } else if (currentPassword.length() < 6 || currentPassword.length() > 20) {
            // Inform user password is not long enough
            // Authentication will fail if password is shorter than this value
            // as Firebase requires this minimum password length
            valid = false;
        } else if (newPassword.length() < 6 || newPassword.length() > 20) {
            // Inform user password is not long enough
            // Authentication will fail if password is shorter than this value
            // as Firebase requires this minimum password length
            valid = false;
        }
        else if (newPassword.equals(currentPassword)) {
            valid = false;
        } else {
            // Clear error messages
        }
        return valid;
    }
}
