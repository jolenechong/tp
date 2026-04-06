package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.ConfirmationFlagIndicator.containsConfirmationFlag;
import static seedu.address.logic.parser.ConfirmationFlagIndicator.removeConfirmationFlag;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    public static final String MESSAGE_WRONGLY_FORMED_FLAG =
            "Invalid format. The '-y' flag must be standalone.\n"
                    + "Example: edit -y <target_email> t/";

    public static final String CONFIRMATION_INDICATOR = "-y";

    public static final int TOKEN_MIN_LENGTH = 1;

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG);

        Email email;
        String[] preambleTokens = argMultimap.getPreamble().trim().split(ParserUtil.SEPARATOR_SPACE);
        boolean needsConfirmation = !containsConfirmationFlag(preambleTokens, CONFIRMATION_INDICATOR, TOKEN_MIN_LENGTH);
        String emailBeforeParsed;
        if (!needsConfirmation) {
            emailBeforeParsed = removeConfirmationFlag(preambleTokens, CONFIRMATION_INDICATOR);
        } else {
            emailBeforeParsed = String.join(" ", preambleTokens);
        }

        try {
            email = ParserUtil.parseEmail(emailBeforeParsed).getValue();
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);

        StringBuilder warnings = new StringBuilder();
        EditPersonDescriptor editPersonDescriptor = parsePerson(argMultimap, warnings);

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        if (!warnings.isEmpty()) {
            return new EditCommand(email, editPersonDescriptor, warnings.toString(), needsConfirmation);
        }

        return new EditCommand(email, editPersonDescriptor, needsConfirmation);
    }

    private EditPersonDescriptor parsePerson(ArgumentMultimap argMultimap, StringBuilder warnings)
            throws ParseException {
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            ParseResult<Name> nameResult = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
            appendWarning(warnings, nameResult.getWarning());
            editPersonDescriptor.setName(nameResult.getValue());
        }
        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            ParseResult<Phone> phoneResult = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
            appendWarning(warnings, phoneResult.getWarning());
            editPersonDescriptor.setPhone(phoneResult.getValue());
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            ParseResult<Email> emailResult = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
            appendWarning(warnings, emailResult.getWarning());
            editPersonDescriptor.setEmail(emailResult.getValue());
        }
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            editPersonDescriptor.setAddress(ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get()));
        }
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editPersonDescriptor::setTags);

        return editPersonDescriptor;
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
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }

}
