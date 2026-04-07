package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.product.RestockThreshold;

public class SetThresholdCommandTest {

    private static final String THRESHOLD_3 = "3";
    private static final String THRESHOLD_7 = "7";
    private static final String THRESHOLD_9 = "9";
    private static final int THRESHOLD_7_INT = 7;

    @Test
    public void constructor_nullThreshold_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SetThresholdCommand(null));
    }

    @Test
    public void execute_validThreshold_returnsSuccess() {
        Model model = new ModelManager();
        SetThresholdCommand command = command(THRESHOLD_7);

        CommandResult result = command.execute(model);

        assertEquals(String.format(SetThresholdCommand.MESSAGE_SUCCESS, THRESHOLD_7), result.getFeedbackToUser());
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, result.getFeedbackType());
        assertEquals(THRESHOLD_7_INT, model.getUserPrefs().getDefaultRestockThresholdValue());
    }

    @Test
    public void execute_validThreshold_preservesOtherUserPrefsFields() {
        Model model = new ModelManager();
        UserPrefs initialPrefs = new UserPrefs(model.getUserPrefs());

        command(THRESHOLD_7).execute(model);

        assertEquals(initialPrefs.getAddressBookFilePath(), model.getUserPrefs().getAddressBookFilePath());
        assertEquals(initialPrefs.getGuiSettings(), model.getUserPrefs().getGuiSettings());
    }

    @Test
    public void getPendingConfirmation_returnsInactivePendingConfirmation() {
        SetThresholdCommand command = command(THRESHOLD_7);
        PendingConfirmation pendingConfirmation = command.getPendingConfirmation();

        assertFalse(pendingConfirmation.getNeedConfirmation());
    }

    @Test
    public void equals() {
        SetThresholdCommand firstCommand = command(THRESHOLD_3);
        SetThresholdCommand secondCommand = command(THRESHOLD_3);
        SetThresholdCommand thirdCommand = command(THRESHOLD_9);

        assertEquals(firstCommand, firstCommand);
        assertEquals(firstCommand, secondCommand);
        assertNotEquals(firstCommand, thirdCommand);
        assertNotEquals(firstCommand, null);
        assertNotEquals(firstCommand, 1);
    }

    @Test
    public void getThreshold_returnsConfiguredThreshold() {
        RestockThreshold threshold = new RestockThreshold(THRESHOLD_7);
        SetThresholdCommand command = new SetThresholdCommand(threshold);

        assertEquals(threshold, command.getThreshold());
    }

    @Test
    public void toString_containsThresholdField() {
        SetThresholdCommand command = command(THRESHOLD_7);

        assertTrue(command.toString().contains("threshold=" + THRESHOLD_7));
    }

    private SetThresholdCommand command(String threshold) {
        return new SetThresholdCommand(new RestockThreshold(threshold));
    }
}
