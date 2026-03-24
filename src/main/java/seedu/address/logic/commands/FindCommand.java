package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.NameContainsKeywordsScoredPredicate;
import seedu.address.model.product.VendorEmailMatchesContactsPredicate;

/**
 * Finds and lists all persons in address book whose name contains any of the argument keywords
 * and updates product list to show products associated with the listed contacts.
 * Case-insensitive, token-level partial matching is used. Matches are ranked by relevance.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";
    public static final String COMMAND_USAGE = COMMAND_WORD + " KEYWORD [MORE_KEYWORDS]...";
    public static final String COMMAND_DESCRIPTION = "Lists all contacts matching KEYWORD.";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all contacts whose names contain any of "
            + "the specified keywords.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " adafruit cytron";

    public static final String MESSAGE_DISPLAY_PRODUCTS = " Products associated with these contacts listed on "
            + "the right!";

    private final NameContainsKeywordsScoredPredicate predicate;

    /**
     * Creates a find command with the given name-matching predicate.
     */
    public FindCommand(NameContainsKeywordsScoredPredicate predicate) {
        requireNonNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        model.updateFilteredPersonList(predicate);

        VendorEmailMatchesContactsPredicate productPredicate = new VendorEmailMatchesContactsPredicate(
                model.getFilteredPersonList());
        model.updateFilteredProductList(productPredicate);

        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW + MESSAGE_DISPLAY_PRODUCTS,
                        model.getFilteredPersonList().size()));
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
        if (!(other instanceof FindCommand)) {
            return false;
        }

        FindCommand otherFindCommand = (FindCommand) other;
        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
