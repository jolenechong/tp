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
import seedu.address.model.product.Product;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.EditProductDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.ProductBuilder;

public class UndoCommandIntegrationTest {

    private static final int FIRST_INDEX = 0;
    private static final String EDITED_PHONE = "998";
    private static final String EDITED_QUANTITY = "999";
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(new VendorVault(getTypicalAddressBook(), getTypicalInventory()),
                new UserPrefs(), new Aliases());
    }

    // ======================== No history ========================

    @Test
    public void execute_noUndoAvailable_failure() {
        // EP: boundary, undo with empty history
        assertCommandFailure(new UndoCommand(), model,
                UndoCommand.MESSAGE_FAILURE);
    }

    // ======================== Contacts ========================

    @Test
    public void execute_afterAddPerson_undoSuccess() throws CommandException {
        // EP: undo a typical add-person operation
        Person person = new PersonBuilder().build();
        new AddCommand(person).execute(model);

        String expectedMessage = buildUndoMessage(AddCommand.MESSAGE_ACTION_SUMMARY, Messages.format(person));
        assertCommandSuccess(new UndoCommand(), model, expectedMessage, freshModel());
    }

    @Test
    public void execute_afterEditPerson_undoSuccess() throws CommandException {
        // EP: undo a typical edit-person operation
        Person personToEdit = model.getFilteredPersonList().get(FIRST_INDEX);
        Person editedPerson = new PersonBuilder(personToEdit).withPhone(EDITED_PHONE).build();
        EditCommand.EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone(EDITED_PHONE).build();
        new EditCommand(personToEdit.getEmail(), descriptor).execute(model);

        String expectedMessage = buildUndoMessage(EditCommand.MESSAGE_ACTION_SUMMARY, Messages.format(editedPerson));
        assertCommandSuccess(new UndoCommand(), model, expectedMessage, freshModel());
    }

    @Test
    public void execute_afterDeletePerson_undoSuccess() throws CommandException {
        // EP: undo a typical delete-person operation
        Person personToDelete = model.getFilteredPersonList().get(FIRST_INDEX);
        new DeleteCommand(personToDelete.getEmail(), false).execute(model);

        String expectedMessage = buildUndoMessage(DeleteCommand.MESSAGE_ACTION_SUMMARY,
                Messages.format(personToDelete));
        assertCommandSuccess(new UndoCommand(), model, expectedMessage, freshModel());
    }

    @Test
    public void execute_afterArchivePerson_undoSuccess() throws CommandException {
        // EP: undo a typical archive-person operation
        Person personToArchive = model.getFilteredPersonList().get(FIRST_INDEX);
        new ArchiveCommand(personToArchive.getEmail().toString()).execute(model);

        String expectedMessage = buildUndoMessage(ArchiveCommand.MESSAGE_ACTION_SUMMARY,
                Messages.format(personToArchive));
        assertCommandSuccess(new UndoCommand(), model, expectedMessage, freshModel());
    }

    @Test
    public void execute_afterRestorePerson_undoSuccess() throws CommandException {
        // EP: undo a typical restore-person after an archive operation
        Person personToRestore = model.getFilteredPersonList().get(FIRST_INDEX);
        new ArchiveCommand(personToRestore.getEmail().toString()).execute(model);
        new RestoreCommand(personToRestore.getEmail().toString()).execute(model);

        // undo is a 1-step operations, so  expected model is state after archive, not original state
        Model expectedModel = freshModel();
        new ArchiveCommand(personToRestore.getEmail().toString()).execute(expectedModel);

        String expectedMessage = buildUndoMessage(RestoreCommand.MESSAGE_ACTION_SUMMARY,
                Messages.format(personToRestore));
        assertCommandSuccess(new UndoCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_afterClear_undoSuccess() throws CommandException {
        // EP: undo a clear-all-contacts operation
        new ClearCommand(false).execute(model);

        String expectedMessage = UndoCommand.MESSAGE_SUCCESS + ClearCommand.MESSAGE_ACTION_SUMMARY;
        assertCommandSuccess(new UndoCommand(), model, expectedMessage, freshModel());
    }

    // ======================== Products ========================

    @Test
    public void execute_afterAddProduct_undoSuccess() throws CommandException {
        // EP: undo a typical add-product operation
        Product product = new ProductBuilder().build();
        new AddProductCommand(product).execute(model);

        String expectedMessage = buildUndoMessage(AddProductCommand.MESSAGE_ACTION_SUMMARY,
                Messages.formatProduct(product));
        assertCommandSuccess(new UndoCommand(), model, expectedMessage, freshModel());
    }

    @Test
    public void execute_afterEditProduct_undoSuccess() throws CommandException {
        // EP: undo a typical edit-product operation
        Product productToEdit = model.getFilteredProductList().get(FIRST_INDEX);
        Product editedProduct = new ProductBuilder(productToEdit).withQuantity(EDITED_QUANTITY).build();
        EditProductCommand.EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withQuantity(EDITED_QUANTITY).build();
        new EditProductCommand(productToEdit.getIdentifier().toString(), descriptor).execute(model);

        String expectedMessage = buildUndoMessage(EditProductCommand.MESSAGE_ACTION_SUMMARY,
                Messages.formatProduct(editedProduct));
        assertCommandSuccess(new UndoCommand(), model, expectedMessage, freshModel());
    }

    @Test
    public void execute_afterDeleteProduct_undoSuccess() throws CommandException {
        // EP: undo a typical delete-product operation
        Product productToDelete = model.getFilteredProductList().get(FIRST_INDEX);
        new DeleteProductCommand(productToDelete.getIdentifier(), false).execute(model);

        String expectedMessage = buildUndoMessage(DeleteProductCommand.MESSAGE_ACTION_SUMMARY,
                productToDelete.toString());
        assertCommandSuccess(new UndoCommand(), model, expectedMessage, freshModel());
    }

    @Test
    public void execute_afterArchiveProduct_undoSuccess() throws CommandException {
        // EP: undo a typical archive-product operation
        Product productToArchive = model.getFilteredProductList().get(FIRST_INDEX);
        new ArchiveProductCommand(productToArchive.getIdentifier().toString()).execute(model);

        String expectedMessage = buildUndoMessage(ArchiveProductCommand.MESSAGE_ACTION_SUMMARY,
                productToArchive.toString());
        assertCommandSuccess(new UndoCommand(), model, expectedMessage, freshModel());
    }

    @Test
    public void execute_afterRestoreProduct_undoSuccess() throws CommandException {
        // EP: undo a restore-product operation — requires prior archive to have a restorable product
        Product productToRestore = model.getFilteredProductList().get(FIRST_INDEX);
        new ArchiveProductCommand(productToRestore.getIdentifier().toString()).execute(model);
        new RestoreProductCommand(productToRestore.getIdentifier().toString()).execute(model);

        Model expectedModel = freshModel();
        new ArchiveProductCommand(productToRestore.getIdentifier().toString()).execute(expectedModel);

        String expectedMessage = buildUndoMessage(RestoreProductCommand.MESSAGE_ACTION_SUMMARY,
                productToRestore.toString());
        assertCommandSuccess(new UndoCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_afterClearProduct_undoSuccess() throws CommandException {
        // EP: undo a clear-all-products operation
        new ClearProductCommand(false).execute(model);

        String expectedMessage = UndoCommand.MESSAGE_SUCCESS + ClearProductCommand.MESSAGE_ACTION_SUMMARY;
        assertCommandSuccess(new UndoCommand(), model, expectedMessage, freshModel());
    }

    // ======================== others ========================

    @Test
    public void getPendingConfirmation_returnsInactivePendingConfirmation() {
        // EP: undo itself should never require confirmation
        UndoCommand undoCommand = new UndoCommand();
        PendingConfirmation pendingConfirmation = undoCommand.getPendingConfirmation();
        assertFalse(pendingConfirmation.getNeedConfirmation());
    }

    // ======================== helpers ========================

    /**
     * Returns a fresh {@code Model} initialised from the typical data sets.
     */
    private Model freshModel() {
        return new ModelManager(new VendorVault(getTypicalAddressBook(), getTypicalInventory()),
                new UserPrefs(), new Aliases());
    }

    /**
     * Builds the expected undo success message.
     */
    private String buildUndoMessage(String actionSummaryTemplate, String formattedEntity) {
        return UndoCommand.MESSAGE_SUCCESS + String.format(actionSummaryTemplate, formattedEntity);
    }
}
