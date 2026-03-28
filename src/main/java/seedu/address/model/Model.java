package seedu.address.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.alias.Alias;
import seedu.address.model.alias.exceptions.DuplicateAliasException;
import seedu.address.model.alias.exceptions.NoAliasFoundInAliasListException;
import seedu.address.model.person.Email;
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

    void setAliases(ReadOnlyAliases aliases);

    ReadOnlyAliases getAliases();

    // =========== Person ==================================================================================
    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Returns Person if a person with the same email as {@code email} exists in the address book.
     */
    Optional<Person> findByEmail(Email email);

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

    /**
     * Returns the first person in the list whose name is similar to {@code candidate},
     * excluding {@code exclude} (may be null).
     *
     * @param candidate the person whose name is being compared against the list.
     * @param exclude the person to exclude from the search (may be null).
     * @return Optional Person the matched person (if any).
     */
    Optional<Person> findSimilarNameMatch(Person candidate, Person exclude);

    /**
     * Returns the first product in the product whose name is similar to {@code candidate},
     * excluding {@code exclude} (may be null).
     *
     * @param candidate the product whose name is being compared against the list.
     * @param exclude the product to exclude from the search (may be null).
     * @return Optional Product the matched product (if any).
     */
    Optional<Product> findSimilarNameMatch(Product candidate, Product exclude);

    /**
     * Returns the first person in the list whose phone number is similar to {@code condidate}
     * excluding {@code exclude} (may be null).
     *
     * @param candidate the person whose phone number is being compared against the list.
     * @param exclude the person to exclude from the search (may be null).
     * @return Optional Person the matched person (if any).
     */
    Optional<Person> findSimilarPhoneMatch(Person candidate, Person exclude);

    /**
     * Returns the first person in the list whose address is similar to {@code candidate},
     * excluding {@code exclude} (may be null).
     *
     * @param candidate the person whose address is being compared against the list.
     * @param exclude the person to exclude from the search (may be null).
     * @return Optional Person the matched person (if any).
     */
    Optional<Person> findSimilarAddressMatch(Person candidate, Person exclude);


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

    // =========== Alias List Operations ======================================================================
    public void addAlias(Alias alias) throws DuplicateAliasException;

    public Alias findAlias(String aliasStr) throws NoAliasFoundInAliasListException;

    public void removeAlias(String aliasStr) throws NoAliasFoundInAliasListException;

    /**
     * Returns an unmodifiable view of the alias list.
     */
    List<Alias> getAliasList();

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

    // =========== VendorVaultVersioning ========================================================================
    /**
     * Commits the current state of vendor vault with a summary. This should be called after any operation
     * that modifies vendor vault, such as adding, deleting, or editing a person (same as vendor).
     */
    void commitVendorVault(String actionSummary);

    /**
     * Undoes the last committed state in vendor vault, reverting to the previous state.
     */
    Optional<String> undoVendorVault();

    /**
     * Returns true if there are states in vendor vault that can be undone.
     */
    boolean canUndoVendorVault();

    /**
     * Redoes the last undone state in the vendor vault, restoring the vendor vault to the state before the undo.
     */
    Optional<String> redoVendorVault();

    /**
     * Returns true if there are states in the vendor vault that can be redone.
     */
    boolean canRedoVendorVault();

}
