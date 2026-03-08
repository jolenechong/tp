package seedu.address.logic.commands;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Represents a pending confirmation state that encapsulates the actions to be executed
 * based on the user's confirmation response.
 *
 */
public class PendingConfirmation {

    private final Supplier<Optional<CommandResult>> onConfirm;
    private final Supplier<Optional<CommandResult>> onCancel;
    private final boolean needConfirmation;

    /**
     * Constructs an empty {@code PendingConfiguration} with no pending actions.
     * Both confirm and cancel action default to return Optional.empty()
     */
    public PendingConfirmation() {
        this.onConfirm = () -> Optional.empty();
        this.onCancel = () -> Optional.empty();
        this.needConfirmation = false;
    }

    /**
     * Constructs an active {@code PendingConfiguration} with the specified
     * confirm and cancel actions.
     *
     * @param onConfirm the action to execute when the user confirms
     * @param onCancel the action to execute when the user cancels
     */
    public PendingConfirmation(
            Supplier<Optional<CommandResult>> onConfirm,
            Supplier<Optional<CommandResult>> onCancel) {
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
        this.needConfirmation = true;
    }

    public boolean getNeedConfirmation() {
        return this.needConfirmation;
    }

    public Supplier<Optional<CommandResult>> getOnConfirm() {
        return onConfirm;
    }

    public Supplier<Optional<CommandResult>> getOnCancel() {
        return onCancel;
    }
}
