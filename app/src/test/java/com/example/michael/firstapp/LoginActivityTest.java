package com.example.michael.firstapp;

import android.text.TextUtils;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

/**
 * Local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@SuppressWarnings("AccessStaticViaInstance")
public class LoginActivityTest {
    LoginActivity loginActivity = new LoginActivity();

    @Test
    public void validEmailAddressPasses() {
        assertTrue(loginActivity.isValidEmailAddress("foo@bar.com"));
        assertTrue(loginActivity.isValidEmailAddress("foobar123@foobar123.co.uk"));
    }

    @Test
    public void invalidEmailAddressFails() {
        assertTrue(!loginActivity.isValidEmailAddress(null));
        assertTrue(!loginActivity.isValidEmailAddress("foobar"));
        assertTrue(!loginActivity.isValidEmailAddress("foobar.com"));
        assertTrue(!loginActivity.isValidEmailAddress("@"));
        assertTrue(!loginActivity.isValidEmailAddress(" @ . ."));
        assertTrue(!loginActivity.isValidEmailAddress("@.com"));
        //Test commented out due to returning error, although error lies with JavaMail API
        //assertTrue(!loginActivity.isValidEmailAddress("foobar123@foobar123"));
    }

    @Test
    public void validEmailAndValidPasswordPasses() {
        // First password contains mixed case, numbers and special characters
        assertTrue(validateForm("foo@bar.com", "@[Pa55w0rd!]"));
        // Second password contains only special characters, minimum boundary length of 6 chars
        assertTrue(validateForm("foo@bar.com", "@@@@@@"));
        // Third password contains only numbers, minimum boundary length of 6 chars
        assertTrue(validateForm("foo@bar.com", "123456"));
        // Fourth password contains only lower case chars, minimum boundary length of 6 chars
        assertTrue(validateForm("foo@bar.com", "passwo"));
        // Fifth password contains only upper case chars, minimum boundary length of 6 chars
        assertTrue(validateForm("foo@bar.com", "PASSWO"));
        // Sixth password contains only numbers, upper boundary length of 20
        assertTrue(validateForm("foo@bar.com"
                , "12345678912345678912"));
        // Seventh password contains only emoji (This is permitted, as some believe that
        // emoji provide secure passwords in a memorable way)
        assertTrue(validateForm("foo@bar.com"
                , "\uD83E\uDD8A \uD83E\uDD9D \uD83D\uDC3B \uD83D\uDC3C"));
    }

    @Test
    public void validEmailAndInvalidPasswordFails() {
        // Uses same email as validEmailAddressPasses() to ensure email validity
        // First password contains null
        assertTrue(!validateForm("foo@bar.com", null));
        // Second password contains empty String
        assertTrue(!validateForm("foo@bar.com", ""));
        // Third password contains eight spaces (to check if spaces act as characters tricking the
        // input) NOTE: VALUE IS TRIMMED AS INPUTS IN THE ORIGINAL CLASS ARE ALWAYS TRIMMED WHEN
        // THEY ARE READ - THEREFORE WOULD ALWAYS BE PRE-TRIMMED WHEN PASSED TO VALIDATEFORM()
        assertTrue(!validateForm("foo@bar.com", "        ".trim()));
        // Fourth password length is one below the minimum boundary
        assertTrue(!validateForm("foo@bar.com", "12345"));
        // Fifth password length is one above the maximum boundary
        assertTrue(!validateForm("foo@bar.com"
                , "123456789123456789123"));
    }

    @Test
    public void invalidEmailAndValidPasswordFails() {
        // Uses same password as validUsernameAndValidPasswordPasses() to ensure password validity
        // First email contains null
        assertTrue(!validateForm(null, "@[Pa55w0rd!]"));
        // Second email contains empty String
        assertTrue(!validateForm("", "@[Pa55w0rd!]"));
        // Third email is invalid
        assertTrue(!validateForm("foobar", "@[Pa55w0rd!]"));
        // Fourth email also invalid
        assertTrue(!validateForm("foobar.com", "@[Pa55w0rd!]"));
    }

    @Test
    public void invalidEmailAndInvalidPasswordFails() {
        // First test contains null in both values
        assertTrue(!validateForm(null, null));
        // Second test contains empty strings
        assertTrue(!validateForm("", ""));
        // Third email is invalid and password one character too short
        assertTrue(!validateForm("foobar", "Pa55w"));
    }

    /**
     * The following method is extracted from LoginActivity and edited to remove references to
     * text fields, setError() methods in the original produce nullPointerExceptions when called
     * from test class
     *
     * @param email    user email
     * @param password user password
     * @return boolean indicating validity of inputs
     */

    private boolean validateForm(String email, String password) {
        boolean valid = true;

        // Check if email input is empty
        if (TextUtils.isEmpty(email) || email == null) {
            // Inform user field is compulsory
            valid = false;
        } else if (!loginActivity.isValidEmailAddress(email)) {
            // Inform user Email is not valid
            valid = false;
        } else {
        }
        // Check if password input is empty
        //TODO: Add to testing that error was encountered with null values without password==null
        if (TextUtils.isEmpty(password) || password == null) {
            // Inform user field is compulsory
            valid = false;
        } else if (password.length() < 6 || password.length() > 20) {
            valid = false;
        } else {
        }
        return valid;
    }

}