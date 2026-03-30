package seedu.address.logic.commands;

import java.util.List;

/**
 * Represents the types of commands related to Inventory.
 */
public enum InventoryCommand implements CommandWord {
    ADDPRODUCT(AddProductCommand.COMMAND_WORD,
            AddProductCommand.COMMAND_USAGE, AddProductCommand.COMMAND_DESCRIPTION),
    ARCHIVEPRODUCT(ArchiveProductCommand.COMMAND_WORD,
            ArchiveProductCommand.COMMAND_USAGE, ArchiveProductCommand.COMMAND_DESCRIPTION),
    CLEARPRODUCT(ClearProductCommand.COMMAND_WORD,
            ClearProductCommand.COMMAND_USAGE, ClearProductCommand.COMMAND_DESCRIPTION),
    DELETEPRODUCT(DeleteProductCommand.COMMAND_WORD,
            DeleteProductCommand.COMMAND_USAGE, DeleteProductCommand.COMMAND_DESCRIPTION),
    EDITPRODUCT(EditProductCommand.COMMAND_WORD,
            EditProductCommand.COMMAND_USAGE, EditProductCommand.COMMAND_DESCRIPTION),
    FINDPRODUCT(FindProductCommand.COMMAND_WORD,
            FindProductCommand.COMMAND_USAGE, FindProductCommand.COMMAND_DESCRIPTION),
    LISTPRODUCT(ListProductsCommand.COMMAND_WORD,
            ListProductsCommand.COMMAND_USAGE, ListProductsCommand.COMMAND_DESCRIPTION),
    RESTOREPRODUCT(RestoreProductCommand.COMMAND_WORD,
            RestoreProductCommand.COMMAND_USAGE, RestoreProductCommand.COMMAND_DESCRIPTION);

    private final String commandWord;
    private final String commandUsage;
    private final String commandDescription;

    InventoryCommand(String commandWord, String commandUsage, String commandDescription) {
        this.commandWord = commandWord;
        this.commandUsage = commandUsage;
        this.commandDescription = commandDescription;
    }

    @Override
    public String getCommandWord() {
        return commandWord;
    }

    @Override
    public String getCommandUsage() {
        return commandUsage;
    }

    @Override
    public String getCommandDescription() {
        return commandDescription;
    }

    public static List<CommandWord> getInventoryCommands() {
        return List.of(values());
    }
}
