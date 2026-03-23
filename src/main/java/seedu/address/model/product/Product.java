package seedu.address.model.product;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.logic.parser.ParserUtil;
import seedu.address.model.person.Email;
import seedu.address.model.product.warnings.DuplicateProductWarning;

/**
 * Represents a Product in the inventory.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Product {

    // Identity fields
    private final Identifier identifier;

    // Data fields
    private final Name name;
    private final Quantity quantity;
    private final RestockThreshold threshold;
    private final Email vendorEmail;
    private final boolean isArchived;

    /**
     * Creates a Product that is not archived.
     */
    public Product(Identifier identifier, Name name, Quantity quantity, RestockThreshold threshold) {
        this(identifier, name, quantity, threshold, null, false);
    }

    /**
     * Creates a Product with optional vendor email that is not archived.
     */
    public Product(Identifier identifier, Name name, Quantity quantity, RestockThreshold threshold,
                   Email vendorEmail) {
        this(identifier, name, quantity, threshold, vendorEmail, false);
    }

    /**
     * Creates a Product with the specified archived status.
     * Every field must be present and not null.
     */
    public Product(Identifier identifier, Name name, Quantity quantity, RestockThreshold threshold,
                   boolean isArchived) {
        this(identifier, name, quantity, threshold, null, isArchived);
    }

    /**
     * Creates a Product with optional vendor email and archived status.
     */
    public Product(Identifier identifier, Name name, Quantity quantity, RestockThreshold threshold,
                   Email vendorEmail, boolean isArchived) {
        requireAllNonNull(identifier, name, quantity, threshold);
        this.identifier = identifier;
        this.name = name;
        this.quantity = quantity;
        this.threshold = threshold;
        this.vendorEmail = vendorEmail;
        this.isArchived = isArchived;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Name getName() {
        return name;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public RestockThreshold getRestockThreshold() {
        return threshold;
    }

    public Optional<Email> getVendorEmail() {
        return Optional.ofNullable(vendorEmail);
    }

    public boolean isArchived() {
        return isArchived;
    }

    public Product archive() {
        return new Product(identifier, name, quantity, threshold, vendorEmail, true);
    }

    public Product restore() {
        return new Product(identifier, name, quantity, threshold, vendorEmail, false);
    }

    /**
     * Returns a copy of this product with no associated vendor email.
     */
    public Product clearVendorEmail() {
        return new Product(identifier, name, quantity, threshold, null, isArchived);
    }

    /**
     * Returns a copy of this product with the given vendor email.
     */
    public Product withVendorEmail(Email vendorEmail) {
        requireNonNull(vendorEmail);
        return new Product(identifier, name, quantity, threshold, vendorEmail, isArchived);
    }

    /**
     * Returns a {@code DuplicateProductWarning} if this product has a similar name to {@code otherProduct}.
     */
    public DuplicateProductWarning generateDuplicateWarning(Product otherProduct) {
        requireNonNull(otherProduct);

        return new DuplicateProductWarning(
                hasSimilarName(otherProduct),
                DuplicateProductWarning.MESSAGE_SIMILAR_NAME);
    }

    /**
     * Returns true if this product has a name similar to the specified {@code otherProduct}.
     * <p>
     * Two names are considered similar if they share one or more words.
     * </p>
     *
     * @param otherProduct the product to compare with
     * @return {@code true} if the names are similar, {@code false} otherwise
     */
    public boolean isSimilarNameTo(Product otherProduct) {
        requireNonNull(otherProduct);
        return hasSimilarName(otherProduct);
    }

    private boolean hasSimilarName(Product otherProduct) {
        String thisName = this.name.fullName.toLowerCase().trim();
        String otherName = otherProduct.getName().fullName.toLowerCase().trim();

        if (thisName.equals(otherName)) {
            return true;
        }

        String[] thisParts = thisName.split(ParserUtil.SEPARATOR_SPACE);
        String[] otherParts = otherName.split(ParserUtil.SEPARATOR_SPACE);

        Set<String> nameParts = new HashSet<>(java.util.Arrays.asList(thisParts));

        return Arrays.stream(otherParts).anyMatch(nameParts::contains);
    }

    /**
     * Returns true if both products have the same identifier.
     * This defines a weaker notion of equality between two products.
     */
    public boolean isSameProduct(Product otherProduct) {
        if (otherProduct == this) {
            return true;
        }

        return otherProduct != null
                && otherProduct.getIdentifier().equals(getIdentifier());
    }

    /**
     * Returns true if both products have the same identity and data fields.
     * This defines a stronger notion of equality between two products.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Product)) {
            return false;
        }

        Product otherProduct = (Product) other;
        return identifier.equals(otherProduct.identifier)
                && name.equals(otherProduct.name)
                && quantity.equals(otherProduct.quantity)
                && threshold.equals(otherProduct.threshold)
                && Objects.equals(vendorEmail, otherProduct.vendorEmail)
                && isArchived == otherProduct.isArchived;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, name, quantity, threshold, vendorEmail, isArchived);
    }

    @Override
    public String toString() {
        String emailStr = getVendorEmail().map(Email::toString).orElse("-");

        return String.format("%s; ID: %s; Qty: %s; Threshold: %s; Email: %s",
                name,
                identifier,
                quantity,
                threshold,
                emailStr);
    }
}
