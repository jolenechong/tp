package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON_STRING;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteCommand;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new DeleteCommand(INDEX_FIRST_PERSON_STRING, true));
    }

    @Test
    public void parse_validArgsWithConfirmationFlag_returnsDeleteCommand() {
        assertParseSuccess(parser, "-y 1", new DeleteCommand(INDEX_FIRST_PERSON_STRING, false));
    }

    @Test
    public void parse_validArgsWithConfirmationFlagAfterIndex_returnsDeleteCommand() {
        assertParseSuccess(parser, "1 -y", new DeleteCommand(INDEX_FIRST_PERSON_STRING, false));
    }

    @Test
    public void parse_validArgsWithLeadingAndTrailingSpaces_returnsDeleteCommand() {
        assertParseSuccess(parser, "    1    ", new DeleteCommand(INDEX_FIRST_PERSON_STRING, true));
    }

    @Test
    public void parse_wronglyFormedFlagAttachedToIndex_throwsParseException() {
        assertParseFailure(parser, "-y1", DeleteCommandParser.MESSAGE_WRONGLY_FORMED_FLAG);
        assertParseFailure(parser, "-y12", DeleteCommandParser.MESSAGE_WRONGLY_FORMED_FLAG);
    }
}
