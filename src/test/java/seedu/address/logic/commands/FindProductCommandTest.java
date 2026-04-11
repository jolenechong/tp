package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PRODUCTS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalProducts.EGGS;
import static seedu.address.testutil.TypicalProducts.OIL;
import static seedu.address.testutil.TypicalProducts.RICE;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Aliases;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.VendorVault;
import seedu.address.model.person.Person;
import seedu.address.model.person.VendorEmailMatchesProductsPredicate;
import seedu.address.model.product.Product;
import seedu.address.model.product.ProductNameContainsKeywordsScoredPredicate;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.ProductBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code FindProductCommand}.
 */
public class FindProductCommandTest {
    private Model model = new ModelManager(new VendorVault(
            getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());
    private Model expectedModel = new ModelManager(new VendorVault(
            getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

    @Test
    public void equals() {
        ProductNameContainsKeywordsScoredPredicate firstPredicate =
                new ProductNameContainsKeywordsScoredPredicate(Collections.singletonList("first"));
        ProductNameContainsKeywordsScoredPredicate secondPredicate =
                new ProductNameContainsKeywordsScoredPredicate(Collections.singletonList("second"));

        FindProductCommand findFirstCommand = new FindProductCommand(firstPredicate);
        FindProductCommand findSecondCommand = new FindProductCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindProductCommand findFirstCommandCopy = new FindProductCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void constructor_nullPredicate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new FindProductCommand(null));
    }

    @Test
    public void execute_zeroKeywords_noProductFound() {
        String expectedMessage = String.format(MESSAGE_PRODUCTS_LISTED_OVERVIEW, 0);
        ProductNameContainsKeywordsScoredPredicate predicate = preparePredicate(" ");
        FindProductCommand command = new FindProductCommand(predicate);

        expectedModel.updateFilteredProductList(predicate);
        expectedModel.updateFilteredPersonList(
                new VendorEmailMatchesProductsPredicate(expectedModel.getFilteredProductList()));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredProductList());
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PRODUCTS_LISTED_OVERVIEW, 3);
        ProductNameContainsKeywordsScoredPredicate predicate = preparePredicate("Rice Oil Eggs");
        FindProductCommand command = new FindProductCommand(predicate);

        expectedModel.updateFilteredProductList(predicate);
        expectedModel.updateFilteredPersonList(
                new VendorEmailMatchesProductsPredicate(expectedModel.getFilteredProductList()));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(RICE, OIL, EGGS), model.getFilteredProductList());
    }

    @Test
    public void execute_findProduct_updatesFilteredPersonListToMatchedVendors() {
        Person matchedVendor = new PersonBuilder()
                .withName("Matched Vendor")
                .withEmail("vendor@example.com")
                .build();
        Person unmatchedVendor = new PersonBuilder()
                .withName("Unmatched Vendor")
                .withEmail("other@example.com")
                .build();

        Product matchedProduct = new ProductBuilder()
                .withIdentifier("SKU-MATCH")
                .withName("Acme Board")
                .withVendorEmail("vendor@example.com")
                .build();

        Model localModel = new ModelManager(new VendorVault(), new UserPrefs(), new Aliases());
        localModel.addPerson(matchedVendor);
        localModel.addPerson(unmatchedVendor);
        localModel.addProduct(matchedProduct);

        FindProductCommand command = new FindProductCommand(preparePredicate("Acme"));
        command.execute(localModel);

        assertEquals(1, localModel.getFilteredProductList().size());
        assertEquals(List.of(matchedVendor), localModel.getFilteredPersonList());
    }

    @Test
    public void execute_noVendorEmail_noContactShown() {
        Product productWithoutVendor = new ProductBuilder()
                .withIdentifier("SKU-NOVENDOR")
                .withName("No Vendor Board")
                .withoutVendorEmail()
                .build();

        Model localModel = new ModelManager(new VendorVault(), new UserPrefs(), new Aliases());
        localModel.addPerson(new PersonBuilder().withEmail("vendor@example.com").build());
        localModel.addProduct(productWithoutVendor);

        FindProductCommand command = new FindProductCommand(preparePredicate("No Vendor"));
        command.execute(localModel);

        assertEquals(List.of(productWithoutVendor), localModel.getFilteredProductList());
        assertEquals(Collections.emptyList(), localModel.getFilteredPersonList());
    }

    @Test
    public void execute_archivedMatchedVendor_contactExcluded() {
        Person archivedMatchedVendor = new PersonBuilder()
                .withName("Archived Vendor")
                .withEmail("archived@example.com")
                .build()
                .archive();
        Product matchedProduct = new ProductBuilder()
                .withIdentifier("SKU-ARCH")
                .withName("Archived Vendor Board")
                .withVendorEmail("archived@example.com")
                .build();

        Model localModel = new ModelManager(new VendorVault(), new UserPrefs(), new Aliases());
        localModel.addPerson(archivedMatchedVendor);
        localModel.addProduct(matchedProduct);

        FindProductCommand command = new FindProductCommand(preparePredicate("Archived Vendor"));
        command.execute(localModel);

        assertEquals(List.of(matchedProduct), localModel.getFilteredProductList());
        assertEquals(Collections.emptyList(), localModel.getFilteredPersonList());
    }

    @Test
    public void execute_relevanceOrder_ignoresLowStockPriority() {
        // EP: command does not trigger low-stock sorting of results
        Product lowStockPrefixMatch = new ProductBuilder()
                .withIdentifier("SKU-LOW")
                .withName("Desktop Organizer")
                .withQuantity("1")
                .withThreshold("10")
                .build();
        Product nonLowStockExactMatch = new ProductBuilder()
                .withIdentifier("SKU-EXACT")
                .withName("Desk")
                .withQuantity("50")
                .withThreshold("10")
                .build();

        Model localModel = new ModelManager(new VendorVault(), new UserPrefs(), new Aliases());
        localModel.addProduct(lowStockPrefixMatch);
        localModel.addProduct(nonLowStockExactMatch);

        ProductNameContainsKeywordsScoredPredicate predicate = preparePredicate("desk");
        FindProductCommand command = new FindProductCommand(predicate);
        command.execute(localModel);

        assertEquals(List.of(nonLowStockExactMatch, lowStockPrefixMatch), localModel.getFilteredProductList());
    }

    @Test
    public void getPendingConfirmation_returnsInactivePendingConfirmation() {
        ProductNameContainsKeywordsScoredPredicate predicate = preparePredicate(" ");
        FindProductCommand command = new FindProductCommand(predicate);
        PendingConfirmation pendingConfirmation = command.getPendingConfirmation();
        assertFalse(pendingConfirmation.getNeedConfirmation());
    }

    /**
     * Parses {@code userInput} into a {@code ProductNameContainsKeywordsScoredPredicate}.
     */
    private ProductNameContainsKeywordsScoredPredicate preparePredicate(String userInput) {
        return new ProductNameContainsKeywordsScoredPredicate(Arrays.asList(userInput.split("\\s+")));
    }
}
