package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class CommandTypeTest {

    @Test
    public void getCommandWord_allTypes_returnsCorrectWord() {
        assertEquals(AddCommand.COMMAND_WORD, CommandType.ADD.getCommandWord());
        assertEquals(AddProductCommand.COMMAND_WORD, CommandType.ADDPRODUCT.getCommandWord());
        assertEquals(ArchiveCommand.COMMAND_WORD, CommandType.ARCHIVE.getCommandWord());
        assertEquals(ArchiveProductCommand.COMMAND_WORD, CommandType.ARCHIVEPRODUCT.getCommandWord());
        assertEquals(ClearCommand.COMMAND_WORD, CommandType.CLEAR.getCommandWord());
        assertEquals(DeleteCommand.COMMAND_WORD, CommandType.DELETE.getCommandWord());
        assertEquals(EditCommand.COMMAND_WORD, CommandType.EDIT.getCommandWord());
        assertEquals(ExitCommand.COMMAND_WORD, CommandType.EXIT.getCommandWord());
        assertEquals(FindCommand.COMMAND_WORD, CommandType.FIND.getCommandWord());
        assertEquals(HelpCommand.COMMAND_WORD, CommandType.HELP.getCommandWord());
        assertEquals(ListCommand.COMMAND_WORD, CommandType.LIST.getCommandWord());
        assertEquals(RedoCommand.COMMAND_WORD, CommandType.REDO.getCommandWord());
        assertEquals(RestoreCommand.COMMAND_WORD, CommandType.RESTORE.getCommandWord());
        assertEquals(RestoreProductCommand.COMMAND_WORD, CommandType.RESTOREPRODUCT.getCommandWord());
        assertEquals(UndoCommand.COMMAND_WORD, CommandType.UNDO.getCommandWord());
    }

    @Test
    public void valueOf_validName_returnsCorrectEnum() {
        assertEquals(CommandType.ADD, CommandType.valueOf("ADD"));
        assertEquals(CommandType.UNDO, CommandType.valueOf("UNDO"));
    }

    @Test
    public void valueOf_invalidName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> CommandType.valueOf("INVALID"));
    }

    @Test
    public void isValidCommand_validAliasCommandWord_returnsTrue() {
        assertTrue(CommandType.isValidAliasCommand(AddCommand.COMMAND_WORD));
        assertTrue(CommandType.isValidAliasCommand(ListCommand.COMMAND_WORD));
        assertTrue(CommandType.isValidAliasCommand(UndoCommand.COMMAND_WORD));
    }

    @Test
    public void isValidAliasCommand_emptyString_returnsFalse() {
        assertFalse(CommandType.isValidAliasCommand(""));
    }

    @Test
    public void isValidCommand_invalidAliasCommandWord_returnsFalse() {
        assertFalse(CommandType.isValidAliasCommand("invalidCommand"));
    }

    @Test
    public void isValidCommand_validAliasCommandWordWrongCase_returnsFalse() {
        assertFalse(CommandType.isValidAliasCommand(AddCommand.COMMAND_WORD.toUpperCase()));
    }
}
