package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_ALIAS;

import java.util.List;
import java.util.Optional;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.alias.Alias;
import seedu.address.model.alias.exceptions.DuplicateAliasException;

/**
 * Adds a new alias
 */
public class AliasCommand extends Command {

    public static final String COMMAND_WORD = "alias";

    public static final String MESSAGE_ADD_ALIAS_SUCCESS = "Added Alias: %1$s";

    public static final String MESSAGE_EMPTY_ALIAS_LIST = "No alias added yet. Add alias using the alias command.";

    public static final String MESSAGE_DISPLAY_ALIAS_LIST = "Here are your current aliases:\n";

    private final Optional<Alias> alias;

    public AliasCommand(Alias alias) {
        this.alias = Optional.of(alias);
    }

    public AliasCommand() {
        this.alias = Optional.empty();
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (hasAlias()) {
            return addAliasToModel(model, alias.get());
        } else {
            return showCurrentAliases(model);
        }
    }

    public boolean hasAlias() {
        return alias.isPresent();
    }

    /**
     * Adds the given Alias to the model's alias list.
     */
    public CommandResult addAliasToModel(Model model, Alias alias) throws CommandException {
        try {
            model.addAlias(alias);
        } catch (DuplicateAliasException e) {
            throw new CommandException(MESSAGE_DUPLICATE_ALIAS);
        }
        return new CommandResult(String.format(MESSAGE_ADD_ALIAS_SUCCESS, alias.getAlias()));
    }

    /**
     * Retrieves and displays all aliases currently stored in the model.
     */
    public CommandResult showCurrentAliases(Model model) throws CommandException {
        List<Alias> aliasList = model.getAliases().getAliasList();
        if (aliasList.isEmpty()) {
            return new CommandResult(MESSAGE_EMPTY_ALIAS_LIST);
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MESSAGE_DISPLAY_ALIAS_LIST);
        for (int i = 1; i <= aliasList.size(); i++) {
            Alias currAlias = aliasList.get(i - 1);
            stringBuilder.append(i)
                    .append(")")
                    .append(currAlias.getOriginalCommand())
                    .append("->")
                    .append(currAlias.getAlias())
                    .append("\n");
        }
        return new CommandResult(stringBuilder.toString());
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }
}
