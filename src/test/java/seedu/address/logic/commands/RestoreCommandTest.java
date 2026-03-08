package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.TypicalPersons;

/**
 * Tests for RestoreCommand.
 */
public class RestoreCommandTest {

    @Test
    public void execute_restoreVendor_success() throws CommandException {

        Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());

        Person vendor = model.getFilteredPersonList().get(0);

        model.archivePerson(vendor);

        RestoreCommand restoreCommand = new RestoreCommand(Index.fromOneBased(1));

        restoreCommand.execute(model);

        Person restoredVendor = model.getAddressBook().getPersonList().get(0);

        assertFalse(restoredVendor.isArchived());
    }

    @Test
    public void getPendingConfirmation_returnsInactivePendingConfirmation() {
        RestoreCommand restoreCommand = new RestoreCommand(Index.fromOneBased(1));
        PendingConfirmation pendingConfirmation = restoreCommand.getPendingConfirmation();
        assertFalse(pendingConfirmation.getNeedConfirmation());
    }
}
