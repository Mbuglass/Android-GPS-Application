package com.example.michael.firstapp;

import android.text.TextUtils;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class NewMarkerActivityTest {

    // NOTE: THE EMPTY STRING TEST HAD TO BE REMOVED DUE TO A KNOWN BUG WITH JUNIT
    // AND TEXTUTILS.ISEMPTY() NOT FUNCTIONING TOGETHER
    // "We are aware that the default behavior is problematic when using classes like Log
    // or TextUtils and will evaluate possible solutions in future releases." - Google
    // http://tools.android.com/tech-docs/unit-testing-support#TOC-Method-...-not-mocked.-

    @Test
    public void validTitleAndValidDescriptionPasses() {
        // First contains text only
        assertTrue(validateForm("Example Title", "Example Description"));
        // Second contains assortment of special characters including quotation marks
        assertTrue(validateForm("!\"£$%^&*()-=#'@`", "!\"£$%^&*()-=#'@`"));
        // Third contains emoji
        assertTrue(validateForm("\uD83E\uDD9D ", "\uD83E\uDD9D "));
    }

    @Test
    public void invalidTitleAndValidDescriptionFails() {
        // First title contains null
        assertTrue(!validateForm(null, "Example Description"));
        // Second title contains empty string
        assertTrue(!validateForm("", "Example Description"));
    }

    @Test
    public void validTitleAndInvalidDescriptionFails() {
        // First description contains null
        assertTrue(!validateForm("Example Title", null));
        // Second description contains empty string
        assertTrue(!validateForm("Example Title", ""));
    }

    @Test
    public void invalidTitleAndInvalidDescriptionFails() {
        // First title and description contain null
        assertTrue(!validateForm(null, null));
        // Second title and description contains empty string
        assertTrue(!validateForm("", ""));
    }

    /**
     * The following method is extracted from NewMarkerActivity and edited to remove references to
     * text fields, setError() methods in the original produce nullPointerExceptions when called
     * from test class
     *
     * @param title marker title input
     * @param desc  marker description input
     * @return boolean indicating validity of inputs
     */
    private boolean validateForm(String title, String desc) {
        boolean valid = true;

        // Check if title input is empty
        if (TextUtils.isEmpty(title) || title == null) {
            // Inform user field is compulsory
            valid = false;
        }
        // Check if title input is empty
        else if (TextUtils.isEmpty(desc) || desc == null) {
            // Inform user field is compulsory
            valid = false;
        }
        return valid;
    }
}
