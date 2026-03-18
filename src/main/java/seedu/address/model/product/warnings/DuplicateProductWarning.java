package seedu.address.model.product.warnings;

import seedu.address.model.product.Identifier;
import seedu.address.model.product.Name;

/**
 * Represents a warning that is generated when a similar product is detected.
 */
public class DuplicateProductWarning {

    public static final String MESSAGE_SIMILAR_NAME = "⚠ Warning: There's a product with a similar name"
            + " (id: %s, name: %s), is this intentional?";

    private final boolean isDuplicated;
    private final String warning;

    /**
     * Constructs a {@code DuplicateProductWarning} with the specified value and warning message.
     *
     * @param isDuplicated   Whether a potential duplicate is detected.
     * @param warning The warning message associated with the duplicate detection.
     */
    public DuplicateProductWarning(boolean isDuplicated, String warning) {
        this.isDuplicated = isDuplicated;
        this.warning = warning;
    }

    public boolean isDuplicated() {
        return isDuplicated;
    }

    public String getWarning() {
        return warning;
    }

    /**
     * Helper method to format warning string.
     *
     * @param id identifer of similar product
     * @param name name of similar product
     * @return String of the formatted warning message
     */
    public static String formatNameWarning(Identifier id, Name name) {
        return String.format(MESSAGE_SIMILAR_NAME, id, name);
    }
}

