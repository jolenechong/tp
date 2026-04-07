package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PRODUCTS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalAliases.getTypicalAliases;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.BOB;
import static seedu.address.testutil.TypicalProducts.EGGS;
import static seedu.address.testutil.TypicalProducts.OIL;
import static seedu.address.testutil.TypicalProducts.RICE;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.alias.Alias;
import seedu.address.model.alias.exceptions.DuplicateAliasException;
import seedu.address.model.alias.exceptions.NoAliasFoundInAliasListException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.NameContainsKeywordsScoredPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.RankedPersonPredicate;
import seedu.address.model.product.Product;
import seedu.address.model.product.ProductNameContainsKeywordsScoredPredicate;
import seedu.address.model.product.RankedProductPredicate;
import seedu.address.model.product.exceptions.DuplicateProductException;
import seedu.address.model.product.exceptions.ProductNotFoundException;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.ProductBuilder;

public class ModelManagerTest {

    private static final String NAME_SIMILAR_PERSON = "Alice Tan";
    private static final String PHONE_SIMILAR_PERSON = "12351253";
    private static final String ADDRESS_SIMILAR_PERSON = "123, Jurong West Ave 6";
    private static final String SKU_OTHER_PRODUCT = "SKU-2001";
    private static final String NAME_SIMILAR_PRODUCT = "Eggs Large";
    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        // EP: default constructor initializes empty state and default preferences.
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
        assertEquals(new Inventory(), new Inventory(modelManager.getInventory()));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setAddressBookFilePath(Paths.get("address/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        userPrefs.setDefaultRestockThresholdValue(7);
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setAddressBookFilePath(Paths.get("new/address/book/file/path"));
        userPrefs.setDefaultRestockThresholdValue(3);
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setAddressBookFilePath(null));
    }

    @Test
    public void setAddressBookFilePath_validPath_setsAddressBookFilePath() {
        // EP: valid file path should be persisted in user prefs.
        Path path = Paths.get("address/book/file/path");
        modelManager.setAddressBookFilePath(path);
        assertEquals(path, modelManager.getAddressBookFilePath());
    }

    @Test
    public void setInventory_nullInventory_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setInventory(null));
    }

    @Test
    public void setInventory_validInventory_replacesInventoryData() {
        Inventory inventory = getTypicalInventory();
        modelManager.setInventory(inventory);
        assertEquals(inventory, new Inventory(modelManager.getInventory()));
    }

    @Test
    public void setVendorVault_nullVendorVault_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setVendorVault(null));
    }

    @Test
    public void setVendorVault_validVendorVault_replacesAddressBookAndInventory() {
        VendorVault vendorVault = new VendorVault();
        vendorVault.setAddressBook(new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build());
        vendorVault.setInventory(getTypicalInventory());

        modelManager.setVendorVault(vendorVault);

        assertEquals(vendorVault.getPersonList(), modelManager.getAddressBook().getPersonList());
        assertEquals(vendorVault.getProductList(), modelManager.getInventory().getProductList());
    }

    @Test
    public void getVendorVault_returnsCombinedState() {
        modelManager.addPerson(ALICE);
        modelManager.addProduct(OIL);

        ReadOnlyVendorVault vendorVault = modelManager.getVendorVault();

        assertEquals(modelManager.getAddressBook().getPersonList(), vendorVault.getPersonList());
        assertEquals(modelManager.getInventory().getProductList(), vendorVault.getProductList());
    }

    @Test
    public void setAliases_validAliases_replacesAliasesData() {
        Aliases aliases = getTypicalAliases();
        modelManager.setAliases(aliases);
        assertEquals(aliases, modelManager.getAliases());
    }

    @Test
    public void setAliases_nullAliases_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setAliases(null));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void findSimilarNameMatch_personMatchExists_returnsMatch() {
        ModelManager model = createModelWithAliceAndBob();
        Person candidate = new PersonBuilder(ALICE).withName(NAME_SIMILAR_PERSON).build();

        assertEquals(ALICE, model.findSimilarNameMatch(candidate, null).orElse(null));
    }

    @Test
    public void findSimilarNameMatch_personExcludedMatch_returnsEmpty() {
        ModelManager model = createModelWithAliceAndBob();
        Person candidate = new PersonBuilder(ALICE).withName(NAME_SIMILAR_PERSON).build();

        assertTrue(model.findSimilarNameMatch(candidate, ALICE).isEmpty());
    }

    @Test
    public void findSimilarAddressMatch_personMatchExists_returnsMatch() {
        ModelManager model = createModelWithAliceAndBob();
        Person candidate = new PersonBuilder(ALICE).withAddress(ADDRESS_SIMILAR_PERSON).build();

        assertEquals(ALICE, model.findSimilarAddressMatch(candidate, null).orElse(null));
    }

    @Test
    public void findSimilarAddressMatch_personExcludedMatch_returnsEmpty() {
        ModelManager model = createModelWithAliceAndBob();
        Person candidate = new PersonBuilder(ALICE).withAddress(ADDRESS_SIMILAR_PERSON).build();

        assertTrue(model.findSimilarAddressMatch(candidate, ALICE).isEmpty());
    }

    @Test
    public void findSimilarPhoneMatch_personMatchExists_returnsMatch() {
        ModelManager model = createModelWithAliceAndBob();
        Person candidate = new PersonBuilder(ALICE).withPhone(PHONE_SIMILAR_PERSON).build();

        assertEquals(ALICE, model.findSimilarPhoneMatch(candidate, null).orElse(null));
    }

    @Test
    public void findSimilarPhoneMatch_personExcludedMatch_returnsEmpty() {
        ModelManager model = createModelWithAliceAndBob();
        Person candidate = new PersonBuilder(ALICE).withPhone(PHONE_SIMILAR_PERSON).build();

        assertTrue(model.findSimilarPhoneMatch(candidate, ALICE).isEmpty());
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void updateFilteredPersonList_scoredPredicate_sortsByRelevance() {
        // EP: scored predicate ranks exact > prefix > substring matches.
        Person substring = new PersonBuilder().withName("Mali Ong").withEmail("substring@example.com").build();
        Person prefix = new PersonBuilder().withName("Alice Wong").withEmail("prefix@example.com").build();
        Person exact = new PersonBuilder().withName("Ali Tan").withEmail("exact@example.com").build();

        modelManager.addPerson(substring);
        modelManager.addPerson(prefix);
        modelManager.addPerson(exact);

        modelManager.updateFilteredPersonList(new NameContainsKeywordsScoredPredicate(List.of("ali")));
        assertEquals(List.of(exact, prefix, substring), modelManager.getFilteredPersonList());
    }

    @Test
    public void updateFilteredPersonList_afterScoredPredicate_resetsToUnderlyingOrder() {
        // EP: switching back to non-ranked predicate should clear custom comparator ordering.
        Person substring = new PersonBuilder().withName("Mali Ong").withEmail("substring@example.com").build();
        Person prefix = new PersonBuilder().withName("Alice Wong").withEmail("prefix@example.com").build();
        Person exact = new PersonBuilder().withName("Ali Tan").withEmail("exact@example.com").build();

        modelManager.addPerson(substring);
        modelManager.addPerson(prefix);
        modelManager.addPerson(exact);

        modelManager.updateFilteredPersonList(new NameContainsKeywordsScoredPredicate(List.of("ali")));
        assertEquals(List.of(exact, prefix, substring), modelManager.getFilteredPersonList());

        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        assertEquals(List.of(substring, prefix, exact), modelManager.getFilteredPersonList());
    }

    @Test
    public void updateFilteredPersonList_rankedPredicateInterface_dispatchesComparatorAndFilter() {
        // EP: RankedPersonPredicate interface path should apply both filter and comparator.
        Person alpha = new PersonBuilder().withName("Ali Alpha").withEmail("alpha@example.com").build();
        Person beta = new PersonBuilder().withName("Ali Beta").withEmail("beta@example.com").build();
        Person nonMatch = new PersonBuilder().withName("Chris Tan").withEmail("chris@example.com").build();

        modelManager.addPerson(alpha);
        modelManager.addPerson(beta);
        modelManager.addPerson(nonMatch);

        modelManager.updateFilteredPersonList(new StubRankedPersonPredicate("ali"));
        assertEquals(List.of(beta, alpha), modelManager.getFilteredPersonList());
    }

    @Test
    public void updateFilteredPersonList_rankedPredicate_excludesArchivedPersons() {
        // EP: ranked person filtering must still exclude archived contacts.
        AddressBook addressBook = new AddressBook();
        Person archivedMatching = new PersonBuilder()
                .withName("Ali Archived")
                .withEmail("archived@example.com")
                .build()
                .archive();
        Person activeMatching = new PersonBuilder()
                .withName("Ali Active")
                .withEmail("active@example.com")
                .build();
        addressBook.addPerson(archivedMatching);
        addressBook.addPerson(activeMatching);

        ModelManager model = new ModelManager(new VendorVault(
                addressBook, new Inventory()), new UserPrefs(), new Aliases());
        model.updateFilteredPersonList(new StubRankedPersonPredicate("ali"));

        assertEquals(List.of(activeMatching), model.getFilteredPersonList());
    }

    @Test
    public void hasProduct_nullProduct_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasProduct(null));
    }

    @Test
    public void hasProduct_productNotInInventory_returnsFalse() {
        assertFalse(modelManager.hasProduct(OIL));
    }

    @Test
    public void hasProduct_productInInventory_returnsTrue() {
        // EP: querying existing product should return true.
        modelManager.addProduct(OIL);
        assertTrue(modelManager.hasProduct(OIL));
    }

    @Test
    public void findSimilarNameMatch_productNullCandidate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.findSimilarNameMatch((Product) null, OIL));
    }

    @Test
    public void findSimilarNameMatch_productSimilarMatchFound_returnsMatch() {
        Product existingProduct = new ProductBuilder(EGGS).build();
        Product candidate = new ProductBuilder()
                .withIdentifier(SKU_OTHER_PRODUCT)
                .withName(NAME_SIMILAR_PRODUCT)
                .build();

        modelManager.addProduct(existingProduct);

        assertEquals(existingProduct, modelManager.findSimilarNameMatch(candidate, null).orElse(null));
    }

    @Test
    public void findSimilarNameMatch_productExcludedMatch_returnsEmpty() {
        Product existingProduct = new ProductBuilder(EGGS).build();
        Product candidate = new ProductBuilder()
                .withIdentifier(SKU_OTHER_PRODUCT)
                .withName(NAME_SIMILAR_PRODUCT)
                .build();

        modelManager.addProduct(existingProduct);

        assertTrue(modelManager.findSimilarNameMatch(candidate, existingProduct).isEmpty());
    }

    @Test
    public void deleteProduct_missingProduct_throwsProductNotFoundException() {
        assertThrows(ProductNotFoundException.class, () -> modelManager.deleteProduct(OIL));
    }

    @Test
    public void deleteProduct_existingProduct_removesProduct() {
        modelManager.addProduct(OIL);

        modelManager.deleteProduct(OIL);

        assertFalse(modelManager.hasProduct(OIL));
    }

    @Test
    public void setProduct_nullTargetProduct_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setProduct(null, OIL));
    }

    @Test
    public void setProduct_nullEditedProduct_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setProduct(OIL, null));
    }

    @Test
    public void setProduct_targetProductNotInInventory_throwsProductNotFoundException() {
        assertThrows(ProductNotFoundException.class, () -> modelManager.setProduct(OIL, OIL));
    }

    @Test
    public void setProduct_editedProductHasNonUniqueIdentity_throwsDuplicateProductException() {
        modelManager.addProduct(OIL);
        modelManager.addProduct(RICE);

        assertThrows(DuplicateProductException.class, () -> modelManager.setProduct(RICE, OIL));
    }

    @Test
    public void setProduct_validProduct_replacesProduct() {
        modelManager.addProduct(OIL);

        modelManager.setProduct(OIL, RICE);

        assertFalse(modelManager.hasProduct(OIL));
        assertTrue(modelManager.hasProduct(RICE));
    }

    @Test
    public void getFilteredProductList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredProductList().remove(0));
    }

    @Test
    public void updateFilteredProductList_filtersProductList() {
        modelManager.addProduct(OIL);
        modelManager.addProduct(RICE);
        modelManager.updateFilteredProductList(product -> product.getIdentifier().equals(OIL.getIdentifier()));

        assertEquals(1, modelManager.getFilteredProductList().size());
        assertEquals(OIL, modelManager.getFilteredProductList().get(0));
    }

    @Test
    public void updateFilteredProductList_scoredPredicate_sortsByRelevance() {
        // EP: scored product predicate ranks exact > prefix > substring matches.
        Product substring = new ProductBuilder().withIdentifier("SKU-SUB").withName("Tali Watch").build();
        Product prefix = new ProductBuilder().withIdentifier("SKU-PRE").withName("Aliphatic Cake").build();
        Product exact = new ProductBuilder().withIdentifier("SKU-EXA").withName("Ali").build();

        modelManager.addProduct(substring);
        modelManager.addProduct(prefix);
        modelManager.addProduct(exact);

        modelManager.updateFilteredProductList(new ProductNameContainsKeywordsScoredPredicate(List.of("ali")));
        assertEquals(List.of(exact, prefix, substring), modelManager.getFilteredProductList());
    }

    @Test
    public void updateFilteredProductList_scoredPredicate_excludesArchivedProducts() {
        // EP: ranked product filtering must still exclude archived products.
        Inventory inventory = new Inventory();
        Product archivedMatching = new ProductBuilder()
                .withIdentifier("SKU-ARCH-MATCH")
                .withName("Ali Archived")
                .build()
                .archive();
        Product activeMatching = new ProductBuilder()
                .withIdentifier("SKU-ACTIVE-MATCH")
                .withName("Ali Active")
                .build();
        inventory.addProduct(archivedMatching);
        inventory.addProduct(activeMatching);

        ModelManager model = new ModelManager(new VendorVault(new AddressBook(), inventory),
                new UserPrefs(), new Aliases());
        model.updateFilteredProductList(new ProductNameContainsKeywordsScoredPredicate(List.of("ali")));

        assertEquals(List.of(activeMatching), model.getFilteredProductList());
    }

    @Test
    public void updateFilteredProductList_afterScoredPredicate_resetsToUnderlyingOrder() {
        // EP: switching back to non-ranked predicate clears ranked comparator ordering.
        Product substring = new ProductBuilder().withIdentifier("SKU-SUB2").withName("Tali Watch").build();
        Product prefix = new ProductBuilder().withIdentifier("SKU-PRE2").withName("Aliphatic Cake").build();
        Product exact = new ProductBuilder().withIdentifier("SKU-EXA2").withName("Ali").build();

        modelManager.addProduct(substring);
        modelManager.addProduct(prefix);
        modelManager.addProduct(exact);

        modelManager.updateFilteredProductList(new ProductNameContainsKeywordsScoredPredicate(List.of("ali")));
        assertEquals(List.of(exact, prefix, substring), modelManager.getFilteredProductList());

        modelManager.updateFilteredProductList(PREDICATE_SHOW_ALL_PRODUCTS);
        assertEquals(List.of(substring, prefix, exact), modelManager.getFilteredProductList());
    }

    @Test
    public void updateFilteredProductList_rankedPredicateInterface_dispatchesComparatorAndFilter() {
        // EP: RankedProductPredicate interface path applies filter and comparator.
        Product alpha = new ProductBuilder().withIdentifier("SKU-ALPHA").withName("Ali Alpha").build();
        Product beta = new ProductBuilder().withIdentifier("SKU-BETA").withName("Ali Beta").build();
        Product nonMatch = new ProductBuilder().withIdentifier("SKU-OTHER").withName("Rice Bag").build();

        modelManager.addProduct(alpha);
        modelManager.addProduct(beta);
        modelManager.addProduct(nonMatch);

        modelManager.updateFilteredProductList(new StubRankedProductPredicate("ali"));
        assertEquals(List.of(beta, alpha), modelManager.getFilteredProductList());
    }

    @Test
    public void getInventory_notNull() {
        ModelManager modelManager = new ModelManager(new VendorVault(), new UserPrefs(), new Aliases());
        assertNotNull(modelManager.getInventory());
    }

    @Test
    public void archiveProduct_setsArchivedFlag() {
        ModelManager model = new ModelManager(new VendorVault(), new UserPrefs(), new Aliases());
        Product product = new ProductBuilder().build();

        model.addProduct(product);
        model.archiveProduct(product);

        assertTrue(model.getInventory().getProductList().get(0).isArchived());
    }

    @Test
    public void constructor_productsFilteredCorrectly() {
        // EP: newly added active product should appear in filtered product list.
        ModelManager model = new ModelManager(new VendorVault(), new UserPrefs(), new Aliases());

        Product product = new ProductBuilder().build();
        model.addProduct(product);

        assertEquals(1, model.getFilteredProductList().size());
    }

    @Test
    public void archiveProduct_updatesFilteredList() {
        ModelManager model = new ModelManager(new VendorVault(), new UserPrefs(), new Aliases());

        Product product = new ProductBuilder().build();
        model.addProduct(product);

        model.archiveProduct(product);

        assertEquals(0, model.getFilteredProductList().size());
    }

    @Test
    public void restoreProduct_updatesFilteredList() {
        ModelManager model = new ModelManager(new VendorVault(), new UserPrefs(), new Aliases());

        Product product = new ProductBuilder().build();
        model.addProduct(product);

        model.archiveProduct(product);

        Product archived = model.getInventory().getProductList().get(0);

        model.restoreProduct(archived);

        assertEquals(1, model.getFilteredProductList().size());
    }


    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();
        Aliases aliases = new Aliases();

        // same values -> returns true
        VendorVault vendorVault = new VendorVault(addressBook, new Inventory());
        VendorVault differentVendorVault = new VendorVault(differentAddressBook, new Inventory());

        modelManager = new ModelManager(vendorVault, userPrefs, aliases);
        ModelManager modelManagerCopy = new ModelManager(vendorVault, userPrefs, aliases);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentVendorVault, userPrefs, aliases)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(Arrays.asList(keywords)));
        assertFalse(modelManager.equals(new ModelManager(vendorVault, userPrefs, aliases)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setAddressBookFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(vendorVault, differentUserPrefs, aliases)));

        // different inventory -> returns false
        ModelManager modelManagerWithProduct = new ModelManager(vendorVault, userPrefs, aliases);
        modelManagerWithProduct.addProduct(OIL);
        assertFalse(modelManager.equals(modelManagerWithProduct));

        // different filteredProductList -> returns false
        ModelManager modelManagerWithFilteredProducts = new ModelManager(vendorVault, userPrefs, aliases);
        modelManagerWithFilteredProducts.addProduct(OIL);
        modelManagerWithFilteredProducts.addProduct(RICE);
        modelManagerWithFilteredProducts.updateFilteredProductList(
                product -> product.getIdentifier().equals(OIL.getIdentifier()));
        ModelManager modelManagerWithAllProductsShown = new ModelManager(vendorVault, userPrefs, aliases);
        modelManagerWithAllProductsShown.addProduct(OIL);
        modelManagerWithAllProductsShown.addProduct(RICE);
        modelManagerWithAllProductsShown.updateFilteredProductList(PREDICATE_SHOW_ALL_PRODUCTS);
        assertFalse(modelManagerWithFilteredProducts.equals(modelManagerWithAllProductsShown));
    }

    @Test
    public void constructor_filtersArchivedProducts() {
        Inventory inventory = new Inventory();
        Product active = new ProductBuilder().withIdentifier("SKU-ACTIVE").build();
        Product archived = new ProductBuilder().withIdentifier("SKU-ARCHIVED").build().archive();
        inventory.addProduct(active);
        inventory.addProduct(archived);

        ModelManager model = new ModelManager(new VendorVault(new AddressBook(), inventory),
                new UserPrefs(), new Aliases());
        assertEquals(active, model.getFilteredProductList().get(0));
    }

    @Test
    public void restoreProduct_showsProductInFilteredList() {
        ModelManager model = new ModelManager(new VendorVault(), new UserPrefs(), new Aliases());

        Product product = new ProductBuilder().build();
        model.addProduct(product);

        model.archiveProduct(product);

        Product archived = model.getInventory().getProductList().get(0);

        model.restoreProduct(archived);

        assertEquals(1, model.getFilteredProductList().size());
    }

    @Test
    public void updateFilteredProductList_showActiveProducts_filtersArchived() {
        ModelManager model = new ModelManager(new VendorVault(), new UserPrefs(), new Aliases());

        Product product = new ProductBuilder().build();
        model.addProduct(product);

        model.archiveProduct(product);

        model.updateFilteredProductList(Model.PREDICATE_SHOW_ACTIVE_PRODUCTS);

        assertTrue(model.getFilteredProductList().isEmpty());
    }

    @Test
    public void setInventory_success() {
        ModelManager modelManager = new ModelManager(new VendorVault(), new UserPrefs(), new Aliases());

        Inventory inventory = new Inventory();
        modelManager.setInventory(inventory);

        assertEquals(inventory, modelManager.getInventory());
    }

    @Test
    public void addAlias_nullAlias_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.addAlias(null));
    }

    @Test
    public void addAlias_validAlias_success() throws Exception {
        Alias alias = new Alias("ls", ListCommand.COMMAND_WORD);
        modelManager.addAlias(alias);
        assertEquals(alias, modelManager.findAlias("ls"));
    }

    @Test
    public void addAlias_duplicateAlias_throwsDuplicateAliasException() throws Exception {
        Alias alias = new Alias("ls", ListCommand.COMMAND_WORD);
        modelManager.addAlias(alias);
        assertThrows(DuplicateAliasException.class, () -> modelManager.addAlias(alias));
    }

    @Test
    public void findAlias_nullAliasStr_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.findAlias(null));
    }

    @Test
    public void findAlias_existingAlias_returnsAlias() throws Exception {
        Alias alias = new Alias("ls", ListCommand.COMMAND_WORD);
        modelManager.addAlias(alias);
        assertEquals(alias, modelManager.findAlias("ls"));
    }

    @Test
    public void findAlias_nonExistentAlias_throwsNoAliasFoundInAliasListException() {
        assertThrows(NoAliasFoundInAliasListException.class, () -> modelManager.findAlias("nonexistent"));
    }

    @Test
    public void getAliasList_emptyAliases_returnsEmptyList() {
        Aliases aliases = new Aliases();
        modelManager.setAliases(aliases);
        assertEquals(Collections.emptyList(), modelManager.getAliasList());
    }

    @Test
    public void getAliasList_withAliases_returnsEmptyList() {
        Aliases aliases = new Aliases();
        Alias alias1 = new Alias("ls", "list");
        Alias alias2 = new Alias("a", "add");

        aliases.addAlias(alias1);
        aliases.addAlias(alias2);

        List<Alias> result = aliases.getAliasList();
        assertEquals(2, result.size());
        assertTrue(result.contains(alias1));
        assertTrue(result.contains(alias2));
    }

    @Test
    public void removeAlias_nullAlias_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.removeAlias(null));
    }

    @Test
    public void removeAlias_validAlias_success() throws Exception {
        Alias alias = new Alias("ls", ListCommand.COMMAND_WORD);
        modelManager.addAlias(alias);
        assertEquals(alias, modelManager.findAlias("ls"));
        modelManager.removeAlias(alias.getAlias());
        assertThrows(NoAliasFoundInAliasListException.class, () -> modelManager.findAlias(alias.getAlias()));
    }

    @Test
    public void removeAlias_noAlias_throwsNoAliasFoundInAliasListException() throws Exception {
        assertThrows(NoAliasFoundInAliasListException.class, () -> modelManager.removeAlias("ls"));
    }

    private ModelManager createModelWithAliceAndBob() {
        ModelManager model = new ModelManager();
        model.addPerson(ALICE);
        model.addPerson(BOB);
        return model;
    }

    private static class StubRankedPersonPredicate implements RankedPersonPredicate {
        private final String keyword;

        StubRankedPersonPredicate(String keyword) {
            this.keyword = keyword.toLowerCase(Locale.ROOT);
        }

        @Override
        public boolean test(Person person) {
            String name = person.getName().fullName;
            return name.toLowerCase(Locale.ROOT).contains(keyword);
        }

        @Override
        public Comparator<Person> createPersonComparator() {
            return Comparator.comparing((Person person) -> person.getName().fullName).reversed();
        }
    }

    private static class StubRankedProductPredicate implements RankedProductPredicate {
        private final String keyword;

        StubRankedProductPredicate(String keyword) {
            this.keyword = keyword.toLowerCase(Locale.ROOT);
        }

        @Override
        public boolean test(Product product) {
            String name = product.getName().fullName;
            return name.toLowerCase(Locale.ROOT).contains(keyword);
        }

        @Override
        public Comparator<Product> createProductComparator() {
            return Comparator.comparing((Product product) -> product.getName().fullName).reversed();
        }
    }
}
