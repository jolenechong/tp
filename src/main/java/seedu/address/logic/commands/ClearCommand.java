package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;

/**
 * Clears the address book.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String COMMAND_USAGE = COMMAND_WORD;
    public static final String COMMAND_DESCRIPTION = "Clears all contacts.";

    public static final String MESSAGE_SUCCESS = "All vendor contacts cleared!";
    public static final String CONFIRMATION_CLEAR_MESSAGE =
            "Confirm (y) you want to clear ALL vendor contacts:";
    public static final String MESSAGE_CLEAR_FAILURE = "Clear contact operation cancelled";
    public static final String MESSAGE_ACTION_SUMMARY = "clearing of all products";

    private PendingConfirmation pendingConfirmation = new PendingConfirmation();

    private final boolean needsConfirmation;

    /**
     * Creates a ClearCommand that will prompt for confirmation.
     */
    public ClearCommand() {
        this.needsConfirmation = true;
    }

    /**
     * Creates a ClearCommand with explicit control over whether confirmation is needed.
     */
    public ClearCommand(boolean needsConfirmation) {
        this.needsConfirmation = needsConfirmation;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        if (!this.needsConfirmation) {
            return this.doClear(model);
        }

        pendingConfirmation = new PendingConfirmation(() -> this.onConfirm(model), () -> this.onCancel(model));

        return new CommandResult(CONFIRMATION_CLEAR_MESSAGE);
    }

    /**
     * Performs the actual clear operation.
     */
    private CommandResult doClear(Model model) {
        model.setAddressBook(new AddressBook());
        model.commitVendorVault(MESSAGE_ACTION_SUMMARY);
        return new CommandResult(MESSAGE_SUCCESS);
    }

    /**
     * Executes the clear upon confirmation.
     */
    private Optional<CommandResult> onConfirm(Model model) {
        return Optional.of(this.doClear(model));
    }

    /**
     * Cancels the clear operation.
     */
    private Optional<CommandResult> onCancel(Model model) {
        return Optional.of(new CommandResult(MESSAGE_CLEAR_FAILURE));
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return this.pendingConfirmation;
    }
}
