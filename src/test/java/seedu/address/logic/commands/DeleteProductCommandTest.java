package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.DeleteProductCommand.CONFIRMATION_DELETE_PRODUCT_MESSAGE;
import static seedu.address.logic.commands.DeleteProductCommand.MESSAGE_DELETE_PRODUCT_SUCCESS;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE_PRODUCTS;
import static seedu.address.testutil.TypicalAliases.getTypicalAliases;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.VendorVault;
import seedu.address.model.product.Product;

public class DeleteProductCommandTest {

    private Model model = new ModelManager(
            new VendorVault(getTypicalAddressBook(), getTypicalInventory()),
            new UserPrefs(), getTypicalAliases());

    @Test
    public void execute_validProductUnfilteredListDeleteOnly_success() {
        Product productToDelete = model.getFilteredProductList().get(0);

        DeleteProductCommand command =
                new DeleteProductCommand(productToDelete.getIdentifier().toString(), false);

        ModelManager expectedModel = new ModelManager(model.getVendorVault(), new UserPrefs(), getTypicalAliases());

        expectedModel.deleteProduct(productToDelete);
        String successPart = String.format(MESSAGE_DELETE_PRODUCT_SUCCESS, productToDelete);
        expectedModel.commitVendorVault(successPart);
        expectedModel.updateFilteredProductList(PREDICATE_SHOW_ACTIVE_PRODUCTS);

        assertCommandSuccess(command, model, successPart, expectedModel);
    }

    @Test
    public void execute_validProductConfirmation_success() {
        Product productToDelete = model.getFilteredProductList().get(0);

        DeleteProductCommand command =
                new DeleteProductCommand(productToDelete.getIdentifier().toString(), true);

        ModelManager expectedModel = new ModelManager(model.getVendorVault(), new UserPrefs(), getTypicalAliases());

        expectedModel.updateFilteredProductList(
                p -> p.getIdentifier().toString().equals(productToDelete.getIdentifier().toString())
        );

        assertCommandSuccess(command, model,
                CONFIRMATION_DELETE_PRODUCT_MESSAGE,
                expectedModel);
    }

    @Test
    public void execute_validProductDeleteAndConfirm_success() {
        Product productToDelete = model.getFilteredProductList().get(0);

        DeleteProductCommand command =
                new DeleteProductCommand(productToDelete.getIdentifier().toString(), true);

        PendingConfirmation pending;

        try {
            command.execute(model);
            pending = command.getPendingConfirmation();
        } catch (CommandException e) {
            throw new AssertionError(e);
        }

        ConfirmCommand confirmCommand =
                new ConfirmCommand(pending.getOnConfirm());

        Model expectedModel = new ModelManager(model.getVendorVault(), new UserPrefs(), getTypicalAliases());

        expectedModel.deleteProduct(productToDelete);
        String successPart = String.format(MESSAGE_DELETE_PRODUCT_SUCCESS, productToDelete);
        expectedModel.commitVendorVault(successPart);
        expectedModel.updateFilteredProductList(PREDICATE_SHOW_ACTIVE_PRODUCTS);

        assertCommandSuccess(confirmCommand, model, successPart, expectedModel);
    }

    @Test
    public void execute_validProductDeleteAndCancel_success() {

        Product productToDelete = model.getFilteredProductList().get(0);

        DeleteProductCommand command =
                new DeleteProductCommand(productToDelete.getIdentifier().toString(), true);

        PendingConfirmation pending;

        try {
            command.execute(model);
            pending = command.getPendingConfirmation();
        } catch (CommandException e) {
            throw new AssertionError(e);
        }

        CancelCommand cancelCommand =
                new CancelCommand(pending.getOnCancel());

        Model expectedModel = new ModelManager(model.getVendorVault(), new UserPrefs(), getTypicalAliases());

        expectedModel.updateFilteredProductList(PREDICATE_SHOW_ACTIVE_PRODUCTS);

        assertCommandSuccess(cancelCommand, model,
                DeleteProductCommand.MESSAGE_DELETE_FAILURE,
                expectedModel);
    }

    @Test
    public void execute_noConfirmation_deletesProduct() {

        Product productToDelete = model.getFilteredProductList().get(0);

        DeleteProductCommand command =
                new DeleteProductCommand(productToDelete.getIdentifier().toString(), false);

        CommandResult result;

        try {
            result = command.execute(model);
        } catch (CommandException e) {
            throw new AssertionError(e);
        }

        assertEquals(
                String.format(MESSAGE_DELETE_PRODUCT_SUCCESS,
                        productToDelete),
                result.getFeedbackToUser());

        assertFalse(model.getFilteredProductList().contains(productToDelete));
    }

    @Test
    public void execute_withConfirmation_showsConfirmationMessage() {

        Product productToDelete = model.getFilteredProductList().get(0);

        DeleteProductCommand command =
                new DeleteProductCommand(productToDelete.getIdentifier().toString(), true);

        CommandResult result;

        try {
            result = command.execute(model);
        } catch (CommandException e) {
            throw new AssertionError(e);
        }

        assertEquals(CONFIRMATION_DELETE_PRODUCT_MESSAGE,
                result.getFeedbackToUser());
    }

    @Test
    public void equals() {

        DeleteProductCommand first =
                new DeleteProductCommand("P001", true);

        DeleteProductCommand second =
                new DeleteProductCommand("P002", true);

        DeleteProductCommand firstCopy =
                new DeleteProductCommand("P001", true);

        assertTrue(first.equals(first));
        assertTrue(first.equals(firstCopy));
        assertFalse(first.equals(second));
        assertFalse(first.equals(null));
        assertFalse(first.equals(1));
    }

    @Test
    public void toString_containsIdentifier() {
        DeleteProductCommand command = new DeleteProductCommand("P001", true);

        String str = command.toString();

        assertTrue(str.contains("P001"));
    }

    @Test
    public void execute_invalidProductId_throwsCommandException() {
        Model model = new ModelManager(
                new VendorVault(getTypicalAddressBook(), getTypicalInventory()),
                new UserPrefs(), getTypicalAliases());

        DeleteProductCommand command = new DeleteProductCommand("INVALID_ID", false);

        assertThrows(CommandException.class, () -> command.execute(model));
    }
}
