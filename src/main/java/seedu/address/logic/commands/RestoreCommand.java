package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;

import java.util.List;

import seedu.address.commons.core.index.Index;
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

    public static final String MESSAGE_USAGE =
            COMMAND_WORD + " vendor INDEX\n"
            + "Restores the archived vendor identified by the index number used in the displayed vendor list.\n"
            + "Example: " + COMMAND_WORD + " vendor 1";

    public static final String MESSAGE_RESTORE_SUCCESS =
            "Restored Vendor: %1$s";

    private final Index targetIndex;

    /**
     * Creates a RestoreCommand to restore the vendor at the specified {@code Index}.
     *
     * @param targetIndex Index of the vendor in the filtered vendor list.
     */
    public RestoreCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
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

        // show archived vendors temporarily
        model.updateFilteredPersonList(person -> person.isArchived());

        List<Person> archivedList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= archivedList.size()) {
            throw new CommandException(MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person vendorToRestore = archivedList.get(targetIndex.getZeroBased());

        model.restorePerson(vendorToRestore);

        return new CommandResult(String.format(MESSAGE_RESTORE_SUCCESS, vendorToRestore));
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }
}
