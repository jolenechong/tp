package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsScoredPredicate;
import seedu.address.model.person.PersonTagContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {
    private static final String TAG_MODE_FLAG = "-t";
    private static final String ESCAPED_TAG_MODE_FLAG = "/-t";

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        List<String> rawTokens = Arrays.asList(trimmedArgs.split("\\s+"));
        boolean isTagMode = rawTokens.contains(TAG_MODE_FLAG);

        if (isTagMode) {
            List<String> tagKeywords = rawTokens.stream()
                    .filter(token -> !TAG_MODE_FLAG.equals(token))
                    .map(this::toKeyword)
                    .collect(Collectors.toList());

            if (tagKeywords.isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
            }

            return new FindCommand(new PersonTagContainsKeywordsPredicate(tagKeywords));
        }

        List<String> nameKeywords = rawTokens.stream()
                .map(this::toKeyword)
                .collect(Collectors.toList());

        return new FindCommand(new NameContainsKeywordsScoredPredicate(nameKeywords));
    }

    private String toKeyword(String token) {
        if (ESCAPED_TAG_MODE_FLAG.equals(token)) {
            return TAG_MODE_FLAG;
        }
        return token;
    }

}
