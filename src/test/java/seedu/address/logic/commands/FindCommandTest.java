package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.BOB;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalProducts.AIRPODS;
import static seedu.address.testutil.TypicalProducts.IPAD;
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
import seedu.address.model.person.NameAndTagMatchesPredicate;
import seedu.address.model.person.NameContainsKeywordsScoredPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonTagContainsKeywordsPredicate;
import seedu.address.model.product.Product;
import seedu.address.model.product.VendorEmailMatchesContactsPredicate;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.ProductBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private static final String KEYWORD_ALICE = "Alice";
    private static final String TAG_VIP = "vip";

    private final Model model = new ModelManager(new VendorVault(
            getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());
    private final Model expectedModel = new ModelManager(new VendorVault(
            getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

    @Test
    public void equals() {
        NameContainsKeywordsScoredPredicate firstPredicate = namePredicate("first");
        NameContainsKeywordsScoredPredicate secondPredicate = namePredicate("second");

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
        NameContainsKeywordsScoredPredicate predicate = namePredicate();
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        updateExpectedProductFilter(expectedModel);
        assertCommandSuccess(command, model, messageForCount(0), expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
        assertEquals(Collections.emptyList(), model.getFilteredProductList());
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        NameContainsKeywordsScoredPredicate predicate = namePredicate("Kurz", "Elle", "Kunz");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        updateExpectedProductFilter(expectedModel);
        assertCommandSuccess(command, model, messageForCount(3), expectedModel);
        assertEquals(Arrays.asList(CARL, ELLE, FIONA), model.getFilteredPersonList());
        assertEquals(Collections.emptyList(), model.getFilteredProductList());
    }

    @Test
    public void execute_keywordMatchesVendor_filtersLinkedProducts() {
        Product linkedProduct = new ProductBuilder(IPAD)
                .withVendorEmail(CARL.getEmail().toString()).build();
        Product unrelatedProduct = new ProductBuilder(AIRPODS)
                .withVendorEmail(BOB.getEmail().toString()).build();

        Inventory inventory = new Inventory(getTypicalInventory());
        inventory.addProduct(linkedProduct);
        inventory.addProduct(unrelatedProduct);

        VendorVault vault = new VendorVault(getTypicalAddressBook(), inventory);
        Model localModel = modelFromVault(vault);
        Model localExpectedModel = modelFromVault(vault);

        NameContainsKeywordsScoredPredicate predicate = namePredicate("Carl");
        FindCommand command = new FindCommand(predicate);
        localExpectedModel.updateFilteredPersonList(predicate);
        updateExpectedProductFilter(localExpectedModel);

        assertCommandSuccess(command, localModel, messageForCount(1), localExpectedModel);
        assertEquals(Collections.singletonList(linkedProduct), localModel.getFilteredProductList());
    }

    @Test
    public void execute_partialKeyword_ranksByRelevance() {
        // EP: each match tier appears exactly once for keyword "ali"
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

        Model localModel = modelFromData(addressBook, new Inventory());
        Model localExpectedModel = modelFromData(addressBook, new Inventory());

        NameContainsKeywordsScoredPredicate predicate = namePredicate("ali");
        FindCommand command = new FindCommand(predicate);

        localExpectedModel.updateFilteredPersonList(predicate);
        updateExpectedProductFilter(localExpectedModel);

        assertCommandSuccess(command, localModel, messageForCount(3), localExpectedModel);
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

        Model localModel = modelFromData(addressBook, inventory);
        Model localExpectedModel = modelFromData(addressBook, inventory);

        NameContainsKeywordsScoredPredicate predicate = namePredicate("Carl");
        FindCommand command = new FindCommand(predicate);

        localExpectedModel.updateFilteredPersonList(predicate);
        updateExpectedProductFilter(localExpectedModel);

        assertCommandSuccess(command, localModel, messageForCount(0), localExpectedModel);
        assertEquals(Collections.emptyList(), localModel.getFilteredPersonList());
    }

    @Test
    public void execute_tagModeMultipleKeywords_filtersByTagAndExcludesArchived() {
        // This scenario verifies two things together for tag filtering:
        // OR semantics across keywords and exclusion of archived contacts.
        Person vipContact = new PersonBuilder().withName("Vip Contact").withPhone("11111")
                .withEmail("vip@example.com").withAddress("Vip Street").withTags("vip").build();
        Person leadContact = new PersonBuilder().withName("Lead Contact").withPhone("22222")
                .withEmail("lead@example.com").withAddress("Lead Street").withTags("lead").build();
        Person archivedVipContact = new PersonBuilder().withName("Archived Vip").withPhone("33333")
                .withEmail("archivedvip@example.com").withAddress("Archived Street")
                .withTags("vip").build().archive();

        Product vipLinkedProduct = new ProductBuilder().withIdentifier("SKU-9001")
                .withName("Vip Product").withVendorEmail(vipContact.getEmail().toString()).build();
        Product leadLinkedProduct = new ProductBuilder().withIdentifier("SKU-9002")
                .withName("Lead Product").withVendorEmail(leadContact.getEmail().toString()).build();
        Product archivedVipLinkedProduct = new ProductBuilder().withIdentifier("SKU-9003")
                .withName("Archived Vip Product")
                .withVendorEmail(archivedVipContact.getEmail().toString()).build();

        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(vipContact);
        addressBook.addPerson(leadContact);
        addressBook.addPerson(archivedVipContact);

        Inventory inventory = new Inventory();
        inventory.addProduct(vipLinkedProduct);
        inventory.addProduct(leadLinkedProduct);
        inventory.addProduct(archivedVipLinkedProduct);

        Model localModel = modelFromData(addressBook, inventory);
        Model localExpectedModel = modelFromData(addressBook, inventory);

        PersonTagContainsKeywordsPredicate predicate =
                new PersonTagContainsKeywordsPredicate(Arrays.asList("vip", "lead"));
        FindCommand command = new FindCommand(predicate);

        localExpectedModel.updateFilteredPersonList(person -> !person.isArchived() && predicate.test(person));
        updateExpectedProductFilter(localExpectedModel);

        assertCommandSuccess(command, localModel, messageForCount(2), localExpectedModel);
        assertEquals(Arrays.asList(vipContact, leadContact), localModel.getFilteredPersonList());
        assertEquals(Arrays.asList(vipLinkedProduct, leadLinkedProduct), localModel.getFilteredProductList());
    }

    @Test
    public void execute_nameAndTagCombined_filtersByAnd() {
        NameAndTagCombinedTestContext context = prepareNameAndTagCombinedContext();
        FindCommand command = new FindCommand(context.combinedPredicate);

        context.localExpectedModel.updateFilteredPersonList(context.combinedPredicate);
        updateExpectedProductFilter(context.localExpectedModel);

        assertCommandSuccess(command, context.localModel, messageForCount(1), context.localExpectedModel);
        assertEquals(Collections.singletonList(context.exactMatch), context.localModel.getFilteredPersonList());
    }

    @Test
    public void execute_nameAndTagCombined_updatesLinkedProducts() {
        NameAndTagCombinedTestContext context = prepareNameAndTagCombinedContext();
        FindCommand command = new FindCommand(context.combinedPredicate);

        context.localExpectedModel.updateFilteredPersonList(context.combinedPredicate);
        updateExpectedProductFilter(context.localExpectedModel);

        assertCommandSuccess(command, context.localModel, messageForCount(1), context.localExpectedModel);
        assertEquals(Collections.singletonList(context.exactLinkedProduct),
                context.localModel.getFilteredProductList());
    }

    @Test
    public void getPendingConfirmation_returnsInactivePendingConfirmation() {
        NameContainsKeywordsScoredPredicate predicate = namePredicate();
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

    private NameContainsKeywordsScoredPredicate namePredicate(String... keywords) {
        return new NameContainsKeywordsScoredPredicate(Arrays.asList(keywords));
    }

    private String messageForCount(int count) {
        return String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, count);
    }

    private Model modelFromData(AddressBook addressBook, Inventory inventory) {
        return modelFromVault(new VendorVault(addressBook, inventory));
    }

    private Model modelFromVault(VendorVault vault) {
        return new ModelManager(vault, new UserPrefs(), new Aliases());
    }

    private NameAndTagCombinedTestContext prepareNameAndTagCombinedContext() {
        Person exactMatch = new PersonBuilder().withName("Alice Chan").withPhone("11111")
                .withEmail("alice.vip@example.com").withAddress("Alpha Road").withTags("vip").build();
        Person nameOnlyMatch = new PersonBuilder().withName("Alice Tan").withPhone("22222")
                .withEmail("alice.nontag@example.com").withAddress("Beta Road").withTags("lead").build();
        Person tagOnlyMatch = new PersonBuilder().withName("Brenda Lim").withPhone("33333")
                .withEmail("brenda.vip@example.com").withAddress("Gamma Road").withTags("vip").build();

        Product exactLinkedProduct = new ProductBuilder().withIdentifier("SKU-9101")
                .withName("Alice Vip Product").withVendorEmail(exactMatch.getEmail().toString()).build();
        Product nameOnlyLinkedProduct = new ProductBuilder().withIdentifier("SKU-9102")
                .withName("Alice Lead Product").withVendorEmail(nameOnlyMatch.getEmail().toString()).build();
        Product tagOnlyLinkedProduct = new ProductBuilder().withIdentifier("SKU-9103")
                .withName("Brenda Vip Product").withVendorEmail(tagOnlyMatch.getEmail().toString()).build();

        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(exactMatch);
        addressBook.addPerson(nameOnlyMatch);
        addressBook.addPerson(tagOnlyMatch);

        Inventory inventory = new Inventory();
        inventory.addProduct(exactLinkedProduct);
        inventory.addProduct(nameOnlyLinkedProduct);
        inventory.addProduct(tagOnlyLinkedProduct);

        Model localModel = modelFromData(addressBook, inventory);
        Model localExpectedModel = modelFromData(addressBook, inventory);

        NameContainsKeywordsScoredPredicate namePredicate =
                new NameContainsKeywordsScoredPredicate(Collections.singletonList(KEYWORD_ALICE));
        PersonTagContainsKeywordsPredicate tagPredicate =
                new PersonTagContainsKeywordsPredicate(Collections.singletonList(TAG_VIP));
        NameAndTagMatchesPredicate combinedPredicate = new NameAndTagMatchesPredicate(namePredicate, tagPredicate);

        return new NameAndTagCombinedTestContext(localModel, localExpectedModel,
                exactMatch, exactLinkedProduct, combinedPredicate);
    }

    private static class NameAndTagCombinedTestContext {
        private final Model localModel;
        private final Model localExpectedModel;
        private final Person exactMatch;
        private final Product exactLinkedProduct;
        private final NameAndTagMatchesPredicate combinedPredicate;

        private NameAndTagCombinedTestContext(Model localModel, Model localExpectedModel,
                                              Person exactMatch, Product exactLinkedProduct,
                                              NameAndTagMatchesPredicate combinedPredicate) {
            this.localModel = localModel;
            this.localExpectedModel = localExpectedModel;
            this.exactMatch = exactMatch;
            this.exactLinkedProduct = exactLinkedProduct;
            this.combinedPredicate = combinedPredicate;
        }
    }

    private void updateExpectedProductFilter(Model model) {
        model.updateFilteredProductList(new VendorEmailMatchesContactsPredicate(model.getFilteredPersonList()));
    }
}
