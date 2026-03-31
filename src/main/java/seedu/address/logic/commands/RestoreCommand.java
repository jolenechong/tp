package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Restores an archived vendor in the address book.
 * The vendor is identified using their email address.
 *
 * If no email is provided, the archived vendor list is shown.
 * If there are no archived vendors, the user is informed and encouraged to archive first.
 *
 * Usage: {@code restore [EMAIL]}
 * Example: {@code restore sg.sales@cytron.io}
 *
 * Note: Internally vendors are represented as {@code Person}.
 */
public class RestoreCommand extends Command {

    public static final String COMMAND_WORD = "restore";
    public static final String COMMAND_USAGE = COMMAND_WORD + " [EMAIL]";
    public static final String COMMAND_DESCRIPTION =
            "Restores an archived contact; lists all archived if no email given";

    public static final String MESSAGE_USAGE =
            COMMAND_WORD + ": Restores an archived vendor using email.\n"
            + "Parameters: EMAIL\n"
            + "Example: " + COMMAND_WORD + " sg.sales@cytron.io";

    public static final String MESSAGE_RESTORE_SUCCESS =
            "Restored Vendor: %1$s";

    public static final String MESSAGE_ACTION_SUMMARY =
            "restoring of contact: %1$s";

    public static final String MESSAGE_VENDOR_NOT_FOUND =
            "No archived vendor found with email.";

    public static final String MESSAGE_LIST_ARCHIVED =
            "Archived vendors listed below. Use 'restore EMAIL' to restore one.";

    public static final String MESSAGE_NONE_ARCHIVED =
            "No vendors are currently archived.\n"
            + "Use 'archive EMAIL' to archive a vendor first.";

    private final String email;

    /**
     * Creates a RestoreCommand to restore the archived vendor with the specified {@code email}.
     *
     * Pass {@code null} to list all archived vendors without restoring.
     *
     * @param email Email of the vendor to restore, or {@code null} to list archived vendors.
     */
    public RestoreCommand(String email) {
        this.email = email != null ? email.toLowerCase() : null;
    }

    /**
     * Executes the restore command.
     *
     * If no email is provided and there are archived vendors, the archived list is shown
     * with a success message. If there are no archived vendors, the user is informed and
     * encouraged to archive a vendor first.
     * If an email is provided but does not match any archived vendor, the archived list is shown
     * and an error is returned.
     * Otherwise, the matched vendor is restored.
     *
     * @param model {@code Model} which the command should operate on. Must not be null.
     * @return {@code CommandResult} indicating the result of the command execution.
     * @throws CommandException if no archived vendor matches the given email.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (email == null) {
            List<Person> archivedPersons = model.getAddressBook().getPersonList().stream()
                    .filter(Person::isArchived)
                    .toList();

            if (archivedPersons.isEmpty()) {
                return new CommandResult(MESSAGE_NONE_ARCHIVED, CommandResult.FEEDBACK_TYPE_SUCCESS);
            }

            model.updateFilteredPersonList(Person::isArchived);
            return new CommandResult(MESSAGE_LIST_ARCHIVED, CommandResult.FEEDBACK_TYPE_SUCCESS);
        }

        List<Person> persons = model.getAddressBook().getPersonList();

        Person personToRestore = persons.stream()
                .filter(person -> person.isArchived()
                        && person.getEmail().value.equals(email))
                .findFirst()
                .orElse(null);

        if (personToRestore == null) {
            model.updateFilteredPersonList(Person::isArchived);
            throw new CommandException(MESSAGE_VENDOR_NOT_FOUND);
        }

        model.restorePerson(personToRestore);
        Person restoredPerson = personToRestore.restore();
        String actionSummary = String.format(MESSAGE_ACTION_SUMMARY, Messages.format(restoredPerson));
        model.commitVendorVault(actionSummary);

        String successPart = String.format(MESSAGE_RESTORE_SUCCESS, Messages.format(restoredPerson));
        return new CommandResult(successPart);
    }

    /**
     * Returns the pending confirmation state for this command.
     *
     * @return a new {@code PendingConfirmation} instance.
     */
    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }
}
