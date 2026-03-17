package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Email;
import seedu.address.model.product.Name;
import seedu.address.model.product.Product;
import seedu.address.model.product.Quantity;
import seedu.address.model.product.RestockThreshold;

/**
 * Edits the details of an existing {@link Product} in the inventory.
 *
 * The product is identified using its unique identifier.
 * Only fields provided in the {@link EditProductDescriptor} will be edited.
 *
 * Editable fields:
 * - Name
 * - Quantity
 * - Restock threshold
 * - Vendor email
 *
 * Vendor email is optional and may be cleared.
 * If provided, the system checks that the vendor email exists in contacts.
 */
public class EditProductCommand extends Command {

    public static final String COMMAND_WORD = "editproduct";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the details of the product identified by its identifier.\n"
            + "Example: editproduct SKU-1001 n/iPad q/15 th/5 e/vendor@email.com";

    public static final String MESSAGE_EDIT_PRODUCT_SUCCESS = "Edited Product: %1$s";
    public static final String MESSAGE_INVALID_PRODUCT_ID =
            "No product found with the specified identifier.";
    public static final String MESSAGE_NOT_EDITED =
            "At least one field to edit must be provided.";
    public static final String MESSAGE_VENDOR_EMAIL_NOT_FOUND =
            "No contact with the specified email was found.";

    private final String targetIdentifier;
    private final EditProductDescriptor editProductDescriptor;

    /**
     * Creates an {@code EditProductCommand}.
     *
     * @param targetIdentifier identifier of the product to edit
     * @param editProductDescriptor descriptor containing fields to edit
     */
    public EditProductCommand(String targetIdentifier,
                              EditProductDescriptor editProductDescriptor) {

        requireNonNull(targetIdentifier);
        requireNonNull(editProductDescriptor);

        if (!editProductDescriptor.isAnyFieldEdited()) {
            throw new IllegalArgumentException(MESSAGE_NOT_EDITED);
        }

        this.targetIdentifier = targetIdentifier;
        this.editProductDescriptor = new EditProductDescriptor(editProductDescriptor);
    }

    /**
     * Executes the edit command.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Product productToEdit = model.getInventory().getProductList().stream()
                .filter(p -> p.getIdentifier().value.equals(targetIdentifier))
                .findFirst()
                .orElseThrow(() -> new CommandException(MESSAGE_INVALID_PRODUCT_ID));

        Product editedProduct = createEditedProduct(productToEdit, editProductDescriptor, model);

        model.setProduct(productToEdit, editedProduct);

        model.updateFilteredProductList(Model.PREDICATE_SHOW_ACTIVE_PRODUCTS);
        model.commitVendorVault();

        return new CommandResult(
                String.format(MESSAGE_EDIT_PRODUCT_SUCCESS,
                        Messages.formatProduct(editedProduct)));
    }

    /**
     * Creates the edited product based on descriptor values.
     */
    private static Product createEditedProduct(Product productToEdit,
                                               EditProductDescriptor descriptor,
                                               Model model)
            throws CommandException {

        Name updatedName =
                descriptor.getName().orElse(productToEdit.getName());

        Quantity updatedQuantity =
                descriptor.getQuantity().orElse(productToEdit.getQuantity());

        RestockThreshold updatedThreshold =
                descriptor.getThreshold().orElse(productToEdit.getRestockThreshold());

        Email updatedVendorEmail =
                getUpdatedVendorEmail(productToEdit, descriptor, model);

        return new Product(
                productToEdit.getIdentifier(),
                updatedName,
                updatedQuantity,
                updatedThreshold,
                updatedVendorEmail,
                productToEdit.isArchived());
    }

    /**
     * Determines the updated vendor email after edit.
     */
    private static Email getUpdatedVendorEmail(Product productToEdit,
                                               EditProductDescriptor descriptor,
                                               Model model)
            throws CommandException {

        if (!descriptor.isVendorEmailEdited()) {
            return productToEdit.getVendorEmail().orElse(null);
        }

        Email updatedEmail = descriptor.getVendorEmail().orElse(null);

        if (updatedEmail == null) {
            return null;
        }

        if (model.findByEmail(updatedEmail).isEmpty()) {
            throw new CommandException(MESSAGE_VENDOR_EMAIL_NOT_FOUND);
        }

        return updatedEmail;
    }

    /**
     * Required by Command architecture.
     */
    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof EditProductCommand)) {
            return false;
        }

        EditProductCommand otherCommand = (EditProductCommand) other;

        return targetIdentifier.equals(otherCommand.targetIdentifier)
                && editProductDescriptor.equals(otherCommand.editProductDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIdentifier", targetIdentifier)
                .add("editProductDescriptor", editProductDescriptor)
                .toString();
    }

    /**
     * Stores fields to edit for a product.
     */
    public static class EditProductDescriptor {

        private Name name;
        private Quantity quantity;
        private RestockThreshold threshold;
        private Email vendorEmail;
        private boolean vendorEmailEdited;

        public EditProductDescriptor() {}

        /**
         * Copy constructor.
         */
        public EditProductDescriptor(EditProductDescriptor toCopy) {
            setName(toCopy.name);
            setQuantity(toCopy.quantity);
            setThreshold(toCopy.threshold);

            if (toCopy.vendorEmailEdited) {
                setVendorEmail(toCopy.vendorEmail);
            }
        }

        /**
         * Returns true if any field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, quantity, threshold)
                    || vendorEmailEdited;
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setQuantity(Quantity quantity) {
            this.quantity = quantity;
        }

        public Optional<Quantity> getQuantity() {
            return Optional.ofNullable(quantity);
        }

        public void setThreshold(RestockThreshold threshold) {
            this.threshold = threshold;
        }

        public Optional<RestockThreshold> getThreshold() {
            return Optional.ofNullable(threshold);
        }

        /**
         * Sets vendor email.
         * Null clears the vendor email.
         */
        public void setVendorEmail(Email vendorEmail) {
            this.vendorEmail = vendorEmail;
            this.vendorEmailEdited = true;
        }

        public Optional<Email> getVendorEmail() {
            return Optional.ofNullable(vendorEmail);
        }

        public boolean isVendorEmailEdited() {
            return vendorEmailEdited;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            if (!(other instanceof EditProductDescriptor)) {
                return false;
            }

            EditProductDescriptor e = (EditProductDescriptor) other;

            return Objects.equals(name, e.name)
                    && Objects.equals(quantity, e.quantity)
                    && Objects.equals(threshold, e.threshold)
                    && Objects.equals(vendorEmail, e.vendorEmail)
                    && vendorEmailEdited == e.vendorEmailEdited;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("quantity", quantity)
                    .add("threshold", threshold)
                    .add("vendorEmail", vendorEmail)
                    .toString();
        }
    }
}
