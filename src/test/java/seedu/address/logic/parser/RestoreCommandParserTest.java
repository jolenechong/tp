package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RestoreCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class RestoreCommandParserTest {

    private final RestoreCommandParser parser = new RestoreCommandParser();

    @Test
    public void parse_validArgs_returnsRestoreCommand() throws Exception {
        RestoreCommand command = parser.parse("vendor 1");
        assertTrue(command instanceof RestoreCommand);
    }

    @Test
    public void parse_invalidPrefix_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("person 1"));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("vendor x"));
    }
}
