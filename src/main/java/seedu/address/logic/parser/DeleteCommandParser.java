package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_CONFIRMATION_FLAG;
import static seedu.address.logic.parser.ConfirmationFlagIndicator.containsConfirmationFlag;
import static seedu.address.logic.parser.ConfirmationFlagIndicator.removeConfirmationFlag;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;

/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    public static final String CONFIRMATION_INDICATOR = "-y";

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     *
     * The parse exception will be handled in the DeleteCommand class as it can display the valid range.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {

        String argsTrimmed = args.trim();

        String[] tokens = argsTrimmed.split("\\s+");
        boolean needsConfirmation = !containsConfirmationFlag(
                tokens, CONFIRMATION_INDICATOR, MESSAGE_INVALID_CONFIRMATION_FLAG);
        String emailBeforeParsed = removeConfirmationFlag(tokens, CONFIRMATION_INDICATOR);

        if (emailBeforeParsed.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }

        ParseResult<Email> email;
        try {
            email = ParserUtil.parseEmail(emailBeforeParsed);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage());
        }

        return new DeleteCommand(email.getValue(), needsConfirmation);
    }
}
