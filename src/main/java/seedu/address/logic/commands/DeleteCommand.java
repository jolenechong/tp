package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE_PERSONS;

import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.person.NameEqualsKeywordsPredicate;
import seedu.address.model.person.Person;

/**
 * Deletes a person identified using it's displayed index from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the index number used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String VALID_CONTACT_DELETE_RANGE = "\nThe valid delete range is from 1 to ";

    public static final String CONTACT_IS_EMPTY = "\nThere is no contact to delete. Consider adding some contacts.";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";

    public static final String CONFIRMATION_DELETE_PERSON_MESSAGE =
            "Confirm (y) you want to delete the following person shown below:";

    public static final String MESSAGE_DELETE_FAILURE = "Did not delete person";

    private PendingConfirmation pendingConfirmation = new PendingConfirmation();

    private final String targetIndexString;
    private final boolean needsConfirmation;

    /**
     * Creates a DeleteCommand to delete the person at the specified {@code targetIndexString}.
     *
     * @param targetIndexString the index string of the person to be deleted, which could have trailing spaces
     */
    public DeleteCommand(String targetIndexString, boolean needsConfirmation) {
        this.targetIndexString = targetIndexString.trim();
        this.needsConfirmation = needsConfirmation;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        Index targetIndex;
        try {
            targetIndex = ParserUtil.parseIndex(targetIndexString);
        } catch (ParseException e) {
            throw new CommandException(formatExceptionMessage(lastShownList));
        }

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(formatExceptionMessage(lastShownList));
        }

        Person personToDelete = lastShownList.get(targetIndex.getZeroBased());

        if (!this.needsConfirmation) {
            return this.deletePerson(model, personToDelete);
        }

        this.pendingConfirmation = new PendingConfirmation(() ->
                this.onConfirm(model, personToDelete), () -> this.onCancel(model));

        NameEqualsKeywordsPredicate predicate = new NameEqualsKeywordsPredicate(personToDelete);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(CONFIRMATION_DELETE_PERSON_MESSAGE);
    }

    /**
     * Formats an exception message for an invalid person index based on the current list state.
     * If there is no contacts in the list, it will prompt user to add contacts first
     *
     */
    public String formatExceptionMessage(List<Person> lastShownList) {
        return lastShownList.isEmpty()
                ? Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX + CONTACT_IS_EMPTY
                : Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX + VALID_CONTACT_DELETE_RANGE + lastShownList.size();
    }

    /**
     * Deletes the specified person from the model and commits the change.
     * After deletion, the list will show all the contacts.
     *
     */
    public CommandResult deletePerson(Model model, Person personToDelete) {
        model.deletePerson(personToDelete);

        model.commitVendorVault();
        model.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS);
        return new CommandResult(
                String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    /**
     * Executes the deletion of specified person from model upon confirmation
     * Resets the displayed person list to show all the person after deletion
     *
     */
    public Optional<CommandResult> onConfirm(Model model, Person personToDelete) {
        return Optional.of(this.deletePerson(model, personToDelete));
    }

    /**
     * Cancels the deletion and reset the displayed person list to show all persons.
     *
     */
    public Optional<CommandResult> onCancel(Model model) {
        model.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS);
        return Optional.of(new CommandResult(
                String.format(MESSAGE_DELETE_FAILURE)));
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return this.pendingConfirmation;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand)) {
            return false;
        }

        DeleteCommand otherDeleteCommand = (DeleteCommand) other;
        return targetIndexString.equals(otherDeleteCommand.targetIndexString);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndexString)
                .toString();
    }
}
