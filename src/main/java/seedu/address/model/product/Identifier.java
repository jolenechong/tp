package seedu.address.model.product;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Product's identifier in the inventory.
 * Guarantees: immutable; is valid as declared in {@link #isValidIdentifier(String)}
 */
public class Identifier {
    public static final String MESSAGE_CONSTRAINTS = "Identifier should not be blank.";
    public static final int MAX_LENGTH = 120;
    public static final String MESSAGE_LENGTH_CONSTRAINTS =
            "Identifier should be less than " + MAX_LENGTH + " characters.";
    public static final String MESSAGE_WARN =
            "⚠ Warning: Identifier contains unusual symbols, is this intentional?";

    public static final String VALIDATION_REGEX = "[A-Za-z0-9][A-Za-z0-9/-]{0,119}";
    public static final String WARNING_VALIDATION_REGEX = "[^\\s].{0,119}";

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
        requireNonNull(test);
        return test.matches(WARNING_VALIDATION_REGEX);
    }

    /**
     * Returns true if a given string is a valid identifier.
     * Stronger validation than {@link #isValidIdentifier(String)}.
     * Used for warning users about potential issues with their input.
     *
     * @param test the string to test.
     * @return true if the string is a valid identifier according to the stronger
     *         validation criteria.
     */
    public static boolean isValidIdentifierWarn(String test) {
        requireNonNull(test);
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
