package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;
import seedu.address.model.product.Product;
import seedu.address.model.util.SampleDataUtil;

/**
 * Lists all products in the inventory to the user.
 *
 * Usage: listproducts
 *
 * Example:
 * {@code listproducts}
 */
public class ListProductsCommand extends Command {

    public static final String COMMAND_WORD = "listproducts";

    public static final String MESSAGE_SUCCESS = "Listed all products";

    /**
     * Executes the listproducts command.
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
        //If the GUI currently displays no products, sample products will be automatically populated.
        if (model.getInventory().getProductList().isEmpty()) {
            for (Product product : SampleDataUtil.getSampleProducts()) {
                model.addProduct(product);
            }
        }

        model.updateFilteredProductList(Model.PREDICATE_SHOW_ACTIVE_PRODUCTS);
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }
}
