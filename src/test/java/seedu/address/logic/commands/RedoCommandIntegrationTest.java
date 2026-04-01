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

public class RedoCommandIntegrationTest {

    private static final int FIRST_INDEX = 0;
    private static final String EDITED_PHONE = "998";
    private static final String EDITED_QUANTITY = "999";

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(
                new VendorVault(getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());
    }

    // ======================== No history ========================

    @Test
    public void execute_noRedoAvailable_failure() {
        assertCommandFailure(new RedoCommand(), model,
                RedoCommand.MESSAGE_FAILURE);
    }

    // ======================== Contacts ========================

    @Test
    public void execute_afterUndoAddPerson_redoSuccess() throws CommandException {
        // EP: redo a typical add-person operation
        Person person = new PersonBuilder().build();
        new AddCommand(person).execute(model);
        new UndoCommand().execute(model);

        Model expectedModel = freshModel();
        new AddCommand(person).execute(expectedModel);

        String expectedMessage = buildRedoMessage(AddCommand.MESSAGE_ACTION_SUMMARY, Messages.format(person));
        assertCommandSuccess(new RedoCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_afterUndoEditPerson_redoSuccess() throws CommandException {
        // EP: redo a typical edit-person operation
        Person personToEdit = model.getFilteredPersonList().get(FIRST_INDEX);
        Person editedPerson = new PersonBuilder(personToEdit).withPhone(EDITED_PHONE).build();
        EditCommand.EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone(EDITED_PHONE).build();
        new EditCommand(personToEdit.getEmail(), descriptor).execute(model);
        new UndoCommand().execute(model);

        Model expectedModel = freshModel();
        new EditCommand(personToEdit.getEmail(), descriptor).execute(expectedModel);

        String expectedMessage = buildRedoMessage(EditCommand.MESSAGE_ACTION_SUMMARY, Messages.format(editedPerson));
        assertCommandSuccess(new RedoCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_afterUndoDeletePerson_redoSuccess() throws CommandException {
        // EP: redo a typical delete-person operation
        Person personToDelete = model.getFilteredPersonList().get(FIRST_INDEX);
        new DeleteCommand(personToDelete.getEmail(), false).execute(model);
        new UndoCommand().execute(model);

        Model expectedModel = freshModel();
        new DeleteCommand(personToDelete.getEmail(), false).execute(expectedModel);

        String expectedMessage = buildRedoMessage(DeleteCommand.MESSAGE_ACTION_SUMMARY,
                Messages.format(personToDelete));
        assertCommandSuccess(new RedoCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_afterUndoArchivePerson_redoSuccess() throws CommandException {
        // EP: redo a typical archive-person operation
        Person personToArchive = model.getFilteredPersonList().get(FIRST_INDEX);
        new ArchiveCommand(personToArchive.getEmail().toString()).execute(model);
        new UndoCommand().execute(model);

        Model expectedModel = freshModel();
        new ArchiveCommand(personToArchive.getEmail().toString()).execute(expectedModel);

        String expectedMessage = buildRedoMessage(ArchiveCommand.MESSAGE_ACTION_SUMMARY,
                Messages.format(personToArchive));
        assertCommandSuccess(new RedoCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_afterUndoRestorePerson_redoSuccess() throws CommandException {
        // EP: redo a restore-person operation — requires prior archive to have a restorable person
        Person personToRestore = model.getFilteredPersonList().get(FIRST_INDEX);
        new ArchiveCommand(personToRestore.getEmail().toString()).execute(model);
        new RestoreCommand(personToRestore.getEmail().toString()).execute(model);
        new UndoCommand().execute(model); // undo restore, landing in post-archive state

        // expected state is post-restore redo re-applies the restore on top of archive
        Model expectedModel = freshModel();
        new ArchiveCommand(personToRestore.getEmail().toString()).execute(expectedModel);
        new RestoreCommand(personToRestore.getEmail().toString()).execute(expectedModel);

        String expectedMessage = buildRedoMessage(RestoreCommand.MESSAGE_ACTION_SUMMARY,
                Messages.format(personToRestore));
        assertCommandSuccess(new RedoCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_afterUndoClear_redoSuccess() throws CommandException {
        // EP: redo a clear-all-contacts operation
        new ClearCommand(false).execute(model);
        new UndoCommand().execute(model);

        Model expectedModel = freshModel();
        new ClearCommand(false).execute(expectedModel);

        String expectedMessage = RedoCommand.MESSAGE_SUCCESS + ClearCommand.MESSAGE_ACTION_SUMMARY;
        assertCommandSuccess(new RedoCommand(), model, expectedMessage, expectedModel);
    }

    // ======================== Products ========================

    @Test
    public void execute_afterUndoAddProduct_redoSuccess() throws CommandException {
        // EP: redo a typical add-product operation
        Product product = new ProductBuilder().build();
        new AddProductCommand(product).execute(model);
        new UndoCommand().execute(model);

        Model expectedModel = freshModel();
        new AddProductCommand(product).execute(expectedModel);

        String expectedMessage = buildRedoMessage(AddProductCommand.MESSAGE_ACTION_SUMMARY,
                Messages.formatProduct(product));
        assertCommandSuccess(new RedoCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_afterUndoEditProduct_redoSuccess() throws CommandException {
        // EP: redo a typical edit-product operation
        Product productToEdit = model.getFilteredProductList().get(FIRST_INDEX);
        Product editedProduct = new ProductBuilder(productToEdit).withQuantity(EDITED_QUANTITY).build();
        EditProductCommand.EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withQuantity(EDITED_QUANTITY).build();
        new EditProductCommand(productToEdit.getIdentifier().toString(), descriptor).execute(model);
        new UndoCommand().execute(model);

        Model expectedModel = freshModel();
        new EditProductCommand(productToEdit.getIdentifier().toString(), descriptor).execute(expectedModel);

        String expectedMessage = buildRedoMessage(EditProductCommand.MESSAGE_ACTION_SUMMARY,
                Messages.formatProduct(editedProduct));
        assertCommandSuccess(new RedoCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_afterUndoDeleteProduct_redoSuccess() throws CommandException {
        // EP: redo a typical delete-product operation
        Product productToDelete = model.getFilteredProductList().get(FIRST_INDEX);
        new DeleteProductCommand(productToDelete.getIdentifier(), false).execute(model);
        new UndoCommand().execute(model);

        Model expectedModel = freshModel();
        new DeleteProductCommand(productToDelete.getIdentifier(), false).execute(expectedModel);

        String expectedMessage = buildRedoMessage(DeleteProductCommand.MESSAGE_ACTION_SUMMARY,
            productToDelete.toString());
        assertCommandSuccess(new RedoCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_afterUndoArchiveProduct_redoSuccess() throws CommandException {
        // EP: redo a typical archive-product operation
        Product productToArchive = model.getFilteredProductList().get(FIRST_INDEX);
        new ArchiveProductCommand(productToArchive.getIdentifier().toString()).execute(model);
        new UndoCommand().execute(model);

        Model expectedModel = freshModel();
        new ArchiveProductCommand(productToArchive.getIdentifier().toString()).execute(expectedModel);

        String expectedMessage = buildRedoMessage(ArchiveProductCommand.MESSAGE_ACTION_SUMMARY,
                productToArchive.toString());
        assertCommandSuccess(new RedoCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_afterUndoRestoreProduct_redoSuccess() throws CommandException {
        // EP: redo a restore-product operation requires prior archive to have a restorable product
        Product productToRestore = model.getFilteredProductList().get(FIRST_INDEX);
        new ArchiveProductCommand(productToRestore.getIdentifier().toString()).execute(model);
        new RestoreProductCommand(productToRestore.getIdentifier().toString()).execute(model);
        new UndoCommand().execute(model);

        // expected state is post-restore — redo re-applies the restore on top of archive
        Model expectedModel = freshModel();
        new ArchiveProductCommand(productToRestore.getIdentifier().toString()).execute(expectedModel);
        new RestoreProductCommand(productToRestore.getIdentifier().toString()).execute(expectedModel);

        String expectedMessage = buildRedoMessage(RestoreProductCommand.MESSAGE_ACTION_SUMMARY,
                productToRestore.toString());
        assertCommandSuccess(new RedoCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_afterUndoClearProduct_redoSuccess() throws CommandException {
        // EP: redo a clear-all-products operation
        new ClearProductCommand(false).execute(model);
        new UndoCommand().execute(model);

        Model expectedModel = freshModel();
        new ClearProductCommand(false).execute(expectedModel);

        String expectedMessage = RedoCommand.MESSAGE_SUCCESS + ClearProductCommand.MESSAGE_ACTION_SUMMARY;
        assertCommandSuccess(new RedoCommand(), model, expectedMessage, expectedModel);
    }

    // ======================== others ========================

    @Test
    public void getPendingConfirmation_returnsInactivePendingConfirmation() {
        RedoCommand redoCommand = new RedoCommand();
        PendingConfirmation pendingConfirmation = redoCommand.getPendingConfirmation();
        assertFalse(pendingConfirmation.getNeedConfirmation());
    }

    // ======================== helpers ========================

    /**
     * Returns a fresh {@code Model} initialised from the typical data sets.
     * Used as the baseline when constructing expected models in redo-success assertions.
     */
    private Model freshModel() {
        return new ModelManager(new VendorVault(getTypicalAddressBook(), getTypicalInventory()),
                new UserPrefs(), new Aliases());
    }

    /**
     * Builds the expected redo success message by combining {@code RedoCommand.MESSAGE_SUCCESS}
     * with the formatted action summary of the redone command.
     */
    private String buildRedoMessage(String actionSummaryTemplate, String formattedEntity) {
        return RedoCommand.MESSAGE_SUCCESS + String.format(actionSummaryTemplate, formattedEntity);
    }
}

