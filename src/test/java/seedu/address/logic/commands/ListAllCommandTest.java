package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE_PERSONS;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE_PRODUCTS;
import static seedu.address.testutil.TypicalAliases.getTypicalAliases;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import org.junit.jupiter.api.Test;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.VendorVault;

public class ListAllCommandTest {

    private Model model = new ModelManager(
            new VendorVault(getTypicalAddressBook(), getTypicalInventory()),
            new UserPrefs(), getTypicalAliases());

    private Model expectedModel = new ModelManager(
            new VendorVault(getTypicalAddressBook(), getTypicalInventory()),
            new UserPrefs(), getTypicalAliases());

    @Test
    public void execute_listAll_success() {
        ListAllCommand command = new ListAllCommand();

        expectedModel.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS);
        expectedModel.updateFilteredProductList(PREDICATE_SHOW_ACTIVE_PRODUCTS);

        assertCommandSuccess(command, model,
                ListAllCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void getPendingConfirmation_returnsObject() {
        ListAllCommand command = new ListAllCommand();

        assertNotNull(command.getPendingConfirmation());
    }

    @Test
    public void toStringMethod() {
        ListAllCommand command = new ListAllCommand();
        String expected = new ToStringBuilder(command).toString();
        assertEquals(expected, command.toString());
    }
}
