package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.util.SampleDataUtil;

/**
 * Lists all persons in the address book to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS = "Listed all persons";


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        // If the GUI currently shows no vendors, this indicates that the
        // vendor list is empty (for example after all vendors were deleted).
        // In this case, sample vendors are automatically populated
        if (model.getAddressBook().getPersonList().isEmpty()) {
            for (Person person : SampleDataUtil.getSamplePersons()) {
                model.addPerson(person);
            }
        }

        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ACTIVE_PERSONS);
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return new PendingConfirmation();
    }
}
