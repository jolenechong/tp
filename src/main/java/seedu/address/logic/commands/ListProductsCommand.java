package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Lists all products in the inventory to the user.
 *
 * Usage: listproduct
 *
 * Example:
 * {@code listproduct}
 */
public class ListProductsCommand extends Command {

    public static final String COMMAND_WORD = "listproduct";

    public static final String INVALID_COMMAND_WORD = "listproducts";

    public static final String MESSAGE_SUCCESS = "Listed all products";

    public static final String INVALID_COMMAND_SUGGESTION =
        "Unknown command: listproduct. Did you mean 'listproducts'?";


    /**
     * Executes the listproduct command.
     *
     * Updates the filtered product list in the model so that all products
     * in the inventory are displayed to the user.
     *
     * @param model {@code Model} which the command should operate on.
     * @return CommandResult indicating the result of command execution.
     */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        model.updateFilteredProductList(Model.PREDICATE_SHOW_ACTIVE_PRODUCTS);
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }
}
