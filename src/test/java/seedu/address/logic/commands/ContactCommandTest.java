package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ContactCommandTest {
    @Test
    public void getCommandWord_specificCommands_matchExpected() {
        assertEquals(AddCommand.COMMAND_WORD, ContactCommand.ADD.getCommandWord());
        assertEquals(ArchiveCommand.COMMAND_WORD, ContactCommand.ARCHIVE.getCommandWord());
        assertEquals(ClearCommand.COMMAND_WORD, ContactCommand.CLEAR.getCommandWord());
        assertEquals(DeleteCommand.COMMAND_WORD, ContactCommand.DELETE.getCommandWord());
        assertEquals(EditCommand.COMMAND_WORD, ContactCommand.EDIT.getCommandWord());
        assertEquals(FindCommand.COMMAND_WORD, ContactCommand.FIND.getCommandWord());
        assertEquals(ListCommand.COMMAND_WORD, ContactCommand.LIST.getCommandWord());
        assertEquals(RestoreCommand.COMMAND_WORD, ContactCommand.RESTORE.getCommandWord());
    }

    @Test
    public void getGeneralCommands_returnsAllCommands() {
        List<CommandWord> commands = ContactCommand.getContactCommands();
        assertEquals(ContactCommand.values().length, commands.size());
    }

    @Test
    public void getCommandWord_allCommands_notNullOrEmpty() {
        for (ContactCommand command : ContactCommand.values()) {
            assertNotNull(command.getCommandWord(), command + " has null command word");
            assertFalse(command.getCommandWord().isEmpty(), command + " has empty command word");
        }
    }

    @Test
    public void getCommandUsage_allCommands_notNullOrEmpty() {
        for (ContactCommand command : ContactCommand.values()) {
            assertNotNull(command.getCommandUsage(), command + " has null command usage");
            assertFalse(command.getCommandUsage().isEmpty(), command + " has empty command usage");
        }
    }

    @Test
    public void getCommandDescription_allCommands_notNullOrEmpty() {
        for (ContactCommand command : ContactCommand.values()) {
            assertNotNull(command.getCommandDescription(), command + " has null command description");
            assertFalse(command.getCommandDescription().isEmpty(), command + " has empty command description");
        }
    }
}
