package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.TypicalAliases.getTypicalAliases;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.VendorVault;

public class ClearCommandParserTest {

    private final ClearCommandParser parser = new ClearCommandParser();

    @Test
    public void parse_noFlag_needsConfirmation() throws Exception {
        Model model = new ModelManager(
                new VendorVault(getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), getTypicalAliases());

        ClearCommand command = parser.parse("");
        CommandResult result = command.execute(model);

        assertEquals(ClearCommand.CONFIRMATION_CLEAR_MESSAGE, result.getFeedbackToUser());
        assertFalse(model.getAddressBook().getPersonList().isEmpty());
    }

    @Test
    public void parse_standaloneFlag_skipsConfirmation() throws Exception {
        Model model = new ModelManager(
                new VendorVault(getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), getTypicalAliases());

        ClearCommand command = parser.parse("-y");
        CommandResult result = command.execute(model);

        assertEquals(ClearCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
    }

    @Test
    public void parse_malformedFlagAttached_throwsParseException() {
        assertParseFailure(parser, "-yabc", ClearCommandParser.MESSAGE_WRONGLY_FORMED_FLAG);
        assertParseFailure(parser, "-y1", ClearCommandParser.MESSAGE_WRONGLY_FORMED_FLAG);
    }

    @Test
    public void parse_malformedFlagInToken_throwsParseException() {
        assertParseFailure(parser, "foo -ybar", ClearCommandParser.MESSAGE_WRONGLY_FORMED_FLAG);
    }
}

