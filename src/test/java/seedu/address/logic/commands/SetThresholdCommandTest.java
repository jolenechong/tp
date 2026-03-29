package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.product.RestockThreshold;

public class SetThresholdCommandTest {

    @Test
    public void execute_validThreshold_returnsSuccess() {
        Model model = new ModelManager();
        SetThresholdCommand command = new SetThresholdCommand(new RestockThreshold("7"));

        CommandResult result = command.execute(model);

        assertEquals(String.format(SetThresholdCommand.MESSAGE_SUCCESS, "7"), result.getFeedbackToUser());
        assertEquals(7, model.getUserPrefs().getDefaultRestockThresholdValue());
    }

    @Test
    public void getPendingConfirmation_returnsObject() {
        SetThresholdCommand command = new SetThresholdCommand(new RestockThreshold("3"));
        PendingConfirmation pendingConfirmation = command.getPendingConfirmation();

        assertNotNull(pendingConfirmation);
        assertFalse(pendingConfirmation.getNeedConfirmation());
    }

    @Test
    public void equals() {
        SetThresholdCommand firstCommand = new SetThresholdCommand(new RestockThreshold("3"));
        SetThresholdCommand secondCommand = new SetThresholdCommand(new RestockThreshold("3"));
        SetThresholdCommand thirdCommand = new SetThresholdCommand(new RestockThreshold("4"));

        assertEquals(firstCommand, firstCommand);
        assertEquals(firstCommand, secondCommand);
        assertNotEquals(firstCommand, thirdCommand);
        assertNotEquals(firstCommand, null);
        assertNotEquals(firstCommand, 1);
    }
}
