package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Represents an undo command that undoes the last command that modified the address book.
 */
public class UndoCommand extends Command {
    public static final String COMMAND_WORD = "undo";
    public static final String COMMAND_USAGE = COMMAND_WORD;
    public static final String COMMAND_DESCRIPTION = "Undoes previous change.";

    public static final String MESSAGE_SUCCESS = "Undo successful!";
    public static final String MESSAGE_FAILURE = "Nothing to undo.";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        if (!model.canUndoVendorVault()) {
            throw new CommandException(MESSAGE_FAILURE);
        }
        model.undoVendorVault();
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }
}
