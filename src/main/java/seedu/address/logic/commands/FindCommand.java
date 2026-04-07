package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.RankedPersonPredicate;
import seedu.address.model.product.VendorEmailMatchesContactsPredicate;

/**
 * Finds and lists all persons in address book whose name contains any of the argument keywords
 * and updates product list to show products associated with the listed contacts.
 * Case-insensitive, token-level partial matching is used. Matches are ranked by relevance.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";
    public static final String COMMAND_USAGE = COMMAND_WORD + " [n/NAME_KEYWORD]... [t/TAG_KEYWORD]...";
    public static final String COMMAND_DESCRIPTION = "Displays contacts with matching name and/or tag";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": " + COMMAND_DESCRIPTION + "\n"
            + "Parameters: [n/NAME_KEYWORD]... [t/TAG_KEYWORD]... (at least one prefix required)\n"
            + "Examples: " + COMMAND_WORD + " n/TechSource t/electronics\n";

    private final Predicate<Person> predicate;

    /**
     * Creates a find command with the given person-matching predicate.
     */
    public FindCommand(Predicate<Person> predicate) {
        requireNonNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        if (predicate instanceof RankedPersonPredicate) {
            model.updateFilteredPersonList(predicate);
        } else {
            model.updateFilteredPersonList(person -> !person.isArchived() && predicate.test(person));
        }

        VendorEmailMatchesContactsPredicate productPredicate = new VendorEmailMatchesContactsPredicate(
                model.getFilteredPersonList());
        model.updateFilteredProductList(productPredicate);

        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW,
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
