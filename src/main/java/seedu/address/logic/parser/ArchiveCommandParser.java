package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;

/**
 * Parses input arguments and creates a new {@code ArchiveCommand}.
 *
 * Expected format: {@code archive EMAIL}
 * Example: {@code archive sg.sales@cytron.io}
 */
public class ArchiveCommandParser implements Parser<ArchiveCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the {@code ArchiveCommand}
     * and returns an {@code ArchiveCommand} object for execution.
     *
     * If the email is blank, a {@code ParseException} is thrown with the full usage message
     * in the format {@code "Invalid command format!\n<usage>"}, aligning with the behaviour of
     * commands like {@code edit}.
     * If the email format is invalid (e.g. {@code sales@}), a {@code ParseException} is thrown
     * with an appropriate format error message, aligning with the behaviour of {@code delete}.
     *
     * @param args user input arguments (expected to be a single email string)
     * @return {@code ArchiveCommand} with the parsed email
     * @throws ParseException if the input is blank or the email format is invalid
     */
    @Override
    public ArchiveCommand parse(String args) throws ParseException {
        String trimmed = args.trim();

        if (trimmed.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE));
        }

        // Validate email format the same way delete does — throws ParseException on blank/invalid format
        ParseResult<Email> emailResult = ParserUtil.parseEmail(trimmed);

        return new ArchiveCommand(emailResult.getValue().value);
    }
}
