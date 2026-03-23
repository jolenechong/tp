package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;

import seedu.address.logic.commands.FindProductCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.product.ProductNameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindProductCommand object
 */
public class FindProductCommandParser implements Parser<FindProductCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindProductCommand
     * and returns a FindProductCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindProductCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindProductCommand.MESSAGE_USAGE));
        }

        String[] productNameKeywords = trimmedArgs.split("\\s+");

        return new FindProductCommand(
                new ProductNameContainsKeywordsPredicate(Arrays.asList(productNameKeywords)));
    }
}
