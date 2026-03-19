package seedu.address.model.person.warnings;

import seedu.address.model.person.Address;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

/**
 * Represents a warning that is generated when a duplicate person is detected.
 */
public class DuplicatePersonWarning {

    public static final String MESSAGE_SIMILAR_NAME = "⚠ Warning: There's a contact with a similar name (name: %s), "
            + "is this intentional?";
    public static final String MESSAGE_SIMILAR_ADDRESS = "⚠ Warning: There's a contact with a similar address "
            + "(name: %s, address: %s), is this intentional?";
    public static final String MESSAGE_SIMILAR_PHONE = "⚠ Warning: There's a contact with a similar phone number "
            + "(name: %s, phone number: %s)";

    private final String warning;

    /**
     * Constructs a {@code DuplicatePersonWarning} with the specified value and warning message.
     *
     * @param warning The string warning message associated with the duplicate person detection.
     */
    public DuplicatePersonWarning(String warning) {
        this.warning = warning;
    }

    public String getWarning() {
        return warning;
    }

    /**
     * Helper method to format name warning.
     *
     * @param name name of similar person
     * @return String of formatted warning message
     */
    public static String formatNameWarning(Name name) {
        return String.format(MESSAGE_SIMILAR_NAME, name);
    }

    /**
     * Helper method to format address warning.
     *
     * @param name name of same similar person
     * @param address of same similar person
     * @return String of formatted warning message
     */
    public static String formatAddressWarning(Name name, Address address) {
        return String.format(MESSAGE_SIMILAR_ADDRESS, name, address);
    }

    /**
     * Helper method to format name warning.
     *
     * @param name name of similar person
     * @return String of formatted warning message
     */
    public static String formatPhoneWarning(Name name, Phone phone) {
        return String.format(MESSAGE_SIMILAR_PHONE, name, phone);
    }

}
