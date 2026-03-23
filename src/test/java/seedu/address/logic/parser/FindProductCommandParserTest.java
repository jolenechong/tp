package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindProductCommand;
import seedu.address.model.product.ProductNameContainsKeywordsPredicate;

public class FindProductCommandParserTest {

    private FindProductCommandParser parser = new FindProductCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                FindProductCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindProductCommand expectedFindCommand =
                new FindProductCommand(new ProductNameContainsKeywordsPredicate(Arrays.asList("Motherboard", "ssD")));
        assertParseSuccess(parser, "Motherboard ssD", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Motherboard \n \t ssD  \t", expectedFindCommand);
    }
}
