package seedu.address.logic.parser;

import static seedu.address.logic.parser.ConfirmationFlagIndicator.containsConfirmationFlag;

import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ClearCommand object.
 */
public class ClearCommandParser implements Parser<ClearCommand> {

    public static final String CLEAR_CONFIRMATION_FLAG = "-y";

    public static final boolean REQUIRE_CONFIRMATION = true;

    public static final int TOKEN_MIN_LENGTH = 0;

    /**
     * Parses the given {@code String} of arguments in the context of the ClearCommand
     * and returns a ClearCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public ClearCommand parse(String args) throws ParseException {
        String argsTrimmed = args.trim();

        if (argsTrimmed.isEmpty()) {
            return new ClearCommand(REQUIRE_CONFIRMATION);
        }

        String[] tokens = argsTrimmed.split(ParserUtil.SEPARATOR_SPACE);
        boolean hasConfirmFlag = containsConfirmationFlag(
                tokens, CLEAR_CONFIRMATION_FLAG, TOKEN_MIN_LENGTH);
        return new ClearCommand(!hasConfirmFlag);
    }

}

