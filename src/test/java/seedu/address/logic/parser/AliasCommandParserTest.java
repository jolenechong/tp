package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_ALIAS_CONTAINS_SPACE;
import static seedu.address.logic.Messages.MESSAGE_ALIAS_FORMATTED_WRONGLY;
import static seedu.address.logic.Messages.MESSAGE_ALIAS_IS_A_PREDEFINED_COMMAND;
import static seedu.address.logic.Messages.MESSAGE_ORIGINAL_COMMAND_DOES_NOT_EXISTS;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AliasCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class AliasCommandParserTest {

    private final AliasCommandParser parser = new AliasCommandParser();

    @Test
    public void parse_validArgs_returnsAliasCommand() throws Exception {
        AliasCommand result = parser.parse("list ls");
        assertTrue(result instanceof AliasCommand);
        assertTrue(result.hasAlias());
    }

    @Test
    public void parse_onlyOneToken_throwsParseException() {
        assertThrows(ParseException.class,
                MESSAGE_ALIAS_FORMATTED_WRONGLY, () -> parser.parse("list"));
    }

    @Test
    public void parse_invalidOriginalCommand_throwsParseException() {
        assertThrows(ParseException.class,
                String.format(MESSAGE_ORIGINAL_COMMAND_DOES_NOT_EXISTS, "invalidCommand"), () ->
                        parser.parse("invalidCommand ls"));
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
                String.format(MESSAGE_ALIAS_IS_A_PREDEFINED_COMMAND, "find"), () -> parser.parse("list find"));
    }

    @Test
    public void parse_argsWithLeadingAndTrailingWhitespace_returnsAliasCommand() throws Exception {
        AliasCommand result = parser.parse("     list ls     ");
        assertTrue(result instanceof AliasCommand);
        assertTrue(result.hasAlias());
    }

    @Test
    public void parse_emptyArgs_returnsAliasCommandWithNoAlias() throws Exception {
        AliasCommand result = parser.parse("");
        assertFalse(result.hasAlias());
    }

    @Test
    public void parse_whitespaceOnlyArgs_returnsAliasCommandWithNoAlias() throws Exception {
        AliasCommand result = parser.parse("   ");
        assertFalse(result.hasAlias());
    }
}
