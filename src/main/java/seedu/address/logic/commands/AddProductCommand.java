package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PRODUCT;
import static seedu.address.logic.commands.CommandUtil.SEPARATOR_NEW_LINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_IDENTIFIER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_QUANTITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_THRESHOLD;
import static seedu.address.model.product.warnings.DuplicateProductWarning.formatNameWarning;

import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Email;
import seedu.address.model.product.Product;

/**
 * Adds a product to the inventory.
 */
public class AddProductCommand extends Command {

    public static final String COMMAND_WORD = "addproduct";
    public static final String COMMAND_USAGE = COMMAND_WORD + " " + PREFIX_IDENTIFIER + "IDENTIFIER "
            + PREFIX_NAME + "NAME "
            + "[" + PREFIX_QUANTITY + "QUANTITY] "
            + "[" + PREFIX_THRESHOLD + "THRESHOLD] "
            + "[" + PREFIX_EMAIL + "VENDOR_EMAIL]";
    public static final String COMMAND_DESCRIPTION = "Adds a product.";


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
    public static final String MESSAGE_VENDOR_DOES_NOT_EXIST =
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

        validateNoDuplicate(model);

        Optional<Email> vendorEmail = toAdd.getVendorEmail();
        if (vendorEmail.isPresent()) {
            Email value = vendorEmail.get();
            if (model.findByEmail(value).isEmpty()) {
                throw new CommandException(String.format(MESSAGE_VENDOR_DOES_NOT_EXIST, value));
            }
        }

        String allWarnings = buildWarnings(model, warnings);

        model.addProduct(toAdd);
        model.commitVendorVault();

        String formattedWarnings = allWarnings.isEmpty() ? "" : SEPARATOR_NEW_LINE + allWarnings;
        String feedbackType = allWarnings.isEmpty()
                ? CommandResult.FEEDBACK_TYPE_SUCCESS
                : CommandResult.FEEDBACK_TYPE_WARN;

        return new CommandResult(String.format(MESSAGE_SUCCESS + formattedWarnings, Messages.formatProduct(toAdd)),
                feedbackType);
    }

    private String buildWarnings(Model model, String originalWarnings) {
        StringBuilder warningsBuilder = new StringBuilder(originalWarnings);
        appendSimilarProductWarnings(model, warningsBuilder);
        return warningsBuilder.toString();
    }

    private void validateNoDuplicate(Model model) throws CommandException {
        if (model.hasProduct(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PRODUCT);
        }
    }

    /**
     * Adds warnings when similar products are detected to avoid accidental duplicates.
     */
    private void appendSimilarProductWarnings(Model model, StringBuilder warnings) {
        model.findSimilarNameMatch(toAdd, null).ifPresent(match ->
                appendWarning(warnings, formatNameWarning(match.getIdentifier(), match.getName())));
    }

    private void appendWarning(StringBuilder warnings, String message) {
        if (!warnings.isEmpty()) {
            warnings.append(SEPARATOR_NEW_LINE);
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
