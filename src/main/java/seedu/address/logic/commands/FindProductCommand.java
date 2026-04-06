package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.VendorEmailMatchesProductsPredicate;
import seedu.address.model.product.ProductNameContainsKeywordsScoredPredicate;

/**
 * Finds and lists all products in inventory whose name contains any of the argument keywords.
 * Keyword matching is case insensitive.
 * Updates contact list to show vendors associated with the listed products.
 */
public class FindProductCommand extends Command {

    public static final String COMMAND_WORD = "findproduct";
    public static final String COMMAND_USAGE = COMMAND_WORD + " KEYWORD [MORE_KEYWORDS]...";
    public static final String COMMAND_DESCRIPTION =
            "Displays products with matching name";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": " + COMMAND_DESCRIPTION + "\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " uno";

    private final ProductNameContainsKeywordsScoredPredicate predicate;

    /**
     * Creates a findproduct command with the given name matching predicate.
     */
    public FindProductCommand(ProductNameContainsKeywordsScoredPredicate predicate) {
        requireNonNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        model.updateFilteredProductList(predicate);
        VendorEmailMatchesProductsPredicate personPredicate = new VendorEmailMatchesProductsPredicate(
                model.getFilteredProductList());
        model.updateFilteredPersonList(personPredicate);

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
