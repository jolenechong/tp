package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddProductCommand;
import seedu.address.logic.commands.AliasCommand;
import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.commands.ArchiveProductCommand;
import seedu.address.logic.commands.CancelCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.ClearProductCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.ConfirmCommand;
import seedu.address.logic.commands.DeleteAliasCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteProductCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditProductCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.FindProductCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListAllCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ListProductsCommand;
import seedu.address.logic.commands.PendingConfirmation;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.RestoreCommand;
import seedu.address.logic.commands.RestoreProductCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.alias.Alias;
import seedu.address.model.alias.exceptions.NoAliasFoundInAliasListException;

/**
 * Parses user input.
 */
public class AddressBookParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
    private static final Logger logger = LogsCenter.getLogger(AddressBookParser.class);

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(
            String userInput,
            PendingConfirmation pendingConfirmation,
            Model model) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWordFromInput = matcher.group("commandWord");

        String commandWord;
        try {
            Alias alias = model.findAlias(commandWordFromInput);
            commandWord = alias.getOriginalCommand();
        } catch (NoAliasFoundInAliasListException e) {
            commandWord = commandWordFromInput;
        }

        final String arguments = matcher.group("arguments");

        // Note to developers: Change the log level in config.json to enable lower level (i.e., FINE, FINER and lower)
        // log messages such as the one below.
        // Lower level log messages are used sparingly to minimize noise in the code.
        logger.fine("Command word: " + commandWord + "; Arguments: " + arguments);

        if (pendingConfirmation.getNeedConfirmation()) {
            String lowerCaseCommandWord = commandWord.toLowerCase();
            if (lowerCaseCommandWord.equals(ConfirmCommand.COMMAND_WORD)) {
                return new ConfirmCommand(pendingConfirmation.getOnConfirm());
            } else {
                return new CancelCommand(pendingConfirmation.getOnCancel());
            }
        } else {
            switch (commandWord) {

            case AddCommand.COMMAND_WORD:
                return new AddCommandParser().parse(arguments);

            case EditCommand.COMMAND_WORD:
                return new EditCommandParser().parse(arguments);

            case DeleteCommand.COMMAND_WORD:
                return new DeleteCommandParser().parse(arguments);

            case FindCommand.COMMAND_WORD:
                return new FindCommandParser().parse(arguments);

            case ClearCommand.COMMAND_WORD:
                return new ClearCommandParser().parse(arguments);

            case UndoCommand.COMMAND_WORD:
                return new UndoCommand();

            case RedoCommand.COMMAND_WORD:
                return new RedoCommand();

            case ListCommand.COMMAND_WORD:
                return new ListCommand();

            case ExitCommand.COMMAND_WORD:
                return new ExitCommand();

            case HelpCommand.COMMAND_WORD:
                return new HelpCommand();

            case ArchiveCommand.COMMAND_WORD:
                return new ArchiveCommandParser().parse(arguments);

            case RestoreCommand.COMMAND_WORD:
                return new RestoreCommandParser().parse(arguments);

            case AddProductCommand.COMMAND_WORD:
                return new AddProductCommandParser().parse(arguments);

            case ListProductsCommand.COMMAND_WORD:
                return new ListProductsCommand();

            case ListProductsCommand.INVALID_COMMAND_WORD:
                throw new ParseException(ListProductsCommand.INVALID_COMMAND_SUGGESTION);

            case ArchiveProductCommand.COMMAND_WORD:
                return new ArchiveProductCommandParser().parse(arguments);

            case RestoreProductCommand.COMMAND_WORD:
                return new RestoreProductCommandParser().parse(arguments);

            case ClearProductCommand.COMMAND_WORD:
                return new ClearProductCommandParser().parse(arguments);

            case DeleteProductCommand.COMMAND_WORD:
                return new DeleteProductCommandParser().parse(arguments);

            case EditProductCommand.COMMAND_WORD:
                return new EditProductCommandParser().parse(arguments);

            case FindProductCommand.COMMAND_WORD:
                return new FindProductCommandParser().parse(arguments);

            case ListAllCommand.COMMAND_WORD:
                return new ListAllCommand();

            case AliasCommand.COMMAND_WORD:
                return new AliasCommandParser().parse(arguments);

            case DeleteAliasCommand.COMMAND_WORD:
                return new DeleteAliasCommandParser().parse(arguments);

            default:
                logger.finer("This user input caused a ParseException: " + userInput);
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            }
        }
    }

}
