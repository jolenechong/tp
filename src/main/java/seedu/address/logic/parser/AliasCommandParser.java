package seedu.address.logic.parser;

import seedu.address.logic.commands.AliasCommand;
import seedu.address.logic.commands.CommandType;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.alias.Alias;

public class AliasCommandParser implements Parser<AliasCommand> {

    public static final String MESSAGE_ORIGINAL_COMMAND_DOES_NOT_EXISTS =
            "The original command does not exists.\n"
            + "For the list of commands visit the User Guide.";

    public static final String MESSAGE_ALIAS_CONTAINS_SPACE =
            "The alias should not contain any spaces.";

    public static final String MESSAGE_FORMATTED_WRONGLY =
            "Message is formatted wrongly.";

    public AliasCommand parse(String args) throws ParseException {
        String argsTrimmed = args.trim();

        String[] tokens = argsTrimmed.split(" ", 2);
        System.out.println(args);
        System.out.println(tokens.length);
        if (tokens.length <= 1) {
            throw new ParseException(MESSAGE_FORMATTED_WRONGLY);
        }

        String originalCommand = tokens[0];
        String newAlias = tokens[1];

        if (!CommandType.isValidCommand(originalCommand)) {
            throw new ParseException(MESSAGE_ORIGINAL_COMMAND_DOES_NOT_EXISTS);
        }

        if (newAlias.contains(" ")) {
            throw new ParseException(MESSAGE_ALIAS_CONTAINS_SPACE);
        }

        Alias alias = new Alias(newAlias, originalCommand);
        return new AliasCommand(alias);
    }
}
