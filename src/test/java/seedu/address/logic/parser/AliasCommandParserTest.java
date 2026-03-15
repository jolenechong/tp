package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_ALIAS_CONTAINS_SPACE;
import static seedu.address.logic.Messages.MESSAGE_ALIAS_IS_A_PREDEFINED_COMMAND;
import static seedu.address.logic.Messages.MESSAGE_FORMATTED_WRONGLY;
import static seedu.address.logic.Messages.MESSAGE_ORIGINAL_COMMAND_DOES_NOT_EXISTS;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AliasCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class AliasCommandParserTest {

    private final AliasCommandParser parser = new AliasCommandParser();

    @Test
    public void parse_validArgs_returnsAliasCommand() throws Exception {
        assertTrue(parser.parse("list ls") instanceof AliasCommand);
    }

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertThrows(ParseException.class,
                MESSAGE_FORMATTED_WRONGLY, () -> parser.parse(""));
    }

    @Test
    public void parse_onlyWhitespace_throwsParseException() {
        assertThrows(ParseException.class,
                MESSAGE_FORMATTED_WRONGLY, () -> parser.parse("   "));
    }

    @Test
    public void parse_onlyOneToken_throwsParseException() {
        assertThrows(ParseException.class,
                MESSAGE_FORMATTED_WRONGLY, () -> parser.parse("list"));
    }

    @Test
    public void parse_invalidOriginalCommand_throwsParseException() {
        assertThrows(ParseException.class,
                MESSAGE_ORIGINAL_COMMAND_DOES_NOT_EXISTS, () -> parser.parse("invalidCommand ls"));
    }

    @Test
    public void parse_aliasContainsSpace_throwsParseException() {
        assertThrows(ParseException.class,
                MESSAGE_ALIAS_CONTAINS_SPACE, () -> parser.parse("list l s"));
    }

    @Test
    public void parse_aliasIsPredefinedCommand_throwsParseException() {
        // "find" is a predefined command, so it cannot be used as an alias
        assertThrows(ParseException.class,
                MESSAGE_ALIAS_IS_A_PREDEFINED_COMMAND, () -> parser.parse("list find"));
    }

    @Test
    public void parse_argsWithLeadingAndTrailingWhitespace_returnsAliasCommand() throws Exception {
        assertTrue(parser.parse("  list ls  ") instanceof AliasCommand);
    }
}
