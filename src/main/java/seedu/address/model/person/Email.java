package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's email in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidEmail(String)}
 */
public class Email {

    public static final String WARNING_VALIDATION_REGEX = "^.{0,256}$";
    public static final String MESSAGE_BLANK = "Email should not be blank.";
    public static final String MESSAGE_WARN = "⚠ Warning: Email address is unusually long, is this intentional?";
    public static final String MESSAGE_DOMAIN_FORMAT_WARN =
            "⚠ Warning: Email uses a non-standard domain (e.g. user@localhost, without \".\"). Is this intentional?";
    public static final int MAX_LENGTH = 320;
    public static final String MESSAGE_LENGTH_CONSTRAINTS = "Email should be at most "
            + MAX_LENGTH + " characters.";
    public static final String MESSAGE_CONSTRAINTS = "Email should be a valid format (e.g. user@example.com).";
    public static final String DOMAIN_SEPARATOR = "@";
    public static final String DOT_SEPARATOR = ".";
    private static final String SPECIAL_CHARACTERS = "+_.-";
    // alphanumeric and special characters
    private static final String ALPHANUMERIC_NO_UNDERSCORE = "[^\\W_]+"; // alphanumeric characters except underscore
    private static final String LOCAL_PART_REGEX = "^" + ALPHANUMERIC_NO_UNDERSCORE + "([" + SPECIAL_CHARACTERS + "]"
            + ALPHANUMERIC_NO_UNDERSCORE + ")*";
    private static final String DOMAIN_PART_REGEX = ALPHANUMERIC_NO_UNDERSCORE
            + "(-" + ALPHANUMERIC_NO_UNDERSCORE + ")*";
    private static final String DOMAIN_LAST_PART_REGEX = "(" + DOMAIN_PART_REGEX + "){2,}$"; // At least two chars
    private static final String DOMAIN_REGEX = "(" + DOMAIN_PART_REGEX + "\\.)*" + DOMAIN_LAST_PART_REGEX;
    public static final String VALIDATION_REGEX = LOCAL_PART_REGEX + DOMAIN_SEPARATOR + DOMAIN_REGEX;

    public final String value;

    /**
     * Constructs an {@code Email}.
     *
     * @param email A valid email address.
     */
    public Email(String email) {
        requireNonNull(email);
        checkArgument(isValidEmail(email), MESSAGE_CONSTRAINTS);
        value = email.toLowerCase();
    }

    /**
     * Returns if a given string is a valid email.
     */
    public static boolean isValidEmail(String test) {
        requireNonNull(test);

        if (test.length() > MAX_LENGTH) {
            return false;
        }
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if a given string is a valid email with stronger validation.
     * Used for warning users about potential issues with their input.
     *
     * @param test the string to test.
     * @return true if the string is a valid email according to the length validation criteria.
     */
    public static boolean isValidEmailWarn(String test) {
        requireNonNull(test);

        return test.matches(WARNING_VALIDATION_REGEX);
    }

    /**
     * Returns true if a valid email's domain part (after '@') does not contain a dot.
     * This may indicate a missing or incomplete domain format.
     *
     * @param test the string to test.
     * @return true if the domain part does not contain a dot.
     */
    public static boolean isMissingDomainFormatWarn(String test) {
        requireNonNull(test);

        int atIndex = test.lastIndexOf(DOMAIN_SEPARATOR);
        boolean hasNoDomainSeparator = atIndex < 0;
        boolean hasNoDomainPart = atIndex == test.length() - 1;

        if (hasNoDomainSeparator || hasNoDomainPart) {
            // No valid domain exists to evaluate, so treat as no warning.
            return false;
        }

        String domainPart = test.substring(atIndex + 1);
        return !domainPart.contains(DOT_SEPARATOR);
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
        if (!(other instanceof Email)) {
            return false;
        }

        Email otherEmail = (Email) other;
        return value.equals(otherEmail.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
