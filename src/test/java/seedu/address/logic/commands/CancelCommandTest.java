package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class CancelCommandTest {

    @Test
    public void getPendingConfirmation_returnsInactivePendingConfirmation() {
        CancelCommand cancelCommand = new CancelCommand(() -> Optional.empty());
        PendingConfirmation pendingConfirmation = cancelCommand.getPendingConfirmation();
        assertFalse(pendingConfirmation.getNeedConfirmation());
    }
}
