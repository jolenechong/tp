package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SetThresholdCommand;
import seedu.address.model.product.RestockThreshold;

public class SetThresholdCommandParserTest {

    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, SetThresholdCommand.MESSAGE_USAGE);

    private final SetThresholdCommandParser parser = new SetThresholdCommandParser();

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertParseFailure(parser, "", INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, " ", INVALID_FORMAT_MESSAGE);
    }

    @Test
    public void parse_multipleTokens_throwsParseException() {
        assertParseFailure(parser, "5 6", INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, "5 \n 6", INVALID_FORMAT_MESSAGE);
    }

    @Test
    public void parse_invalidThreshold_throwsParseException() {
        assertParseFailure(parser, "-1", RestockThreshold.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "a", RestockThreshold.MESSAGE_CONSTRAINTS);

        // BV: value beyond integer range should be rejected.
        assertParseFailure(parser, "2147483648", RestockThreshold.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_validThreshold_returnsCommand() {
        assertParseSuccess(parser, "5", createCommand("5"));

        // BV: minimum allowed threshold is 0.
        assertParseSuccess(parser, " 0 ", createCommand("0"));

        // BV: maximum integer threshold remains valid.
        assertParseSuccess(parser, "2147483647", createCommand("2147483647"));
    }

    private SetThresholdCommand createCommand(String threshold) {
        return new SetThresholdCommand(new RestockThreshold(threshold));
    }
}
