package seedu.address.model.product;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Product's identifier in the inventory.
 * Guarantees: immutable; is valid as declared in {@link #isValidIdentifier(String)}
 */
public class Identifier {

    public static final String MESSAGE_CONSTRAINTS =
            "Identifiers should not be blank, should be at most 15 characters long, and may only contain letters, "
                    + "numbers, hyphens (-), and slashes (/).";
    public static final int MAX_LENGTH = 15;
    public static final String MESSAGE_LENGTH_CONSTRAINTS =
            "Identifiers should be less than " + MAX_LENGTH + " characters.";

    public static final String VALIDATION_REGEX = "[A-Za-z0-9][A-Za-z0-9/-]{0,14}";

    public final String value;

    /**
     * Constructs an {@code Identifier}.
     *
     * @param identifier A valid identifier.
     */
    public Identifier(String identifier) {
        requireNonNull(identifier);
        checkArgument(isValidIdentifier(identifier), MESSAGE_CONSTRAINTS);
        value = identifier;
    }

    /**
     * Returns true if a given string is a valid identifier.
     */
    public static boolean isValidIdentifier(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Identifier)) {
            return false;
        }

        Identifier otherIdentifier = (Identifier) other;
        return value.equals(otherIdentifier.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
