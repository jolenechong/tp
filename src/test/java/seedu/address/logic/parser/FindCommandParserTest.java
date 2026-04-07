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
    private static final String MESSAGE_NAME_KEYWORD_BLANK = "Name keyword should not be blank.";
    private static final String MESSAGE_TAG_KEYWORD_BLANK = "Tag keyword should not be blank.";

    private final FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        // BV: empty input should be rejected.
        // EP: whitespace-only input belongs to the same invalid partition after trimming.
        assertInvalidFormat("");
        assertInvalidFormat("     ");
    }

    @Test
    public void parse_namePrefixOnly_returnsFindCommand() {
        // EP: n/-only input produces name-based search.
        FindCommand expectedFindCommand = createNameCommand("Alice", "Bob");
        assertParseSuccess(parser, "n/Alice n/Bob", expectedFindCommand);

        // BV: whitespace splitting inside a value should produce multiple keywords.
        assertParseSuccess(parser, "n/Alice Bob", expectedFindCommand);

        // BV: repeated spaces are normalized after token filtering.
        assertParseSuccess(parser, "n/Alice   Bob", expectedFindCommand);
    }

    @Test
    public void parse_tagPrefixOnly_returnsFindCommand() {
        // EP: t/-only input produces tag-based search.
        FindCommand expectedFindCommand = createTagCommand("vip", "priority");
        assertParseSuccess(parser, "t/vip t/priority", expectedFindCommand);

        // BV: whitespace splitting inside a value should produce multiple keywords.
        assertParseSuccess(parser, "t/vip priority", expectedFindCommand);

        // BV: repeated spaces are normalized after token filtering.
        assertParseSuccess(parser, "t/vip   priority", expectedFindCommand);
    }

    @Test
    public void parse_nameAndTagPrefixes_returnsFindCommand() {
        // EP: when both n/ and t/ are present, both predicates are combined with AND
        FindCommand expectedFindCommand = createCombinedCommand(
            Arrays.asList("Alice", "Bob"), Arrays.asList("vip", "lead"));
        assertParseSuccess(parser, "n/Alice Bob t/vip lead", expectedFindCommand);

        // EP: repeated prefixes in mixed order remain cumulative.
        assertParseSuccess(parser, "t/vip n/Alice t/lead n/Bob", expectedFindCommand);
    }

    @Test
    public void parse_nonPrefixPreamble_throwsParseException() {
        // EP: reject non-prefix preamble before valid prefixes.
        assertNonPrefixFailure("Alice n/Bob");

        // BV: unknown prefix text is rejected.
        assertNonPrefixFailure("x/Alice n/Bob");
    }

    @Test
    public void parse_noPrefixes_throwsInvalidFormat() {
        // EP: no prefixes at all should be treated as invalid command format.
        assertInvalidFormat("Alice Bob");
        assertInvalidFormat("syn");
    }

    @Test
    public void parse_blankNameKeyword_throwsParseException() {
        // BV: blank value after n/ should produce targeted name-keyword guidance.
        assertParseFailure(parser, "n/", MESSAGE_NAME_KEYWORD_BLANK);
        assertParseFailure(parser, "n/ t/vip", MESSAGE_NAME_KEYWORD_BLANK);
    }

    @Test
    public void parse_blankTagKeyword_throwsParseException() {
        // BV: blank value after t/ should produce targeted tag-keyword guidance.
        assertParseFailure(parser, "t/", MESSAGE_TAG_KEYWORD_BLANK);
        assertParseFailure(parser, "t/ n/Alice", MESSAGE_TAG_KEYWORD_BLANK);
    }

    @Test
    public void parse_caseSensitivePrefixes_throwsParseException() {
        assertInvalidFormat("N/Alice T/vip");
    }

    private void assertInvalidFormat(String userInput) {
        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    private void assertNonPrefixFailure(String userInput) {
        assertParseFailure(parser, userInput, MESSAGE_NON_PREFIX_BEFORE_PREFIX + FindCommand.MESSAGE_USAGE);
    }

    private FindCommand createNameCommand(String... keywords) {
        return new FindCommand(new NameContainsKeywordsScoredPredicate(Arrays.asList(keywords)));
    }

    private FindCommand createTagCommand(String... keywords) {
        return new FindCommand(new PersonTagContainsKeywordsPredicate(Arrays.asList(keywords)));
    }

    private FindCommand createCombinedCommand(java.util.List<String> nameKeywords, java.util.List<String> tagKeywords) {
        NameContainsKeywordsScoredPredicate namePredicate = new NameContainsKeywordsScoredPredicate(nameKeywords);
        PersonTagContainsKeywordsPredicate tagPredicate = new PersonTagContainsKeywordsPredicate(tagKeywords);
        return new FindCommand(new NameAndTagMatchesPredicate(namePredicate, tagPredicate));
    }

}
