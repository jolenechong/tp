package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PRODUCT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_IDENTIFIER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_QUANTITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_THRESHOLD;
import static seedu.address.logic.parser.ParserUtil.NEWLINE;
import static seedu.address.model.product.warnings.DuplicateProductWarning.MESSAGE_SIMILAR_NAME;

import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Email;
import seedu.address.model.product.Product;
import seedu.address.model.product.warnings.DuplicateProductWarning;

/**
 * Adds a product to the inventory.
 */
public class AddProductCommand extends Command {

    public static final String COMMAND_WORD = "addproduct";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a product to the inventory. "
            + "Parameters: "
            + PREFIX_IDENTIFIER + "IDENTIFIER "
            + PREFIX_NAME + "NAME "
            + "[" + PREFIX_QUANTITY + "QUANTITY] "
            + "[" + PREFIX_THRESHOLD + "THRESHOLD] "
            + "[" + PREFIX_EMAIL + "VENDOR_EMAIL]\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_IDENTIFIER + "A1 "
            + PREFIX_NAME + "iPad 11 Pro "
            + PREFIX_QUANTITY + "20 "
            + PREFIX_THRESHOLD + "5 "
            + PREFIX_EMAIL + "johnd@example.com";

    public static final String MESSAGE_SUCCESS = "New product added: %1$s";
    public static final String MESSAGE_INVALID_VENDOR =
            "Vendor email %1$s does not match any existing contact.";

    private final Product toAdd;
    private String warnings = "";

    /**
     * Creates an AddProductCommand to add the specified {@code Product}.
     */
    public AddProductCommand(Product product) {
        requireNonNull(product);
        toAdd = product;
    }

    /**
     * Creates an AddProductCommand with warnings to show after success.
     */
    public AddProductCommand(Product product, String warnings) {
        requireNonNull(product);
        toAdd = product;
        this.warnings = warnings;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasProduct(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PRODUCT);
        }

        Optional<Email> vendorEmail = toAdd.getVendorEmail();
        if (vendorEmail.isPresent()) {
            Email value = vendorEmail.get();
            if (model.findByEmail(value).isEmpty()) {
                throw new CommandException(String.format(MESSAGE_INVALID_VENDOR, value));
            }
        }

        StringBuilder allWarnings = new StringBuilder(warnings);
        checkForSimilarProducts(model, allWarnings);
        model.addProduct(toAdd);
        model.commitVendorVault();

        String formattedWarnings = allWarnings.isEmpty() ? "" : NEWLINE + allWarnings;
        String feedbackType = allWarnings.isEmpty()
                ? CommandResult.FEEDBACK_TYPE_SUCCESS
                : CommandResult.FEEDBACK_TYPE_WARN;

        return new CommandResult(String.format(MESSAGE_SUCCESS + formattedWarnings, Messages.formatProduct(toAdd)),
                feedbackType);
    }

    /**
     * Checks for similar products in the model and appends warnings if found.
     */
    private void checkForSimilarProducts(Model model, StringBuilder warnings) {
        for (Product existingProduct : model.getFilteredProductList()) {
            DuplicateProductWarning duplicateWarning = toAdd.isSameProductWarn(existingProduct);

            if (!duplicateWarning.getValue()) {
                continue;
            }

            String warning = duplicateWarning.getWarning();
            if (warning.equals(MESSAGE_SIMILAR_NAME)) {
                appendWarning(warnings, String.format(
                        MESSAGE_SIMILAR_NAME, existingProduct.getIdentifier(), existingProduct.getName()));
                break;
            }
        }
    }

    private void appendWarning(StringBuilder warnings, String message) {
        if (!warnings.isEmpty()) {
            warnings.append(NEWLINE);
        }
        warnings.append(message);
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }

    /**
     * Returns the warnings to show after successfully adding the product.
     *
     * @return the warnings to show
     */
    public String getWarnings() {
        return warnings;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddProductCommand)) {
            return false;
        }

        AddProductCommand otherCommand = (AddProductCommand) other;
        return toAdd.equals(otherCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
