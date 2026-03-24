package seedu.address.logic.commands;

import java.util.List;

/**
 * Represents the types of commands related to Contact.
 */
public enum ContactCommand implements CommandWord {
    ADD(AddCommand.COMMAND_WORD, AddCommand.COMMAND_USAGE, AddCommand.COMMAND_DESCRIPTION),
    ARCHIVE(ArchiveCommand.COMMAND_WORD, ArchiveCommand.COMMAND_USAGE, ArchiveCommand.COMMAND_DESCRIPTION),
    CLEAR(ClearCommand.COMMAND_WORD, ClearCommand.COMMAND_USAGE, ClearCommand.COMMAND_DESCRIPTION),
    DELETE(DeleteCommand.COMMAND_WORD, DeleteCommand.COMMAND_USAGE, DeleteCommand.COMMAND_DESCRIPTION),
    EDIT(EditCommand.COMMAND_WORD, EditCommand.COMMAND_USAGE, EditCommand.COMMAND_DESCRIPTION),
    FIND(FindCommand.COMMAND_WORD, FindCommand.COMMAND_USAGE, FindCommand.COMMAND_DESCRIPTION),
    LIST(ListCommand.COMMAND_WORD, ListCommand.COMMAND_USAGE, ListCommand.COMMAND_DESCRIPTION),
    RESTORE(RestoreCommand.COMMAND_WORD, RestoreCommand.COMMAND_USAGE, RestoreCommand.COMMAND_DESCRIPTION);

    private final String commandWord;
    private final String commandUsage;
    private final String commandDescription;

    ContactCommand(String commandWord, String commandUsage, String commandDescription) {
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

    public static List<CommandWord> getContactCommands() {
        return List.of(values());
    }
}
