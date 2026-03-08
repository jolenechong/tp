package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Archives a vendor in the address book.
 * The vendor is identified using the index from the currently displayed vendor list.
 *
 * Usage: archive vendor INDEX
 * Example: archive vendor 1
 *
 * Note: Internally vendors are represented as {@code Person}.
 */
public class ArchiveCommand extends Command {

    public static final String COMMAND_WORD = "archive";

    public static final String MESSAGE_USAGE =
            COMMAND_WORD + " vendor INDEX\n"
            + "Archives the vendor identified by the index number used in the displayed vendor list.\n"
            + "Example: " + COMMAND_WORD + " vendor 1";

    public static final String MESSAGE_ARCHIVE_SUCCESS =
            "Archived Vendor: %1$s";

    private final Index targetIndex;

    /**
     * Creates an ArchiveCommand to archive the vendor at the specified {@code Index}.
     *
     * @param targetIndex Index of the vendor in the filtered vendor list.
     */
    public ArchiveCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    /**
     * Executes the archive command.
     *
     * @param model {@code Model} which the command should operate on.
     * @return CommandResult indicating the result of the command execution.
     * @throws CommandException if the index is invalid.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person vendorToArchive = lastShownList.get(targetIndex.getZeroBased());

        model.archivePerson(vendorToArchive);

        return new CommandResult(String.format(MESSAGE_ARCHIVE_SUCCESS, vendorToArchive));
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }
}
