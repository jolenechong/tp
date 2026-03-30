package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.alias.Alias;
import seedu.address.model.alias.exceptions.DuplicateAliasException;
import seedu.address.model.alias.exceptions.NoAliasFoundInAliasListException;
import seedu.address.model.person.Email;
import seedu.address.model.person.NameContainsKeywordsScoredPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.product.Product;
import seedu.address.model.product.ProductNameContainsKeywordsScoredPredicate;

/**
 * Represents the in-memory model of the vendor vault data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final VendorVault vendorVault;
    private final AddressBook addressBook;
    private final Inventory inventory;
    private final Aliases aliases;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final SortedList<Person> sortedFilteredPersons;
    private final FilteredList<Product> filteredProducts;
    private final SortedList<Product> sortedFilteredProducts;
    private final VersionedVendorVault versionedVendorVault;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyVendorVault vendorVault, ReadOnlyUserPrefs userPrefs, ReadOnlyAliases readOnlyAliases) {
        requireAllNonNull(vendorVault, userPrefs);

        logger.fine("Initializing with VendorVault: " + vendorVault + " and user prefs " + userPrefs);

        this.vendorVault = new VendorVault(vendorVault);
        this.addressBook = this.vendorVault.getAddressBook();
        this.inventory = this.vendorVault.getInventory();
        this.aliases = new Aliases(readOnlyAliases);

        this.userPrefs = new UserPrefs(userPrefs);

        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        filteredPersons.setPredicate(Model.PREDICATE_SHOW_ACTIVE_PERSONS);
        sortedFilteredPersons = new SortedList<>(filteredPersons);
        filteredProducts = new FilteredList<>(this.inventory.getProductList());
        filteredProducts.setPredicate(product -> !product.isArchived());
        sortedFilteredProducts = new SortedList<>(filteredProducts);
        versionedVendorVault = new VersionedVendorVault(this.vendorVault);
    }

    public ModelManager() {
        this(new VendorVault(), new UserPrefs(), new Aliases());
    }

    // =========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    // =========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        vendorVault.setAddressBook(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    // =========== Inventory ==================================================================================
    @Override
    public void setInventory(ReadOnlyInventory inventory) {
        vendorVault.setInventory(inventory);
    }

    @Override
    public ReadOnlyInventory getInventory() {
        return inventory;
    }

    // =========== VendorVault ==================================================================================

    @Override
    public void setVendorVault(ReadOnlyVendorVault vendorVault) {
        requireNonNull(vendorVault);
        this.vendorVault.resetData(vendorVault);
    }

    @Override
    public ReadOnlyVendorVault getVendorVault() {
        return vendorVault;
    }

    @Override
    public void setAliases(ReadOnlyAliases aliases) {
        requireNonNull(aliases);
        this.aliases.resetData(aliases);
    }

    @Override
    public ReadOnlyAliases getAliases() {
        return aliases;
    }


    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public Optional<Person> findByEmail(Email email) {
        requireNonNull(email);
        return addressBook.findByEmail(email);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
    }

    @Override
    public Optional<Person> findSimilarNameMatch(Person candidate, Person exclude) {
        requireNonNull(candidate);
        return addressBook.findSimilarNameMatch(candidate, exclude);
    }

    @Override
    public Optional<Product> findSimilarNameMatch(Product candidate, Product exclude) {
        requireNonNull(candidate);
        return inventory.findSimilarNameMatch(candidate, exclude);
    }

    @Override
    public Optional<Person> findSimilarPhoneMatch(Person candidate, Person exclude) {
        requireNonNull(candidate);
        return addressBook.findSimilarPhoneMatch(candidate, exclude);
    }

    @Override
    public Optional<Person> findSimilarAddressMatch(Person candidate, Person exclude) {
        requireNonNull(candidate);
        return addressBook.findSimilarAddressMatch(candidate, exclude);
    }

    @Override
    public void archivePerson(Person person) {
        requireNonNull(person);

        Person archivedPerson = person.archive();
        addressBook.setPerson(person, archivedPerson);

        updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS);
    }

    @Override
    public void restorePerson(Person person) {
        requireNonNull(person);

        Person restoredPerson = person.restore();
        addressBook.setPerson(person, restoredPerson);

        updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS);
    }

    //=========== Product / Inventory Operations =============================================================
    @Override
    public void setProduct(Product target, Product editedProduct) {
        requireAllNonNull(target, editedProduct);
        inventory.setProduct(target, editedProduct);
    }

    @Override
    public boolean hasProduct(Product product) {
        requireNonNull(product);
        return inventory.hasProduct(product);
    }

    @Override
    public void deleteProduct(Product target) {
        inventory.removeProduct(target);
    }

    @Override
    public void addProduct(Product product) {
        inventory.addProduct(product);
        updateFilteredProductList(PREDICATE_SHOW_ALL_PRODUCTS);
    }

    // =========== Alias List Operations ======================================================================
    @Override
    public void addAlias(Alias alias) throws DuplicateAliasException {
        requireNonNull(alias);
        aliases.addAlias(alias);
    }

    @Override
    public Alias findAlias(String aliasStr) throws NoAliasFoundInAliasListException {
        requireNonNull(aliasStr);
        return aliases.findAlias(aliasStr);
    }

    @Override
    public void removeAlias(String aliasStr) throws NoAliasFoundInAliasListException {
        requireNonNull(aliasStr);
        aliases.removeAlias(aliasStr);
    }

    @Override
    public List<Alias> getAliasList() {
        return aliases.getAliasList();
    }

    // =========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return sortedFilteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        if (predicate instanceof NameContainsKeywordsScoredPredicate) {
            NameContainsKeywordsScoredPredicate scoredPredicate = (NameContainsKeywordsScoredPredicate) predicate;
            filteredPersons.setPredicate(person -> !person.isArchived() && scoredPredicate.test(person));
            sortedFilteredPersons.setComparator(scoredPredicate.createPersonComparator());
            return;
        }

        filteredPersons.setPredicate(predicate);
        sortedFilteredPersons.setComparator(null);
    }

    //=========== Filtered Product List Accessors ============================================================
    /**
     * Returns an unmodifiable view of the list of {@code Product} backed by the internal list of
     * {@code inventory}
     */
    @Override
    public ObservableList<Product> getFilteredProductList() {
        return sortedFilteredProducts;
    }

    @Override
    public void updateFilteredProductList(Predicate<Product> predicate) {
        requireNonNull(predicate);
        if (predicate instanceof ProductNameContainsKeywordsScoredPredicate) {
            ProductNameContainsKeywordsScoredPredicate scoredPredicate =
                    (ProductNameContainsKeywordsScoredPredicate) predicate;
            filteredProducts.setPredicate(product -> !product.isArchived() && scoredPredicate.test(product));
            sortedFilteredProducts.setComparator(scoredPredicate.createProductComparator());
            return;
        }

        filteredProducts.setPredicate(predicate);
        sortedFilteredProducts.setComparator(null);
    }

    @Override
    public void archiveProduct(Product product) {
        requireNonNull(product);

        Product archived = product.archive();
        inventory.setProduct(product, archived);

        updateFilteredProductList(p -> !p.isArchived());
    }

    @Override
    public void restoreProduct(Product product) {
        requireNonNull(product);

        Product restored = product.restore();
        inventory.setProduct(product, restored);

        updateFilteredProductList(p -> !p.isArchived());
    }

    // =========== VendorVaultVersionControl ===================================================================

    @Override
    public void commitVendorVault(String actionSummary) {
        versionedVendorVault.commit(vendorVault, actionSummary);
    }


    @Override
    public String undoVendorVault() {
        return versionedVendorVault.undo(vendorVault);
    }

    @Override
    public boolean canUndoVendorVault() {
        return versionedVendorVault.canUndo();
    }

    @Override
    public String redoVendorVault() {
        return versionedVendorVault.redo(vendorVault);
    }

    @Override
    public boolean canRedoVendorVault() {
        return versionedVendorVault.canRedo();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return vendorVault.equals(otherModelManager.vendorVault)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons)
                && filteredProducts.equals(otherModelManager.filteredProducts);
    }

}
