package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC_WARN;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_WARN;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC_WARN;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_WARN;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC_LONG;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditCommandParserTest {

    private static final String SPACE_PREFIX_TAG = " " + PREFIX_TAG;
    private static final String TAG_EMPTY = SPACE_PREFIX_TAG;
    private static final String TARGET_EMAIL = VALID_EMAIL_BOB;
    private static final Email TARGET_EMAIL_OBJ = new Email(TARGET_EMAIL);

    private static final String EMPTY_INPUT = "";
    private static final String INVALID_TARGET_EMAIL = "not-an-email";
    private static final String RANDOM_PREAMBLE = " some random string";
    private static final String INVALID_PREFIX_PREAMBLE = " i/ string";
    private static final String SKIP_FLAG = "-y";
    private static final String ATTACHED_SKIP_FLAG_WITH_TRAILING = " -yabc";
    private static final String SKIP_FLAG_WITH_SPACE = "-y ";

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no target email specified
        assertParseFailure(parser, VALID_NAME_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, TARGET_EMAIL, EditCommand.MESSAGE_NOT_EDITED);

        // no target email and no field specified
        assertParseFailure(parser, EMPTY_INPUT, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // invalid target email
        assertParseFailure(parser, INVALID_TARGET_EMAIL + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, TARGET_EMAIL + RANDOM_PREAMBLE, MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, TARGET_EMAIL + INVALID_PREFIX_PREAMBLE, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        // EP: invalid name
        assertParseFailure(parser, TARGET_EMAIL + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS);

        // EP: invalid phone
        assertParseFailure(parser, TARGET_EMAIL + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS);

        // EP: invalid email
        assertParseFailure(parser, TARGET_EMAIL + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS);

        // EP: invalid address
        assertParseFailure(parser, TARGET_EMAIL + INVALID_ADDRESS_DESC, Address.MESSAGE_CONSTRAINTS);

        // EP: invalid tag
        assertParseFailure(parser, TARGET_EMAIL + INVALID_TAG_DESC_LONG,
                Tag.MESSAGE_LENGTH_CONSTRAINTS);

        // invalid phone followed by valid email
        assertParseFailure(parser, TARGET_EMAIL + INVALID_PHONE_DESC + EMAIL_DESC_AMY, Phone.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Person} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser, TARGET_EMAIL + TAG_DESC_FRIEND + TAG_DESC_HUSBAND + TAG_EMPTY,
                Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET_EMAIL + TAG_DESC_FRIEND + TAG_EMPTY + TAG_DESC_HUSBAND,
                Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET_EMAIL + TAG_EMPTY + TAG_DESC_FRIEND + TAG_DESC_HUSBAND,
                Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser,
                TARGET_EMAIL + INVALID_NAME_DESC + INVALID_EMAIL_DESC + VALID_ADDRESS_AMY + VALID_PHONE_AMY,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        String userInput = TARGET_EMAIL + PHONE_DESC_BOB + TAG_DESC_HUSBAND
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + NAME_DESC_AMY + TAG_DESC_FRIEND;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_AMY).withAddress(VALID_ADDRESS_AMY)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();
        EditCommand expectedCommand = new EditCommand(TARGET_EMAIL_OBJ, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        String userInput = TARGET_EMAIL + PHONE_DESC_BOB + EMAIL_DESC_AMY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_AMY).build();
        EditCommand expectedCommand = new EditCommand(TARGET_EMAIL_OBJ, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        String userInput = TARGET_EMAIL + NAME_DESC_AMY;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(TARGET_EMAIL_OBJ, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = TARGET_EMAIL + PHONE_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        expectedCommand = new EditCommand(TARGET_EMAIL_OBJ, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = TARGET_EMAIL + EMAIL_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withEmail(VALID_EMAIL_AMY).build();
        expectedCommand = new EditCommand(TARGET_EMAIL_OBJ, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // address
        userInput = TARGET_EMAIL + ADDRESS_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withAddress(VALID_ADDRESS_AMY).build();
        expectedCommand = new EditCommand(TARGET_EMAIL_OBJ, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = TARGET_EMAIL + TAG_DESC_FRIEND;
        descriptor = new EditPersonDescriptorBuilder().withTags(VALID_TAG_FRIEND).build();
        expectedCommand = new EditCommand(TARGET_EMAIL_OBJ, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddCommandParserTest#parse_repeatedNonTagValue_failure()

        // valid followed by invalid
        String userInput = TARGET_EMAIL + INVALID_PHONE_DESC + PHONE_DESC_BOB;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid followed by valid
        userInput = TARGET_EMAIL + PHONE_DESC_BOB + INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // mulltiple valid fields repeated
        userInput = TARGET_EMAIL + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY
                + TAG_DESC_FRIEND + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY + TAG_DESC_FRIEND
                + PHONE_DESC_BOB + ADDRESS_DESC_BOB + EMAIL_DESC_BOB + TAG_DESC_HUSBAND;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));

        // multiple invalid values
        userInput = TARGET_EMAIL + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC
                + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));
    }

    @Test
    public void parse_resetTags_success() {
        String userInput = TARGET_EMAIL + TAG_EMPTY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(TARGET_EMAIL_OBJ, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_fieldsWithWarnings_success() {
        // EP: only name triggers a warning
        assertParseSuccess(parser, TARGET_EMAIL + INVALID_NAME_DESC_WARN,
                new EditCommand(TARGET_EMAIL_OBJ,
                        new EditPersonDescriptorBuilder().withName(INVALID_NAME_WARN).build()));

        // EP: only phone triggers a warning
        assertParseSuccess(parser, TARGET_EMAIL + INVALID_PHONE_DESC_WARN,
                new EditCommand(TARGET_EMAIL_OBJ,
                        new EditPersonDescriptorBuilder().withPhone(INVALID_PHONE_WARN).build()));

        // EP: both name and phone trigger warnings -> joined with newline
        assertParseSuccess(parser, TARGET_EMAIL + INVALID_NAME_DESC_WARN + INVALID_PHONE_DESC_WARN,
                new EditCommand(TARGET_EMAIL_OBJ,
                        new EditPersonDescriptorBuilder()
                                .withName(INVALID_NAME_WARN)
                                .withPhone(INVALID_PHONE_WARN).build()));
    }

    @Test
    public void parse_resetTagsWithSkipFlag_skipsConfirmationPrompt() {
        // EP: -y flag present -> skips confirmation, tags cleared directly
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();
        assertParseSuccess(parser, SKIP_FLAG_WITH_SPACE + TARGET_EMAIL + TAG_EMPTY,
                new EditCommand(TARGET_EMAIL_OBJ, descriptor, true));
    }
}
