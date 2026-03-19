package seedu.address.model;

import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.model.person.Person;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /**
     * Returns an unmodifiable view of the persons list.
     * This list will not contain any duplicate persons.
     */
    ObservableList<Person> getPersonList();

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

}
