package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.ConfirmationFlagIndicator.containsConfirmationFlag;

import seedu.address.logic.commands.ClearProductCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a {@link ClearProductCommand}.
 */
public class ClearProductCommandParser implements Parser<ClearProductCommand> {

    public static final String MESSAGE_INVALID_FORMAT =
            "Usage: clearproduct";

    public static final String CLEAR_CONFIRMATION_FLAG = "-y";

    @Override
    public ClearProductCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String argsTrimmed = args.trim();

        if (argsTrimmed.isEmpty()) {
            return new ClearProductCommand(true);
        }

        String[] tokens = argsTrimmed.split(ParserUtil.SEPARATOR_SPACE);
        boolean hasConfirmFlag = containsConfirmationFlag(
                tokens, CLEAR_CONFIRMATION_FLAG);

        return new ClearProductCommand(!hasConfirmFlag);
    }
}
