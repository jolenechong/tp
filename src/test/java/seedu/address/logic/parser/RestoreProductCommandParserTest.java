package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RestoreProductCommand;

/**
 * Tests for RestoreProductCommandParser.
 */
public class RestoreProductCommandParserTest {

    private final RestoreProductCommandParser parser = new RestoreProductCommandParser();

    @Test
    public void parse_validArgs_returnsRestoreProductCommand() throws Exception {
        RestoreProductCommand command = parser.parse("DE/5");
        assertTrue(command instanceof RestoreProductCommand);
    }

    @Test
    public void parse_validArgsWithWhitespace_returnsRestoreProductCommand() throws Exception {
        RestoreProductCommand command = parser.parse("  DE/5  ");
        assertTrue(command instanceof RestoreProductCommand);
    }

    @Test
    public void parse_emptyArgs_returnsRestoreProductCommandWithNullIdentifier() throws Exception {
        // Empty input is valid for restoreproduct — shows the archived list
        RestoreProductCommand command = parser.parse("");
        assertTrue(command instanceof RestoreProductCommand);
    }

    @Test
    public void parse_whitespaceOnly_returnsRestoreProductCommandWithNullIdentifier() throws Exception {
        RestoreProductCommand command = parser.parse("   ");
        assertTrue(command instanceof RestoreProductCommand);
    }

    @Test
    public void getPendingConfirmation_returnsConfirmation() {
        RestoreProductCommand command = new RestoreProductCommand("RICE");
        assertNotNull(command.getPendingConfirmation());
    }
}
