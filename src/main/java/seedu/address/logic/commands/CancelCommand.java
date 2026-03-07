package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.function.Supplier;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Confirms the previous question.
 */
public class CancelCommand extends Command {

    // TODO check if INVALID_STATE is unreachable
    public static final String INVALID_STATE = "This is an invalid state";

    private final Supplier<Optional<CommandResult>> onCancel;

    public CancelCommand(Supplier<Optional<CommandResult>> onCancel) {
        this.onCancel = onCancel;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        return onCancel.get().orElseThrow(() -> new CommandException(INVALID_STATE));
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }
}
