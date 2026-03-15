package seedu.address.logic.parser;

import java.util.Arrays;
import java.util.stream.Collectors;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;

/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    public static final String MESSAGE_WRONGLY_FORMED_FLAG =
            "Invalid format. The '-y' flag must be standalone.\n"
                    + "Example: delete -y <email>";

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
        boolean needsConfirmation = !checkConfirmationIndicator(tokens);
        String emailBeforeParsed = removeConfirmationIndicator(tokens);
        ParseResult<Email> email = ParserUtil.parseEmail(emailBeforeParsed);

        return new DeleteCommand(email.getValue(), needsConfirmation);
    }

    private boolean checkConfirmationIndicator(String[] tokens) throws ParseException {
        boolean hasWronglyFormedFlag = Arrays.stream(tokens)
                .anyMatch(t -> t.startsWith(CONFIRMATION_INDICATOR) && !t.equals(CONFIRMATION_INDICATOR));
        if (hasWronglyFormedFlag) {
            throw new ParseException(MESSAGE_WRONGLY_FORMED_FLAG);
        }
        return Arrays.asList(tokens).contains(CONFIRMATION_INDICATOR);
    }

    private String removeConfirmationIndicator(String[] tokens) {
        return Arrays.stream(tokens)
                .filter(t -> !t.equals(CONFIRMATION_INDICATOR))
                .collect(Collectors.joining(" "));
    }
}
