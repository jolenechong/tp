package seedu.address.logic.parser;

import java.util.Arrays;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    public static final String MESSAGE_WRONGLY_FORMED_FLAG =
            "Invalid format. The '-y' flag must be separate from the index.\n"
                    + "Example: delete -y 1";

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

        return checkConfirmationIndicator(tokens)
                ? new DeleteCommand(removeConfirmationIndicator(args), false)
                : new DeleteCommand(args, true);
    }

    private boolean checkConfirmationIndicator(String[] tokens) throws ParseException {
        boolean hasWronglyFormedFlag = Arrays.stream(tokens)
                .anyMatch(t -> t.startsWith(CONFIRMATION_INDICATOR) && !t.equals(CONFIRMATION_INDICATOR));
        if (hasWronglyFormedFlag) {
            throw new ParseException(MESSAGE_WRONGLY_FORMED_FLAG);
        }
        return Arrays.asList(tokens).contains(CONFIRMATION_INDICATOR);
    }

    private String removeConfirmationIndicator(String args) {
        return args.replaceFirst(CONFIRMATION_INDICATOR, "");
    }
}
