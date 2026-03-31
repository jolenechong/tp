package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
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

        CommandResult result = archiveCommand.execute(model);

        // Vendor should now be archived in the address book
        Person archivedVendor = model.getAddressBook().getPersonList().stream()
                .filter(p -> p.getEmail().equals(vendorToArchive.getEmail()))
                .findFirst().get();

        assertTrue(archivedVendor.isArchived());

        // Success message should contain full contact details (not just name)
        String expectedMessage = String.format(ArchiveCommand.MESSAGE_ARCHIVE_SUCCESS,
                Messages.format(vendorToArchive));
        assertEquals(expectedMessage, result.getFeedbackToUser());
    }

    @Test
    public void execute_archiveVendor_returnsSuccessFeedbackType() throws CommandException {
        Model model = new ModelManager(
                new VendorVault(getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

        Person vendorToArchive = model.getFilteredPersonList().get(0);
        ArchiveCommand archiveCommand = new ArchiveCommand(vendorToArchive.getEmail().value);

        CommandResult result = archiveCommand.execute(model);

        // Should be green success, not yellow warning
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, result.getFeedbackType());
    }

    @Test
    public void execute_vendorNotFound_throwsCommandException() {
        Model model = new ModelManager(new VendorVault(
                getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

        ArchiveCommand command = new ArchiveCommand("notfound@email.com");

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));
        assertEquals(ArchiveCommand.MESSAGE_VENDOR_NOT_FOUND, exception.getMessage());
    }

    @Test
    public void execute_vendorAlreadyArchived_throwsCommandException() throws CommandException {
        Model model = new ModelManager(new VendorVault(
                getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

        Person vendor = model.getFilteredPersonList().get(0);

        // Archive the vendor first
        new ArchiveCommand(vendor.getEmail().value).execute(model);

        // Attempting to archive again should throw with the already-archived message
        ArchiveCommand archiveAgain = new ArchiveCommand(vendor.getEmail().value);
        CommandException exception = assertThrows(CommandException.class, () -> archiveAgain.execute(model));
        assertEquals(ArchiveCommand.MESSAGE_ALREADY_ARCHIVED, exception.getMessage());
    }

    @Test
    public void execute_emailIsCaseInsensitive_success() throws CommandException {
        Model model = new ModelManager(
                new VendorVault(getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

        Person vendorToArchive = model.getFilteredPersonList().get(0);
        String upperCaseEmail = vendorToArchive.getEmail().value.toUpperCase();

        ArchiveCommand archiveCommand = new ArchiveCommand(upperCaseEmail);
        archiveCommand.execute(model);

        Person archivedVendor = model.getAddressBook().getPersonList().stream()
                .filter(p -> p.getEmail().equals(vendorToArchive.getEmail()))
                .findFirst().get();

        assertTrue(archivedVendor.isArchived());
    }

    @Test
    public void getPendingConfirmation_returnsInactivePendingConfirmation() {
        ArchiveCommand archiveCommand = new ArchiveCommand(TypicalPersons.getTypicalPersons().get(0).getEmail().value);

        PendingConfirmation pendingConfirmation = archiveCommand.getPendingConfirmation();
        assertFalse(pendingConfirmation.getNeedConfirmation());
    }
}
