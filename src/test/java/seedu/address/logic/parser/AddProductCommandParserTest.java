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
    private static final int CUSTOM_DEFAULT_THRESHOLD = 45;
    private static final String VALID_ALL_FIELDS_AIRPODS = IDENTIFIER_DESC_AIRPODS + PRODUCT_NAME_DESC_AIRPODS
            + QUANTITY_DESC_AIRPODS + THRESHOLD_DESC_AIRPODS + EMAIL_DESC_BOB;
    private static final String VALID_REQUIRED_FIELDS_IPAD = IDENTIFIER_DESC_IPAD + PRODUCT_NAME_DESC_IPAD;
    private static final String VALID_IPAD_WITH_THRESHOLD_AND_EMAIL = IDENTIFIER_DESC_IPAD + PRODUCT_NAME_DESC_IPAD
            + THRESHOLD_DESC_IPAD + EMAIL_DESC_AMY;
    private static final String VALID_IPAD_WITH_QUANTITY_AND_EMAIL = IDENTIFIER_DESC_IPAD + PRODUCT_NAME_DESC_IPAD
            + QUANTITY_DESC_IPAD + EMAIL_DESC_AMY;
    private static final String VALID_IPAD_WITH_QUANTITY_AND_THRESHOLD = IDENTIFIER_DESC_IPAD + PRODUCT_NAME_DESC_IPAD
            + QUANTITY_DESC_IPAD + THRESHOLD_DESC_IPAD;

    private final AddProductCommandParser parser = new AddProductCommandParser(() -> DEFAULT_RESTOCK_THRESHOLD_VALUE);

    @Test
    public void parse_allFieldsPresent_success() {
        assertParseSuccessWithProduct(parser, VALID_ALL_FIELDS_AIRPODS, new ProductBuilder(AIRPODS).build());
    }

    @Test
    public void parse_whitespacePreamble_success() {
        // BV: whitespace-only preamble is accepted and should not affect parsing.
        assertParseSuccessWithProduct(parser, PREAMBLE_WHITESPACE + VALID_ALL_FIELDS_AIRPODS,
                new ProductBuilder(AIRPODS).build());
    }

    @Test
    public void parse_someOptionalPrefixesMissing_success() {
        // EP: only required fields present should use defaults and still succeed.
        assertParseSuccessWithProduct(parser, VALID_REQUIRED_FIELDS_IPAD, new ProductBuilder(IPAD).build());
    }

    @Test
    public void parse_quantityPrefixMissing_warningMessage() throws Exception {
        assertWarnings(parser, VALID_IPAD_WITH_THRESHOLD_AND_EMAIL,
                AddProductCommandParser.MESSAGE_QUANTITY_DEFAULTED);
    }

    @Test
    public void parse_thresholdPrefixMissing_warningMessage() throws Exception {
        assertWarnings(parser, VALID_IPAD_WITH_QUANTITY_AND_EMAIL,
                String.format(AddProductCommandParser.MESSAGE_THRESHOLD_DEFAULTED,
                        DEFAULT_RESTOCK_THRESHOLD_VALUE));
    }

    @Test
    public void parse_missingThreshold_usesConfiguredDefault() throws Exception {
        // EP: injected threshold supplier should control default threshold when t/ is omitted.
        Product expectedProduct = new ProductBuilder(AIRPODS).build();
        AddProductCommand expectedCommand = new AddProductCommand(expectedProduct);

        AddProductCommandParser parserWithConfiguredDefault =
                new AddProductCommandParser(() -> CUSTOM_DEFAULT_THRESHOLD);
        AddProductCommand command = parserWithConfiguredDefault.parse(
                IDENTIFIER_DESC_AIRPODS + PRODUCT_NAME_DESC_AIRPODS + QUANTITY_DESC_AIRPODS + EMAIL_DESC_BOB);

        assertEquals(expectedCommand, command);
    }

    @Test
    public void parse_emailPrefixMissing_warningMessage() throws Exception {
        assertWarnings(parser, VALID_IPAD_WITH_QUANTITY_AND_THRESHOLD,
                AddProductCommandParser.MESSAGE_VENDOR_EMAIL_MISSING);
    }

    @Test
    public void parse_allOptionalPrefixesMissing_warningMessage() throws Exception {
        String expectedWarnings = AddProductCommandParser.MESSAGE_QUANTITY_DEFAULTED + SEPARATOR_NEW_LINE
                + String.format(AddProductCommandParser.MESSAGE_THRESHOLD_DEFAULTED,
                DEFAULT_RESTOCK_THRESHOLD_VALUE) + SEPARATOR_NEW_LINE
                + AddProductCommandParser.MESSAGE_VENDOR_EMAIL_MISSING;
        assertWarnings(parser, VALID_REQUIRED_FIELDS_IPAD, expectedWarnings);
    }

    @Test
    public void parse_identifierPrefixMissing_failure() {
        assertParseFailure(parser, VALID_IDENTIFIER_IPAD + PRODUCT_NAME_DESC_IPAD,
                getMissingPrefixMessage(PREFIX_IDENTIFIER, FIELD_IDENTIFIER));
    }

    @Test
    public void parse_namePrefixMissing_failure() {
        assertParseFailure(parser, IDENTIFIER_DESC_IPAD + VALID_PRODUCT_NAME_IPAD,
                getMissingPrefixMessage(PREFIX_NAME, FIELD_PRODUCT_NAME));
    }

    @Test
    public void parse_allRequiredPrefixesMissing_failure() {
        // BV: when all required prefixes are absent, parser should return generic usage error.
        assertParseFailure(parser, VALID_IDENTIFIER_IPAD + VALID_PRODUCT_NAME_IPAD,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
    }

    @Test
    public void parse_nonEmptyPreamble_failure() {
        // EP: non-prefix preamble belongs to invalid partition and should be rejected.
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + IDENTIFIER_DESC_IPAD + PRODUCT_NAME_DESC_IPAD,
                MESSAGE_NON_PREFIX_BEFORE_PREFIX + MESSAGE_USAGE);
    }

    @Test
    public void parse_duplicatedPrefixes_failure() {
        // EP: each duplicated prefix should fail with precise duplicate-prefix error message.
        assertDuplicatePrefixFailure(IDENTIFIER_DESC_IPAD, PREFIX_IDENTIFIER);
        assertDuplicatePrefixFailure(PRODUCT_NAME_DESC_IPAD, PREFIX_NAME);
        assertDuplicatePrefixFailure(QUANTITY_DESC_IPAD, PREFIX_QUANTITY);
        assertDuplicatePrefixFailure(THRESHOLD_DESC_IPAD, PREFIX_THRESHOLD);
        assertDuplicatePrefixFailure(EMAIL_DESC_BOB, PREFIX_EMAIL);
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
        // EP: soft-validated fields should parse and surface warnings instead of hard failure.
        String expectedWarnings = Identifier.MESSAGE_WARN + SEPARATOR_NEW_LINE + Name.MESSAGE_WARN;
        assertWarnings(parser,
                INVALID_IDENTIFIER_DESC_WARN + INVALID_PRODUCT_NAME_DESC_WARN + QUANTITY_DESC_IPAD
                        + THRESHOLD_DESC_IPAD + EMAIL_DESC_AMY,
                expectedWarnings);
    }

    private static void assertParseSuccessWithProduct(AddProductCommandParser parser,
                                                      String userInput,
                                                      Product expectedProduct) {
        assertParseSuccess(parser, userInput, new AddProductCommand(expectedProduct));
    }

    private static void assertWarnings(AddProductCommandParser parser,
                                       String userInput,
                                       String expectedWarnings) throws ParseException {
        AddProductCommand command = parser.parse(userInput);
        assertEquals(expectedWarnings, command.getWarnings());
    }

    private static String getMissingPrefixMessage(Prefix prefix, String fieldName) {
        return MESSAGE_MISSING_PREFIX + String.format(MESSAGE_MISSING_FIELD_FORMAT, prefix, fieldName);
    }

    private void assertDuplicatePrefixFailure(String duplicatedPrefixArgs, Prefix duplicatedPrefix) {
        assertParseFailure(parser, duplicatedPrefixArgs + VALID_ALL_FIELDS_AIRPODS,
                Messages.getErrorMessageForDuplicatePrefixes(duplicatedPrefix));
    }
}
