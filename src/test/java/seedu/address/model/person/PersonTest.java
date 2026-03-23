package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void isSimilarPhoneTo_numbersShorterThanMinLength_returnsFalse() {
        Person shortNumber =
                new PersonBuilder().withPhone("123").build();

        Person other =
                new PersonBuilder().withPhone("456").build();

        assertFalse(shortNumber.isSimilarPhoneTo(other));
    }

    @Test
    public void isSimilarPhoneTo_emptyOrShortNormalizedDigits_returnsFalse() throws Exception {
        Person valid = new PersonBuilder().withPhone("92345678").build();

        Person thisEmptyDigits = createPersonWithRawPhoneValue("abc", "empty-this@example.com");
        assertFalse(thisEmptyDigits.isSimilarPhoneTo(valid));

        Person otherEmptyDigits = createPersonWithRawPhoneValue("xyz", "empty-other@example.com");
        assertFalse(valid.isSimilarPhoneTo(otherEmptyDigits));

        Person thisShortDigits = createPersonWithRawPhoneValue("12", "short-this@example.com");
        assertFalse(thisShortDigits.isSimilarPhoneTo(valid));

        Person otherShortDigits = createPersonWithRawPhoneValue("12", "short-other@example.com");
        assertFalse(valid.isSimilarPhoneTo(otherShortDigits));
    }

    @Test
    public void isSimilarPhoneTo_contiguousMatchExists_returnsTrue() {
        Person first = new PersonBuilder().withPhone("91234567").build();
        Person second = new PersonBuilder().withPhone("00123456").build();

        assertTrue(first.isSimilarPhoneTo(second));
    }

    @Test
    public void hasContiguousMatch_inputShorterThanMinLength_returnsFalse() throws Exception {
        Person person = new PersonBuilder().withPhone("81234567").build();

        assertFalse(invokeHasContiguousMatch(person, "12", "123"));
        assertFalse(invokeHasContiguousMatch(person, "123", "12"));
    }

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same email, all other attributes different -> returns true
        Person editedAlice = new PersonBuilder(ALICE)
                .withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB)
                .withAddress(VALID_ADDRESS_BOB)
                .withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // same phone but different email -> returns false (only email is checked for duplicates)
        Person samePhoneDifferentEmail = new PersonBuilder(ALICE)
                .withName(VALID_NAME_BOB)
                .withEmail(VALID_EMAIL_BOB)
                .build();
        assertFalse(ALICE.isSamePerson(samePhoneDifferentEmail));

        // different email -> returns false
        Person differentIdentity = new PersonBuilder(ALICE)
                .withPhone("11111111, 22222222")
                .withEmail(VALID_EMAIL_BOB)
                .build();
        assertFalse(ALICE.isSamePerson(differentIdentity));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = ALICE.getName()
                + ", Phone: " + ALICE.getPhone()
                + ", Email: " + ALICE.getEmail()
                + ", Address: " + ALICE.getAddress();

        if (!ALICE.getTags().isEmpty()) {
            expected += ", Tags: " + ALICE.getTags();
        }

        assertEquals(expected, ALICE.toString());
    }

    private Person createPersonWithRawPhoneValue(String rawPhoneValue, String email) throws Exception {
        Phone phone = new Phone("123");
        Field valueField = Phone.class.getDeclaredField("value");
        valueField.setAccessible(true);
        valueField.set(phone, rawPhoneValue);

        return new Person(
                new Name("Test Vendor"),
                phone,
                new Email(email),
                new Address("123 Test Street"),
                java.util.Set.of());
    }

    private boolean invokeHasContiguousMatch(Person person, String str1, String str2) throws Exception {
        Method method = Person.class.getDeclaredMethod("hasContiguousMatch", String.class, String.class);
        method.setAccessible(true);
        return (boolean) method.invoke(person, str1, str2);
    }

}
