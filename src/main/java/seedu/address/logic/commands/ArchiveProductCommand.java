package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.product.Product;

/**
 * Archives a product in the inventory.
 * The product is identified using its identifier.
 *
 * Usage: {@code archiveproduct IDENTIFIER}
 * Example: {@code archiveproduct DE/5}
 */
public class ArchiveProductCommand extends Command {

    public static final String COMMAND_WORD = "archiveproduct";
    public static final String COMMAND_USAGE = COMMAND_WORD + " IDENTIFIER";
    public static final String COMMAND_DESCRIPTION = "Archives a product.";

    public static final String MESSAGE_USAGE =
        COMMAND_WORD + " IDENTIFIER\n"
        + "Archives the product identified by the identifier.\n"
        + "Example: " + COMMAND_WORD + " DE/5";

    public static final String MESSAGE_ACTION_SUMMARY =
        "archiving of product: %1$s";


    private static final String MESSAGE_ARCHIVE_SUCCESS =
        "Archived Product: %1$s";

    private static final String MESSAGE_PRODUCT_NOT_FOUND =
        "No product found with identifier.";

    private static final String MESSAGE_ALREADY_ARCHIVED =
        "This product is already archived. Did you want to restore it?";

    private final String identifier;


    /**
     * Creates an ArchiveProductCommand to archive the product with the specified {@code identifier}.
     *
     * @param identifier Identifier of the product to archive. Must not be null.
     */
    public ArchiveProductCommand(String identifier) {
        requireNonNull(identifier);
        this.identifier = identifier;
    }

    /**
     * Executes the archive product command.
     *
     * Searches the full product list for a product matching the given identifier.
     * If the product is already archived, an informative error is shown suggesting restore.
     * If no product is found, an error message is shown.
     * Otherwise, the product is archived and a success message is returned.
     *
     * @param model {@code Model} which the command should operate on. Must not be null.
     * @return {@code CommandResult} indicating the result of the command execution.
     * @throws CommandException if no product is found with the given identifier,
     *                          or if the product is already archived.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Product productToArchive = model.getInventory().getProductList().stream()
                .filter(p -> p.getIdentifier().value.equals(identifier))
                .findFirst()
                .orElseThrow(() -> new CommandException(MESSAGE_PRODUCT_NOT_FOUND));

        if (productToArchive.isArchived()) {
            throw new CommandException(MESSAGE_ALREADY_ARCHIVED);
        }

        model.archiveProduct(productToArchive);

        String actionSummary = String.format(MESSAGE_ACTION_SUMMARY, productToArchive);
        model.commitVendorVault(actionSummary);

        String successMessage = String.format(MESSAGE_ARCHIVE_SUCCESS, Messages.formatProduct(productToArchive));
        return new CommandResult(successMessage, CommandResult.FEEDBACK_TYPE_SUCCESS);
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
