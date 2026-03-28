package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.ParserUtil.SEPARATOR_NEW_LINE;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.product.Product;

/**
 * Archives a product in the inventory.
 * The product is identified using its identifier.
 *
 * Usage: archiveproduct IDENTIFIER
 * Example: archiveproduct p1
 */
public class ArchiveProductCommand extends Command {

    public static final String COMMAND_WORD = "archiveproduct";
    public static final String COMMAND_USAGE = COMMAND_WORD + " IDENTIFIER";
    public static final String COMMAND_DESCRIPTION = "Archives a product.";

    public static final String MESSAGE_USAGE =
            COMMAND_WORD + " IDENTIFIER\n"
                    + "Archives the product identified by the identifier.\n"
                    + "Example: " + COMMAND_WORD + " p1";

    private static final String MESSAGE_ARCHIVE_SUCCESS =
            "Archived Product: %1$s";

    private static final String MESSAGE_ARCHIVE_WARNING =
            "⚠ Warning: Use 'restoreproduct %2$s' to restore this product.";

    private static final String MESSAGE_PRODUCT_NOT_FOUND =
            "No product found with identifier: %1$s";

    private final String identifier;

    /**
     * Creates an ArchiveProductCommand to archive the specified product.
     *
     * @param identifier Identifier of the product to archive.
     */
    public ArchiveProductCommand(String identifier) {
        requireNonNull(identifier);
        this.identifier = identifier;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Product productToArchive = model.getInventory().getProductList().stream()
                .filter(p -> p.getIdentifier().value.equals(identifier))
                .findFirst()
                .orElseThrow(() ->
                        new CommandException(String.format(MESSAGE_PRODUCT_NOT_FOUND, identifier)));

        model.archiveProduct(productToArchive);
        model.commitVendorVault(formatSuccessPart(productToArchive));

        String message = String.format(
                MESSAGE_ARCHIVE_SUCCESS + SEPARATOR_NEW_LINE + MESSAGE_ARCHIVE_WARNING,
                productToArchive,
                productToArchive.getIdentifier().value
        );

        return new CommandResult(message, CommandResult.FEEDBACK_TYPE_WARN);
    }

    private String formatSuccessPart(Product productToArchive) {
        return String.format(
                MESSAGE_ARCHIVE_SUCCESS + SEPARATOR_NEW_LINE,
                productToArchive,
                productToArchive.getIdentifier().value
        );
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }
}
