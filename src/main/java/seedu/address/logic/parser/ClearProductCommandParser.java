package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.ClearProductCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a {@link ClearProductCommand}.
 */
public class ClearProductCommandParser implements Parser<ClearProductCommand> {

    public static final String MESSAGE_INVALID_FORMAT =
            "Usage: clearproduct";

    @Override
    public ClearProductCommand parse(String args) throws ParseException {
        requireNonNull(args);

        if (!args.trim().isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_FORMAT);
        }

        return new ClearProductCommand(true);
    }
}
