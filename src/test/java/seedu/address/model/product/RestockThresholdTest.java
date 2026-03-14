package seedu.address.model.product;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RestockThresholdTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RestockThreshold(null));
    }

    @Test
    public void constructor_invalidRestockThreshold_throwsIllegalArgumentException() {
        String invalidRestockThreshold = "";
        assertThrows(IllegalArgumentException.class, () -> new RestockThreshold(invalidRestockThreshold));
    }

    @Test
    public void isValidRestockThreshold() {
        // null threshold
        assertThrows(NullPointerException.class, () -> RestockThreshold.isValidRestockThreshold(null));

        // invalid thresholds
        assertFalse(RestockThreshold.isValidRestockThreshold("")); // empty string
        assertFalse(RestockThreshold.isValidRestockThreshold(" ")); // spaces only
        assertFalse(RestockThreshold.isValidRestockThreshold("-1")); // negative value
        assertFalse(RestockThreshold.isValidRestockThreshold("1.5")); // decimal number
        assertFalse(RestockThreshold.isValidRestockThreshold("abc")); // non-numeric
        assertFalse(RestockThreshold.isValidRestockThreshold("1 0")); // spaces between digits
        assertFalse(RestockThreshold.isValidRestockThreshold("2147483648")); // integer overflow

        // valid thresholds
        assertTrue(RestockThreshold.isValidRestockThreshold("0"));
        assertTrue(RestockThreshold.isValidRestockThreshold("25"));
        assertTrue(RestockThreshold.isValidRestockThreshold("0023")); // leading zeros
        assertTrue(RestockThreshold.isValidRestockThreshold(String.valueOf(Integer.MAX_VALUE)));
    }

    @Test
    public void equals() {
        RestockThreshold threshold = new RestockThreshold("10");

        // same values -> returns true
        assertTrue(threshold.equals(new RestockThreshold("10")));

        // same object -> returns true
        assertTrue(threshold.equals(threshold));

        // null -> returns false
        assertFalse(threshold.equals(null));

        // different types -> returns false
        assertFalse(threshold.equals(5.0f));

        // different values -> returns false
        assertFalse(threshold.equals(new RestockThreshold("5")));
    }
}
