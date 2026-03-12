package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.regex.Pattern;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {

    public static final String MESSAGE_CONSTRAINTS = "Phone number should not be empty and must be at least 3 digits.";
    public static final String MESSAGE_WARN =
            "⚠ Warning: Phone number contains unusual symbols, is this intentional?\n"
                    + "Phone number(s) should adhere to these constraints:\n"
                    + "1. It should contain only digits, spaces, "
                    + "'+' or '-' in the number part, optionally followed by a specification in parentheses.\n"
                    + "2. Multiple phone numbers should be separated by commas. "
                    + "\nExample: 12345678 (HP), 62345678 (Office)";
    public static final String SOFT_VALIDATION_REGEX = "\\d{3,}";
    public static final String VALIDATION_REGEX = "^(?=(?:.*\\d){3,}).*$";
    private static final Pattern PHONE_ENTRY_PATTERN =
            Pattern.compile("^(?<number>[0-9 +\\-]+?)(?:\\s*\\((?<spec>[^()]+)\\))?$");
    private static final Pattern PHONE_NUMBER_CHAR_PATTERN = Pattern.compile("^[0-9 +\\-]+$");
    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = phone;
    }

    /**
     * Returns true if a given string is a valid phone number.
     * Validates that the phone number(s) are not empty and contain at least 3 characters.
     * Multiple phone numbers can be separated by commas, and each can have an optional specification in parentheses.
     *
     * @param test the string to test.
     * @return true if the string is a valid phone number according to the validation criteria.
     */
    public static boolean isValidPhone(String test) {
        if (!test.contains(",")) {
            return isValidPhoneEntry(test.trim());
        }

        if (test.trim().endsWith(",")) { // end with , means last entry is empty
            return isValidPhoneEntry("");
        }

        String[] phoneEntries = test.trim().split(",");
        for (String phoneEntry : phoneEntries) {
            if (!isValidPhoneEntry(phoneEntry)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isValidPhoneEntry(String phoneEntry) {
        String trimmedPhone = phoneEntry.trim();
        return !trimmedPhone.isEmpty() && trimmedPhone.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if a given string is a valid phone number according to stricter validation.
     * Stronger validation than {@link #isValidPhone(String)}.
     * Used for warning users about potential issues with their input.
     *
     * @param test the string to test.
     * @return true if the string is a valid phone number according to the stronger validation criteria.
     */
    public static boolean isValidPhoneWarn(String test) {
        if (!test.contains(",")) {
            return isValidPhoneEntryWarn(test.trim());
        }

        String[] phoneEntries = test.trim().split(",");
        for (String phoneEntry : phoneEntries) {
            if (!isValidPhoneEntryWarn(phoneEntry.trim())) {
                return false;
            }
        }

        return true;
    }

    private static boolean isValidPhoneEntryWarn(String phoneEntry) {
        return phoneEntry.trim().matches(SOFT_VALIDATION_REGEX);
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
        if (!(other instanceof Phone)) {
            return false;
        }

        Phone otherPhone = (Phone) other;
        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
