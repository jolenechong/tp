package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.ConfirmationFlagIndicator.containsConfirmationFlag;
import static seedu.address.logic.parser.ConfirmationFlagIndicator.removeConfirmationFlag;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

public class ConfirmationFlagIndicatorTest {
    private static final String FLAG = "-y";
    private static final String EXCEPTION_MESSAGE = "Invalid confirmation flag.";
    private static final int MIN_TOKEN_LENGTH = 1;

    @Test
    public void containsConfirmationFlag_flagPresent_returnsTrue() throws ParseException {
        String[] tokens = {FLAG, " 1"};
        assertTrue(containsConfirmationFlag(tokens, FLAG, MIN_TOKEN_LENGTH));
    }

    @Test
    public void containsConfirmationFlag_flagAbsent_returnsFalse() throws ParseException {
        String[] tokens = {"delete", "1"};
        assertFalse(containsConfirmationFlag(tokens, FLAG, MIN_TOKEN_LENGTH));
    }

    @Test
    public void containsConfirmationFlag_emptyTokens_returnsFalse() throws ParseException {
        String[] tokens = {};
        assertFalse(containsConfirmationFlag(tokens, FLAG, MIN_TOKEN_LENGTH));
    }

    @Test
    public void containsConfirmationFlag_flagPresentZeroMinTokenLength_returnsTrue() throws ParseException {
        String[] tokens = {FLAG, " 1"};
        assertTrue(containsConfirmationFlag(tokens, FLAG, 0));
    }

    @Test
    public void removeConfirmationFlag_flagPresent_removesFlag() {
        String[] tokens = {"delete", FLAG, "1"};
        assertEquals("delete 1", removeConfirmationFlag(tokens, FLAG));
    }

    @Test
    public void removeConfirmationFlag_flagAbsent_returnsAllTokens() {
        String[] tokens = {"delete", "1"};
        assertEquals("delete 1", removeConfirmationFlag(tokens, FLAG));
    }

    @Test
    public void removeConfirmationFlag_flagOnly_returnsEmptyString() {
        String[] tokens = {FLAG};
        assertEquals("", removeConfirmationFlag(tokens, FLAG));
    }

    @Test
    public void removeConfirmationFlag_emptyTokens_returnsEmptyString() {
        String[] tokens = {};
        assertEquals("", removeConfirmationFlag(tokens, FLAG));
    }

    @Test
    public void removeConfirmationFlag_malformedFlagToken_isNotRemoved() {
        String malformed = FLAG + "XYZ";
        String[] tokens = {"delete", malformed};
        assertEquals("delete " + malformed, removeConfirmationFlag(tokens, FLAG));
    }
}
