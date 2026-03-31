package seedu.address.logic.parser;

import seedu.address.logic.commands.RestoreCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;

/**
 * Parses input arguments and creates a new {@code RestoreCommand}.
 *
 * Expected format: {@code restore [EMAIL]}
 * Example: {@code restore sg.sales@cytron.io}
 *
 * If no email is given, a {@code RestoreCommand} with {@code null} email is returned,
 * which will list all archived vendors. If an email is given but its format is invalid
 * (e.g. {@code sales@}), a {@code ParseException} is thrown, aligning with the behaviour
 * of {@code delete}.
 */
public class RestoreCommandParser implements Parser<RestoreCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the {@code RestoreCommand}
     * and returns a {@code RestoreCommand} object for execution.
     *
     * If the argument is blank, a {@code RestoreCommand} with a null email is returned,
     * which causes the archived vendor list to be displayed.
     * If a non-blank argument is provided, it is validated as an email. An invalid email format
     * (e.g. {@code sales@}) will throw a {@code ParseException}.
     *
     * @param args user input arguments (expected to be an optional email string)
     * @return {@code RestoreCommand} with the parsed email or {@code null} if no email provided
     * @throws ParseException if a non-blank argument has an invalid email format
     */
    @Override
    public RestoreCommand parse(String args) throws ParseException {
        String trimmed = args.trim();

        if (trimmed.isEmpty()) {
            return new RestoreCommand(null);
        }

        // Validate email format the same way delete does — throws ParseException on blank/invalid format
        ParseResult<Email> emailResult = ParserUtil.parseEmail(trimmed);

        return new RestoreCommand(emailResult.getValue().value);
    }
}
