package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.function.Supplier;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Confirms the previous question.
 */
public class ConfirmCommand extends Command {

    public static final String COMMAND_WORD = "y";
    public static final String INVALID_STATE = "This is an invalid state";

    private final Supplier<Optional<CommandResult>> onConfirm;

    public ConfirmCommand(Supplier<Optional<CommandResult>> onConfirm) {
        this.onConfirm = onConfirm;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        return onConfirm.get().orElseThrow(() -> new CommandException(INVALID_STATE));
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }
}
