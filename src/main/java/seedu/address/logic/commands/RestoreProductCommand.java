package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.ParserUtil.SEPARATOR_NEW_LINE;

import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.product.Product;

/**
 * Restores an archived product in the inventory.
 * The product is identified using its identifier.
 *
 * If no identifier is provided, the archived product list is shown.
 * If there are no archived products, the user is informed and encouraged to archive first.
 *
 * Usage: {@code restoreproduct [IDENTIFIER]}
 * Example: {@code restoreproduct DE/5}
 */
public class RestoreProductCommand extends Command {

    public static final String COMMAND_WORD = "restoreproduct";
    public static final String COMMAND_USAGE = COMMAND_WORD + " [IDENTIFIER]";
    public static final String COMMAND_DESCRIPTION =
            "Restores an archived product; lists all archived if no identifier given";

    public static final String MESSAGE_USAGE =
            COMMAND_WORD + " IDENTIFIER\n"
                    + "Restores the archived product identified by the identifier.\n"
                    + "Example: " + COMMAND_WORD + " DE/5";

    public static final String MESSAGE_ACTION_SUMMARY =
            "restoring of product: %1$s";

    public static final String MESSAGE_LIST_ARCHIVED =
            "Archived products listed below. Use 'restoreproduct IDENTIFIER' to restore one.";

    public static final String MESSAGE_NONE_ARCHIVED =
            "No products are currently archived.\n"
            + "Use 'archiveproduct IDENTIFIER' to archive a product first.";

    private static final String MESSAGE_RESTORE_SUCCESS =
            "Restored Product: %1$s";

    private static final String MESSAGE_IDENTIFIER_REQUIRED =
            "Please provide the identifier of the product to restore.";

    private static final String MESSAGE_PRODUCT_NOT_FOUND =
            "No archived product found with identifier.";

    private final String identifier;

    /**
     * Creates a RestoreProductCommand to restore the archived product with the specified {@code identifier}.
     *
     * Pass {@code null} to list all archived products without restoring.
     *
     * @param identifier Identifier of the product to restore, or {@code null} to list archived products.
     */
    public RestoreProductCommand(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Executes the restore product command.
     *
     * If no identifier is provided, all archived products are shown.
     * If there are no archived products, the user is informed and encouraged to archive first.
     * If an identifier is provided but does not match any archived product, the archived list is shown
     * and an error is returned.
     * Otherwise, the matched product is restored.
     *
     * @param model {@code Model} which the command should operate on. Must not be null.
     * @return {@code CommandResult} indicating the result of the command execution.
     * @throws CommandException if no archived product matches the given identifier.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (identifier == null || identifier.isEmpty()) {
            List<Product> archivedProducts = model.getInventory().getProductList().stream()
                    .filter(Product::isArchived)
                    .toList();

            if (archivedProducts.isEmpty()) {
                return new CommandResult(MESSAGE_NONE_ARCHIVED, CommandResult.FEEDBACK_TYPE_SUCCESS);
            }

            model.updateFilteredProductList(Product::isArchived);
            return new CommandResult(MESSAGE_LIST_ARCHIVED, CommandResult.FEEDBACK_TYPE_SUCCESS);
        }

        Product productToRestore = model.getInventory().getProductList().stream()
                .filter(p -> p.isArchived()
                        && p.getIdentifier().value.equals(identifier))
                .findFirst()
                .orElse(null);

        if (productToRestore == null) {
            model.updateFilteredProductList(Product::isArchived);
            throw new CommandException(
                    MESSAGE_PRODUCT_NOT_FOUND + SEPARATOR_NEW_LINE + MESSAGE_LIST_ARCHIVED
            );
        }

        model.restoreProduct(productToRestore);

        String actionSummary = String.format(MESSAGE_ACTION_SUMMARY, productToRestore);
        model.commitVendorVault(actionSummary);

        String successPart = String.format(MESSAGE_RESTORE_SUCCESS, productToRestore);
        return new CommandResult(successPart, CommandResult.FEEDBACK_TYPE_SUCCESS);
    }

    /**
     * Returns the pending confirmation state for this command.
     *
     * @return a new {@code PendingConfirmation} instance.
     */
    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }
}
