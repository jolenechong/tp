package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.product.ProductNameContainsKeywordsPredicate;

/**
 * Finds and lists all products in inventory whose name contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindProductCommand extends Command {

    public static final String COMMAND_WORD = "findproduct";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all products whose names contain any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " motherboard ssd";

    private final ProductNameContainsKeywordsPredicate predicate;

    public FindProductCommand(ProductNameContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        model.updateFilteredProductList(predicate);

        return new CommandResult(
                String.format(Messages.MESSAGE_PRODUCTS_LISTED_OVERVIEW,
                        model.getFilteredProductList().size()));
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindProductCommand)) {
            return false;
        }

        FindProductCommand otherFindProductCommand = (FindProductCommand) other;
        return predicate.equals(otherFindProductCommand.predicate);
    }
}
