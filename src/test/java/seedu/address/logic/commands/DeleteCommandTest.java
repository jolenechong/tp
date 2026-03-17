package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_EMAIL;
import static seedu.address.logic.commands.CommandResult.FEEDBACK_TYPE_WARN;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.logic.commands.DeleteCommand.CONFIRMATION_DELETE_PERSON_MESSAGE;
import static seedu.address.logic.commands.DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS;
import static seedu.address.logic.commands.DeleteCommand.MESSAGE_PRODUCTS_DELINKED;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.TestUtil.getProductByIdentifier;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
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
import seedu.address.model.person.Email;
import seedu.address.model.person.NameEqualsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.product.Product;
import seedu.address.testutil.ProductBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private static final Email NON_EXISTENT_EMAIL = new Email("missing.person@example.com");
    private static final Email AMY_EMAIL = new Email(VALID_EMAIL_AMY);
    private static final Email BOB_EMAIL = new Email(VALID_EMAIL_BOB);

    private Model model = new ModelManager(new VendorVault(
            getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

    @Test
    public void execute_validIndexUnfilteredListDeleteOnly_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(personToDelete.getEmail(), true);

        ModelManager expectedModel = new ModelManager(model.getVendorVault(), new UserPrefs(), new Aliases());
        NameEqualsKeywordsPredicate predicate = new NameEqualsKeywordsPredicate(personToDelete);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(deleteCommand, model, CONFIRMATION_DELETE_PERSON_MESSAGE, expectedModel);
    }

    @Test
    public void execute_validIndexUnfilteredListDeleteAndConfirm_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(personToDelete.getEmail(), true);

        ModelManager expectedModel = new ModelManager(model.getVendorVault(), new UserPrefs(), new Aliases());
        NameEqualsKeywordsPredicate predicate = new NameEqualsKeywordsPredicate(personToDelete);
        expectedModel.updateFilteredPersonList(predicate);

        PendingConfirmation pendingConfirmation;
        try {
            CommandResult result = deleteCommand.execute(model);
            pendingConfirmation = deleteCommand.getPendingConfirmation();
            CommandResult expectedCommandResult = new CommandResult(CONFIRMATION_DELETE_PERSON_MESSAGE);
            assertEquals(expectedCommandResult, result);
            assertEquals(expectedModel, model);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }

        ConfirmCommand confirmCommand = new ConfirmCommand(pendingConfirmation.getOnConfirm());

        expectedModel.deletePerson(personToDelete);
        expectedModel.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        assertCommandSuccess(confirmCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIndexUnfilteredListDeleteAndCancel_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(personToDelete.getEmail(), true);

        ModelManager expectedModel = new ModelManager(model.getVendorVault(), new UserPrefs(), new Aliases());
        NameEqualsKeywordsPredicate predicate = new NameEqualsKeywordsPredicate(personToDelete);
        expectedModel.updateFilteredPersonList(predicate);

        PendingConfirmation pendingConfirmation;
        try {
            CommandResult result = deleteCommand.execute(model);
            pendingConfirmation = deleteCommand.getPendingConfirmation();
            CommandResult expectedCommandResult = new CommandResult(CONFIRMATION_DELETE_PERSON_MESSAGE);
            assertEquals(expectedCommandResult, result);
            assertEquals(expectedModel, model);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }

        CancelCommand cancelCommand = new CancelCommand(pendingConfirmation.getOnCancel());

        expectedModel.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        assertCommandSuccess(cancelCommand, model, DeleteCommand.MESSAGE_DELETE_FAILURE, expectedModel);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(personToDelete.getEmail(), true);

        Model expectedModel = new ModelManager(model.getVendorVault(), new UserPrefs(), new Aliases());
        NameEqualsKeywordsPredicate predicate = new NameEqualsKeywordsPredicate(personToDelete);
        expectedModel.updateFilteredPersonList(predicate);

        PendingConfirmation pendingConfirmation;
        try {
            CommandResult result = deleteCommand.execute(model);
            pendingConfirmation = deleteCommand.getPendingConfirmation();
            CommandResult expectedCommandResult = new CommandResult(CONFIRMATION_DELETE_PERSON_MESSAGE);
            assertEquals(expectedCommandResult, result);
            assertEquals(expectedModel, model);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }

        ConfirmCommand confirmCommand = new ConfirmCommand(pendingConfirmation.getOnConfirm());

        expectedModel.deletePerson(personToDelete);
        expectedModel.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        assertCommandSuccess(confirmCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIndexNoConfirmation_deletesPerson() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(personToDelete.getEmail(), false);

        CommandResult result;
        try {
            result = deleteCommand.execute(model);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
        assertEquals(String.format(MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete)), result.getFeedbackToUser());
        assertFalse(model.hasPerson(personToDelete));
    }

    @Test
    public void execute_validIndexWithConfirmation_showsConfirmationMessage() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(personToDelete.getEmail(), true);

        CommandResult result;
        try {
            result = deleteCommand.execute(model);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }

        assertEquals(CONFIRMATION_DELETE_PERSON_MESSAGE, result.getFeedbackToUser());
        assertTrue(model.hasPerson(personToDelete));
    }

    @Test
    public void execute_nonExistentEmail_throwsCommandException() {
        DeleteCommand deleteCommand = new DeleteCommand(NON_EXISTENT_EMAIL, false);

        assertCommandFailure(deleteCommand, model, MESSAGE_INVALID_PERSON_DISPLAYED_EMAIL);
    }

    @Test
    public void deletePerson_withLinkedProducts_clearsVendorEmailAndWarns() {
        Person personToDelete = model.getFilteredPersonList().get(0);
        Product linkedProductOne = new ProductBuilder()
                .withIdentifier("SKU-010")
                .withName("Linked Product One")
                .withVendorEmail(personToDelete.getEmail().value)
                .build();

        model.addProduct(linkedProductOne);

        DeleteCommand deleteCommand = new DeleteCommand(personToDelete.getEmail(), false);
        CommandResult result;
        try {
            result = deleteCommand.execute(model);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }

        Product updatedLinkedOne = getProductByIdentifier(model, "SKU-010");
        assertTrue(updatedLinkedOne.getVendorEmail().isEmpty());

        String expectedSuccessMessage = String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete));
        String expectedWarning = String.format(MESSAGE_PRODUCTS_DELINKED, 1, "SKU-010");
        assertEquals(expectedSuccessMessage + "/n" + expectedWarning, result.getFeedbackToUser());
        assertEquals(FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void onConfirm_withLinkedProducts_clearsVendorEmailAndWarns() {
        Person personToDelete = model.getFilteredPersonList().get(0);
        Product linkedProduct = new ProductBuilder()
                .withIdentifier("SKU-012")
                .withName("Linked Product")
                .withVendorEmail(personToDelete.getEmail().value)
                .build();

        model.addProduct(linkedProduct);

        DeleteCommand deleteCommand = new DeleteCommand(personToDelete.getEmail(), true);
        PendingConfirmation pendingConfirmation;
        try {
            deleteCommand.execute(model);
            pendingConfirmation = deleteCommand.getPendingConfirmation();
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }

        ConfirmCommand confirmCommand = new ConfirmCommand(pendingConfirmation.getOnConfirm());
        CommandResult result;
        try {
            result = confirmCommand.execute(model);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }

        Product updatedLinked = getProductByIdentifier(model, "SKU-012");
        assertTrue(updatedLinked.getVendorEmail().isEmpty());

        String expectedSuccessMessage = String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete));
        String expectedWarning = String.format(MESSAGE_PRODUCTS_DELINKED, 1, "SKU-012");
        assertEquals(expectedSuccessMessage + "/n" + expectedWarning, result.getFeedbackToUser());
        assertEquals(FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void collectLinkedProducts_noLinkedProducts_returnsEmptyList() {
        Person personToDelete = model.getFilteredPersonList().get(0);
        DeleteCommand deleteCommand = new DeleteCommand(personToDelete.getEmail(), false);

        assertTrue(deleteCommand.collectLinkedProducts(model, personToDelete).isEmpty());
    }

    @Test
    public void collectLinkedProducts_hasLinkedProducts_returnsMatchingProducts() {
        Person personToDelete = model.getFilteredPersonList().get(0);
        Person anotherPerson = model.getFilteredPersonList().get(1);
        Product linkedProductOne = new ProductBuilder()
                .withIdentifier("SKU-001")
                .withName("Linked Product One")
                .withVendorEmail(personToDelete.getEmail().value)
                .build();
        Product linkedProductTwoArchived = new ProductBuilder()
                .withIdentifier("SKU-002")
                .withName("Linked Product Two")
                .withVendorEmail(personToDelete.getEmail().value)
                .build()
                .archive();
        Product unlinkedProduct = new ProductBuilder()
                .withIdentifier("SKU-003")
                .withName("Unlinked Product")
                .withVendorEmail(anotherPerson.getEmail().value)
                .build();

        model.addProduct(linkedProductOne);
        model.addProduct(linkedProductTwoArchived);
        model.addProduct(unlinkedProduct);

        DeleteCommand deleteCommand = new DeleteCommand(personToDelete.getEmail(), false);
        assertEquals(2, deleteCommand.collectLinkedProducts(model, personToDelete).size());
        assertTrue(deleteCommand.collectLinkedProducts(model, personToDelete).contains(linkedProductOne));
        assertTrue(deleteCommand.collectLinkedProducts(model, personToDelete).contains(linkedProductTwoArchived));
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(AMY_EMAIL, true);
        DeleteCommand deleteSecondCommand = new DeleteCommand(BOB_EMAIL, true);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(AMY_EMAIL, true);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        DeleteCommand deleteCommand = new DeleteCommand(AMY_EMAIL, true);
        String expected = DeleteCommand.class.getCanonicalName() + "{targetEmail=" + VALID_EMAIL_AMY + "}";
        assertEquals(expected, deleteCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }

}
