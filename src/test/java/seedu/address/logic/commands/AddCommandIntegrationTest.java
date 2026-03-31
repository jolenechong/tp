package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.assertSimilarAddressWarning;
import static seedu.address.logic.commands.CommandTestUtil.assertSimilarNameWarning;
import static seedu.address.logic.commands.CommandTestUtil.assertSimilarPhoneWarning;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Aliases;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.VendorVault;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private static final String EMAIL_UNIQUE_1 = "uniqueemail1@example.com";
    private static final String EMAIL_UNIQUE_2 = "uniqueemail2@example.com";
    private static final String EMAIL_UNIQUE_3 = "uniqueemail3@example.com";
    private static final String PHONE_UNIQUE_1 = "00000001";
    private static final String PHONE_UNIQUE_2 = "00000002";
    private static final String ADDRESS_UNIQUE = "99 Unique Avenue";
    private static final String NAME_DIFFERENT_1 = "Totally Different Name";
    private static final String NAME_DIFFERENT_2 = "Another Different Name";
    private static final int PHONE_SHARED_SUFFIX_LENGTH = 3;
    private static final String PHONE_SHARED_SUFFIX_PREFIX = "000";
    private static final String PHONE_SHARED_SUFFIX_POSTFIX = "00";

    private Model model;
    private Person firstTypicalPerson;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(new VendorVault(
                getTypicalAddressBook(), getTypicalInventory()), new UserPrefs() , new Aliases());
        firstTypicalPerson = model.getAddressBook().getPersonList().get(0);
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getVendorVault(), new UserPrefs(), new Aliases());
        expectedModel.addPerson(validPerson);

        CommandResult expectedCommandResult = new CommandResult(
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                false, false, CommandResult.FEEDBACK_TYPE_SUCCESS, true);

        assertCommandSuccess(new AddCommand(validPerson), model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                String.format(Messages.MESSAGE_DUPLICATE_PERSON,
                        personInList.getName(), personInList.getEmail()));
    }

    @Test
    public void execute_similarName_warningShown() throws Exception {
        // EP: new name contains a single word that appears in existing name → detects similar name
        String firstName = firstTypicalPerson.getName().fullName.split(" ")[0];
        Person newPerson = buildUniquePerson(VALID_NAME_BOB + " " + firstName, EMAIL_UNIQUE_1, PHONE_UNIQUE_1, null);

        assertSimilarNameWarning(new AddCommand(newPerson).execute(model), firstTypicalPerson);
    }

    @Test
    public void execute_similarAddress_warningShown() throws Exception {
        // EP: new address is a subset of existing address → detects similar address
        String existingAddress = firstTypicalPerson.getAddress().value;
        String partialAddress = existingAddress.contains(" ")
                ? existingAddress.substring(0, existingAddress.lastIndexOf(" "))
                : existingAddress;
        Person newPerson = buildUniquePerson(NAME_DIFFERENT_1, EMAIL_UNIQUE_2, PHONE_UNIQUE_2, partialAddress);

        assertSimilarAddressWarning(new AddCommand(newPerson).execute(model), firstTypicalPerson);
    }

    @Test
    public void execute_similarPhone_warningShown() throws Exception {
        // EP: new phone shares consecutive 3-digit with existing phone → detects similar phone
        String sharedSuffix = firstTypicalPerson.getPhone().value;
        sharedSuffix = sharedSuffix.substring(sharedSuffix.length() - PHONE_SHARED_SUFFIX_LENGTH);
        String newPhone = PHONE_SHARED_SUFFIX_PREFIX + sharedSuffix + PHONE_SHARED_SUFFIX_POSTFIX;
        Person newPerson = buildUniquePerson(NAME_DIFFERENT_2, EMAIL_UNIQUE_3, newPhone, ADDRESS_UNIQUE);

        assertSimilarPhoneWarning(new AddCommand(newPerson).execute(model), firstTypicalPerson);
    }

    /**
     * Builds a {@code Person} with the given fields. If {@code address} is null,
     * the default address from {@code PersonBuilder} is used.
     */
    private Person buildUniquePerson(String name, String email, String phone, String address) {
        PersonBuilder builder = new PersonBuilder()
                .withName(name)
                .withEmail(email)
                .withPhone(phone);
        if (address != null) {
            builder = builder.withAddress(address);
        }
        return builder.build();
    }

}
