package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_FIELD_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_PREFIX;
import static seedu.address.logic.Messages.MESSAGE_NON_PREFIX_BEFORE_PREFIX;
import static seedu.address.logic.commands.AddProductCommand.MESSAGE_USAGE;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.IDENTIFIER_DESC_AIRPODS;
import static seedu.address.logic.commands.CommandTestUtil.IDENTIFIER_DESC_IPAD;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_IDENTIFIER_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_IDENTIFIER_DESC_WARN;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PRODUCT_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PRODUCT_NAME_DESC_WARN;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_QUANTITY_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_THRESHOLD_DESC;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.commands.CommandTestUtil.PRODUCT_NAME_DESC_AIRPODS;
import static seedu.address.logic.commands.CommandTestUtil.PRODUCT_NAME_DESC_IPAD;
import static seedu.address.logic.commands.CommandTestUtil.QUANTITY_DESC_AIRPODS;
import static seedu.address.logic.commands.CommandTestUtil.QUANTITY_DESC_IPAD;
import static seedu.address.logic.commands.CommandTestUtil.THRESHOLD_DESC_AIRPODS;
import static seedu.address.logic.commands.CommandTestUtil.THRESHOLD_DESC_IPAD;
import static seedu.address.logic.commands.CommandTestUtil.VALID_IDENTIFIER_IPAD;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PRODUCT_NAME_IPAD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_IDENTIFIER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_QUANTITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_THRESHOLD;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.ParserUtil.FIELD_IDENTIFIER;
import static seedu.address.logic.parser.ParserUtil.FIELD_PRODUCT_NAME;
import static seedu.address.logic.parser.ParserUtil.SEPARATOR_NEW_LINE;
import static seedu.address.model.UserPrefs.DEFAULT_RESTOCK_THRESHOLD_VALUE;
import static seedu.address.testutil.TypicalProducts.AIRPODS;
import static seedu.address.testutil.TypicalProducts.IPAD;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddProductCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;
import seedu.address.model.product.Identifier;
import seedu.address.model.product.Name;
import seedu.address.model.product.Product;
import seedu.address.model.product.Quantity;
import seedu.address.model.product.RestockThreshold;
import seedu.address.testutil.ProductBuilder;

public class AddProductCommandParserTest {
    private final AddProductCommandParser parser = new AddProductCommandParser(() -> DEFAULT_RESTOCK_THRESHOLD_VALUE);

    @Test
    public void parse_allFieldsPresent_success() {
        Product expectedProduct = new ProductBuilder(AIRPODS).build();
        AddProductCommand expectedCommand = new AddProductCommand(expectedProduct);
        String userInput = IDENTIFIER_DESC_AIRPODS + PRODUCT_NAME_DESC_AIRPODS + QUANTITY_DESC_AIRPODS
                + THRESHOLD_DESC_AIRPODS + EMAIL_DESC_BOB;

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_whitespacePreamble_success() {
        Product expectedProduct = new ProductBuilder(AIRPODS).build();
        AddProductCommand expectedCommand = new AddProductCommand(expectedProduct);
        String userInput = PREAMBLE_WHITESPACE + IDENTIFIER_DESC_AIRPODS + PRODUCT_NAME_DESC_AIRPODS
                + QUANTITY_DESC_AIRPODS + THRESHOLD_DESC_AIRPODS + EMAIL_DESC_BOB;

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someOptionalPrefixesMissing_success() {
        Product expectedProduct = new ProductBuilder(IPAD).build();
        AddProductCommand expectedCommand = new AddProductCommand(expectedProduct);
        String userInput = IDENTIFIER_DESC_IPAD + PRODUCT_NAME_DESC_IPAD;

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_quantityPrefixMissing_warningMessage() throws Exception {
        String expectedWarning = AddProductCommandParser.MESSAGE_QUANTITY_DEFAULTED;
        AddProductCommand command = parser.parse(
                IDENTIFIER_DESC_IPAD + PRODUCT_NAME_DESC_IPAD + THRESHOLD_DESC_IPAD + EMAIL_DESC_AMY);

        assertEquals(expectedWarning, command.getWarnings());
    }

    @Test
    public void parse_thresholdPrefixMissing_warningMessage() throws Exception {
        String expectedWarning = String.format(AddProductCommandParser.MESSAGE_THRESHOLD_DEFAULTED, 0);
        AddProductCommand command = parser.parse(
                IDENTIFIER_DESC_IPAD + PRODUCT_NAME_DESC_IPAD + QUANTITY_DESC_IPAD + EMAIL_DESC_AMY);

        assertEquals(expectedWarning, command.getWarnings());
    }

    @Test
    public void parse_missingThreshold_usesConfiguredDefault() throws Exception {
        Product expectedProduct = new ProductBuilder(AIRPODS).build();
        AddProductCommand expectedCommand = new AddProductCommand(expectedProduct);

        AddProductCommandParser parserWithConfiguredDefault = new AddProductCommandParser(() -> 45);
        AddProductCommand command = parserWithConfiguredDefault.parse(
                IDENTIFIER_DESC_AIRPODS + PRODUCT_NAME_DESC_AIRPODS + QUANTITY_DESC_AIRPODS + EMAIL_DESC_BOB);

        assertEquals(expectedCommand, command);
    }

    @Test
    public void parse_emailPrefixMissing_warningMessage() throws Exception {
        String expectedWarning = AddProductCommandParser.MESSAGE_VENDOR_EMAIL_MISSING;
        AddProductCommand command = parser.parse(
                IDENTIFIER_DESC_IPAD + PRODUCT_NAME_DESC_IPAD + QUANTITY_DESC_IPAD + THRESHOLD_DESC_IPAD);

        assertEquals(expectedWarning, command.getWarnings());
    }

    @Test
    public void parse_allOptionalPrefixesMissing_warningMessage() throws Exception {
        String expectedWarnings = AddProductCommandParser.MESSAGE_QUANTITY_DEFAULTED + SEPARATOR_NEW_LINE
                + String.format(AddProductCommandParser.MESSAGE_THRESHOLD_DEFAULTED, 0) + SEPARATOR_NEW_LINE
                + AddProductCommandParser.MESSAGE_VENDOR_EMAIL_MISSING;
        AddProductCommand command = parser.parse(IDENTIFIER_DESC_IPAD + PRODUCT_NAME_DESC_IPAD);

        assertEquals(expectedWarnings, command.getWarnings());
    }

    @Test
    public void parse_identifierPrefixMissing_failure() {
        String userInput = VALID_IDENTIFIER_IPAD + PRODUCT_NAME_DESC_IPAD;
        String expectedMessage = MESSAGE_MISSING_PREFIX + String.format(MESSAGE_MISSING_FIELD_FORMAT, PREFIX_IDENTIFIER,
                FIELD_IDENTIFIER);

        assertParseFailure(parser, userInput, expectedMessage);
    }

    @Test
    public void parse_namePrefixMissing_failure() {
        String userInput = IDENTIFIER_DESC_IPAD + VALID_PRODUCT_NAME_IPAD;
        String expectedMessage = MESSAGE_MISSING_PREFIX + String.format(MESSAGE_MISSING_FIELD_FORMAT, PREFIX_NAME,
                FIELD_PRODUCT_NAME);

        assertParseFailure(parser, userInput, expectedMessage);
    }

    @Test
    public void parse_allRequiredPrefixesMissing_failure() {
        String userInput = VALID_IDENTIFIER_IPAD + VALID_PRODUCT_NAME_IPAD;
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE);

        assertParseFailure(parser, userInput, expectedMessage);
    }

    @Test
    public void parse_nonEmptyPreamble_failure() {
        String userInput = PREAMBLE_NON_EMPTY + IDENTIFIER_DESC_IPAD + PRODUCT_NAME_DESC_IPAD;
        String expectedMessage = MESSAGE_NON_PREFIX_BEFORE_PREFIX + MESSAGE_USAGE;

        assertParseFailure(parser, userInput, expectedMessage);
    }

    @Test
    public void parse_duplicatedPrefixes_failure() {
        String validArgs = IDENTIFIER_DESC_AIRPODS + PRODUCT_NAME_DESC_AIRPODS + QUANTITY_DESC_AIRPODS
                + THRESHOLD_DESC_AIRPODS + EMAIL_DESC_BOB;

        assertParseFailure(parser, IDENTIFIER_DESC_IPAD + validArgs,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_IDENTIFIER));

        assertParseFailure(parser, PRODUCT_NAME_DESC_IPAD + validArgs,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        assertParseFailure(parser, QUANTITY_DESC_IPAD + validArgs,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_QUANTITY));

        assertParseFailure(parser, THRESHOLD_DESC_IPAD + validArgs,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_THRESHOLD));

        assertParseFailure(parser, EMAIL_DESC_BOB + validArgs,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));
    }

    @Test
    public void parse_invalidFieldValue_failure() {
        assertParseFailure(parser, INVALID_IDENTIFIER_DESC + PRODUCT_NAME_DESC_IPAD + QUANTITY_DESC_IPAD,
                Identifier.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, IDENTIFIER_DESC_IPAD + INVALID_PRODUCT_NAME_DESC + QUANTITY_DESC_IPAD,
                Name.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, IDENTIFIER_DESC_IPAD + PRODUCT_NAME_DESC_IPAD + INVALID_QUANTITY_DESC,
                Quantity.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, IDENTIFIER_DESC_IPAD + PRODUCT_NAME_DESC_IPAD + QUANTITY_DESC_IPAD
                + INVALID_THRESHOLD_DESC, RestockThreshold.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, IDENTIFIER_DESC_IPAD + PRODUCT_NAME_DESC_IPAD + QUANTITY_DESC_IPAD
                + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_softValidatedFields_warningMessage() throws ParseException {
        String expectedWarnings = Identifier.MESSAGE_WARN + SEPARATOR_NEW_LINE + Name.MESSAGE_WARN;
        AddProductCommand command = parser.parse(
                INVALID_IDENTIFIER_DESC_WARN + INVALID_PRODUCT_NAME_DESC_WARN + QUANTITY_DESC_IPAD
                + THRESHOLD_DESC_IPAD + EMAIL_DESC_AMY);

        assertEquals(expectedWarnings, command.getWarnings());
    }
}
