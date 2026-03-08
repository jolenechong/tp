package seedu.address.model.product;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class IdentifierTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Identifier(null));
    }

    @Test
    public void constructor_invalidIdentifier_throwsIllegalArgumentException() {
        String invalidIdentifier = "";
        assertThrows(IllegalArgumentException.class, () -> new Identifier(invalidIdentifier));
    }

    @Test
    public void isValidIdentifier() {
        // null identifier
        assertThrows(NullPointerException.class, () -> Identifier.isValidIdentifier(null));

        // invalid identifiers
        assertFalse(Identifier.isValidIdentifier("")); // empty string
        assertFalse(Identifier.isValidIdentifier(" ")); // spaces only
        assertFalse(Identifier.isValidIdentifier("a".repeat(257))); // exceeds max length

        // valid identifiers
        assertTrue(Identifier.isValidIdentifier("A1"));
        assertTrue(Identifier.isValidIdentifier("12345"));
        assertTrue(Identifier.isValidIdentifier("00223"));
        assertTrue(Identifier.isValidIdentifier("A-1/2")); // supported symbols
        assertTrue(Identifier.isValidIdentifier("a".repeat(256))); // exactly max length

        // warned identifiers
        assertFalse(Identifier.isValidIdentifierWarn("A B")); // spaces between characters
        assertFalse(Identifier.isValidIdentifierWarn("A@1")); // unsupported @
        assertFalse(Identifier.isValidIdentifierWarn("-A1")); // does not start with alphanumeric
    }

    @Test
    public void equals() {
        Identifier identifier = new Identifier("A1");

        // same values -> returns true
        assertTrue(identifier.equals(new Identifier("A1")));

        // same object -> returns true
        assertTrue(identifier.equals(identifier));

        // null -> returns false
        assertFalse(identifier.equals(null));

        // different types -> returns false
        assertFalse(identifier.equals(5.0f));

        // different values -> returns false
        assertFalse(identifier.equals(new Identifier("A")));
    }
}
