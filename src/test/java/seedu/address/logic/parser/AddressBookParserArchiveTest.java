package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.commands.PendingConfirmation;
import seedu.address.logic.commands.RestoreCommand;

public class AddressBookParserArchiveTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parse_archiveCommand() throws Exception {
        assertTrue(parser.parseCommand("archive vendor 1", new PendingConfirmation()) instanceof ArchiveCommand);
    }

    @Test
    public void parse_restoreCommand() throws Exception {
        assertTrue(parser.parseCommand("restore vendor 1", new PendingConfirmation()) instanceof RestoreCommand);
    }
}
