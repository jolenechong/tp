package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_CONFIRMATION_FLAG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.DeleteProductCommandParser.MESSAGE_INVALID_FORMAT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteProductCommand;

/**
 * Tests for {@link DeleteProductCommandParser}.
 */
public class DeleteProductCommandParserTest {

    private static final String VALID_ID = "id";

    private final DeleteProductCommandParser parser = new DeleteProductCommandParser();

    @Test
    public void parse_validArgs_returnsCommand() throws Exception {
        DeleteProductCommand command = parser.parse("P001");
    }

    @Test
    public void parse_validArgsWithConfirmationFlag_returnsDeleteCommand() {
        assertParseSuccess(parser, "-y " + VALID_ID, new DeleteProductCommand(VALID_ID, false));
    }

    @Test
    public void parse_validArgsWithConfirmationFlagAfterIndex_returnsDeleteCommand() {
        assertParseSuccess(parser, VALID_ID + " -y", new DeleteProductCommand(VALID_ID, false));
    }

    @Test
    public void parse_wronglyFormedFlagAttachedToIndex_throwsParseException() {
        assertParseFailure(parser, "-y" + VALID_ID, MESSAGE_INVALID_CONFIRMATION_FLAG);
        assertParseFailure(parser, "-y1" + VALID_ID, MESSAGE_INVALID_CONFIRMATION_FLAG);
    }

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }
}
