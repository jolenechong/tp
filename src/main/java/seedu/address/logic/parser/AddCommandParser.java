package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_FIELD_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_PREFIX;
import static seedu.address.logic.Messages.MESSAGE_NON_PREFIX_BEFORE_PREFIX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.ParserUtil.FIELD_ADDRESS;
import static seedu.address.logic.parser.ParserUtil.FIELD_EMAIL;
import static seedu.address.logic.parser.ParserUtil.FIELD_NAME;
import static seedu.address.logic.parser.ParserUtil.FIELD_PHONE;
import static seedu.address.logic.parser.ParserUtil.SEPARATOR_COMMA;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    private record RequiredField(Prefix prefix, String name) {}

    /**
     * Throws a ParseException if any of the required prefixes are not present in the given
     * {@code ArgumentMultimap} or non-empty preamble.
     */
    private void requireAddCommandPrefixes(ArgumentMultimap argMultimap) throws ParseException {
        requirePrefixes(argMultimap,
                new RequiredField(PREFIX_NAME, FIELD_NAME),
                new RequiredField(PREFIX_PHONE, FIELD_PHONE),
                new RequiredField(PREFIX_EMAIL, FIELD_EMAIL),
                new RequiredField(PREFIX_ADDRESS, FIELD_ADDRESS)
        );
        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(MESSAGE_NON_PREFIX_BEFORE_PREFIX + AddCommand.MESSAGE_USAGE);
        }
    }

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG);

        requireAddCommandPrefixes(argMultimap);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);

        StringBuilder warnings = new StringBuilder();
        Person person = parsePerson(argMultimap, warnings);

        if (!warnings.isEmpty()) {
            return new AddCommand(person, warnings.toString());
        }
        return new AddCommand(person);
    }

    private Person parsePerson(ArgumentMultimap argMultimap, StringBuilder warnings) throws ParseException {
        ParseResult<Name> nameResult = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        ParseResult<Phone> phoneResult = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        ParseResult<Email> emailResult = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());
        Set<Tag> tags = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        appendWarning(warnings, nameResult.getWarning());
        appendWarning(warnings, emailResult.getWarning());
        appendWarning(warnings, phoneResult.getWarning());

        return new Person(nameResult.getValue(), phoneResult.getValue(), emailResult.getValue(), address, tags);
    }

    /**
     * Appends the warning to the warnings StringBuilder if the warning is present.
     * Each warning will be separated by a new line.
     */
    private static void appendWarning(StringBuilder warnings, Optional<String> warning) {
        warning.ifPresent(w -> {
            if (!warnings.isEmpty()) {
                warnings.append(ParserUtil.SEPARATOR_NEW_LINE);
            }
            warnings.append(w);
        });
    }

    /**
     * Throws a ParseException if any of the prefixes are missing in the given
     * {@code ArgumentMultimap}.
     */
    private static void requirePrefixes(ArgumentMultimap map, RequiredField... requiredFields)
            throws ParseException {

        List<RequiredField> missingFields = getMissingFields(map, requiredFields);

        if (missingFields.isEmpty()) {
            return;
        }

        if (missingFields.size() == requiredFields.length) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        throw new ParseException(MESSAGE_MISSING_PREFIX + formatMissingFields(missingFields));
    }

    private static List<RequiredField> getMissingFields(ArgumentMultimap map, RequiredField... fields) {
        return Arrays.stream(fields)
                .filter(field -> map.getValue(field.prefix).isEmpty())
                .toList();
    }

    private static String formatMissingFields(List<RequiredField> missingFields) {
        return missingFields.stream()
                .map(f -> String.format(MESSAGE_MISSING_FIELD_FORMAT, f.prefix, f.name))
                .collect(Collectors.joining(SEPARATOR_COMMA));
    }

}
