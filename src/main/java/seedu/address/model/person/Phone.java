package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {

    public static final String MESSAGE_CONSTRAINTS = "Phone number should not be empty "
            + "and must be at least 3 digits.";
    public static final String MESSAGE_WARN =
            "⚠ Warning: Phone number contains unusual symbols, is this intentional?";
    public static final String WARNING_VALIDATION_REGEX =
            "^(?=(?:.*\\d){3,})[\\d+\\- ]+$";
    public static final String VALIDATION_REGEX = "^(?=(?:.*\\d){3,}).*$";
    public static final String VALIDATION_EXCLUDE_DIGITS_REGEX = "[^0-9]";
    public static final int MIN_LENGTH = 3;
    private static final String PHONE_SEPARATOR = ",";
    private static final int SPLIT_ALL_ENTRIES = -1;
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
        requireNonNull(test);

        if (!test.contains(PHONE_SEPARATOR)) {
            return isValidPhoneEntry(test.trim());
        }

        List<String> entries = splitPhoneEntries(test);
        return !entries.isEmpty() && entries.stream().allMatch(Phone::isValidPhoneEntry);
    }

    private static boolean isValidPhoneEntry(String phoneEntry) {
        String trimmedPhone = phoneEntry.trim();
        return !trimmedPhone.isEmpty() && trimmedPhone.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if a given string is a valid phone number according to stricter validation.
     * Used for warning users about potential issues with their input.
     *
     * @param test the string to test.
     * @return true if the string is a valid phone number according to the stronger validation criteria.
     */
    public static boolean isValidPhoneWarn(String test) {
        requireNonNull(test);

        if (!test.contains(PHONE_SEPARATOR)) {
            return isValidPhoneEntryWarn(test.trim());
        }

        String[] rawEntries = test.trim().split(PHONE_SEPARATOR, SPLIT_ALL_ENTRIES);
        List<String> entries = splitPhoneEntries(test);
        return !entries.isEmpty()
                && entries.size() == rawEntries.length
                && entries.stream().allMatch(Phone::isValidPhoneEntryWarn);
    }

    private static boolean isValidPhoneEntryWarn(String phoneEntry) {
        return phoneEntry.trim().matches(WARNING_VALIDATION_REGEX);
    }

    private static List<String> splitPhoneEntries(String test) {
        return Arrays.stream(test.trim().split(PHONE_SEPARATOR, SPLIT_ALL_ENTRIES))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
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
