package seedu.address.model;

import java.nio.file.Path;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.Person;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

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

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /**
     * Commits the current state of the address book to the vendor vault. This should be called after any operation
     * that modifies the address book, such as adding, deleting, or editing a person.
     */
    void commitVendorVault();

    /**
     * Undoes the last committed state in the vendor vault, reverting the address book to the previous state.
     */
    void undoVendorVault();

    /**
     * Redoes the last undone state in the vendor vault, restoring the address book to the state before the undo.
     * TODO: TO BE IMPLEMENTED AFTER UNDO FUNCTIONALITY IS IMPLEMENTED
     */
    void redoVendorVault();

    /**
     * Returns true if there are states in the vendor vault that can be undone.
     *
     * @return true if there are states in the vendor vault that can be undone, false otherwise.
     */
    boolean canUndoVendorVault();

    /**
     * Returns true if there are states in the vendor vault that can be redone.
     * TODO: TO BE IMPLEMENTED AFTER UNDO FUNCTIONALITY IS IMPLEMENTED
     *
     * @return true if there are states in the vendor vault that can be redone, false otherwise.
     */
    boolean canRedoVendorVault();

}
