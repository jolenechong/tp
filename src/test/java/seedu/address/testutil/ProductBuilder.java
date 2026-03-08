package seedu.address.testutil;

import seedu.address.model.product.Identifier;
import seedu.address.model.product.Name;
import seedu.address.model.product.Product;
import seedu.address.model.product.Quantity;

/**
 * A utility class to help with building Product objects.
 */
public class ProductBuilder {

    public static final String DEFAULT_IDENTIFIER = "A1";
    public static final String DEFAULT_NAME = "iPad";
    public static final String DEFAULT_QUANTITY = "0";

    private Name name;
    private Quantity quantity;
    private Identifier identifier;

    /**
     * Creates a {@code ProductBuilder} with the default details.
     */
    public ProductBuilder() {
        identifier = new Identifier(DEFAULT_IDENTIFIER);
        name = new Name(DEFAULT_NAME);
        quantity = new Quantity(DEFAULT_QUANTITY);
    }

    /**
     * Initializes the ProductBuilder with the data of {@code productToCopy}.
     */
    public ProductBuilder(Product productToCopy) {
        identifier = productToCopy.getIdentifier();
        name = productToCopy.getName();
        quantity = productToCopy.getQuantity();
    }

    /**
     * Sets the {@code Identifier} of the {@code Product} that we are building.
     */
    public ProductBuilder withIdentifier(String identifier) {
        this.identifier = new Identifier(identifier);
        return this;
    }

    /**
     * Sets the {@code Name} of the {@code Product} that we are building.
     */
    public ProductBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Sets the {@code Quantity} of the {@code Product} that we are building.
     */
    public ProductBuilder withQuantity(String quantity) {
        this.quantity = new Quantity(quantity);
        return this;
    }

    public Product build() {
        return new Product(identifier, name, quantity);
    }

}
