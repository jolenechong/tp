package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.TypicalPersons;

/**
 * Tests for ArchiveCommand.
 */
public class ArchiveCommandTest {

    @Test
    public void execute_archiveVendor_success() throws CommandException {

        Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());

        Person vendorToArchive = model.getFilteredPersonList().get(0);

        ArchiveCommand archiveCommand = new ArchiveCommand(Index.fromOneBased(1));

        archiveCommand.execute(model);

        Person archivedVendor = model.getAddressBook().getPersonList().get(0);

        assertTrue(archivedVendor.isArchived());
    }

    @Test
    public void getPendingConfirmation_returnsInactivePendingConfirmation() {
        ArchiveCommand archiveCommand = new ArchiveCommand(Index.fromOneBased(1));

        PendingConfirmation pendingConfirmation = archiveCommand.getPendingConfirmation();
        assertFalse(pendingConfirmation.getNeedConfirmation());
    }
}
