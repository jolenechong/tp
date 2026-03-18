package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_ALIAS_CONTAINS_SPACE;
import static seedu.address.logic.Messages.MESSAGE_ALIAS_FORMATTED_WRONGLY;
import static seedu.address.logic.Messages.MESSAGE_ALIAS_IS_A_PREDEFINED_COMMAND;
import static seedu.address.logic.Messages.MESSAGE_ORIGINAL_COMMAND_DOES_NOT_EXISTS;

import seedu.address.logic.commands.AliasCommand;
import seedu.address.logic.commands.CommandType;
import seedu.address.logic.commands.ValidCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.alias.Alias;

/**
 * Parses input arguments and creates a new AliasCommand Object
 */
public class AliasCommandParser implements Parser<AliasCommand> {

    /**
     * Parses the given {@code String} of arguments and transform it to a new Alias object.
     * The new alias object will be parsed to AliasCommand object for execution
     *
     */
    public AliasCommand parse(String args) throws ParseException {
        String argsTrimmed = args.trim();

        if (argsTrimmed.isEmpty()) {
            return new AliasCommand();
        }

        String[] tokens = argsTrimmed.split(" ", 2);
        System.out.println(args);
        System.out.println(tokens.length);
        if (tokens.length <= 1) {
            throw new ParseException(MESSAGE_ALIAS_FORMATTED_WRONGLY);
        }

        String originalCommand = tokens[0];
        String newAlias = tokens[1];

        if (!CommandType.isValidAliasCommand(originalCommand)) {
            throw new ParseException(String.format(MESSAGE_ORIGINAL_COMMAND_DOES_NOT_EXISTS, originalCommand));
        }

        if (newAlias.contains(" ")) {
            throw new ParseException(MESSAGE_ALIAS_CONTAINS_SPACE);
        }

        if (ValidCommand.isValidCommand(newAlias)) {
            throw new ParseException(String.format(MESSAGE_ALIAS_IS_A_PREDEFINED_COMMAND, newAlias));
        }

        Alias alias = new Alias(newAlias, originalCommand);
        return new AliasCommand(alias);
    }
}
