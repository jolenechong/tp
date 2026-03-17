package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        String[] preambleTokens = argMultimap.getPreamble().trim().split("\\s+");
        boolean needsConfirmation = !checkConfirmationIndicator(preambleTokens);
        String emailBeforeParsed = removeConfirmationIndicator(preambleTokens);

        try {
            email = ParserUtil.parseEmail(emailBeforeParsed).getValue();
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        StringBuilder warnings = new StringBuilder();

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

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        if (!warnings.isEmpty()) {
            return new EditCommand(email, editPersonDescriptor, warnings.toString(), needsConfirmation);
        }

        return new EditCommand(email, editPersonDescriptor, needsConfirmation);
    }

    private boolean checkConfirmationIndicator(String[] tokens) throws ParseException {
        boolean hasWronglyFormedFlag = Arrays.stream(tokens)
                .anyMatch(t -> t.startsWith(CONFIRMATION_INDICATOR) && !t.equals(CONFIRMATION_INDICATOR));
        if (hasWronglyFormedFlag) {
            throw new ParseException(MESSAGE_WRONGLY_FORMED_FLAG);
        }
        return Arrays.asList(tokens).contains(CONFIRMATION_INDICATOR);
    }

    private String removeConfirmationIndicator(String[] tokens) {
        return Arrays.stream(tokens)
                .filter(t -> !t.equals(CONFIRMATION_INDICATOR))
                .collect(Collectors.joining(" "));
    }

    /**
     * Appends the warning to the warnings StringBuilder if the warning is present.
     * Each warning will be separated by a new line.
     */
    private static void appendWarning(StringBuilder warnings, Optional<String> warning) {
        warning.ifPresent(w -> {
            if (!warnings.isEmpty()) {
                warnings.append("\n");
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
