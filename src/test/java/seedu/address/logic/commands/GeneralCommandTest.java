package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

public class GeneralCommandTest {

    @Test
    public void getCommandWord_specificCommands_matchExpected() {
        assertEquals(AliasCommand.COMMAND_WORD, GeneralCommand.ALIAS.getCommandWord());
        assertEquals(ExitCommand.COMMAND_WORD, GeneralCommand.EXIT.getCommandWord());
        assertEquals(HelpCommand.COMMAND_WORD, GeneralCommand.HELP.getCommandWord());
        assertEquals(RedoCommand.COMMAND_WORD, GeneralCommand.REDO.getCommandWord());
        assertEquals(UndoCommand.COMMAND_WORD, GeneralCommand.UNDO.getCommandWord());
    }

    @Test
    public void getGeneralCommands_returnsAllCommands() {
        List<CommandWord> commands = GeneralCommand.getGeneralCommands();
        assertEquals(GeneralCommand.values().length, commands.size());
    }

    @Test
    public void getCommandWord_allCommands_notNullOrEmpty() {
        for (GeneralCommand command : GeneralCommand.values()) {
            assertNotNull(command.getCommandWord(), command + " has null command word");
            assertFalse(command.getCommandWord().isEmpty(), command + " has empty command word");
        }
    }

    @Test
    public void getCommandUsage_allCommands_notNullOrEmpty() {
        for (GeneralCommand command : GeneralCommand.values()) {
            assertNotNull(command.getCommandUsage(), command + " has null command usage");
            assertFalse(command.getCommandUsage().isEmpty(), command + " has empty command usage");
        }
    }

    @Test
    public void getCommandDescription_allCommands_notNullOrEmpty() {
        for (GeneralCommand command : GeneralCommand.values()) {
            assertNotNull(command.getCommandDescription(), command + " has null command description");
            assertFalse(command.getCommandDescription().isEmpty(), command + " has empty command description");
        }
    }
}
