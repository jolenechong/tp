package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Archives a vendor in the address book.
 * The vendor is identified using the index from the currently displayed vendor list.
 *
 * Usage: archive vendor email
 * Example: archive vendor abc@123.com
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
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_ARCHIVE_SUCCESS =
            "Archived Vendor: %1$s"
            + "\n⚠ Warning: Vendor being archived. Use 'restore EMAIL' to restore the vendor.";

    private final String email;

    /**
     * Creates an ArchiveCommand to archive the vendor at the specified {@code Index}.
     *
     * @param email Email of the vendor in the filtered vendor list.
     */
    public ArchiveCommand(String email) {
        requireNonNull(email);
        this.email = email;
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

        List<Person> fullList = model.getVendorVault().getPersonList();

        Person vendorToArchive = fullList.stream()
                .filter(person -> person.getEmail().value.equals(email))
                .findFirst()
                .orElseThrow(() ->
                        new CommandException("No vendor found with email: " + email));

        model.archivePerson(vendorToArchive);
        model.commitVendorVault(formatSuccessPart(vendorToArchive));

        return new CommandResult(
            String.format(MESSAGE_ARCHIVE_SUCCESS, vendorToArchive.getName()),
            CommandResult.FEEDBACK_TYPE_WARN
        );
    }

    private String formatSuccessPart(Person vendorToArchive) {
        return String.format(MESSAGE_ARCHIVE_SUCCESS, vendorToArchive.getName());
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }
}
