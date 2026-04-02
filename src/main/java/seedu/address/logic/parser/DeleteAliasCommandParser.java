package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.DeleteAliasCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteAliasCommand Object
 */
public class DeleteAliasCommandParser {

    public static final String CORRECT_FORMAT =
            "The correct format is: deletealias <ALIAS_TO_BE_DELETED>";

    public static final String MESSAGE_NO_ARGUMENTS = "The alias cannot be empty\n";
    public static final String MESSAGE_TOO_MANY_ARGUMENTS = "There are too many arguments, alias cannot have spaces\n";

    /**
     * Parses the given {@code String} of arguments and checks if it comprises a single word.
     * The string will be parsed to DeleteAliasCommand object for execution
     *
     */
    public DeleteAliasCommand parse(String args) throws ParseException {
        String argsTrimmed = args.trim();

        if (argsTrimmed.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteAliasCommand.MESSAGE_USAGE));
        }

        String[] tokens = argsTrimmed.split(" ", 2);
        if (tokens.length > 1) {
            throw new ParseException(MESSAGE_TOO_MANY_ARGUMENTS + CORRECT_FORMAT);
        }

        String aliasToDelete = tokens[0];

        return new DeleteAliasCommand(aliasToDelete);
    }
}
