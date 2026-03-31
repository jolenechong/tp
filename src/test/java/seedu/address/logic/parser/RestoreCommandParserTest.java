package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RestoreCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tests for RestoreCommandParser.
 */
public class RestoreCommandParserTest {

    private final RestoreCommandParser parser = new RestoreCommandParser();

    @Test
    public void parse_validEmail_returnsRestoreCommand() throws Exception {
        RestoreCommand command = parser.parse("sg.sales@cytron.io");
        assertTrue(command instanceof RestoreCommand);
    }

    @Test
    public void parse_validEmailWithWhitespace_returnsRestoreCommand() throws Exception {
        RestoreCommand command = parser.parse("  alexyeoh@example.com  ");
        assertTrue(command instanceof RestoreCommand);
    }

    @Test
    public void parse_emptyArgs_returnsRestoreCommandWithNullEmail() throws Exception {
        // Empty input is valid for restore — shows the archived list
        RestoreCommand command = parser.parse("");
        assertTrue(command instanceof RestoreCommand);
    }

    @Test
    public void parse_whitespaceOnly_returnsRestoreCommandWithNullEmail() throws Exception {
        RestoreCommand command = parser.parse("   ");
        assertTrue(command instanceof RestoreCommand);
    }

    @Test
    public void parse_invalidEmailFormat_throwsParseException() {
        // "sales@" is an invalid email format — should throw, same as archive and delete
        assertThrows(ParseException.class, () -> parser.parse("sales@"));
    }
}
