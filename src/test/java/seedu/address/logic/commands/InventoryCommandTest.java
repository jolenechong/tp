package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

public class InventoryCommandTest {

    @Test
    public void getCommandWord_specificCommands_matchExpected() {
        assertEquals(AddProductCommand.COMMAND_WORD, InventoryCommand.ADDPRODUCT.getCommandWord());
        assertEquals(ArchiveProductCommand.COMMAND_WORD, InventoryCommand.ARCHIVEPRODUCT.getCommandWord());
        assertEquals(ClearProductCommand.COMMAND_WORD, InventoryCommand.CLEARPRODUCT.getCommandWord());
        assertEquals(DeleteProductCommand.COMMAND_WORD, InventoryCommand.DELETEPRODUCT.getCommandWord());
        assertEquals(EditProductCommand.COMMAND_WORD, InventoryCommand.EDITPRODUCT.getCommandWord());
        assertEquals(FindProductCommand.COMMAND_WORD, InventoryCommand.FINDPRODUCT.getCommandWord());
        assertEquals(ListProductsCommand.COMMAND_WORD, InventoryCommand.LISTPRODUCT.getCommandWord());
        assertEquals(RestoreProductCommand.COMMAND_WORD, InventoryCommand.RESTOREPRODUCT.getCommandWord());
    }

    @Test
    public void getGeneralCommands_returnsAllCommands() {
        List<InventoryCommand> commands = InventoryCommand.getGeneralCommands();
        assertEquals(InventoryCommand.values().length, commands.size());
    }

    @Test
    public void getCommandWord_allCommands_notNullOrEmpty() {
        for (InventoryCommand command : InventoryCommand.values()) {
            assertNotNull(command.getCommandWord(), command + " has null command word");
            assertFalse(command.getCommandWord().isEmpty(), command + " has empty command word");
        }
    }

    @Test
    public void getCommandUsage_allCommands_notNullOrEmpty() {
        for (InventoryCommand command : InventoryCommand.values()) {
            assertNotNull(command.getCommandUsage(), command + " has null command usage");
            assertFalse(command.getCommandUsage().isEmpty(), command + " has empty command usage");
        }
    }

    @Test
    public void getCommandDescription_allCommands_notNullOrEmpty() {
        for (InventoryCommand command : InventoryCommand.values()) {
            assertNotNull(command.getCommandDescription(), command + " has null command description");
            assertFalse(command.getCommandDescription().isEmpty(), command + " has empty command description");
        }
    }
}
