package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_CONFIRMATION_FLAG;
import static seedu.address.logic.parser.ConfirmationFlagIndicator.containsConfirmationFlag;
import static seedu.address.logic.parser.ConfirmationFlagIndicator.removeConfirmationFlag;

import seedu.address.logic.commands.DeleteProductCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.product.Identifier;

/**
 * Parses input arguments and creates a DeleteProductCommand.
 */
public class DeleteProductCommandParser implements Parser<DeleteProductCommand> {

    public static final String CONFIRMATION_INDICATOR = "-y";

    public static final String MESSAGE_INVALID_FORMAT =
            "Product identifier must be provided.\n"
                    + "Example: " + DeleteProductCommand.COMMAND_WORD + " P001";

    @Override
    public DeleteProductCommand parse(String args) throws ParseException {
        requireNonNull(args);

        String argsTrimmed = args.trim();

        String[] tokens = argsTrimmed.split("\\s+");
        boolean needsConfirmation = !containsConfirmationFlag(
                tokens, CONFIRMATION_INDICATOR, MESSAGE_INVALID_CONFIRMATION_FLAG);
        String argsNoConfirmation = removeConfirmationFlag(tokens, CONFIRMATION_INDICATOR);

        if (argsNoConfirmation.isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_FORMAT);
        }

        ParseResult<Identifier> identifier = ParserUtil.parseIdentifier(argsNoConfirmation);

        return new DeleteProductCommand(identifier.getValue(), needsConfirmation);
    }
}
