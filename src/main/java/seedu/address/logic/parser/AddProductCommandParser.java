package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_ALL_PREFIXES_MISSING;
import static seedu.address.logic.Messages.MESSAGE_MISSING_FIELD_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_PREFIX;
import static seedu.address.logic.Messages.MESSAGE_NON_PREFIX_BEFORE_PREFIX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_IDENTIFIER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_QUANTITY;
import static seedu.address.logic.parser.ParserUtil.COMMA_SEPARATOR;
import static seedu.address.logic.parser.ParserUtil.FIELD_IDENTIFIER;
import static seedu.address.logic.parser.ParserUtil.FIELD_PRODUCT_NAME;
import static seedu.address.logic.parser.ParserUtil.NEWLINE;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import seedu.address.logic.commands.AddProductCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.product.Identifier;
import seedu.address.model.product.Name;
import seedu.address.model.product.Product;
import seedu.address.model.product.Quantity;

/**
 * Parses input arguments and creates a new AddProductCommand object.
 */
public class AddProductCommandParser implements Parser<AddProductCommand> {

    public static final String DEFAULT_QUANTITY = "0";
    public static final String MESSAGE_QUANTITY_DEFAULTED =
            "⚠ Warning: Quantity missing, defaulted to 0.";

    private record RequiredField(Prefix prefix, String name) {
    }

    /**
     * Parses the given {@code String} of arguments in the context of AddProductCommand.
     * and returns an AddProductCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddProductCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                args, PREFIX_IDENTIFIER, PREFIX_NAME, PREFIX_QUANTITY);

        requireAddProductPrefixes(argMultimap);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_IDENTIFIER, PREFIX_NAME, PREFIX_QUANTITY);

        StringBuilder warnings = new StringBuilder();
        ParseResult<Identifier> identifierResult = ParserUtil
                .parseIdentifier(argMultimap.getValue(PREFIX_IDENTIFIER).get());
        ParseResult<Name> nameResult = ParserUtil
                .parseProductName(argMultimap.getValue(PREFIX_NAME).get());

        appendWarning(warnings, identifierResult.getWarning());
        appendWarning(warnings, nameResult.getWarning());

        Quantity quantity;
        if (argMultimap.getValue(PREFIX_QUANTITY).isPresent()) {
            quantity = ParserUtil.parseQuantity(argMultimap.getValue(PREFIX_QUANTITY).get()).getValue();
        } else {
            quantity = new Quantity(DEFAULT_QUANTITY);
            appendWarning(warnings, Optional.of(MESSAGE_QUANTITY_DEFAULTED));
        }

        Product product = new Product(identifierResult.getValue(), nameResult.getValue(), quantity);

        if (!warnings.isEmpty()) {
            return new AddProductCommand(product, warnings.toString());
        }

        return new AddProductCommand(product);
    }

    /**
     * Throws a ParseException if any of the required prefixes are not present in the given
     * {@code ArgumentMultimap} or non-empty preamble.
     */
    private void requireAddProductPrefixes(ArgumentMultimap argMultimap) throws ParseException {
        requirePrefixes(argMultimap,
                new RequiredField(PREFIX_IDENTIFIER, FIELD_IDENTIFIER),
                new RequiredField(PREFIX_NAME, FIELD_PRODUCT_NAME));
        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(MESSAGE_NON_PREFIX_BEFORE_PREFIX + AddProductCommand.MESSAGE_USAGE);
        }
    }

    private static void appendWarning(StringBuilder warnings, Optional<String> warning) {
        warning.ifPresent(w -> {
            if (!warnings.isEmpty()) {
                warnings.append(NEWLINE);
            }
            warnings.append(w);
        });
    }

    private static void requirePrefixes(ArgumentMultimap map, RequiredField... requiredFields) throws ParseException {
        List<RequiredField> missingFields = Arrays.stream(requiredFields)
                .filter(field -> map.getValue(field.prefix).isEmpty())
                .toList();

        if (missingFields.isEmpty()) {
            return;
        }

        if (missingFields.size() == requiredFields.length) {
            throw new ParseException(MESSAGE_ALL_PREFIXES_MISSING + AddProductCommand.MESSAGE_USAGE);
        }

        String missingMessage = missingFields.stream()
                .map(field -> String.format(MESSAGE_MISSING_FIELD_FORMAT, field.prefix, field.name))
                .collect(Collectors.joining(COMMA_SEPARATOR));

        throw new ParseException(MESSAGE_MISSING_PREFIX + missingMessage);
    }
}
