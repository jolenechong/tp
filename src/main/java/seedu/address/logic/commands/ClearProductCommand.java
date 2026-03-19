package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE_PRODUCTS;

import java.util.Optional;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Inventory;
import seedu.address.model.Model;

/**
 * Clears all products from the inventory.
 *
 * <p>This command requires user confirmation before deleting all products.
 * If the user confirms, all products will be removed from the model and
 * the change will be committed to the {@code VendorVault}.</p>
 */
public class ClearProductCommand extends Command {

    /** Command word used to trigger this command. */
    public static final String COMMAND_WORD = "clearproduct";

    /** Message shown when asking the user for confirmation. */
    public static final String MESSAGE_CONFIRMATION =
            "Confirm (y) you want to delete ALL products.";

    /** Message shown when the operation succeeds. */
    public static final String MESSAGE_SUCCESS =
            "All products have been deleted.";

    /** Message shown when the operation is cancelled. */
    public static final String MESSAGE_CANCELLED =
            "Clear product operation cancelled.";

    /** Stores the pending confirmation for this command. */
    private PendingConfirmation pendingConfirmation = new PendingConfirmation();

    /** Indicates whether confirmation is required before clearing products. */
    private final boolean needsConfirmation;

    /**
     * Creates a {@code ClearProductCommand}.
     *
     * @param needsConfirmation whether the command requires confirmation before execution
     */
    public ClearProductCommand(boolean needsConfirmation) {
        this.needsConfirmation = needsConfirmation;
    }

    /**
     * Executes the clear product command.
     *
     * <p>If confirmation is required, a {@link PendingConfirmation} will be created
     * and the user will be prompted to confirm the operation. If confirmation is
     * not required, all products will be cleared immediately.</p>
     *
     * @param model the model which the command should operate on
     * @return the result of command execution
     * @throws CommandException if an error occurs during execution
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!needsConfirmation) {
            return clearProducts(model);
        }

        pendingConfirmation = new PendingConfirmation(() ->
            this.onConfirm(model), () ->
            this.onCancel(model)
        );

        return new CommandResult(MESSAGE_CONFIRMATION);
    }

    /**
     * Deletes all products from the model and commits the change.
     *
     * @param model the model to modify
     * @return the result of the clear operation
     */
    private CommandResult clearProducts(Model model) {
        model.setInventory(new Inventory());
        model.commitVendorVault();
        model.updateFilteredProductList(PREDICATE_SHOW_ACTIVE_PRODUCTS);

        return new CommandResult(MESSAGE_SUCCESS);
    }

    /**
     * Executes when the user confirms the clear operation.
     *
     * @param model the model to update
     * @return the command result wrapped in an {@link Optional}
     */
    private Optional<CommandResult> onConfirm(Model model) {
        return Optional.of(clearProducts(model));
    }

    /**
     * Executes when the user cancels the clear operation.
     *
     * @param model the model to update
     * @return the command result wrapped in an {@link Optional}
     */
    private Optional<CommandResult> onCancel(Model model) {
        return Optional.of(new CommandResult(MESSAGE_CANCELLED));
    }

    /**
     * Returns the {@link PendingConfirmation} associated with this command.
     *
     * @return the pending confirmation object
     */
    @Override
    public PendingConfirmation getPendingConfirmation() {
        return pendingConfirmation;
    }

    /**
     * Returns true if both commands have the same confirmation requirement.
     *
     * @param other the object to compare
     * @return true if the commands are equal
     */
    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof ClearProductCommand
                && needsConfirmation == ((ClearProductCommand) other).needsConfirmation);
    }

    /**
     * Returns the string representation of the command.
     *
     * @return string representation of the command
     */
    @Override
    public String toString() {
        return "ClearProductCommand(needsConfirmation=" + needsConfirmation + ")";
    }
}
