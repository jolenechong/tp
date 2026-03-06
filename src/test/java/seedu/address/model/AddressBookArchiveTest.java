package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class AddressBookArchiveTest {

    @Test
    public void archivePerson_personBecomesArchived() {
        AddressBook addressBook = new AddressBook();
        Person person = new PersonBuilder().build();

        addressBook.addPerson(person);
        addressBook.archivePerson(person);

        Person archived = person.archive();
        assertTrue(archived.isArchived());
    }

    @Test
    public void restorePerson_personBecomesActive() {
        AddressBook addressBook = new AddressBook();
        Person person = new PersonBuilder().build();

        addressBook.addPerson(person);

        Person archived = person.archive();
        addressBook.archivePerson(person);
        addressBook.restorePerson(archived);

        Person restored = archived.restore();
        assertTrue(!restored.isArchived());
    }
}
