package seedu.address.logic.commands;

/**
 * Represents the types of commands supported by the application for the alias command.
 * Each constant maps to a specific command's word string used for parsing.
 */
public enum CommandType {

    ADD(AddCommand.COMMAND_WORD),
    ADDPRODUCT(AddProductCommand.COMMAND_WORD),
    ALIAS(AliasCommand.COMMAND_WORD),
    ARCHIVE(ArchiveCommand.COMMAND_WORD),
    ARCHIVEPRODUCT(ArchiveProductCommand.COMMAND_WORD),
    CLEAR(ClearCommand.COMMAND_WORD),
    CLEARPRODUCT(ClearProductCommand.COMMAND_WORD),
    DELETE(DeleteCommand.COMMAND_WORD),
    DELETEPRODUCT(DeleteProductCommand.COMMAND_WORD),
    DELETEALIAS(DeleteAliasCommand.COMMAND_WORD),
    EDIT(EditCommand.COMMAND_WORD),
    EDITPRODUCT(EditProductCommand.COMMAND_WORD),
    EXIT(ExitCommand.COMMAND_WORD),
    FIND(FindCommand.COMMAND_WORD),
    FINDPRODUCT(FindProductCommand.COMMAND_WORD),
    HELP(HelpCommand.COMMAND_WORD),
    LIST(ListCommand.COMMAND_WORD),
    LISTPRODUCT(ListProductsCommand.COMMAND_WORD),
    LISTALL(ListAllCommand.COMMAND_WORD),
    REDO(RedoCommand.COMMAND_WORD),
    RESTORE(RestoreCommand.COMMAND_WORD),
    RESTOREPRODUCT(RestoreProductCommand.COMMAND_WORD),
    SETTHRESHOLD(SetThresholdCommand.COMMAND_WORD),
    UNDO(UndoCommand.COMMAND_WORD);

    private final String commandWord;

    CommandType(String commandWord) {
        this.commandWord = commandWord;
    }

    public String getCommandWord() {
        return this.commandWord;
    }

    /**
     * Validates whether the provided command matches a valid command in the enumeration.
     * Empty strings are considered invalid.
     *
     * @param command The command string to validate.
     * @return True if the command corresponds to a valid command, false otherwise.
     */
    public static boolean isValidAliasCommand(String command) {
        if (command.isEmpty()) {
            return false;
        }

        for (CommandType type: CommandType.values()) {
            if (type.commandWord.equals(command)) {
                return true;
            }
        }

        return false;
    }
}
