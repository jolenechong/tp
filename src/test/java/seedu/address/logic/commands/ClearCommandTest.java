package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalAliases.getTypicalAliases;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Aliases;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.VendorVault;

public class ClearCommandTest {

    @Test
    public void execute_emptyAddressBook_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(false), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyAddressBook_success() {
        Model model = new ModelManager(new VendorVault(
                getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());
        Model expectedModel = new ModelManager(new VendorVault(
                getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());
        expectedModel.setAddressBook(new AddressBook());

        assertCommandSuccess(new ClearCommand(false), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    // ── confirmation prompt ─────────────────────────────────────────────────

    @Test
    public void execute_withConfirmation_showsConfirmationMessage() {
        Model model = new ModelManager(new VendorVault(
                getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), getTypicalAliases());

        ClearCommand clearCommand = new ClearCommand(true);
        CommandResult result = clearCommand.execute(model);

        assertEquals(ClearCommand.CONFIRMATION_CLEAR_MESSAGE, result.getFeedbackToUser());

        // address book must NOT have been cleared yet
        assertFalse(model.getAddressBook().getPersonList().isEmpty());
    }

    @Test
    public void execute_withConfirmation_pendingConfirmationIsActive() {
        Model model = new ModelManager();

        ClearCommand clearCommand = new ClearCommand(true);
        clearCommand.execute(model);

        assertTrue(clearCommand.getPendingConfirmation().getNeedConfirmation());
    }

    @Test
    public void onConfirm_clearsAddressBook() {
        Model model = new ModelManager(new VendorVault(
                getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), getTypicalAliases());

        ClearCommand clearCommand = new ClearCommand(true);
        clearCommand.execute(model);

        Optional<CommandResult> result = clearCommand.getPendingConfirmation().getOnConfirm().get();

        assertTrue(result.isPresent());
        assertEquals(ClearCommand.MESSAGE_SUCCESS, result.get().getFeedbackToUser());
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
    }

    @Test
    public void onCancel_doesNotClearAddressBook() {
        Model model = new ModelManager(new VendorVault(
                getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), getTypicalAliases());
        int originalSize = model.getAddressBook().getPersonList().size();

        ClearCommand clearCommand = new ClearCommand(true);
        clearCommand.execute(model);

        Optional<CommandResult> result = clearCommand.getPendingConfirmation().getOnCancel().get();

        assertTrue(result.isPresent());
        assertEquals(ClearCommand.MESSAGE_CLEAR_FAILURE, result.get().getFeedbackToUser());
        assertEquals(originalSize, model.getAddressBook().getPersonList().size());
    }

    // ── getPendingConfirmation ───────────────────────────────────────────────

    @Test
    public void getPendingConfirmation_beforeExecute_returnsInactive() {
        ClearCommand clearCommand = new ClearCommand();
        assertFalse(clearCommand.getPendingConfirmation().getNeedConfirmation());
    }

}
