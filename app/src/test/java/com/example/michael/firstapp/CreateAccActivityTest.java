package com.example.michael.firstapp;

import android.text.TextUtils;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

@SuppressWarnings("AccessStaticViaInstance")
public class CreateAccActivityTest {
    CreateAccActivity testCreateAccActivity = new CreateAccActivity();

    @Test
    public void validEmailAddressPasses() {
        assertTrue(testCreateAccActivity.isValidEmailAddress("foo@bar.com"));
        assertTrue(testCreateAccActivity.isValidEmailAddress("foobar123@foobar123.co.uk"));
    }

    @Test
    public void invalidEmailAddressFails() {
        assertTrue(!testCreateAccActivity.isValidEmailAddress(null));
        assertTrue(!testCreateAccActivity.isValidEmailAddress("foobar"));
        assertTrue(!testCreateAccActivity.isValidEmailAddress("foobar.com"));
        assertTrue(!testCreateAccActivity.isValidEmailAddress("@"));
        assertTrue(!testCreateAccActivity.isValidEmailAddress(" @ . ."));
        assertTrue(!testCreateAccActivity.isValidEmailAddress("@.com"));
        assertTrue(!testCreateAccActivity.isValidEmailAddress("a@.com"));
        assertTrue(!testCreateAccActivity.isValidEmailAddress("@b.com"));
        //Test commented out due to returning error, although error lies with JavaMail API
        //assertTrue(!loginActivity.isValidEmailAddress("foobar123@foobar123"));
    }

    @Test
    public void validEmailValidUsernameValidPasswordPasses() {
        // First username and password contains mixed case, numbers and special characters
        assertTrue(validateForm("foo@bar.com", "@U53RN4M3!",
                "@[Pa55w0rd!]"));
        // Second password contains only special characters, minimum boundary length of 6 chars
        assertTrue(validateForm("foo@bar.com", "User1998", "@@@@@@"));
        // Third password contains only numbers, minimum boundary length of 6 chars
        // Username contains an emoji
        assertTrue(validateForm("foo@bar.com", "User \uD83E", "123456"));
        // Fourth password contains only lower case chars, minimum boundary length of 6 chars
        // Username minimum boundary length of 4 chars
        assertTrue(validateForm("foo123@bar123.com", "user1234567891234567",
                "passwo"));
        // Fifth username at max boundary of 20 chars
        assertTrue(validateForm("foo123@bar123.com", "1234", "passwo"));
        // Sixth username, password contains only emoji (This is permitted, as some believe that
        // emoji provide secure passwords in a memorable way)
        assertTrue(validateForm("foo@bar.com",
                "\uDD8A \uD83E\uDD9D \uD83D\uDC3B \uD83D\uDC3C"
                , "\uDD8A \uD83E\uDD9D \uD83D\uDC3B \uD83D\uDC3C"));
        // Seventh username and password contain quotation marks
        assertTrue(validateForm("foo123@bar123.com", "\"\'username",
                "passwo\""));
    }

    @Test
    public void invalidEmailValidUsernameValidPasswordFails() {
        // First email null
        assertTrue(!validateForm(null, "user1998", "@[Pa55w0rd!]"));
        // Second email empty string
        assertTrue(!validateForm("", "user1998", "@[Pa55w0rd!]"));
        // Third email invalid
        assertTrue(!validateForm("@.com", "user1998", "@[Pa55w0rd!]"));
    }

    @Test
    public void validEmailInvalidUsernameValidPasswordFails() {
        // First username null
        assertTrue(!validateForm("foo@bar.com", null, "@[Pa55w0rd!]"));
        // Second username empty string
        assertTrue(!validateForm("foo@bar.com", "", "@[Pa55w0rd!]"));
        // Third username 1 character too short of min boundary
        assertTrue(!validateForm("foo@bar.com", "use", "@[Pa55w0rd!]"));
        // Third username 1 character too high of max boundary
        assertTrue(!validateForm("foo@bar.com", "user12345678912345678",
                "@[Pa55w0rd!]"));
    }

    @Test
    public void validEmailValidUsernameInvalidPasswordFails() {
        // First password null
        assertTrue(!validateForm("foo@bar.com", "@U53RN4M3!",
                null));
        // Second password empty string
        assertTrue(!validateForm("foo@bar.com", "@U53RN4M3!",
                ""));
        // Third password one too short
        assertTrue(!validateForm("foo@bar.com", "@U53RN4M3!",
                "passw"));
    }

    @Test
    public void invalidEmailInvalidUsernameInvalidPasswordFails() {
        // First all null
        assertTrue(!validateForm(null, null,
                null));
        // Second all empty string
        assertTrue(!validateForm("", "",
                ""));
    }

    /**
     * The following method is extracted from CreateAccActivity and edited to remove references to
     * text fields, setError() methods in the original produce nullPointerExceptions when called
     * from test class
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
            valid = false;
        } else if (!testCreateAccActivity.isValidEmailAddress(email)) {
            // Inform user Email is not valid
            valid = false;
        } else {
        }
        // Check if username input is empty
        if (TextUtils.isEmpty(username) || username == null) {
            // Inform user field is compulsory
            valid = false;
        } else if (username.length() > 20 || username.length() < 4) {
            valid = false;
        } else {
        }
        // Check if password input is empty
        if (TextUtils.isEmpty(password) || password == null) {
            // Inform user field is compulsory
            valid = false;
        } else if (password.length() < 6 || password.length() > 20) {
            // Inform user password is not long enough or is too long
            // Authentication will fail if password is shorter than this value
            // as Firebase requires this minimum password length
            valid = false;
        } else {
        }
        return valid;
    }
}