package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // -------------------- INVALID NAME --------------------
        assertFalse(Name.isValidName(""));
        assertFalse(Name.isValidName(" "));

        // EP: starts with whitespace
        assertFalse(Name.isValidName(" leading space"));

        // EP: exceeds max length (257 characters)
        assertFalse(Name.isValidName("a".repeat(257)));

        // -------------------- VALID NAME --------------------
        // EP: single character
        assertTrue(Name.isValidName("a"));
        // EP: special characters (valid under isValidName)
        assertTrue(Name.isValidName("peter*"));
        assertTrue(Name.isValidName("^caret"));
        // EP: exactly at max length (256 characters)
        assertTrue(Name.isValidName("a".repeat(256)));

        // -------------------- INVALID NAME, WARNING --------------------
        // EP: empty string
        assertFalse(Name.isValidNameWarn(""));
        // EP: spaces only
        assertFalse(Name.isValidNameWarn(" "));
        // EP: only non-alphanumeric characters
        assertFalse(Name.isValidNameWarn("^"));
        // EP: contains non-alphanumeric characters
        assertFalse(Name.isValidNameWarn("peter*"));

        // -------------------- VALID NAME, NO WARNING --------------------
        // EP: alphabets only
        assertTrue(Name.isValidNameWarn("peter jack"));
        // EP: numbers only
        assertTrue(Name.isValidNameWarn("12345"));
        // EP: alphanumeric characters
        assertTrue(Name.isValidNameWarn("peter the 2nd"));
        // EP: capital letters
        assertTrue(Name.isValidNameWarn("Capital Tan"));
        // EP: with numbers
        assertTrue(Name.isValidNameWarn("David Roger Jackson Ray Jr 2nd"));
        // EP: single alphanumeric character
        assertTrue(Name.isValidNameWarn("A"));
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
