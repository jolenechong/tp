package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {

    public static final String MESSAGE_CONSTRAINTS = "Phone number should not be empty "
            + "and must be at least 3 in length.";
    public static final String MESSAGE_WARN =
            "⚠ Warning: Phone number contains unusual symbols, is this intentional?";
    public static final String SOFT_VALIDATION_REGEX = "\\d{3,}";
    public static final String VALIDATION_REGEX = "^(?=(?:.*\\d){3,}).*$";
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

        String[] phoneEntries = test.trim().split(",", -1);
        boolean hasNonEmptyEntry = false;
        for (String phoneEntry : phoneEntries) {
            String trimmedPhoneEntry = phoneEntry.trim();
            if (trimmedPhoneEntry.isEmpty()) {
                continue;
            }

            hasNonEmptyEntry = true;
            if (!isValidPhoneEntry(trimmedPhoneEntry)) {
                return false;
            }
        }

        return hasNonEmptyEntry;
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
        String[] phoneEntries = test.trim().split(",", -1);
        boolean hasNonEmptyEntry = false;
        boolean hasEmptyEntry = false;
        for (String phoneEntry : phoneEntries) {
            String trimmedPhoneEntry = phoneEntry.trim();
            if (trimmedPhoneEntry.isEmpty()) {
                hasEmptyEntry = true;
                continue;
            }

            hasNonEmptyEntry = true;
            if (!isValidPhoneEntryWarn(trimmedPhoneEntry)) {
                return false;
            }
        }

        return hasNonEmptyEntry && !hasEmptyEntry;
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
