package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_ALL_PREFIXES_MISSING;
import static seedu.address.logic.Messages.MESSAGE_MISSING_FIELD_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_PREFIX;
import static seedu.address.logic.Messages.MESSAGE_NON_PREFIX_BEFORE_PREFIX;
import static seedu.address.logic.commands.CommandTestUtil.IDENTIFIER_DESC_AIRPODS;
import static seedu.address.logic.commands.CommandTestUtil.IDENTIFIER_DESC_IPAD;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_IDENTIFIER_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_IDENTIFIER_DESC_WARN;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_IDENTIFIER_WARN;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PRODUCT_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PRODUCT_NAME_DESC_WARN;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PRODUCT_NAME_WARN;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_QUANTITY_DESC;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.commands.CommandTestUtil.PRODUCT_NAME_DESC_AIRPODS;
import static seedu.address.logic.commands.CommandTestUtil.PRODUCT_NAME_DESC_IPAD;
import static seedu.address.logic.commands.CommandTestUtil.QUANTITY_DESC_AIRPODS;
import static seedu.address.logic.commands.CommandTestUtil.QUANTITY_DESC_IPAD;
import static seedu.address.logic.commands.CommandTestUtil.VALID_IDENTIFIER_IPAD;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PRODUCT_NAME_IPAD;
import static seedu.address.logic.commands.CommandTestUtil.VALID_QUANTITY_IPAD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_IDENTIFIER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.ParserUtil.FIELD_IDENTIFIER;
import static seedu.address.logic.parser.ParserUtil.FIELD_PRODUCT_NAME;
import static seedu.address.logic.parser.ParserUtil.NEWLINE;
import static seedu.address.testutil.TypicalProducts.AIRPODS;
import static seedu.address.testutil.TypicalProducts.IPAD;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddProductCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.product.Identifier;
import seedu.address.model.product.Name;
import seedu.address.model.product.Product;
import seedu.address.model.product.Quantity;
import seedu.address.testutil.ProductBuilder;

public class AddProductCommandParserTest {
    private final AddProductCommandParser parser = new AddProductCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Product expectedProduct = new ProductBuilder(AIRPODS).build();

        assertParseSuccess(parser, PREAMBLE_WHITESPACE + IDENTIFIER_DESC_AIRPODS + PRODUCT_NAME_DESC_AIRPODS
                + QUANTITY_DESC_AIRPODS, new AddProductCommand(expectedProduct));
    }

    @Test
    public void parse_optionalFieldMissing_successWithWarning() throws Exception {
        Product expectedProduct = new ProductBuilder(IPAD).build();

        AddProductCommand result = parser.parse(IDENTIFIER_DESC_IPAD + PRODUCT_NAME_DESC_IPAD);
        assertEquals(new AddProductCommand(expectedProduct), result);
        assertEquals(AddProductCommandParser.MESSAGE_QUANTITY_DEFAULTED, result.getWarnings());
    }

    @Test
    public void parse_missingQuantityWarning_appearsInSuccessMessage() throws Exception {
        Product expectedProduct = new Product(new Identifier(VALID_IDENTIFIER_IPAD),
                new Name(VALID_PRODUCT_NAME_IPAD), new Quantity("0"));
        AddProductCommand command = parser.parse(IDENTIFIER_DESC_IPAD + PRODUCT_NAME_DESC_IPAD);

        String expectedWarnings = AddProductCommandParser.MESSAGE_QUANTITY_DEFAULTED;
        assertEquals(new AddProductCommand(expectedProduct, expectedWarnings),
                new AddProductCommand(expectedProduct, command.getWarnings()));
        assertEquals(expectedWarnings, command.getWarnings());
        assertEquals(1, command.getWarnings().split(NEWLINE).length);
    }

    @Test
    public void parse_compulsoryFieldsMissing_failure() {
        String expectedMessage = AddProductCommand.MESSAGE_USAGE;

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + IDENTIFIER_DESC_IPAD + PRODUCT_NAME_DESC_IPAD,
                MESSAGE_NON_PREFIX_BEFORE_PREFIX + expectedMessage);

        // missing identifier prefix
        assertParseFailure(parser, VALID_IDENTIFIER_IPAD + PRODUCT_NAME_DESC_IPAD,
                MESSAGE_MISSING_PREFIX + String.format(MESSAGE_MISSING_FIELD_FORMAT,
                        PREFIX_IDENTIFIER, FIELD_IDENTIFIER));

        // missing name prefix
        assertParseFailure(parser, IDENTIFIER_DESC_IPAD + VALID_PRODUCT_NAME_IPAD,
                MESSAGE_MISSING_PREFIX + String.format(MESSAGE_MISSING_FIELD_FORMAT, PREFIX_NAME,
                        FIELD_PRODUCT_NAME));

        // all prefixes missing
        assertParseFailure(parser, VALID_IDENTIFIER_IPAD + VALID_PRODUCT_NAME_IPAD,
                MESSAGE_ALL_PREFIXES_MISSING + expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, INVALID_IDENTIFIER_DESC + PRODUCT_NAME_DESC_IPAD + QUANTITY_DESC_IPAD,
                Identifier.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, IDENTIFIER_DESC_IPAD + INVALID_PRODUCT_NAME_DESC + QUANTITY_DESC_IPAD,
                Name.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, IDENTIFIER_DESC_IPAD + PRODUCT_NAME_DESC_IPAD + INVALID_QUANTITY_DESC,
                Quantity.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_repeatedFields_failure() {
        String validArgs = IDENTIFIER_DESC_AIRPODS + PRODUCT_NAME_DESC_AIRPODS + QUANTITY_DESC_AIRPODS;

        assertParseFailure(parser, IDENTIFIER_DESC_IPAD + validArgs,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_IDENTIFIER));

        assertParseFailure(parser, PRODUCT_NAME_DESC_IPAD + validArgs,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
    }

    @Test
    public void parse_softValidationWarnings_success() throws ParseException {
        // invalid identifier and name that trigger warnings but are still accepted by the parser
        Product expectedProduct = new ProductBuilder()
                .withIdentifier(INVALID_IDENTIFIER_WARN)
                .withName(INVALID_PRODUCT_NAME_WARN)
                .withQuantity(VALID_QUANTITY_IPAD)
                .build();

        AddProductCommand expectedCommand = new AddProductCommand(expectedProduct);

        String input = INVALID_IDENTIFIER_DESC_WARN + INVALID_PRODUCT_NAME_DESC_WARN + QUANTITY_DESC_IPAD;

        AddProductCommand result = parser.parse(input);

        assertEquals(expectedCommand, result);

        String expectedWarnings = Identifier.MESSAGE_WARN + NEWLINE
                        + Name.MESSAGE_WARN;

        assertEquals(expectedWarnings, result.getWarnings());
    }
}
