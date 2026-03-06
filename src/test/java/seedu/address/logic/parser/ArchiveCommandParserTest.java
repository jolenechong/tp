package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class ArchiveCommandParserTest {

    private final ArchiveCommandParser parser = new ArchiveCommandParser();

    @Test
    public void parse_validArgs_returnsArchiveCommand() throws Exception {
        ArchiveCommand command = parser.parse("vendor 1");
        assertTrue(command instanceof ArchiveCommand);
    }

    @Test
    public void parse_invalidPrefix_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("person 1"));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("vendor a"));
    }
}
