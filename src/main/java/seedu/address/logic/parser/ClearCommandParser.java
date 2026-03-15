package seedu.address.logic.parser;

import java.util.Arrays;

import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ClearCommand object.
 */
public class ClearCommandParser implements Parser<ClearCommand> {

    public static final String MESSAGE_WRONGLY_FORMED_FLAG =
            "Invalid format. The '-y' flag must be standalone.\n"
                    + "Example: clear -y";

    public static final String CONFIRMATION_INDICATOR = "-y";

    /**
     * Parses the given {@code String} of arguments in the context of the ClearCommand
     * and returns a ClearCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public ClearCommand parse(String args) throws ParseException {
        String argsTrimmed = args.trim();

        if (argsTrimmed.isEmpty()) {
            return new ClearCommand(true);
        }

        String[] tokens = argsTrimmed.split("\\s+");
        boolean hasConfirmFlag = checkConfirmationIndicator(tokens);

        return new ClearCommand(!hasConfirmFlag);
    }

    private boolean checkConfirmationIndicator(String[] tokens) throws ParseException {
        boolean hasWronglyFormedFlag = Arrays.stream(tokens)
                .anyMatch(t -> t.startsWith(CONFIRMATION_INDICATOR) && !t.equals(CONFIRMATION_INDICATOR));
        if (hasWronglyFormedFlag) {
            throw new ParseException(MESSAGE_WRONGLY_FORMED_FLAG);
        }
        return Arrays.asList(tokens).contains(CONFIRMATION_INDICATOR);
    }
}

