package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class EmailTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Email(null));
    }

    @Test
    public void constructor_invalidEmail_throwsIllegalArgumentException() {
        String invalidEmail = "";
        assertThrows(IllegalArgumentException.class, () -> new Email(invalidEmail));
    }

    @Test
    public void isValidEmail() {
        // null email
        assertThrows(NullPointerException.class, () -> Email.isValidEmail(null));

        // blank email
        assertFalse(Email.isValidEmailWarn("")); // empty string
        assertFalse(Email.isValidEmailWarn(" ")); // spaces only

        // missing parts
        assertFalse(Email.isValidEmailWarn("@example.com")); // missing local part
        assertFalse(Email.isValidEmailWarn("peterjackexample.com")); // missing '@' symbol
        assertFalse(Email.isValidEmailWarn("peterjack@")); // missing domain name

        // invalid parts
        assertFalse(Email.isValidEmailWarn("peterjack@-")); // invalid domain name
        assertFalse(Email.isValidEmailWarn("peterjack@exam_ple.com")); // underscore in domain name
        assertFalse(Email.isValidEmailWarn("peter jack@example.com")); // spaces in local part
        assertFalse(Email.isValidEmailWarn("peterjack@exam ple.com")); // spaces in domain name
        assertFalse(Email.isValidEmailWarn(" peterjack@example.com")); // leading space
        assertFalse(Email.isValidEmailWarn("peterjack@example.com ")); // trailing space
        assertFalse(Email.isValidEmailWarn("peterjack@@example.com")); // double '@' symbol
        assertFalse(Email.isValidEmailWarn("peter@jack@example.com")); // '@' symbol in local part
        assertFalse(Email.isValidEmailWarn("-peterjack@example.com")); // local part starts with a hyphen
        assertFalse(Email.isValidEmailWarn("peterjack-@example.com")); // local part ends with a hyphen
        assertFalse(Email.isValidEmailWarn("peter..jack@example.com")); // local part has two consecutive periods
        assertFalse(Email.isValidEmailWarn("peterjack@example@com")); // '@' symbol in domain name
        assertFalse(Email.isValidEmailWarn("peterjack@.example.com")); // domain name starts with a period
        assertFalse(Email.isValidEmailWarn("peterjack@example.com.")); // domain name ends with a period
        assertFalse(Email.isValidEmailWarn("peterjack@-example.com")); // domain name starts with a hyphen
        assertFalse(Email.isValidEmailWarn("peterjack@example.com-")); // domain name ends with a hyphen
        assertFalse(Email.isValidEmailWarn("peterjack@example.c")); // top level domain has less than two chars

        // valid email
        assertTrue(Email.isValidEmailWarn("PeterJack_1190@example.com")); // underscore in local part
        assertTrue(Email.isValidEmailWarn("PeterJack.1190@example.com")); // period in local part
        assertTrue(Email.isValidEmailWarn("PeterJack+1190@example.com")); // '+' symbol in local part
        assertTrue(Email.isValidEmailWarn("PeterJack-1190@example.com")); // hyphen in local part
        assertTrue(Email.isValidEmailWarn("a@bc")); // minimal
        assertTrue(Email.isValidEmailWarn("test@localhost")); // alphabets only
        assertTrue(Email.isValidEmailWarn("123@145")); // numeric local part and domain name
        assertTrue(Email.isValidEmailWarn("a1+be.d@example1.com")); // mixture of alphanumeric and special characters
        assertTrue(Email.isValidEmailWarn("peter_jack@very-very-very-long-example.com")); // long domain name
        assertTrue(Email.isValidEmailWarn("if.you.dream.it_you.can.do.it@example.com")); // long local part
        assertTrue(Email.isValidEmailWarn("e1234567@u.nus.edu")); // more than one period in domain
    }

    @Test
    public void equals() {
        Email email = new Email("valid@email");

        // same values -> returns true
        assertTrue(email.equals(new Email("valid@email")));

        // same object -> returns true
        assertTrue(email.equals(email));

        // null -> returns false
        assertFalse(email.equals(null));

        // different types -> returns false
        assertFalse(email.equals(5.0f));

        // different values -> returns false
        assertFalse(email.equals(new Email("other.valid@email")));
    }
}
