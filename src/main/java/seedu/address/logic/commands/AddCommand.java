package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.ParserUtil.NEWLINE;
import static seedu.address.model.person.warnings.DuplicatePersonWarning.MESSAGE_SIMILAR_ADDRESS;
import static seedu.address.model.person.warnings.DuplicatePersonWarning.MESSAGE_SIMILAR_NAME;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.warnings.DuplicatePersonWarning;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a vendor contact to the address book. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_EMAIL + "EMAIL "
            + PREFIX_ADDRESS + "ADDRESS "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@example.com "
            + PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 "
            + PREFIX_TAG + "friends "
            + PREFIX_TAG + "owesMoney";

    public static final String MESSAGE_SUCCESS = "New contact added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This vendor contact already exists in the address book "
            + "with the same email or phone number. ";

    private final Person toAdd;
    private String warnings = "";

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    /**
     * Creates an AddCommand to add the specified {@code Person}
     * With warnings to show after success.
     */
    public AddCommand(Person person, String warnings) {
        requireNonNull(person);
        this.toAdd = person;
        this.warnings = warnings;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasPerson(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        StringBuilder allWarnings = new StringBuilder(warnings);
        checkForSimilarContacts(model, allWarnings);

        model.addPerson(toAdd);
        model.commitVendorVault();

        String formattedWarnings = allWarnings.isEmpty() ? "" : NEWLINE + allWarnings;
        String feedbackType = allWarnings.isEmpty()
                ? CommandResult.FEEDBACK_TYPE_SUCCESS
                : CommandResult.FEEDBACK_TYPE_WARN;

        return new CommandResult(
                String.format(MESSAGE_SUCCESS + formattedWarnings, Messages.format(toAdd)),
                feedbackType);

    }

    /**
     * Checks for similar contacts in the model and appends warnings if found.
     */
    private void checkForSimilarContacts(Model model, StringBuilder warnings) {
        boolean hasSimilarName = false;
        boolean hasSimilarAddress = false;

        for (Person existingPerson : model.getFilteredPersonList()) {
            DuplicatePersonWarning duplicateWarning = toAdd.isSamePersonWarn(existingPerson);

            if (!duplicateWarning.getValue()) {
                continue;
            }

            String warning = duplicateWarning.getWarning();
            if (warning.equals(MESSAGE_SIMILAR_NAME) && !hasSimilarName) {
                appendWarning(warnings, String.format(MESSAGE_SIMILAR_NAME, existingPerson.getName()));
                hasSimilarName = true;
            } else if (warning.equals(MESSAGE_SIMILAR_ADDRESS) && !hasSimilarAddress) {
                appendWarning(warnings, String.format(
                        MESSAGE_SIMILAR_ADDRESS,
                        existingPerson.getName(),
                        existingPerson.getAddress()));
                hasSimilarAddress = true;
            }

            if (hasSimilarName && hasSimilarAddress) {
                break;
            }
        }
    }

    private void appendWarning(StringBuilder warnings, String message) {
        if (warnings.length() > 0) {
            warnings.append(NEWLINE);
        }
        warnings.append(message);
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }

    /**
     * Returns the warnings to show after successfully adding the person.
     *
     * @return the warnings to show
     */
    public String getWarnings() {
        return warnings;
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
