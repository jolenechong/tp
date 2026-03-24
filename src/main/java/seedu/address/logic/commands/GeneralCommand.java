package seedu.address.logic.commands;

import java.util.List;

/**
 * Represents the types of general commands.
 */
public enum GeneralCommand implements CommandWord {
    ALIAS(AliasCommand.COMMAND_WORD, AliasCommand.COMMAND_USAGE, AliasCommand.COMMAND_DESCRIPTION),
    EXIT(ExitCommand.COMMAND_WORD, ExitCommand.COMMAND_USAGE, ExitCommand.COMMAND_DESCRIPTION),
    HELP(HelpCommand.COMMAND_WORD, HelpCommand.COMMAND_USAGE, HelpCommand.COMMAND_DESCRIPTION),
    LISTALL(ListAllCommand.COMMAND_WORD, ListAllCommand.COMMAND_USAGE, ListAllCommand.COMMAND_DESCRIPTION),
    REDO(RedoCommand.COMMAND_WORD, RedoCommand.COMMAND_USAGE, RedoCommand.COMMAND_DESCRIPTION),
    UNDO(UndoCommand.COMMAND_WORD, UndoCommand.COMMAND_USAGE, UndoCommand.COMMAND_DESCRIPTION);

    private final String commandWord;
    private final String commandUsage;
    private final String commandDescription;

    GeneralCommand(String commandWord, String commandUsage, String commandDescription) {
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

    public static List<CommandWord> getGeneralCommands() {
        return List.of(values());
    }
}
