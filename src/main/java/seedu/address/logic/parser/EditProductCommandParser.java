package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_IDENTIFIER;
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

        // Find the target identifier as everything before the first occurrence of " <knownprefix>".
        // This allows IDs that start with prefix-like patterns (e.g. "q/l") while still
        // correctly detecting a missing identifier when args starts with a known prefix.
        String[] knownPrefixStrings = {
            PREFIX_NAME.getPrefix(), PREFIX_QUANTITY.getPrefix(), PREFIX_THRESHOLD.getPrefix(),
            PREFIX_EMAIL.getPrefix(), PREFIX_IDENTIFIER.getPrefix()
        };

        // Find the earliest " <prefix>" occurrence in args to split identifier from rest.
        // Start searching from index 1 so that an ID beginning with a prefix pattern
        // (e.g. "q/l") at position 0 is not mistaken for a missing identifier.
        int splitPos = -1;
        for (String p : knownPrefixStrings) {
            int idx = args.indexOf(" " + p, 1);
            if (idx != -1 && (splitPos == -1 || idx < splitPos)) {
                splitPos = idx;
            }
        }

        String targetIdentifier;
        String remainingArgs;
        if (splitPos == -1) {
            // No prefix found at all — everything is the identifier (will fail MESSAGE_NOT_EDITED later)
            targetIdentifier = args.trim();
            remainingArgs = "";
        } else {
            targetIdentifier = args.substring(0, splitPos).trim();
            remainingArgs = args.substring(splitPos);
        }

        if (targetIdentifier.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditProductCommand.MESSAGE_USAGE));
        }

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(
                    remainingArgs, PREFIX_IDENTIFIER, PREFIX_NAME, PREFIX_QUANTITY, PREFIX_THRESHOLD, PREFIX_EMAIL);

        argMultimap.verifyNoDuplicatePrefixesFor(
            PREFIX_IDENTIFIER, PREFIX_NAME, PREFIX_QUANTITY, PREFIX_THRESHOLD, PREFIX_EMAIL);

        EditProductDescriptor editProductDescriptor = new EditProductDescriptor();

        if (argMultimap.getValue(PREFIX_IDENTIFIER).isPresent()) {
            editProductDescriptor.setIdentifier(
                ParserUtil.parseIdentifier(argMultimap.getValue(PREFIX_IDENTIFIER).get()).getValue());
        }


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
