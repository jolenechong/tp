package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_ALIAS_IS_NOT_FOUND;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.alias.exceptions.NoAliasFoundInAliasListException;

/**
 * Deletes an existing alias
 */
public class DeleteAliasCommand extends Command {

    public static final String COMMAND_WORD = "deletealias";

    public static final String MESSAGE_DELETE_ALIAS_SUCCESS = "Removed Alias: %1$s";

    public static final String MESSAGE_FIND_EXISTING_ALIAS =
            "Do alias to find existing aliases";

    private final String aliasStr;

    public DeleteAliasCommand(String aliasStr) {
        this.aliasStr = aliasStr;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        try {
            model.removeAlias(aliasStr);
        } catch (NoAliasFoundInAliasListException e) {
            throw new CommandException(MESSAGE_ALIAS_IS_NOT_FOUND + "\n" + MESSAGE_FIND_EXISTING_ALIAS);
        }

        return new CommandResult(String.format(MESSAGE_DELETE_ALIAS_SUCCESS, aliasStr));
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }
}
