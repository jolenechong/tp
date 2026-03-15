package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteProductCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tests for {@link DeleteProductCommandParser}.
 */
public class DeleteProductCommandParserTest {

    private final DeleteProductCommandParser parser = new DeleteProductCommandParser();

    @Test
    public void parse_validArgs_returnsCommand() throws Exception {
        DeleteProductCommand command = parser.parse("P001");
    }

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(""));
    }
}
