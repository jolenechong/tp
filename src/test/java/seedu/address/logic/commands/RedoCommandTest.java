package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import org.junit.jupiter.api.BeforeEach;
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

public class RedoCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(
                new VendorVault(getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());
    }

    @Test
    public void execute_noRedoAvailable_failure() {
        assertCommandFailure(new RedoCommand(), model,
                RedoCommand.MESSAGE_FAILURE);
    }

    @Test
    public void execute_afterUndo_redoSuccess() throws CommandException {
        Person person = new PersonBuilder().build();

        new AddCommand(person).execute(model);
        new UndoCommand().execute(model);

        Model expectedModel =
                new ModelManager(
                        new VendorVault(
                                getTypicalAddressBook(),
                                getTypicalInventory()),
                                new UserPrefs(), new Aliases());
        new AddCommand(person).execute(expectedModel);

        String addSuccessSummary = String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(person));
        String expectedRedoMessage = RedoCommand.MESSAGE_SUCCESS
                + String.format(RedoCommand.MESSAGE_REDID_ACTION, addSuccessSummary);

        assertCommandSuccess(
                new RedoCommand(),
                model,
                expectedRedoMessage,
                expectedModel
        );
    }

    @Test
    public void getPendingConfirmation_returnsInactivePendingConfirmation() {
        RedoCommand redoCommand = new RedoCommand();
        PendingConfirmation pendingConfirmation = redoCommand.getPendingConfirmation();
        assertFalse(pendingConfirmation.getNeedConfirmation());
    }
}

