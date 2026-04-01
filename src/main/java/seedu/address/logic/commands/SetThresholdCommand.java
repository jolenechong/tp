package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.UserPrefs;
import seedu.address.model.product.RestockThreshold;

/**
 * Command that sets the default restock threshold used when adding products.
 */
public class SetThresholdCommand extends Command {

    public static final String COMMAND_WORD = "threshold";
    public static final String COMMAND_USAGE = COMMAND_WORD + " THRESHOLD";
    public static final String COMMAND_DESCRIPTION = "Changes the default restock threshold";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": " + COMMAND_DESCRIPTION + "\n"
            + "Parameters: THRESHOLD\n"
            + "Example: " + COMMAND_WORD + " 5";

    public static final String MESSAGE_SUCCESS = "Default restock threshold set to: %1$s";

    private final RestockThreshold threshold;

    /**
     * Creates a SetThresholdCommand to set the default {@code RestockThreshold}
     */
    public SetThresholdCommand(RestockThreshold threshold) {
        requireNonNull(threshold);
        this.threshold = threshold;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        UserPrefs updatedPrefs = new UserPrefs(model.getUserPrefs());
        updatedPrefs.setDefaultRestockThresholdValue(threshold.value);
        model.setUserPrefs(updatedPrefs);

        return new CommandResult(String.format(MESSAGE_SUCCESS, threshold));
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }

    public RestockThreshold getThreshold() {
        return threshold;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof SetThresholdCommand)) {
            return false;
        }

        SetThresholdCommand otherCommand = (SetThresholdCommand) other;
        return threshold.equals(otherCommand.threshold);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("threshold", threshold)
                .toString();
    }
}
