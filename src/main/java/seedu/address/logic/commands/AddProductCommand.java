package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_IDENTIFIER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_QUANTITY;
import static seedu.address.logic.parser.ParserUtil.NEWLINE;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.product.Product;

/**
 * Adds a product to the inventory.
 */
public class AddProductCommand extends Command {

    public static final String COMMAND_WORD = "addproduct";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a product to the inventory. "
            + "Parameters: "
            + PREFIX_IDENTIFIER + "IDENTIFIER "
            + PREFIX_NAME + "NAME "
            + "[" + PREFIX_QUANTITY + "QUANTITY]\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_IDENTIFIER + "A1 "
            + PREFIX_NAME + "iPad 11 Pro "
            + PREFIX_QUANTITY + "20";

    public static final String MESSAGE_SUCCESS = "New product added: %1$s";

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

        StringBuilder allWarnings = new StringBuilder(warnings);
        model.addProduct(toAdd);

        String formattedWarnings = allWarnings.isEmpty() ? "" : NEWLINE + allWarnings;
        String feedbackType = allWarnings.isEmpty()
                ? CommandResult.FEEDBACK_TYPE_SUCCESS
                : CommandResult.FEEDBACK_TYPE_WARN;

        return new CommandResult(String.format(MESSAGE_SUCCESS + formattedWarnings, Messages.formatProduct(toAdd)),
                feedbackType);
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
