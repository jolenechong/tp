package seedu.address.model;

import java.nio.file.Path;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.Person;
import seedu.address.model.product.Product;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /**
     * {@code Predicate} that shows only active vendors (not archived).
     */
    Predicate<Person> PREDICATE_SHOW_ACTIVE_PERSONS = person -> !person.isArchived();

    /** {@code Predicate} that always evaluate to true */
    Predicate<Product> PREDICATE_SHOW_ALL_PRODUCTS = unused -> true;

    /**
     * {@code Predicate} that shows only active products (not archived).
     */
    Predicate<Product> PREDICATE_SHOW_ACTIVE_PRODUCTS = product -> !product.isArchived();

    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns the user prefs' address book file path.
     */
    Path getAddressBookFilePath();

    /**
     * Sets the user prefs' address book file path.
     */
    void setAddressBookFilePath(Path addressBookFilePath);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Replaces inventory data with the data in {@code inventory}.
     */
    void setInventory(ReadOnlyInventory inventory);

    /** Returns the Inventory */
    ReadOnlyInventory getInventory();

    /**
     * Replaces all vendor vault data with the data in {@code vendorVault}.
     */
    void setVendorVault(ReadOnlyVendorVault vendorVault);

    /** Returns the full VendorVault aggregate. */
    ReadOnlyVendorVault getVendorVault();

    // =========== Person ==================================================================================
    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    void archivePerson(Person person);

    void restorePerson(Person person);

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    // =========== Product ==================================================================================
    /**
     * Returns true if a product with the same identity as {@code product} exists in the inventory.
     */
    boolean hasProduct(Product product);

    /**
     * Deletes the given product.
     * The product must exist in the inventory.
     */
    void deleteProduct(Product target);

    /**
     * Adds the given product.
     * {@code product} must not already exist in the inventory.
     */
    void addProduct(Product product);

    /**
     * Replaces the given product {@code target} with {@code editedProduct}.
     * {@code target} must exist in the inventory.
     * The product identity of {@code editedProduct} must not be the same as another existing product in the inventory.
     */
    void setProduct(Product target, Product editedProduct);

    /** Returns an unmodifiable view of the filtered product list. */
    ObservableList<Product> getFilteredProductList();

    /**
     * Updates the filter of the filtered product list to filter by the given {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredProductList(Predicate<Product> predicate);

    void archiveProduct(Product product);

    void restoreProduct(Product product);

    // =========== AddressBookVersioning ========================================================================
    /**
     * Commits the current state of vendor vault. This should be called after any operation
     * that modifies vendor vault, such as adding, deleting, or editing a person (same as vendor).
     */
    void commitVendorVault();

    /**
     * Undoes the last committed state in vendor vault, reverting to the previous state.
     */
    void undoVendorVault();

    /**
     * Returns true if there are states in vendor vault that can be undone.
     *
     * @return true if there are states in  vendor vault that can be undone, false otherwise.
     */
    boolean canUndoVendorVault();

}
