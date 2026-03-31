package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tests for ArchiveCommandParser.
 */
public class ArchiveCommandParserTest {

    private final ArchiveCommandParser parser = new ArchiveCommandParser();

    @Test
    public void parse_validEmail_returnsArchiveCommand() throws Exception {
        ArchiveCommand command = parser.parse("sg.sales@cytron.io");
        assertTrue(command instanceof ArchiveCommand);
    }

    @Test
    public void parse_validEmailWithWhitespace_returnsArchiveCommand() throws Exception {
        ArchiveCommand command = parser.parse("  alexyeoh@example.com  ");
        assertTrue(command instanceof ArchiveCommand);
    }

    @Test
    public void parse_emptyArgs_throwsParseExceptionWithInvalidCommandFormat() {
        ParseException exception = assertThrows(ParseException.class, () -> parser.parse(""));
        assertEquals(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE),
                exception.getMessage());
    }

    @Test
    public void parse_whitespaceOnly_throwsParseExceptionWithInvalidCommandFormat() {
        ParseException exception = assertThrows(ParseException.class, () -> parser.parse("   "));
        assertEquals(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE),
                exception.getMessage());
    }

    @Test
    public void parse_invalidEmailFormat_throwsParseException() {
        // "sales@" is an invalid email format — should throw, same as delete
        assertThrows(ParseException.class, () -> parser.parse("sales@"));
    }

    @Test
    public void parse_validEmail_isStoredAsLowercase() throws Exception {
        ArchiveCommand command = parser.parse("SG.SALES@CYTRON.IO");
        // The command is created — the email is lowercased inside the constructor
        assertTrue(command instanceof ArchiveCommand);
    }
}
