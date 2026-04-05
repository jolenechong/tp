package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.NameContainsKeywordsScoredPredicate;
import seedu.address.model.person.PersonTagContainsKeywordsPredicate;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        // BV: empty input should be rejected.
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));

        // EP: whitespace-only input belongs to the same invalid partition after trimming.
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // EP: default name-search mode with valid keywords.
        FindCommand expectedFindCommand =
            new FindCommand(new NameContainsKeywordsScoredPredicate(Arrays.asList("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // BV: irregular internal and surrounding whitespace should normalize to the same tokens.
        assertParseSuccess(parser, " \n Alice\t Bob", expectedFindCommand);
    }

    @Test
    public void parse_tagMode_returnsFindCommand() {
        // EP: leading -t selects tag-search mode.
        FindCommand expectedFindCommand =
                new FindCommand(new PersonTagContainsKeywordsPredicate(Arrays.asList("vip", "priority")));
        assertParseSuccess(parser, "-t vip priority", expectedFindCommand);

        // BV: whitespace around flag and keywords should still parse correctly.
        assertParseSuccess(parser, " \n -t \t vip  priority", expectedFindCommand);
    }

    @Test
    public void parse_tagModeWithoutKeywords_throwsParseException() {
        // BV: tag mode requires at least one keyword; exactly one token (-t) is invalid.
        assertParseFailure(parser, "-t", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_nonLeadingTagFlag_throwsParseException() {
        // EP: -t appearing outside the first token is invalid.
        assertParseFailure(parser, "Alice Bob -t", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                FindCommand.MESSAGE_USAGE));
    }

}
