package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidPhone = "";
        assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }

    @Test
    public void isValidPhone() {
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        // invalid phone numbers
        assertFalse(Phone.isValidPhoneWarn("")); // empty string
        assertFalse(Phone.isValidPhoneWarn(" ")); // spaces only
        assertFalse(Phone.isValidPhoneWarn("91")); // less than 3 numbers
        assertFalse(Phone.isValidPhoneWarn("phone")); // non-numeric
        assertFalse(Phone.isValidPhoneWarn("9011p041")); // alphabets within digits
        assertFalse(Phone.isValidPhoneWarn("61234567 (Mobile), 12345678 (Home)"));

        assertFalse(Phone.isValidPhone("61234567, 12 (Home)")); // one too short
        assertFalse(Phone.isValidPhone(",,")); // all entries empty
        assertFalse(Phone.isValidPhone(",")); // all entries empty

        // valid phone numbers
        assertTrue(Phone.isValidPhoneWarn("911")); // exactly 3 numbers
        assertTrue(Phone.isValidPhoneWarn("93121534"));
        assertTrue(Phone.isValidPhoneWarn("124293842033123")); // long phone numbers
        assertTrue(Phone.isValidPhoneWarn("61234567, 12345678")); // multiple phone numbers separated by comma
        assertFalse(Phone.isValidPhoneWarn("12345678,,12345679")); // empty middle entry ignored
        // should warn but not error
        assertFalse(Phone.isValidPhoneWarn("12345678,")); // trailing empty entry ignored, should warn but not error
        assertTrue(Phone.isValidPhoneWarn("9312 1534")); // spaces within digits
        assertTrue(Phone.isValidPhoneWarn("+65 1234-5678")); // spaces + and - are allowed

        assertTrue(Phone.isValidPhone("61234567 (Mobile), 12345678 (Home)"));
        assertTrue(Phone.isValidPhone("12345678,,12345679"));
        assertTrue(Phone.isValidPhone("12345678,"));

        // inputs that result in entries being empty after splitting
        assertFalse(Phone.isValidPhoneWarn(","));
        assertFalse(Phone.isValidPhoneWarn(",,,"));
        assertFalse(Phone.isValidPhoneWarn(" , , "));
    }

    @Test
    public void equals() {
        Phone phone = new Phone("999");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("999")));

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("995")));
    }
}
