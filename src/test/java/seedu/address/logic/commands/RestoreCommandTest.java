package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;
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

        RestoreCommand restoreCommand = new RestoreCommand(vendor.getEmail().value);

        restoreCommand.execute(model);

        Person restoredVendor = model.getAddressBook().getPersonList().get(0);

        assertFalse(restoredVendor.isArchived());
    }

    @Test
    public void getPendingConfirmation_returnsInactivePendingConfirmation() {
        RestoreCommand restoreCommand = new RestoreCommand(TypicalPersons.getTypicalPersons().get(0).getEmail().value);
        PendingConfirmation pendingConfirmation = restoreCommand.getPendingConfirmation();
        assertFalse(pendingConfirmation.getNeedConfirmation());
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        RestoreCommand command = new RestoreCommand("nonexistent@example.com");

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_restoreArchivedPerson_success() throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        Person archived = new PersonBuilder().withEmail("test@email.com").build();
        model.addPerson(archived);
        model.archivePerson(archived);

        RestoreCommand command = new RestoreCommand("test@email.com");

        command.execute(model);

        assertFalse(model.getFilteredPersonList().get(0).isArchived());
    }
}
