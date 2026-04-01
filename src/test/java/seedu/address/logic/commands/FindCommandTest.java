package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Aliases;
import seedu.address.model.Inventory;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.VendorVault;
import seedu.address.model.person.NameContainsKeywordsScoredPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.product.Product;
import seedu.address.model.product.VendorEmailMatchesContactsPredicate;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.ProductBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(new VendorVault(
                getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());
    private Model expectedModel = new ModelManager(new VendorVault(
                getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

    @Test
    public void equals() {
        NameContainsKeywordsScoredPredicate firstPredicate =
            new NameContainsKeywordsScoredPredicate(Collections.singletonList("first"));
        NameContainsKeywordsScoredPredicate secondPredicate =
            new NameContainsKeywordsScoredPredicate(Collections.singletonList("second"));

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        NameContainsKeywordsScoredPredicate predicate = preparePredicate(" ");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        updateExpectedProductFilter(expectedModel);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
        assertEquals(Collections.emptyList(), model.getFilteredProductList());
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsScoredPredicate predicate = preparePredicate("Kurz Elle Kunz");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        updateExpectedProductFilter(expectedModel);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(CARL, ELLE, FIONA), model.getFilteredPersonList());
        assertEquals(Collections.emptyList(), model.getFilteredProductList());
    }

    @Test
    public void execute_keywordMatchesVendor_filtersLinkedProducts() {
        // attempt find carl, inventory should update and unrelated products not shown
        Product linkedProduct = new ProductBuilder().withIdentifier("SKU-9999")
                .withName("Vendor Linked Item").withQuantity("10").withThreshold("5")
                .withVendorEmail(CARL.getEmail().toString()).build();
        Product unrelatedProduct = new ProductBuilder().withIdentifier("SKU-9998")
                .withName("Unrelated Vendor Item").withQuantity("10").withThreshold("5")
                .withVendorEmail("nobody@example.com").build();

        Inventory inventory = new Inventory(getTypicalInventory());
        inventory.addProduct(linkedProduct);
        inventory.addProduct(unrelatedProduct);

        Model localModel = new ModelManager(
                new VendorVault(getTypicalAddressBook(), inventory), new UserPrefs(), new Aliases());
        Model localExpectedModel = new ModelManager(
                new VendorVault(getTypicalAddressBook(), inventory), new UserPrefs(), new Aliases());

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        NameContainsKeywordsScoredPredicate predicate = preparePredicate("Carl");
        FindCommand command = new FindCommand(predicate);

        localExpectedModel.updateFilteredPersonList(predicate);
        updateExpectedProductFilter(localExpectedModel);

        assertCommandSuccess(command, localModel, expectedMessage, localExpectedModel);
        assertEquals(Collections.singletonList(linkedProduct), localModel.getFilteredProductList());
    }

    @Test
    public void execute_partialKeyword_ranksByRelevance() {
        Person exact = new PersonBuilder().withName("Ali").withPhone("11111")
                .withEmail("exact@example.com").withAddress("Exact Street").build();
        Person prefix = new PersonBuilder().withName("Alice").withPhone("22222")
                .withEmail("prefix@example.com").withAddress("Prefix Street").build();
        Person substring = new PersonBuilder().withName("Mali").withPhone("33333")
                .withEmail("substring@example.com").withAddress("Substring Street").build();

        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(substring);
        addressBook.addPerson(prefix);
        addressBook.addPerson(exact);

        Model localModel = new ModelManager(
                new VendorVault(addressBook, new Inventory()), new UserPrefs(), new Aliases());
        Model localExpectedModel = new ModelManager(
                new VendorVault(addressBook, new Inventory()), new UserPrefs(), new Aliases());

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsScoredPredicate predicate = preparePredicate("ali");
        FindCommand command = new FindCommand(predicate);

        localExpectedModel.updateFilteredPersonList(predicate);
        updateExpectedProductFilter(localExpectedModel);

        assertCommandSuccess(command, localModel, expectedMessage, localExpectedModel);
        assertEquals(Arrays.asList(exact, prefix, substring), localModel.getFilteredPersonList());
    }

    @Test
    public void execute_keywordMatchesArchivedContact_excludesArchivedAndLinkedProducts() {
        Person archivedCarl = CARL.archive();
        Person activeOther = new PersonBuilder().withName("Active").withPhone("55555")
                .withEmail("active@example.com").withAddress("Active Street").build();

        Product archivedLinkedProduct = new ProductBuilder().withIdentifier("SKU-7777")
                .withName("Archived Item").withVendorEmail(archivedCarl.getEmail().toString()).build();

        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(archivedCarl);
        addressBook.addPerson(activeOther);

        Inventory inventory = new Inventory();
        inventory.addProduct(archivedLinkedProduct);

        Model localModel = new ModelManager(
                new VendorVault(addressBook, inventory), new UserPrefs(), new Aliases());
        Model localExpectedModel = new ModelManager(
                new VendorVault(addressBook, inventory), new UserPrefs(), new Aliases());

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        NameContainsKeywordsScoredPredicate predicate = preparePredicate("Carl");
        FindCommand command = new FindCommand(predicate);

        localExpectedModel.updateFilteredPersonList(predicate);
        updateExpectedProductFilter(localExpectedModel);

        assertCommandSuccess(command, localModel, expectedMessage, localExpectedModel);
        assertEquals(Collections.emptyList(), localModel.getFilteredPersonList());
    }

    @Test
    public void getPendingConfirmation_returnsInactivePendingConfirmation() {
        NameContainsKeywordsScoredPredicate predicate = preparePredicate(" ");
        FindCommand command = new FindCommand(predicate);
        PendingConfirmation pendingConfirmation = command.getPendingConfirmation();
        assertFalse(pendingConfirmation.getNeedConfirmation());
    }

    @Test
    public void toStringMethod() {
        NameContainsKeywordsScoredPredicate predicate = new NameContainsKeywordsScoredPredicate(
                Arrays.asList("keyword"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsScoredPredicate}.
     */
    private NameContainsKeywordsScoredPredicate preparePredicate(String userInput) {
        return new NameContainsKeywordsScoredPredicate(Arrays.asList(userInput.split("\\s+")));
    }

    private void updateExpectedProductFilter(Model model) {
        model.updateFilteredProductList(new VendorEmailMatchesContactsPredicate(model.getFilteredPersonList()));
    }
}
