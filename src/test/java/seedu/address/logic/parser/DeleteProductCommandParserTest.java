package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteProductCommand;
import seedu.address.model.product.Identifier;

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
        assertParseSuccess(parser, "-y " + VALID_ID, new DeleteProductCommand(new Identifier(VALID_ID), false));
    }

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertParseFailure(parser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteProductCommand.MESSAGE_USAGE));
    }
}
