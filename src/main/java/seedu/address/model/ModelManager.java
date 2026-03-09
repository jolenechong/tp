package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;
import seedu.address.model.product.Product;

/**
 * Represents the in-memory model of the vendor vault data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final Inventory inventory;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final FilteredList<Product> filteredProducts;
    private final VersionedVendorVault versionedVendorVault;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.inventory = new Inventory();

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        filteredPersons.setPredicate(Model.PREDICATE_SHOW_ACTIVE_PERSONS);
        filteredProducts = new FilteredList<>(this.inventory.getProductList());
        filteredProducts.setPredicate(Model.PREDICATE_SHOW_ALL_PRODUCTS);
        versionedVendorVault = new VersionedVendorVault(addressBook);
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
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
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    // =========== Inventory ==================================================================================
    @Override
    public void setInventory(ReadOnlyInventory inventory) {
        this.inventory.resetData(inventory);
    }

    @Override
    public ReadOnlyInventory getInventory() {
        return inventory;
    }

    // =========== VendorVault ==================================================================================

    @Override
    public void setVendorVault(ReadOnlyVendorVault vendorVault) {
        requireNonNull(vendorVault);
        addressBook.resetData(vendorVault);
        inventory.resetData(vendorVault);
    }

    @Override
    public ReadOnlyVendorVault getVendorVault() {
        VendorVault vendorVault = new VendorVault();
        vendorVault.setAddressBook(addressBook);
        vendorVault.setInventory(inventory);
        return vendorVault;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
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

    // =========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    //=========== Filtered Product List Accessors ============================================================
    /**
     * Returns an unmodifiable view of the list of {@code Product} backed by the internal list of
     * {@code inventory}
     */
    @Override
    public ObservableList<Product> getFilteredProductList() {
        return filteredProducts;
    }

    @Override
    public void updateFilteredProductList(Predicate<Product> predicate) {
        requireNonNull(predicate);
        filteredProducts.setPredicate(predicate);
    }

    // =========== VendorVaultVersionControl ===================================================================

    @Override
    public void commitVendorVault() {
        versionedVendorVault.commit(addressBook);
    }

    @Override
    public void undoVendorVault() {
        versionedVendorVault.undo(addressBook);
    }

    @Override
    public boolean canUndoVendorVault() {
        return versionedVendorVault.canUndo();
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
        return addressBook.equals(otherModelManager.addressBook)
                && inventory.equals(otherModelManager.inventory)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons)
                && filteredProducts.equals(otherModelManager.filteredProducts);
    }

}
