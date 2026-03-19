package seedu.address.model.product;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Product's restock threshold in the inventory.
 * Guarantees: immutable; is valid as declared in {@link #isValidRestockThreshold(String)}
 */
public class RestockThreshold {

    public static final String MESSAGE_CONSTRAINTS =
            "Restock threshold should be a non-negative valid integer.";

    public final int value;

    /**
     * Constructs a {@code RestockThreshold}.
     *
     * @param restockThreshold A valid restockThreshold.
     */
    public RestockThreshold(String restockThreshold) {
        requireNonNull(restockThreshold);
        checkArgument(isValidRestockThreshold(restockThreshold), MESSAGE_CONSTRAINTS);
        value = Integer.parseInt(restockThreshold);
    }

    /**
     * Returns true if a given string is a valid restockThreshold.
     */
    public static boolean isValidRestockThreshold(String test) {
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
        if (!(other instanceof RestockThreshold)) {
            return false;
        }

        RestockThreshold otherRestockThreshold = (RestockThreshold) other;
        return value == otherRestockThreshold.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
