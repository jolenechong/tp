package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ClearProductCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class ClearProductCommandParserTest {

    private final ClearProductCommandParser parser = new ClearProductCommandParser();

    @Test
    public void parse_validArgs_returnsCommand() throws Exception {
        ClearProductCommand command = parser.parse("");
        assertNotNull(command);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("extra"));
    }
}
