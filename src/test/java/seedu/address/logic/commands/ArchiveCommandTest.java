package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Aliases;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.VendorVault;
import seedu.address.model.person.Person;
import seedu.address.testutil.TypicalPersons;

/**
 * Tests for ArchiveCommand.
 */
public class ArchiveCommandTest {

    @Test
    public void execute_archiveVendor_success() throws CommandException {

        Model model = new ModelManager(
                new VendorVault(getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

        Person vendorToArchive = model.getFilteredPersonList().get(0);

        ArchiveCommand archiveCommand = new ArchiveCommand(vendorToArchive.getEmail().value);

        archiveCommand.execute(model);

        Person archivedVendor = model.getAddressBook().getPersonList().get(0);

        assertTrue(archivedVendor.isArchived());
    }

    @Test
    public void getPendingConfirmation_returnsInactivePendingConfirmation() {
        ArchiveCommand archiveCommand = new ArchiveCommand(TypicalPersons.getTypicalPersons().get(0).getEmail().value);

        PendingConfirmation pendingConfirmation = archiveCommand.getPendingConfirmation();
        assertFalse(pendingConfirmation.getNeedConfirmation());
    }

    @Test
    public void execute_vendorNotFound_throwsCommandException() {
        Model model = new ModelManager(new VendorVault(
                getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

        ArchiveCommand command = new ArchiveCommand("notfound@email.com");

        assertThrows(CommandException.class, () -> command.execute(model));
    }
}
