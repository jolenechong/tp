package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_NON_PREFIX_BEFORE_PREFIX;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.NameAndTagMatchesPredicate;
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
    public void parse_namePrefixOnly_returnsFindCommand() {
        // EP: n/-only input produces name-based search.
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsScoredPredicate(Arrays.asList("Alice", "Bob")));
        assertParseSuccess(parser, "n/Alice n/Bob", expectedFindCommand);

        // BV: whitespace splitting inside a value should produce multiple keywords.
        assertParseSuccess(parser, "n/Alice Bob", expectedFindCommand);
    }

    @Test
    public void parse_tagPrefixOnly_returnsFindCommand() {
        // EP: t/-only input produces tag-based search.
        FindCommand expectedFindCommand =
                new FindCommand(new PersonTagContainsKeywordsPredicate(Arrays.asList("vip", "priority")));
        assertParseSuccess(parser, "t/vip t/priority", expectedFindCommand);

        // BV: whitespace splitting inside a value should produce multiple keywords.
        assertParseSuccess(parser, "t/vip priority", expectedFindCommand);
    }

    @Test
    public void parse_nameAndTagPrefixes_returnsFindCommand() {
        // EP: when both n/ and t/ are present, both predicates are combined with AND
        NameContainsKeywordsScoredPredicate namePredicate =
                new NameContainsKeywordsScoredPredicate(Arrays.asList("Alice", "Bob"));
        PersonTagContainsKeywordsPredicate tagPredicate =
                new PersonTagContainsKeywordsPredicate(Arrays.asList("vip", "lead"));

        FindCommand expectedFindCommand =
                new FindCommand(new NameAndTagMatchesPredicate(namePredicate, tagPredicate));
        assertParseSuccess(parser, "n/Alice Bob t/vip lead", expectedFindCommand);

        // EP: repeated prefixes in mixed order remain cumulative.
        assertParseSuccess(parser, "t/vip n/Alice t/lead n/Bob", expectedFindCommand);
    }

    @Test
    public void parse_nonPrefixPreamble_throwsParseException() {
        // EP: reject non-prefix preamble before valid prefixes.
        assertParseFailure(parser, "Alice n/Bob", MESSAGE_NON_PREFIX_BEFORE_PREFIX + FindCommand.MESSAGE_USAGE);

        // BV: unknown prefix text is rejected.
        assertParseFailure(parser, "x/Alice n/Bob", MESSAGE_NON_PREFIX_BEFORE_PREFIX + FindCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_emptyEffectiveKeywords_throwsParseException() {
        // BV: both prefixes present but all values empty is invalid.
        assertParseFailure(parser, "n/ t/", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                FindCommand.MESSAGE_USAGE));

        // BV: unknown input without prefixes is invalid
        assertParseFailure(parser, "Alice Bob", MESSAGE_NON_PREFIX_BEFORE_PREFIX + FindCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_caseSensitivePrefixes_throwsParseException() {
        // BV: uppercase prefix variants are not recognized.
        assertParseFailure(parser, "N/Alice T/vip", MESSAGE_NON_PREFIX_BEFORE_PREFIX + FindCommand.MESSAGE_USAGE);
    }

}
