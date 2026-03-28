package seedu.address.logic.commands;

import java.util.Optional;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Represents a redo command that redoes to next stage after an undo.
 */
public class RedoCommand extends Command {
    public static final String COMMAND_WORD = "redo";
    public static final String COMMAND_USAGE = COMMAND_WORD;
    public static final String COMMAND_DESCRIPTION = "Redoes last undone change.";

    public static final String MESSAGE_SUCCESS = "Redo successful, reapplied this change";
    public static final String MESSAGE_FAILURE = "Nothing to redo.";
    public static final String MESSAGE_REDID_ACTION = ": \n%1$s";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        if (!model.canRedoVendorVault()) {
            throw new CommandException(MESSAGE_FAILURE);
        }
        Optional<String> redoneActionSummary = model.redoVendorVault();
        String message = redoneActionSummary
                .map(summary -> MESSAGE_SUCCESS + String.format(MESSAGE_REDID_ACTION, summary))
                .orElse(MESSAGE_SUCCESS);
        return new CommandResult(message);
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }
}
