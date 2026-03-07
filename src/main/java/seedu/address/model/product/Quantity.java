package seedu.address.model.product;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Product's quantity in the inventory.
 * Guarantees: immutable; is valid as declared in {@link #isValidQuantity(String)}
 */
public class Quantity {

    public static final String MESSAGE_CONSTRAINTS =
            "Quantity should be a non-negative integer.";

    public final int value;

    /**
     * Constructs a {@code Quantity}.
     *
     * @param quantity A valid quantity.
     */
    public Quantity(String quantity) {
        requireNonNull(quantity);
        checkArgument(isValidQuantity(quantity), MESSAGE_CONSTRAINTS);
        value = Integer.parseInt(quantity);
    }

    /**
     * Returns true if a given string is a valid quantity.
     */
    public static boolean isValidQuantity(String test) {
        if (test.isBlank()) {
            return false;
        }

        try {
            int parsed = Integer.parseInt(test);
            return parsed >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Quantity)) {
            return false;
        }

        Quantity otherQuantity = (Quantity) other;
        return value == otherQuantity.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
