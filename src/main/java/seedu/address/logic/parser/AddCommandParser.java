package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_MISSING_FIELD_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_PREFIX;
import static seedu.address.logic.Messages.MESSAGE_NON_PREFIX_BEFORE_PREFIX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.ParserConstants.COMMA_SEPARATOR;
import static seedu.address.logic.parser.ParserConstants.FIELD_ADDRESS;
import static seedu.address.logic.parser.ParserConstants.FIELD_EMAIL;
import static seedu.address.logic.parser.ParserConstants.FIELD_NAME;
import static seedu.address.logic.parser.ParserConstants.FIELD_PHONE;

import java.util.Set;

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
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG);

        requirePrefixes(argMultimap,
                new RequiredField(PREFIX_NAME, FIELD_NAME),
                new RequiredField(PREFIX_PHONE, FIELD_PHONE),
                new RequiredField(PREFIX_EMAIL, FIELD_EMAIL),
                new RequiredField(PREFIX_ADDRESS, FIELD_ADDRESS)
        );

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(MESSAGE_NON_PREFIX_BEFORE_PREFIX);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        Person person = new Person(name, phone, email, address, tagList);

        return new AddCommand(person);
    }

    /**
     * Throws a ParseException if any of the prefixes are missing in the given
     * {@code ArgumentMultimap}.
     */
    private static void requirePrefixes(ArgumentMultimap map,
                                        RequiredField... requiredFields) throws ParseException {
        StringBuilder missing = new StringBuilder();

        for (RequiredField field : requiredFields) {
            if (map.getValue(field.prefix).isEmpty()) {
                if (!missing.isEmpty()) {
                    missing.append(COMMA_SEPARATOR);
                }
                missing.append(String.format(MESSAGE_MISSING_FIELD_FORMAT, field.prefix, field.name));
            }
        }
        if (!missing.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_PREFIX + missing);
        }

    }

}
