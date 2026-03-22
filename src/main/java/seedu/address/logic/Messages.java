package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;
import seedu.address.model.product.Product;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_MISSING_PREFIX = "Missing required field(s): ";
    public static final String MESSAGE_NON_PREFIX_BEFORE_PREFIX = "No non-prefix characters before "
            + "prefix(es) is allowed, ";
    public static final String MESSAGE_MISSING_FIELD_FORMAT = "%s (%s)";
    public static final String MESSAGE_ALL_PREFIXES_MISSING = "All required prefixes are missing, ";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The contact index provided is invalid";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_EMAIL = "No contact with"
            + " the specified email was found.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d contacts listed!";
    public static final String MESSAGE_PRODUCTS_LISTED_OVERVIEW = "%1$d products listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";
    public static final String MESSAGE_DUPLICATE_PERSON = "This vendor contact already exists "
            + "with the same email. ";
    public static final String MESSAGE_DUPLICATE_PRODUCT = "This product already exists "
            + "with the same product identifier. ";
    public static final String MESSAGE_DUPLICATE_ALIAS = "This alias already exists "
            + "with the same alias name. ";
    public static final String MESSAGE_ORIGINAL_COMMAND_DOES_NOT_EXISTS = "The original command (%s) does not exists.\n"
                    + "For the list of commands visit the User Guide.";
    public static final String MESSAGE_ALIAS_CANNOT_BE_EMPTY = "The alias should not be empty";
    public static final String MESSAGE_ALIAS_CONTAINS_SPACE = "The alias should not contain any spaces.";
    public static final String MESSAGE_ALIAS_FORMATTED_WRONGLY =
            "Message is formatted wrongly.\nThe correct format is alias <ORIGINAL_COMMAND> <NEW_ALIAS>";
    public static final String MESSAGE_ALIAS_IS_A_PREDEFINED_COMMAND =
            "%s is a predefined command, please choose another alias.";
    public static final String MESSAGE_ALIAS_IS_NOT_FOUND =
            "No alias found in AliasList.";

    public static final String MESSAGE_INVALID_CONFIRMATION_FLAG =
            "Invalid format. The '-y' flag must be standalone.\n"
                    + "Example: clear -y";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail())
                .append("; Address: ")
                .append(person.getAddress())
                .append("; Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

    /**
     * Formats the {@code product} for display to the user.
     */
    public static String formatProduct(Product product) {
        String vendor = product.getVendorEmail().isPresent()
                ? product.getVendorEmail().get().toString()
                : "None";

        return String.format(
                "%s, Qty: %s, Threshold: %s, Vendor: %s",
                product.getName(),
                product.getQuantity(),
                product.getRestockThreshold(),
                vendor
        );
    }
}
