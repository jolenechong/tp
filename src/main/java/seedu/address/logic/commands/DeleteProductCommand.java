package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE_PRODUCTS;

import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.product.Product;

/**
 * Deletes a product identified using its product ID.
 *
 * This command follows the same confirmation workflow as {@code DeleteCommand}.
 * When executed, the command first displays the product to be deleted and asks the user
 * to confirm by typing {@code y}. If confirmed, the product is permanently removed from
 * the inventory.
 */
public class DeleteProductCommand extends Command {

    public static final String COMMAND_WORD = "deleteproduct";
    public static final String COMMAND_USAGE = COMMAND_WORD + " IDENTIFIER";
    public static final String COMMAND_DESCRIPTION = "Deletes a product.";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the product identified by its product identifier.\n"
            + "Parameters: PRODUCT_IDENTIFIER\n"
            + "Example: " + COMMAND_WORD + " P001";

    public static final String MESSAGE_DELETE_PRODUCT_SUCCESS = "Deleted Product: %1$s";

    public static final String CONFIRMATION_DELETE_PRODUCT_MESSAGE =
            "Confirm (y) you want to delete the following product shown below:";

    public static final String MESSAGE_DELETE_FAILURE = "Did not delete product";

    public static final String MESSAGE_INVALID_PRODUCT_ID = "No product found with the specified identifier.";

    private PendingConfirmation pendingConfirmation = new PendingConfirmation();

    private final String targetProductId;

    private final boolean needsConfirmation;

    /**
     * Creates a {@code DeleteProductCommand}.
     *
     * @param targetProductId ID of the product to delete.
     * @param needsConfirmation whether the command should ask the user for confirmation.
     */
    public DeleteProductCommand(String targetProductId, boolean needsConfirmation) {
        this.targetProductId = targetProductId;
        this.needsConfirmation = needsConfirmation;
    }

    /**
     * Executes the delete product command.
     *
     * If confirmation is required, the command sets up a {@link PendingConfirmation}
     * and displays the product to be deleted. Otherwise, the deletion is executed immediately.
     *
     * @param model {@link Model} which the command should operate on
     * @return the result of command execution
     * @throws CommandException if the specified product cannot be found
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Product productToDelete = model.getFilteredProductList().stream()
            .filter(p -> p.getIdentifier().toString().equals(targetProductId))
            .findFirst()
            .orElseThrow(() ->
                    new CommandException(MESSAGE_INVALID_PRODUCT_ID));

        if (!this.needsConfirmation) {
            return this.deleteProduct(model, productToDelete);
        }

        pendingConfirmation = new PendingConfirmation(() ->
            this.onConfirm(model, productToDelete), () ->
            this.onCancel(model)
        );

        model.updateFilteredProductList(p ->
            p.getIdentifier().toString().equals(targetProductId));

        return new CommandResult(CONFIRMATION_DELETE_PRODUCT_MESSAGE);
    }

    /**
     * Deletes the specified product from the model and commits the change.
     *
     * @param model the model to modify
     * @param productToDelete the product to delete
     * @return the result of the deletion command
     */
    private CommandResult deleteProduct(Model model, Product productToDelete) {
        model.deleteProduct(productToDelete);
        String successPart = String.format(MESSAGE_DELETE_PRODUCT_SUCCESS, productToDelete);
        model.commitVendorVault(successPart);
        model.updateFilteredProductList(PREDICATE_SHOW_ACTIVE_PRODUCTS);

        return new CommandResult(
                String.format(MESSAGE_DELETE_PRODUCT_SUCCESS, productToDelete));
    }

    /**
     * Executes when the user confirms the deletion.
     *
     * @param model the model to modify
     * @param productToDelete the product to delete
     * @return the command result wrapped in an {@link Optional}
     */
    private Optional<CommandResult> onConfirm(Model model, Product productToDelete) {
        return Optional.of(this.deleteProduct(model, productToDelete));
    }

    /**
     * Executes when the user cancels the deletion.
     *
     * @param model the model to update
     * @return the command result wrapped in an {@link Optional}
     */
    private Optional<CommandResult> onCancel(Model model) {
        model.updateFilteredProductList(PREDICATE_SHOW_ACTIVE_PRODUCTS);
        return Optional.of(new CommandResult(MESSAGE_DELETE_FAILURE));
    }

    /**
     * Returns the {@link PendingConfirmation} associated with this command.
     */
    @Override
    public PendingConfirmation getPendingConfirmation() {
        return this.pendingConfirmation;
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof DeleteProductCommand
                && targetProductId.equals(((DeleteProductCommand) other).targetProductId));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("productId", targetProductId)
                .add("needsConfirmation", needsConfirmation)
                .toString();
    }
}
