package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Restores an archived vendor in the address book.
 * The vendor is identified using the index from the currently displayed vendor list.
 *
 * Usage: restore vendor INDEX
 * Example: restore vendor 1
 *
 * Note: Internally vendors are represented as {@code Person}.
 */
public class RestoreCommand extends Command {

    public static final String COMMAND_WORD = "restore";
    public static final String COMMAND_USAGE = COMMAND_WORD + " [EMAIL]";
    public static final String COMMAND_DESCRIPTION =
            "Restores an archived contact; lists all archived if no email given";

    public static final String MESSAGE_VENDOR_NOT_FOUND = "No archived vendor found with email: %1$s";

    public static final String MESSAGE_USAGE =
        COMMAND_WORD + ": Restores an archived vendor using email.\n"
        + "Parameters: EMAIL\n"
        + "Example: " + COMMAND_WORD + " alexyeoh@example.com";

    public static final String MESSAGE_RESTORE_SUCCESS =
            "Restored Vendor: %1$s";

    private final String email;

    /**
     * Creates a RestoreCommand to restore the vendor at the specified {@code Index}.
     *
     * @param email Email of the vendor in the filtered vendor list.
     */
    public RestoreCommand(String email) {
        this.email = email;
    }

    /**
     * Executes the restore command.
     *
     * @param model {@code Model} which the command should operate on.
     * @return CommandResult indicating the result of the command execution.
     * @throws CommandException if the index is invalid.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> persons = model.getAddressBook().getPersonList();

        Person personToRestore = persons.stream()
                .filter(person -> person.isArchived()
                        && person.getEmail().value.equals(email))
                .findFirst()
                .orElse(null);

        if (personToRestore == null) {
            model.updateFilteredPersonList(Person::isArchived);
            throw new CommandException(
                "Please provide the email of the vendor to restore.\n"
                + "Archived vendors listed below."
            );
        }

        model.restorePerson(personToRestore);
        String successPart = String.format(MESSAGE_RESTORE_SUCCESS, personToRestore);
        model.commitVendorVault(successPart);

        return new CommandResult(successPart);
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }
}
