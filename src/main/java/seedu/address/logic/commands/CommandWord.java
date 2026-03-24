package seedu.address.logic.commands;

/**
 * Represents a command with an associated word, usage, and description.
 */
public interface CommandWord {
    String getCommandWord();

    String getCommandUsage();

    String getCommandDescription();
}
