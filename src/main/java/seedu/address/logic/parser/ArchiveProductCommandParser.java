package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ArchiveProductCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code ArchiveProductCommand} object.
 *
 * Expected format: {@code archiveproduct IDENTIFIER}
 * Example: {@code archiveproduct DE/5}
 */
public class ArchiveProductCommandParser implements Parser<ArchiveProductCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the {@code ArchiveProductCommand}
     * and returns an {@code ArchiveProductCommand} object for execution.
     *
     * If the identifier is blank, a {@code ParseException} is thrown with the full usage message
     * in the format {@code "Invalid command format!\n<usage>"}, aligning with the behaviour of
     * commands like {@code edit}.
     *
     * @param args user input arguments (expected to be a single identifier string)
     * @return {@code ArchiveProductCommand} with the parsed identifier
     * @throws ParseException if the input is blank
     */
    @Override
    public ArchiveProductCommand parse(String args) throws ParseException {
        requireNonNull(args);

        String identifier = args.trim();

        if (identifier.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveProductCommand.MESSAGE_USAGE));
        }

        return new ArchiveProductCommand(identifier);
    }
}
