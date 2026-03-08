package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class ConfirmCommandTest {

    @Test
    public void getPendingConfirmation_returnsInactivePendingConfirmation() {
        ConfirmCommand confirmCommand = new ConfirmCommand(() -> Optional.empty());
        PendingConfirmation pendingConfirmation = confirmCommand.getPendingConfirmation();
        assertFalse(pendingConfirmation.getNeedConfirmation());
    }
}
