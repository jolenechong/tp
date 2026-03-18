package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PRODUCT_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_QUANTITY_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_THRESHOLD_DESC;
import static seedu.address.logic.commands.CommandTestUtil.PRODUCT_NAME_DESC_AIRPODS;
import static seedu.address.logic.commands.CommandTestUtil.PRODUCT_NAME_DESC_IPAD;
import static seedu.address.logic.commands.CommandTestUtil.QUANTITY_DESC_AIRPODS;
import static seedu.address.logic.commands.CommandTestUtil.QUANTITY_DESC_IPAD;
import static seedu.address.logic.commands.CommandTestUtil.THRESHOLD_DESC_AIRPODS;
import static seedu.address.logic.commands.CommandTestUtil.THRESHOLD_DESC_IPAD;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PRODUCT_NAME_IPAD;
import static seedu.address.logic.commands.CommandTestUtil.VALID_QUANTITY_IPAD;
import static seedu.address.logic.commands.CommandTestUtil.VALID_THRESHOLD_IPAD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_QUANTITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_THRESHOLD;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditProductCommand;
import seedu.address.model.person.Email;
import seedu.address.model.product.Identifier;
import seedu.address.model.product.Name;
import seedu.address.model.product.Quantity;
import seedu.address.model.product.RestockThreshold;
import seedu.address.testutil.EditProductDescriptorBuilder;

public class EditProductCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditProductCommand.MESSAGE_USAGE);

    private final EditProductCommandParser parser = new EditProductCommandParser();

    @Test
    public void parse_allFieldsSpecified_success() {
        String userInput = "SKU-1001" + PRODUCT_NAME_DESC_IPAD + QUANTITY_DESC_IPAD + THRESHOLD_DESC_IPAD
                + " e/" + VALID_EMAIL_AMY;

        EditProductCommand.EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withName(VALID_PRODUCT_NAME_IPAD)
                .withQuantity(VALID_QUANTITY_IPAD)
                .withThreshold(VALID_THRESHOLD_IPAD)
                .withVendorEmail(VALID_EMAIL_AMY)
                .build();
        EditProductCommand expectedCommand = new EditProductCommand("SKU-1001", descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        String userInput = "SKU-1001" + QUANTITY_DESC_AIRPODS;

        EditProductCommand.EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withQuantity("45")
                .build();
        EditProductCommand expectedCommand = new EditProductCommand("SKU-1001", descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_clearVendorEmail_success() {
        String userInput = "SKU-1001 e/";

        EditProductCommand.EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withoutVendorEmail()
                .build();
        EditProductCommand expectedCommand = new EditProductCommand("SKU-1001", descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingIdentifier_failure() {
        assertParseFailure(parser, PRODUCT_NAME_DESC_IPAD, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_noFieldsSpecified_failure() {
        assertParseFailure(parser, "SKU-1001", EditProductCommand.MESSAGE_NOT_EDITED);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "SKU-1001" + INVALID_PRODUCT_NAME_DESC, Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "SKU-1001" + INVALID_QUANTITY_DESC, Quantity.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "SKU-1001" + INVALID_THRESHOLD_DESC, RestockThreshold.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "SKU-1001" + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_repeatedFields_failure() {
        assertParseFailure(parser, "SKU-1001" + PRODUCT_NAME_DESC_IPAD + PRODUCT_NAME_DESC_AIRPODS,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, "SKU-1001" + QUANTITY_DESC_IPAD + QUANTITY_DESC_AIRPODS,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_QUANTITY));
        assertParseFailure(parser, "SKU-1001" + THRESHOLD_DESC_IPAD + THRESHOLD_DESC_AIRPODS,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_THRESHOLD));
        assertParseFailure(parser, "SKU-1001 e/" + VALID_EMAIL_AMY + " e/" + VALID_EMAIL_AMY,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));
    }

    @Test
    public void parse_identifierSpecified_success() throws Exception {
        String userInput = "SKU-1001 id/NEW-ID";

        EditProductCommandParser parser = new EditProductCommandParser();
        EditProductCommand command = (EditProductCommand) parser.parse(userInput);

        EditProductCommand.EditProductDescriptor expectedDescriptor =
                new EditProductCommand.EditProductDescriptor();
        expectedDescriptor.setIdentifier(new Identifier("NEW-ID"));

        EditProductCommand expectedCommand =
                new EditProductCommand("SKU-1001", expectedDescriptor);

        assertEquals(expectedCommand, command);
    }

    @Test
    public void parse_identifierAndNameSpecified_success() throws Exception {
        String userInput = "SKU-1001 id/NEW-ID n/iPad";

        EditProductCommandParser parser = new EditProductCommandParser();
        EditProductCommand command = (EditProductCommand) parser.parse(userInput);

        EditProductCommand.EditProductDescriptor expectedDescriptor =
                new EditProductCommand.EditProductDescriptor();
        expectedDescriptor.setIdentifier(new Identifier("NEW-ID"));
        expectedDescriptor.setName(new Name("iPad"));

        EditProductCommand expectedCommand =
                new EditProductCommand("SKU-1001", expectedDescriptor);

        assertEquals(expectedCommand, command);
    }
}
