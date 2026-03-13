package seedu.address.model.product.warnings;

/**
 * Represents a warning that is generated when a similar product is detected.
 */
public class DuplicateProductWarning {

    public static final String MESSAGE_SIMILAR_NAME = "⚠ Warning: There's a product with a similar name"
            + " (id: %s, name: %s), is this intentional?";

    private final boolean value;
    private final String warning;

    /**
     * Constructs a {@code DuplicateProductWarning} with the specified value and warning message.
     *
     * @param value   Whether a potential duplicate is detected.
     * @param warning The warning message associated with the duplicate detection.
     */
    public DuplicateProductWarning(boolean value, String warning) {
        this.value = value;
        this.warning = warning;
    }

    public boolean getValue() {
        return value;
    }

    public String getWarning() {
        return warning;
    }
}

