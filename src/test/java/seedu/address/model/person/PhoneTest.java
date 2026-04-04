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
    public void isValidPhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));
    }

    @Test
    public void isValidPhone_returnsTrue() {
        // EP: labels in parentheses are allowed by isValidPhone but not isValidPhoneWarn
        assertTrue(Phone.isValidPhone("61234567 (Mobile), 12345678 (Home)"));

        // EP: double comma - empty middle entry ignored
        assertTrue(Phone.isValidPhone("12345678,,12345679"));

        // EP: trailing comma ignored
        assertTrue(Phone.isValidPhone("12345678,"));

        // EP: double trailing comma ignored
        assertTrue(Phone.isValidPhone("12345678,,"));
    }


    @Test
    public void isValidPhone_returnsFalse() {
        // EP: one entry too short
        assertFalse(Phone.isValidPhone("61234567, 12 (Home)"));

        // EP: all entries empty
        assertFalse(Phone.isValidPhone(",,"));
        assertFalse(Phone.isValidPhone(","));
    }

    @Test
    public void isValidPhoneWarn_returnsTrue() {
        // EP: minimum length
        assertTrue(Phone.isValidPhoneWarn("911"));

        // EP: standard valid phone
        assertTrue(Phone.isValidPhoneWarn("93121534"));

        // EP: long phone number
        assertTrue(Phone.isValidPhoneWarn("124293842033123"));

        // EP: spaces allowed
        assertTrue(Phone.isValidPhoneWarn("9312 1534"));

        // EP: symbols allowed
        assertTrue(Phone.isValidPhoneWarn("+65 1234-5678"));

        // EP: multiple numbers split by comma
        assertTrue(Phone.isValidPhoneWarn("61234567, 12345678"));
    }

    @Test
    public void isValidPhoneWarn_returnsFalse() {
        // EP: empty
        assertFalse(Phone.isValidPhoneWarn(""));

        // EP: spaces only
        assertFalse(Phone.isValidPhoneWarn(" "));

        // EP: too short
        assertFalse(Phone.isValidPhoneWarn("91"));

        // EP: non-numeric, no MIN_LENGTH number
        assertFalse(Phone.isValidPhoneWarn("phone"));

        // EP: alphabets mixed
        assertFalse(Phone.isValidPhoneWarn("9011p041"));

        // EP: labels trigger warning
        assertFalse(Phone.isValidPhoneWarn("61234567 (Mobile), 12345678 (Home)"));

        // EP: empty middle entry
        assertFalse(Phone.isValidPhoneWarn("12345678,,12345679"));

        // EP: trailing comma not allowed
        assertFalse(Phone.isValidPhoneWarn("12345678,"));

        // EP: empty entries
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
