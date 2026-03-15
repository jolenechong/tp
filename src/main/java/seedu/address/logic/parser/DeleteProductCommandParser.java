package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.DeleteProductCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a DeleteProductCommand.
 */
public class DeleteProductCommandParser implements Parser<DeleteProductCommand> {

    public static final String MESSAGE_INVALID_FORMAT =
            "Product identifier must be provided.\n"
                    + "Example: " + DeleteProductCommand.COMMAND_WORD + " P001";

    @Override
    public DeleteProductCommand parse(String args) throws ParseException {
        requireNonNull(args);

        String trimmedArgs = args.trim();

        if (trimmedArgs.isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_FORMAT);
        }

        return new DeleteProductCommand(trimmedArgs, true);
    }
}
