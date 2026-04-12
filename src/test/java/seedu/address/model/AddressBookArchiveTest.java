package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
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

        // The person stored in the address book must have isArchived = true
        Person stored = addressBook.getPersonList().get(0);
        assertTrue(stored.isArchived());
    }

    @Test
    public void restorePerson_personBecomesActive() {
        AddressBook addressBook = new AddressBook();
        Person person = new PersonBuilder().build();

        addressBook.addPerson(person);
        addressBook.archivePerson(person);

        Person archived = addressBook.getPersonList().get(0);
        addressBook.restorePerson(archived);

        // The person stored in the address book must have isArchived = false
        Person stored = addressBook.getPersonList().get(0);
        assertFalse(stored.isArchived());
    }

    @Test
    public void person_archiveMethod_returnsNewInstanceWithFlagTrue() {
        // EP: Person#archive() returns an immutable copy, original stays unchanged
        Person original = new PersonBuilder().build();
        assertFalse(original.isArchived());

        Person archived = original.archive();
        assertTrue(archived.isArchived());
        assertFalse(original.isArchived()); // original unchanged
    }

    @Test
    public void person_restoreMethod_returnsNewInstanceWithFlagFalse() {
        // EP: Person#restore() on an archived person returns a copy with isArchived = false
        Person archived = new PersonBuilder().withArchived(true).build();
        assertTrue(archived.isArchived());

        Person restored = archived.restore();
        assertFalse(restored.isArchived());
        assertTrue(archived.isArchived()); // original unchanged
    }

    @Test
    public void person_isArchivedDefault_isFalse() {
        // EP: PersonBuilder default should produce an active (non-archived) person
        Person person = new PersonBuilder().build();
        assertFalse(person.isArchived());
    }

    @Test
    public void person_withArchivedTrue_isArchived() {
        // EP: PersonBuilder.withArchived(true) should set the flag
        Person person = new PersonBuilder().withArchived(true).build();
        assertTrue(person.isArchived());
    }

    @Test
    public void person_archiveDoesNotAffectTags() {
        // EP: archiving must not add any tag to the person's tag set
        Person person = new PersonBuilder().withTags("vip").build();
        Person archived = person.archive();

        assertFalse(archived.getTags().stream()
                .anyMatch(t -> t.tagName.equalsIgnoreCase("archived")));
        // original user-defined tags are preserved
        assertTrue(archived.getTags().stream()
                .anyMatch(t -> t.tagName.equals("vip")));
    }

    @Test
    public void person_restoreDoesNotAffectTags() {
        // EP: restoring must not remove any user-defined tags
        Person archived = new PersonBuilder().withTags("vip").withArchived(true).build();
        Person restored = archived.restore();

        assertTrue(restored.getTags().stream()
                .anyMatch(t -> t.tagName.equals("vip")));
        assertFalse(restored.getTags().stream()
                .anyMatch(t -> t.tagName.equalsIgnoreCase("archived")));
    }
}
