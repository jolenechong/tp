package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_QUANTITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_THRESHOLD;

import seedu.address.logic.commands.EditProductCommand;
import seedu.address.logic.commands.EditProductCommand.EditProductDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new EditProductCommand object.
 */
public class EditProductCommandParser implements Parser<EditProductCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditProductCommand
     * and returns an EditProductCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditProductCommand parse(String args) throws ParseException {
        requireNonNull(args);

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_QUANTITY, PREFIX_THRESHOLD, PREFIX_EMAIL);

        String targetIdentifier = argMultimap.getPreamble().trim();

        if (targetIdentifier.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditProductCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_QUANTITY, PREFIX_THRESHOLD, PREFIX_EMAIL);

        EditProductDescriptor editProductDescriptor = new EditProductDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editProductDescriptor.setName(
                    ParserUtil.parseProductName(argMultimap.getValue(PREFIX_NAME).get()).getValue());
        }

        if (argMultimap.getValue(PREFIX_QUANTITY).isPresent()) {
            editProductDescriptor.setQuantity(
                    ParserUtil.parseQuantity(argMultimap.getValue(PREFIX_QUANTITY).get()).getValue());
        }

        if (argMultimap.getValue(PREFIX_THRESHOLD).isPresent()) {
            editProductDescriptor.setThreshold(
                    ParserUtil.parseThreshold(argMultimap.getValue(PREFIX_THRESHOLD).get()).getValue());
        }

        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {

            String emailValue = argMultimap.getValue(PREFIX_EMAIL).get();

            if (emailValue.isBlank()) {
                editProductDescriptor.setVendorEmail(null);
            } else {
                editProductDescriptor.setVendorEmail(
                        ParserUtil.parseEmail(emailValue).getValue());
            }
        }

        if (!editProductDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditProductCommand.MESSAGE_NOT_EDITED);
        }

        return new EditProductCommand(targetIdentifier, editProductDescriptor);
    }

}
