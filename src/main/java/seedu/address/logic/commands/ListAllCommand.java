package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE_PERSONS;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE_PRODUCTS;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Lists all active contacts and products in the application.
 *
 * This command updates both:
 * - the filtered person list to show all active contacts
 * - the filtered product list to show all active products
 */
public class ListAllCommand extends Command {

    public static final String COMMAND_WORD = "listall";
    public static final String COMMAND_USAGE = COMMAND_WORD;
    public static final String COMMAND_DESCRIPTION = "Lists all active contacts and products.";

    public static final String MESSAGE_SUCCESS =
            "Showing all active contacts and products!";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        model.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS);
        model.updateFilteredProductList(PREDICATE_SHOW_ACTIVE_PRODUCTS);

        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).toString();
    }
}
