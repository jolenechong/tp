package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Represents a redo command that redoes to next stage after an undo.
 */
public class RedoCommand extends Command {
    // Solution inspired by https://se-education.org/addressbook-level3/DeveloperGuide.html#proposed-undoredo-feature

    public static final String COMMAND_WORD = "redo";
    public static final String COMMAND_USAGE = COMMAND_WORD;
    public static final String COMMAND_DESCRIPTION = "Redoes last undone change.";

    public static final String MESSAGE_SUCCESS = "Redo successful:\nReapplied the ";
    public static final String MESSAGE_FAILURE = "Nothing to redo.";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        if (!model.canRedoVendorVault()) {
            throw new CommandException(MESSAGE_FAILURE);
        }
        String redoneActionSummary = model.redoVendorVault();
        return new CommandResult(MESSAGE_SUCCESS + redoneActionSummary);
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }
}
