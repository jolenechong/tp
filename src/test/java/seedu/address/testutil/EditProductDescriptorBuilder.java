package seedu.address.testutil;

import seedu.address.logic.commands.EditProductCommand.EditProductDescriptor;
import seedu.address.model.person.Email;
import seedu.address.model.product.Name;
import seedu.address.model.product.Quantity;
import seedu.address.model.product.RestockThreshold;

/**
 * A utility class for constructing {@link EditProductDescriptor} objects in tests.
 *
 * This builder follows the builder pattern to simplify the creation of
 * {@code EditProductDescriptor} instances by allowing fields to be set
 * incrementally through chained method calls.</p>
 */
public class EditProductDescriptorBuilder {

    private final EditProductDescriptor descriptor;

    /**
     * Creates a new {@code EditProductDescriptorBuilder} with an empty descriptor.
     */
    public EditProductDescriptorBuilder() {
        descriptor = new EditProductDescriptor();
    }

    /**
     * Creates a new {@code EditProductDescriptorBuilder} using the values
     * from an existing {@code EditProductDescriptor}.
     *
     * @param descriptor The descriptor to copy values from.
     */
    public EditProductDescriptorBuilder(EditProductDescriptor descriptor) {
        this.descriptor = new EditProductDescriptor(descriptor);
    }

    /**
     * Sets the {@link Name} of the product in the descriptor.
     *
     * @param name The product name.
     * @return This builder instance for chaining.
     */
    public EditProductDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Sets the {@link Quantity} of the product in the descriptor.
     *
     * @param quantity The product quantity.
     * @return This builder instance for chaining.
     */
    public EditProductDescriptorBuilder withQuantity(String quantity) {
        descriptor.setQuantity(new Quantity(quantity));
        return this;
    }

    /**
     * Sets the {@link RestockThreshold} of the product in the descriptor.
     *
     * @param threshold The restock threshold value.
     * @return This builder instance for chaining.
     */
    public EditProductDescriptorBuilder withThreshold(String threshold) {
        descriptor.setThreshold(new RestockThreshold(threshold));
        return this;
    }

    /**
     * Sets the vendor {@link Email} for the product.
     *
     * @param email The vendor email address.
     * @return This builder instance for chaining.
     */
    public EditProductDescriptorBuilder withVendorEmail(String email) {
        descriptor.setVendorEmail(new Email(email));
        return this;
    }

    /**
     * Clears the vendor email from the descriptor.
     *
     * @return This builder instance for chaining.
     */
    public EditProductDescriptorBuilder withoutVendorEmail() {
        descriptor.setVendorEmail(null);
        return this;
    }

    /**
     * Builds and returns a new {@link EditProductDescriptor} instance.
     *
     * @return A copy of the constructed descriptor.
     */
    public EditProductDescriptor build() {
        return new EditProductDescriptor(descriptor);
    }
}
