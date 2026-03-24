package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PERSON;
import static seedu.address.logic.commands.CommandUtil.EMPTY_STRING;
import static seedu.address.logic.commands.CommandUtil.SEPARATOR_NEW_LINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.person.warnings.DuplicatePersonWarning.formatAddressWarning;
import static seedu.address.model.person.warnings.DuplicatePersonWarning.formatNameWarning;
import static seedu.address.model.person.warnings.DuplicatePersonWarning.formatPhoneWarning;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";
    public static final String COMMAND_USAGE = COMMAND_WORD + " " + PREFIX_NAME + "NAME "
                    + PREFIX_PHONE + "PHONE "
                    + PREFIX_EMAIL + "EMAIL "
                    + PREFIX_ADDRESS + "ADDRESS "
                    + "[" + PREFIX_TAG + "TAG]...";
    public static final String COMMAND_DESCRIPTION = "Adds a contact.";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a vendor contact to VendorVault. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_EMAIL + "EMAIL "
            + PREFIX_ADDRESS + "ADDRESS "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "Cytron Technologies Pte. Ltd. "
            + PREFIX_PHONE + "65480668 "
            + PREFIX_EMAIL + "sg.sales@cytron.io "
            + PREFIX_ADDRESS + "09 Collyer Quay "
            + PREFIX_TAG + "electronics ";

    public static final String MESSAGE_SUCCESS = "New contact added: %1$s";

    private final Person toAdd;
    private final String initialWarnings;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        this(person, EMPTY_STRING);
    }

    /**
     * Creates an AddCommand to add the specified {@code Person}
     * With warnings to show after success.
     */
    public AddCommand(Person person, String initialWarnings) {
        requireNonNull(person);
        this.toAdd = person;
        this.initialWarnings = initialWarnings;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        validateNoDuplicate(model);

        String allWarnings = buildWarnings(model, initialWarnings);

        model.addPerson(toAdd);
        model.commitVendorVault();

        return buildCommandResult(allWarnings);
    }

    private CommandResult buildCommandResult(String allWarnings) {
        boolean hasWarnings = !allWarnings.isEmpty();

        String message = hasWarnings
                ? String.format(MESSAGE_SUCCESS + SEPARATOR_NEW_LINE + allWarnings, Messages.format(toAdd))
                : String.format(MESSAGE_SUCCESS, Messages.format(toAdd));

        String feedbackType = hasWarnings
                ? CommandResult.FEEDBACK_TYPE_WARN
                : CommandResult.FEEDBACK_TYPE_SUCCESS;

        return new CommandResult(message, false, false, feedbackType, true);

    }

    private String buildWarnings(Model model, String originalWarnings) {
        StringBuilder warningsBuilder = new StringBuilder(originalWarnings);
        appendSimilarContactWarnings(model, warningsBuilder);
        return warningsBuilder.toString();
    }

    private void validateNoDuplicate(Model model) throws CommandException {
        if (model.hasPerson(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }
    }

    /**
     * Adds warnings when similar contacts are detected to avoid accidental duplicates.
     */
    private void appendSimilarContactWarnings(Model model, StringBuilder warnings) {
        model.findSimilarNameMatch(toAdd, null).ifPresent(match ->
                appendWarning(warnings, formatNameWarning(match.getName())));

        model.findSimilarAddressMatch(toAdd, null).ifPresent(match ->
                appendWarning(warnings, formatAddressWarning(match.getName(), match.getAddress())));

        model.findSimilarPhoneMatch(toAdd, null).ifPresent(match ->
                appendWarning(warnings, formatPhoneWarning(match.getName(), match.getPhone())));
    }

    private void appendWarning(StringBuilder warnings, String message) {
        if (warnings.length() > 0) {
            warnings.append(SEPARATOR_NEW_LINE);
        }
        warnings.append(message);
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand)) {
            return false;
        }

        AddCommand otherAddCommand = (AddCommand) other;
        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
