package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.commands.PendingConfirmation;
import seedu.address.logic.commands.RestoreCommand;
import seedu.address.model.ModelManager;

public class AddressBookParserArchiveTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parse_archiveCommand() throws Exception {
        assertTrue(parser.parseCommand("archive sg.sales@cytron.io",
                new PendingConfirmation(),
                new ModelManager()) instanceof ArchiveCommand);
    }

    @Test
    public void parse_restoreCommand() throws Exception {
        assertTrue(parser.parseCommand("restore sg.sales@cytron.io",
                new PendingConfirmation(),
                new ModelManager()) instanceof RestoreCommand);
    }
}
