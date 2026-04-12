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

    private static final Prefix[] EDIT_PRODUCT_PREFIXES = {
        PREFIX_IDENTIFIER, PREFIX_NAME, PREFIX_QUANTITY, PREFIX_THRESHOLD, PREFIX_EMAIL
    };

    /**
     * Parses the given {@code String} of arguments in the context of the EditProductCommand
     * and returns an EditProductCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditProductCommand parse(String args) throws ParseException {
        requireNonNull(args);

        String targetIdentifier = extractTargetIdentifier(args);
        if (targetIdentifier.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditProductCommand.MESSAGE_USAGE));
        }

        String remainingArgs = args.substring(args.indexOf(targetIdentifier) + targetIdentifier.length());
        ArgumentMultimap argMultimap = tokenizeProductArgs(remainingArgs);

        EditProductDescriptor editProductDescriptor = buildDescriptor(argMultimap);

        if (!editProductDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditProductCommand.MESSAGE_NOT_EDITED);
        }

        return new EditProductCommand(targetIdentifier, editProductDescriptor);
    }

    /**
     * Extracts the target identifier from the start of {@code args}.
     * The identifier is everything before the first occurrence of a known prefix
     * (searching from index 1 to allow identifiers that begin with a prefix-like pattern).
     */
    private String extractTargetIdentifier(String args) {
        int splitPos = findFirstPrefixPosition(args);
        if (splitPos == -1) {
            return args.trim();
        }
        return args.substring(0, splitPos).trim();
    }

    /**
     * Returns the position of the earliest {@code " <prefix>"} in {@code args},
     * starting from index 1 so that a leading prefix-like ID (e.g. {@code "q/l"}) is not
     * mistaken for a missing identifier.
     * Returns -1 if no prefix is found.
     */
    private int findFirstPrefixPosition(String args) {
        int splitPos = -1;
        for (Prefix prefix : EDIT_PRODUCT_PREFIXES) {
            int idx = args.indexOf(" " + prefix.getPrefix(), 1);
            if (idx != -1 && (splitPos == -1 || idx < splitPos)) {
                splitPos = idx;
            }
        }
        return splitPos;
    }

    /**
     * Tokenizes {@code args} against all known edit-product prefixes and
     * verifies there are no duplicates.
     */
    private ArgumentMultimap tokenizeProductArgs(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, EDIT_PRODUCT_PREFIXES);
        argMultimap.verifyNoDuplicatePrefixesFor(EDIT_PRODUCT_PREFIXES);
        return argMultimap;
    }

    /**
     * Builds an {@code EditProductDescriptor} from the parsed {@code argMultimap}.
     */
    private EditProductDescriptor buildDescriptor(ArgumentMultimap argMultimap) throws ParseException {
        EditProductDescriptor descriptor = new EditProductDescriptor();

        if (argMultimap.getValue(PREFIX_IDENTIFIER).isPresent()) {
            descriptor.setIdentifier(
                    ParserUtil.parseIdentifier(argMultimap.getValue(PREFIX_IDENTIFIER).get()).getValue());
        }
        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            descriptor.setName(
                    ParserUtil.parseProductName(argMultimap.getValue(PREFIX_NAME).get()).getValue());
        }
        if (argMultimap.getValue(PREFIX_QUANTITY).isPresent()) {
            descriptor.setQuantity(
                    ParserUtil.parseQuantity(argMultimap.getValue(PREFIX_QUANTITY).get()).getValue());
        }
        if (argMultimap.getValue(PREFIX_THRESHOLD).isPresent()) {
            descriptor.setThreshold(
                    ParserUtil.parseThreshold(argMultimap.getValue(PREFIX_THRESHOLD).get()).getValue());
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            String emailValue = argMultimap.getValue(PREFIX_EMAIL).get();
            if (emailValue.isBlank()) {
                descriptor.setVendorEmail(null);
            } else {
                descriptor.setVendorEmail(ParserUtil.parseEmail(emailValue).getValue());
            }
        }

        return descriptor;
    }

}
