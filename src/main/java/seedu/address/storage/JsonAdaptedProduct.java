package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Email;
import seedu.address.model.product.Identifier;
import seedu.address.model.product.Name;
import seedu.address.model.product.Product;
import seedu.address.model.product.Quantity;
import seedu.address.model.product.RestockThreshold;

/**
 * Jackson-friendly version of {@link Product}
 */
public class JsonAdaptedProduct {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Product's %s field is missing";

    // Identity fields
    private final String identifier;

    // Data fields
    private final String name;
    private final String quantity;
    private final String threshold;
    private final String vendorEmail;
    private final boolean isArchived;

    /**
     * Constructs a {@Code JsonAdaptedProduct} with the given product details.
     */
    @JsonCreator
    public JsonAdaptedProduct(@JsonProperty("identifier") String identifier,
                              @JsonProperty("name") String name,
                              @JsonProperty("quantity") String quantity,
                              @JsonProperty("threshold") String threshold,
                              @JsonProperty("vendorEmail") String vendorEmail,
                              @JsonProperty("isArchived") boolean isArchived) {
        this.identifier = identifier;
        this.name = name;
        this.quantity = quantity;
        this.threshold = threshold;
        this.vendorEmail = vendorEmail;
        this.isArchived = isArchived;
    }

    /**
     * Converts a given {@code Product} into this class for Jackson use.
     */
    public JsonAdaptedProduct(Product product) {
        this.identifier = product.getIdentifier().toString();
        this.name = product.getName().fullName;
        this.quantity = product.getQuantity().toString();
        this.threshold = product.getRestockThreshold().toString();
        this.vendorEmail = product.getVendorEmail().map(email -> email.value).orElse(null);
        this.isArchived = product.isArchived();
    }

    String getIdentifier() {
        return identifier;
    }

    /**
     * Converts this Jackson-friendly adapted product into the model's {@code Product} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted product.
     */
    public Product toModelType() throws IllegalValueException {
        if (identifier == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    Identifier.class.getSimpleName()));
        }
        if (!Identifier.isValidIdentifier(identifier)) {
            throw new IllegalValueException(Identifier.MESSAGE_CONSTRAINTS);
        }
        final Identifier modelIdentifier = new Identifier(identifier);

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (quantity == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    Quantity.class.getSimpleName()));
        }
        if (!Quantity.isValidQuantity(quantity)) {
            throw new IllegalValueException(Quantity.MESSAGE_CONSTRAINTS);
        }
        final Quantity modelQuantity = new Quantity(quantity);

        if (threshold == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    RestockThreshold.class.getSimpleName()));
        }
        if (!RestockThreshold.isValidRestockThreshold(threshold)) {
            throw new IllegalValueException(RestockThreshold.MESSAGE_CONSTRAINTS);
        }
        final RestockThreshold modelThreshold = new RestockThreshold(threshold);

        Email modelVendorEmail = null;
        if (vendorEmail != null) {
            if (!Email.isValidEmail(vendorEmail)) {
                throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
            }
            modelVendorEmail = new Email(vendorEmail);
        }

        return new Product(modelIdentifier, modelName, modelQuantity, modelThreshold, modelVendorEmail, isArchived);
    }
}
