package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Archives a vendor in the address book.
 * The vendor is identified using their email address.
 *
 * Usage: {@code archive EMAIL}
 * Example: {@code archive sg.sales@cytron.io}
 *
 * Note: Internally vendors are represented as {@code Person}.
 */
public class ArchiveCommand extends Command {

    public static final String COMMAND_WORD = "archive";
    public static final String COMMAND_USAGE = COMMAND_WORD + " EMAIL";
    public static final String COMMAND_DESCRIPTION = "Archives a contact.";

    public static final String MESSAGE_USAGE =
            COMMAND_WORD + " EMAIL\n"
            + "Archives the vendor identified by the email in the displayed vendor list.\n"
            + "Example: " + COMMAND_WORD + " sg.sales@cytron.io";

    public static final String MESSAGE_ARCHIVE_SUCCESS =
            "Archived Vendor: %1$s";

    public static final String MESSAGE_ACTION_SUMMARY =
            "archiving of contact: %1$s";

    public static final String MESSAGE_VENDOR_NOT_FOUND =
            "No vendor found with email.";

    public static final String MESSAGE_ALREADY_ARCHIVED =
            "This vendor is already archived. Did you want to restore it?";

    private final String email;

    /**
     * Creates an ArchiveCommand to archive the vendor with the specified {@code email}.
     *
     * @param email Email of the vendor to archive. Must not be null.
     */
    public ArchiveCommand(String email) {
        requireNonNull(email);
        this.email = email.toLowerCase();
    }

    /**
     * Executes the archive command.
     *
     * Searches the full vendor list for a vendor matching the given email.
     * If the vendor is already archived, an informative error is shown suggesting restore.
     * If no vendor is found, an error message is shown.
     * Otherwise, the vendor is archived and a success message is returned.
     *
     * @param model {@code Model} which the command should operate on. Must not be null.
     * @return {@code CommandResult} indicating the result of the command execution.
     * @throws CommandException if no vendor is found with the given email,
     *                          or if the vendor is already archived.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> fullList = model.getVendorVault().getPersonList();

        Person vendorToArchive = fullList.stream()
                .filter(person -> person.getEmail().value.equals(email))
                .findFirst()
                .orElseThrow(() -> new CommandException(MESSAGE_VENDOR_NOT_FOUND));

        if (vendorToArchive.isArchived()) {
            throw new CommandException(MESSAGE_ALREADY_ARCHIVED);
        }

        model.archivePerson(vendorToArchive);

        String actionSummary = String.format(MESSAGE_ACTION_SUMMARY, Messages.format(vendorToArchive));
        model.commitVendorVault(actionSummary);

        String successMessage = String.format(MESSAGE_ARCHIVE_SUCCESS, Messages.format(vendorToArchive));
        return new CommandResult(successMessage, CommandResult.FEEDBACK_TYPE_SUCCESS);
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
