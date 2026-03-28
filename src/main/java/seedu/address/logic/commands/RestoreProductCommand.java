package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.ParserUtil.SEPARATOR_NEW_LINE;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.product.Product;

/**
 * Restores an archived product in the inventory.
 * The product is identified using its identifier.
 *
 * Usage: restoreproduct IDENTIFIER
 * Example: restoreproduct p1
 */
public class RestoreProductCommand extends Command {

    public static final String COMMAND_WORD = "restoreproduct";
    public static final String COMMAND_USAGE = COMMAND_WORD + " [IDENTIFIER]";
    public static final String COMMAND_DESCRIPTION =
            "Restores an archived product; lists all archived if no identifier given";

    public static final String MESSAGE_USAGE =
            COMMAND_WORD + " IDENTIFIER\n"
                    + "Restores the archived product identified by the identifier.\n"
                    + "Example: " + COMMAND_WORD + " p1";

    private static final String MESSAGE_RESTORE_SUCCESS =
            "Restored Product: %1$s";

    private static final String MESSAGE_IDENTIFIER_REQUIRED =
            "Please provide the identifier of the product to restore.";

    private static final String MESSAGE_PRODUCT_NOT_FOUND =
            "No archived product found with identifier: %1$s";

    private static final String MESSAGE_ARCHIVED_LIST =
            "Archived products listed on the right.";

    private final String identifier;

    /**
     * Creates a RestoreProductCommand to restore the specified product.
     *
     * @param identifier Identifier of the product to restore.
     */
    public RestoreProductCommand(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (identifier == null || identifier.isEmpty()) {
            model.updateFilteredProductList(Product::isArchived);

            throw new CommandException(
                    MESSAGE_IDENTIFIER_REQUIRED + SEPARATOR_NEW_LINE + MESSAGE_ARCHIVED_LIST
            );
        }

        Product productToRestore = model.getInventory().getProductList().stream()
                .filter(p -> p.isArchived()
                        && p.getIdentifier().value.equals(identifier))
                .findFirst()
                .orElse(null);

        if (productToRestore == null) {
            model.updateFilteredProductList(Product::isArchived);

            throw new CommandException(
                    String.format(MESSAGE_PRODUCT_NOT_FOUND, identifier)
                            + SEPARATOR_NEW_LINE + MESSAGE_ARCHIVED_LIST
            );
        }

        model.restoreProduct(productToRestore);
        String successPart = String.format(MESSAGE_RESTORE_SUCCESS, productToRestore);
        model.commitVendorVault(successPart);

        return new CommandResult(successPart, CommandResult.FEEDBACK_TYPE_SUCCESS);
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }
}
