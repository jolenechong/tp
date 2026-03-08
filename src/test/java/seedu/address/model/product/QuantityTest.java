package seedu.address.model.product;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class QuantityTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Quantity(null));
    }

    @Test
    public void constructor_invalidQuantity_throwsIllegalArgumentException() {
        String invalidQuantity = "";
        assertThrows(IllegalArgumentException.class, () -> new Quantity(invalidQuantity));
    }

    @Test
    public void isValidQuantity() {
        // null quantity
        assertThrows(NullPointerException.class, () -> Quantity.isValidQuantity(null));

        // invalid quantities
        assertFalse(Quantity.isValidQuantity("")); // empty string
        assertFalse(Quantity.isValidQuantity(" ")); // spaces only
        assertFalse(Quantity.isValidQuantity("-1")); // negative value
        assertFalse(Quantity.isValidQuantity("1.5")); // decimal number
        assertFalse(Quantity.isValidQuantity("abc")); // non-numeric
        assertFalse(Quantity.isValidQuantity("1 0")); // spaces between digits
        assertFalse(Quantity.isValidQuantity("2147483648")); // integer overflow

        // valid quantities
        assertTrue(Quantity.isValidQuantity("0"));
        assertTrue(Quantity.isValidQuantity("25"));
        assertTrue(Quantity.isValidQuantity("0023")); // leading zeros
        assertTrue(Quantity.isValidQuantity(String.valueOf(Integer.MAX_VALUE)));
    }

    @Test
    public void equals() {
        Quantity quantity = new Quantity("10");

        // same values -> returns true
        assertTrue(quantity.equals(new Quantity("10")));

        // same object -> returns true
        assertTrue(quantity.equals(quantity));

        // null -> returns false
        assertFalse(quantity.equals(null));

        // different types -> returns false
        assertFalse(quantity.equals(5.0f));

        // different values -> returns false
        assertFalse(quantity.equals(new Quantity("5")));
    }
}
