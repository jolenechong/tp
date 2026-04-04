package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

/**
 * Tests for the ParseResult class.
 */
public class ParseResultTest {

    @Test
    public void constructor_nullValue_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ParseResult<>(null, Optional.empty()));
    }

    @Test
    public void constructor_nullWarning_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ParseResult<>("value", null));
    }

    @Test
    public void getValue() {
        // EP: no warning -> returns value
        ParseResult<String> parseResult = new ParseResult<>("value", Optional.empty());
        assertEquals("value", parseResult.getValue());

        // EP: with warning -> returns value unaffected by warning
        ParseResult<String> withWarning = new ParseResult<>("value", Optional.of("warn"));
        assertEquals("value", withWarning.getValue());
    }

    @Test
    public void equals() {
        ParseResult<String> parseResult = new ParseResult<>("value", Optional.empty());

        // same values -> returns true
        assertTrue(parseResult.equals(new ParseResult<>("value", Optional.empty())));

        // same object -> returns true
        assertTrue(parseResult.equals(parseResult));

        // null -> returns false
        assertFalse(parseResult.equals(null));

        // different types -> returns false
        assertFalse(parseResult.equals(0.5f));

        // different value -> returns false
        assertFalse(parseResult.equals(new ParseResult<>("different", Optional.empty())));

        // different warning -> returns false
        assertFalse(parseResult.equals(new ParseResult<>("value", Optional.of("warning"))));

        // same value and warning -> returns true
        ParseResult<String> withWarning = new ParseResult<>("value", Optional.of("warn"));
        assertTrue(withWarning.equals(new ParseResult<>("value", Optional.of("warn"))));
    }

    @Test
    public void hashcode() {
        ParseResult<String> parseResult = new ParseResult<>("value", Optional.empty());

        // same values -> returns same hashcode
        assertEquals(parseResult.hashCode(), new ParseResult<>("value", Optional.empty()).hashCode());

        // different value -> returns different hashcode
        assertNotEquals(parseResult.hashCode(), new ParseResult<>("different", Optional.empty()).hashCode());

        // different warning -> returns different hashcode
        assertNotEquals(parseResult.hashCode(), new ParseResult<>("value", Optional.of("warning")).hashCode());

        // same value and warning -> returns same hashcode
        ParseResult<String> withWarning = new ParseResult<>("value", Optional.of("warn"));
        assertEquals(withWarning.hashCode(), new ParseResult<>("value", Optional.of("warn")).hashCode());
    }

    @Test
    public void toStringMethod() {
        ParseResult<String> parseResult = new ParseResult<>("testValue", Optional.empty());
        assertEquals("ParseResult[value=testValue, warning=Optional.empty]", parseResult.toString());

        ParseResult<String> parseResultWithWarning = new ParseResult<>("testValue", Optional.of("testWarning"));
        assertEquals("ParseResult[value=testValue, warning=Optional[testWarning]]", parseResultWithWarning.toString());
    }
}

