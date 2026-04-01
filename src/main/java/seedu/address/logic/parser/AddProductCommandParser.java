package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_FIELD_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_PREFIX;
import static seedu.address.logic.Messages.MESSAGE_NON_PREFIX_BEFORE_PREFIX;
import static seedu.address.logic.commands.AddProductCommand.MESSAGE_USAGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_IDENTIFIER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_QUANTITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_THRESHOLD;
import static seedu.address.logic.parser.ParserUtil.FIELD_IDENTIFIER;
import static seedu.address.logic.parser.ParserUtil.FIELD_PRODUCT_NAME;
import static seedu.address.logic.parser.ParserUtil.SEPARATOR_COMMA;
import static seedu.address.logic.parser.ParserUtil.SEPARATOR_NEW_LINE;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

import seedu.address.logic.commands.AddProductCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;
import seedu.address.model.product.Identifier;
import seedu.address.model.product.Name;
import seedu.address.model.product.Product;
import seedu.address.model.product.Quantity;
import seedu.address.model.product.RestockThreshold;

/**
 * This class parses input arguments and creates a new {@code AddProductCommand} object.
 */
public class AddProductCommandParser implements Parser<AddProductCommand> {

    public static final String DEFAULT_QUANTITY = "0";
    public static final String MESSAGE_QUANTITY_DEFAULTED =
            "⚠ Warning: Quantity missing, defaulted to 0.";
    public static final String MESSAGE_THRESHOLD_DEFAULTED =
            "⚠ Warning: Restock threshold missing, defaulted to %1$s.";
    public static final String MESSAGE_VENDOR_EMAIL_MISSING =
            "⚠ Warning: Vendor email missing, product will not be associated with a vendor.";

    private final IntSupplier defaultThresholdSupplier;

    public AddProductCommandParser(IntSupplier defaultThresholdSupplier) {
        this.defaultThresholdSupplier = defaultThresholdSupplier;
    }

    private record RequiredField(Prefix prefix, String name) {
    }

    /**
     * Parses the given {@code String} of arguments in the context of AddProductCommand.
     *
     * @return {@code AddProductCommand} object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddProductCommand parse(String args) throws ParseException {
        ArgumentMultimap map = ArgumentTokenizer.tokenize(
                args, PREFIX_IDENTIFIER, PREFIX_NAME, PREFIX_QUANTITY, PREFIX_THRESHOLD, PREFIX_EMAIL);
        requireOnlyAddProductPrefixes(map);
        // From here on, map is guaranteed to contain all required prefixes.
        map.verifyNoDuplicatePrefixesFor(
                PREFIX_IDENTIFIER, PREFIX_NAME, PREFIX_QUANTITY, PREFIX_THRESHOLD, PREFIX_EMAIL);

        StringBuilder warnings = new StringBuilder();

        ParseResult<Identifier> identifierResult = ParserUtil.parseIdentifier(
                map.getValue(PREFIX_IDENTIFIER).get());
        Identifier identifier = identifierResult.getValue();
        appendWarning(warnings, identifierResult.getWarning());

        ParseResult<Name> nameResult = ParserUtil.parseProductName(map.getValue(PREFIX_NAME).get());
        Name name = nameResult.getValue();
        appendWarning(warnings, nameResult.getWarning());

        Quantity quantity;
        Optional<String> quantityString = map.getValue(PREFIX_QUANTITY);
        if (quantityString.isPresent()) {
            quantity = ParserUtil.parseQuantity(quantityString.get()).getValue();
        } else {
            quantity = new Quantity(DEFAULT_QUANTITY);
            appendWarning(warnings, Optional.of(MESSAGE_QUANTITY_DEFAULTED));
        }

        RestockThreshold threshold;
        Optional<String> thresholdString = map.getValue(PREFIX_THRESHOLD);
        if (thresholdString.isPresent()) {
            threshold = ParserUtil.parseThreshold(thresholdString.get()).getValue();
        } else {
            int configuredDefaultThreshold = defaultThresholdSupplier.getAsInt();
            threshold = new RestockThreshold(configuredDefaultThreshold);
            appendWarning(warnings,
                    Optional.of(String.format(MESSAGE_THRESHOLD_DEFAULTED, configuredDefaultThreshold)));
        }

        Email vendorEmail = null;
        Optional<String> vendorEmailValue = map.getValue(PREFIX_EMAIL);
        if (vendorEmailValue.isPresent()) {
            ParseResult<Email> vendorEmailResult = ParserUtil.parseEmail(vendorEmailValue.get());
            vendorEmail = vendorEmailResult.getValue();
            appendWarning(warnings, vendorEmailResult.getWarning());
        } else {
            appendWarning(warnings, Optional.of(MESSAGE_VENDOR_EMAIL_MISSING));
        }

        Product product = new Product(identifier, name, quantity, threshold, vendorEmail);
        if (!warnings.isEmpty()) {
            return new AddProductCommand(product, warnings.toString());
        }
        return new AddProductCommand(product);
    }

    /**
     * Throws a {@code ParseException} if some required prefixes are missing, or if non-prefixes are present.
     *
     * @param map {@code ArgumentMultimap} containing the arguments.
     */
    private void requireOnlyAddProductPrefixes(ArgumentMultimap map) throws ParseException {
        requirePrefixes(map,
                new RequiredField(PREFIX_IDENTIFIER, FIELD_IDENTIFIER),
                new RequiredField(PREFIX_NAME, FIELD_PRODUCT_NAME));

        if (!map.getPreamble().isEmpty()) {
            throw new ParseException(MESSAGE_NON_PREFIX_BEFORE_PREFIX + MESSAGE_USAGE);
        }
    }

    private static void appendWarning(StringBuilder warnings, Optional<String> warning) {
        warning.ifPresent(w -> {
            if (!warnings.isEmpty()) {
                warnings.append(SEPARATOR_NEW_LINE);
            }
            warnings.append(w);
        });
    }

    /**
     * Throws a {@code ParseException} if some required prefixes are missing.
     *
     * @param map {@code ArgumentMultimap} containing the arguments.
     * @param requiredFields One or more {@code RequiredField}(s) to check for.
     */
    private static void requirePrefixes(ArgumentMultimap map, RequiredField... requiredFields) throws ParseException {
        List<RequiredField> missingFields = Arrays.stream(requiredFields)
                .filter(field -> map.getValue(field.prefix).isEmpty())
                .toList();

        if (missingFields.isEmpty()) {
            return;
        }

        if (missingFields.size() == requiredFields.length) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }

        String missingMessage = missingFields.stream()
                .map(field -> String.format(MESSAGE_MISSING_FIELD_FORMAT, field.prefix, field.name))
                .collect(Collectors.joining(SEPARATOR_COMMA));
        throw new ParseException(MESSAGE_MISSING_PREFIX + missingMessage);
    }
}
