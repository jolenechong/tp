package seedu.address.model.product;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Product's name in the inventory.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final String MESSAGE_CONSTRAINTS = "Names should not be blank.";
    public static final int MAX_LENGTH = 256;
    public static final String MESSAGE_LENGTH_CONSTRAINTS = "Names should be less than " + MAX_LENGTH + " characters.";
    public static final String MESSAGE_WARN = "⚠ Warning: Name contains unusual symbols, is this intentional?";

    public static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} .,&+()/\\-']{0,255}";
    public static final String SOFT_VALIDATION_REGEX = "[^\\s].{0,255}";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = name;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        return test.matches(SOFT_VALIDATION_REGEX);
    }

    /**
     * Returns true if a given string is a valid name.
     * Stronger validation than {@link #isValidName(String)}.
     * Used for warning users about potential issues with their input.
     *
     * @param test the string to test.
     * @return true if the string is a valid name according to the stronger validation criteria.
     */
    public static boolean isValidNameWarn(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name)) {
            return false;
        }

        Name otherName = (Name) other;
        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
