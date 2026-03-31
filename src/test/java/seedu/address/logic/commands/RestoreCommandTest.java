package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.TypicalPersons;

/**
 * Tests for RestoreCommand.
 */
public class RestoreCommandTest {

    @Test
    public void execute_restoreVendor_success() throws CommandException {
        Model model = new ModelManager(new VendorVault(
                getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

        Person vendor = model.getFilteredPersonList().get(0);
        model.archivePerson(vendor);

        RestoreCommand restoreCommand = new RestoreCommand(vendor.getEmail().value);
        restoreCommand.execute(model);

        Person restoredVendor = model.getAddressBook().getPersonList().stream()
                .filter(p -> p.getEmail().equals(vendor.getEmail()))
                .findFirst().get();

        assertFalse(restoredVendor.isArchived());
    }

    @Test
    public void execute_restoreVendor_noArchivedTagInMsg()
            throws CommandException {
        Model model = new ModelManager(new VendorVault(
                getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

        Person vendor = model.getFilteredPersonList().get(0);
        model.archivePerson(vendor);

        RestoreCommand restoreCommand = new RestoreCommand(vendor.getEmail().value);
        CommandResult result = restoreCommand.execute(model);

        // Success message should use the restored (un-archived) person's details
        Person restoredPerson = vendor.restore();
        String expectedMessage = String.format(RestoreCommand.MESSAGE_RESTORE_SUCCESS,
                Messages.format(restoredPerson));
        assertEquals(expectedMessage, result.getFeedbackToUser());

        // The [archived] tag must not appear in the success message
        assertFalse(result.getFeedbackToUser().contains("archived"));
    }

    @Test
    public void execute_noEmail_returnsNoneArchived() throws CommandException {
        Model model = new ModelManager(new VendorVault(
                getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

        // No vendors are archived — restoring with null should show the none-archived message
        RestoreCommand command = new RestoreCommand(null);
        CommandResult result = command.execute(model);

        assertEquals(RestoreCommand.MESSAGE_NONE_ARCHIVED, result.getFeedbackToUser());
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, result.getFeedbackType());
    }

    @Test
    public void execute_noEmail_returnsListArchived() throws CommandException {
        Model model = new ModelManager(new VendorVault(
                getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

        // Archive one vendor so the list is non-empty
        model.archivePerson(model.getFilteredPersonList().get(0));

        RestoreCommand command = new RestoreCommand(null);
        CommandResult result = command.execute(model);

        assertEquals(RestoreCommand.MESSAGE_LIST_ARCHIVED, result.getFeedbackToUser());
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, result.getFeedbackType());
    }

    @Test
    public void execute_invalidEmail_throwsCommandException() {
        Model model = new ModelManager(
                new VendorVault(getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

        RestoreCommand command = new RestoreCommand("nonexistent@example.com");

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));
        assertEquals(RestoreCommand.MESSAGE_VENDOR_NOT_FOUND, exception.getMessage());
    }

    @Test
    public void execute_personNotArchived_throwsCommandException() {
        Model model = new ModelManager(new VendorVault(
                getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

        Person person = model.getFilteredPersonList().get(0);

        // Person exists but is not archived
        RestoreCommand command = new RestoreCommand(person.getEmail().value);

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_restoreArchivedPerson_success() throws Exception {
        Model model = new ModelManager(
                new VendorVault(getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

        Person archived = new PersonBuilder().withEmail("test@email.com").build();
        model.addPerson(archived);
        model.archivePerson(archived);

        RestoreCommand command = new RestoreCommand("test@email.com");
        command.execute(model);

        Person restored = model.getAddressBook().getPersonList().stream()
                .filter(p -> p.getEmail().value.equals("test@email.com"))
                .findFirst().get();

        assertFalse(restored.isArchived());
    }

    @Test
    public void execute_emailMatchesArchivedPerson_success() throws Exception {
        Model model = new ModelManager(new VendorVault(
                getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

        Person person = model.getFilteredPersonList().get(0);
        model.archivePerson(person);

        RestoreCommand command = new RestoreCommand(person.getEmail().value);
        command.execute(model);

        Person restored = model.getAddressBook().getPersonList().stream()
                .filter(p -> p.getEmail().equals(person.getEmail()))
                .findFirst().get();

        assertFalse(restored.isArchived());
    }

    @Test
    public void execute_emailIsCaseInsensitive_success() throws Exception {
        Model model = new ModelManager(new VendorVault(
                getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

        Person person = model.getFilteredPersonList().get(0);
        model.archivePerson(person);

        String upperCaseEmail = person.getEmail().value.toUpperCase();
        RestoreCommand command = new RestoreCommand(upperCaseEmail);
        command.execute(model);

        Person restored = model.getAddressBook().getPersonList().stream()
                .filter(p -> p.getEmail().equals(person.getEmail()))
                .findFirst().get();

        assertFalse(restored.isArchived());
    }

    @Test
    public void getPendingConfirmation_returnsInactivePendingConfirmation() {
        RestoreCommand restoreCommand = new RestoreCommand(TypicalPersons.getTypicalPersons().get(0).getEmail().value);
        PendingConfirmation pendingConfirmation = restoreCommand.getPendingConfirmation();
        assertFalse(pendingConfirmation.getNeedConfirmation());
    }
}
