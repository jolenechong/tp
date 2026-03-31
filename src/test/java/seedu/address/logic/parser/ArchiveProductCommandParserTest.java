package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ArchiveProductCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tests for ArchiveProductCommandParser.
 */
public class ArchiveProductCommandParserTest {

    private final ArchiveProductCommandParser parser = new ArchiveProductCommandParser();

    @Test
    public void parse_validArgs_returnsArchiveProductCommand() throws Exception {
        Command command = parser.parse("DS-1001");
        assertTrue(command instanceof ArchiveProductCommand);
    }

    @Test
    public void parse_validArgsWithWhitespace_returnsArchiveProductCommand() throws Exception {
        Command command = parser.parse("  DE/5  ");
        assertTrue(command instanceof ArchiveProductCommand);
    }

    @Test
    public void parse_emptyArgs_throwsParseExceptionWithInvalidCommandFormat() {
        ParseException exception = assertThrows(ParseException.class, () -> parser.parse(""));
        assertEquals(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveProductCommand.MESSAGE_USAGE),
                exception.getMessage());
    }

    @Test
    public void parse_whitespaceOnly_throwsParseExceptionWithInvalidCommandFormat() {
        ParseException exception = assertThrows(ParseException.class, () -> parser.parse("   "));
        assertEquals(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveProductCommand.MESSAGE_USAGE),
                exception.getMessage());
    }

    @Test
    public void getPendingConfirmation_returnsConfirmation() {
        ArchiveProductCommand command = new ArchiveProductCommand("RICE");
        assertNotNull(command.getPendingConfirmation());
    }
}
