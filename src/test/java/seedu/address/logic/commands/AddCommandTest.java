package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_WARN;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_WARN;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_WARN;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertExactlyOneWarning;
import static seedu.address.logic.commands.CommandTestUtil.assertSimilarAddressWarning;
import static seedu.address.logic.commands.CommandTestUtil.assertSimilarNameWarning;
import static seedu.address.logic.commands.CommandTestUtil.assertSimilarPhoneWarning;
import static seedu.address.logic.commands.CommandUtil.SEPARATOR_NEW_LINE;
import static seedu.address.model.person.warnings.DuplicatePersonWarning.MESSAGE_SIMILAR_ADDRESS;
import static seedu.address.model.person.warnings.DuplicatePersonWarning.MESSAGE_SIMILAR_NAME;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyAliases;
import seedu.address.model.ReadOnlyInventory;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.ReadOnlyVendorVault;
import seedu.address.model.alias.Alias;
import seedu.address.model.alias.exceptions.DuplicateAliasException;
import seedu.address.model.alias.exceptions.NoAliasFoundInAliasListException;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.product.Product;
import seedu.address.testutil.PersonBuilder;

public class AddCommandTest {

    // -------------------------------------------------------------------------
    // Person field constants — similar-name tests
    // -------------------------------------------------------------------------
    private static final String NAME_JOHN_DOE = "John Doe";
    private static final String NAME_JOHN_DOE_LOWER_SPACED = "john  doe";
    private static final String NAME_JOHN_DOE_UPPER_SPACED = "John  Doe";
    private static final String NAME_JOHN_DOE_LOWER = "john doe";
    private static final String NAME_JOHN_DOE_SMITH = "John Doe Smith";
    private static final String NAME_JOHN = "James Doe";
    private static final String EMAIL_JOHN_1 = "john1@example.com";
    private static final String EMAIL_JOHN_2 = "john2@example.com";
    private static final String EMAIL_JOHN_NEW = "newjohn@example.com";
    private static final String EMAIL_DIFFERENT = "different@example.com";
    private static final String PHONE_JOHN_1 = "11111111";
    private static final String PHONE_JOHN_2 = "22222222";
    private static final String PHONE_JOHN_NEW = "33333333";
    private static final String PHONE_UNIQUE = "99999999";

    // -------------------------------------------------------------------------
    // Person field constants — similar-address tests
    // -------------------------------------------------------------------------
    private static final String NAME_ALICE_ADDR = "Alice";
    private static final String NAME_DIFFERENT = "Different Name";
    private static final String NAME_BOB_ADDR = "Bob";
    private static final String NAME_CHARLIE = "Charlie";
    private static final String ADDRESS_MAIN_STREET_FULL = "123 Main Street Block A";
    private static final String ADDRESS_MAIN_STREET = "123 Main Street";
    private static final String EMAIL_ALICE_1 = "alice1@example.com";
    private static final String EMAIL_BOB_ADDR = "bob@example.com";
    private static final String EMAIL_CHARLIE = "charlie@example.com";
    private static final String PHONE_ALICE_1 = "11111111";
    private static final String PHONE_BOB_ADDR = "22222222";
    private static final String PHONE_CHARLIE = "33333333";

    // -------------------------------------------------------------------------
    // Person field constants — similar-phone tests
    // -------------------------------------------------------------------------
    private static final String NAME_ALICE_SUPPLIES = "Alice Supplies";
    private static final String NAME_BOB_TRADERS = "Bob Traders";
    private static final String EMAIL_ALICE_SUPPLIES = "alice@example.com";
    private static final String EMAIL_BOB_TRADERS = "bob@example.com";
    private static final String PHONE_ALICE_SUPPLIES = "91234567";
    private static final String PHONE_BOB_TRADERS = "00123456"; // shares suffix with PHONE_ALICE_SUPPLIES
    private static final String ADDRESS_ALPHA_STREET = "1 Alpha Street";
    private static final String ADDRESS_BETA_AVENUE = "99 Beta Avenue";

    // -------------------------------------------------------------------------
    // Person field constants — combined-warnings test
    // -------------------------------------------------------------------------
    private static final String NAME_JOHN_DOE_COMBINED = "John Doe";
    private static final String NAME_MARY_JANE = "Mary Jane";
    private static final String ADDRESS_OLD_STREET = "1 Old Street";
    private static final String ADDRESS_CLEMENTI_ROAD = "123 Clementi Road";
    private static final String EMAIL_JOHN_DOE_COMBINED = "john.doe@example.com";
    private static final String EMAIL_MARY_JANE = "mary.jane@example.com";
    private static final String EMAIL_JOHN_NEW_COMBINED = "john.new@example.com";
    private static final String PHONE_JOHN_DOE_COMBINED = "11111111";
    private static final String PHONE_MARY_JANE = "22222222";
    private static final String PHONE_JOHN_NEW_COMBINED = "33333333";

    @Test
    public void constructor_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null));
    }

    @Test
    public void execute_personAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        Person validPerson = new PersonBuilder().build();

        CommandResult commandResult = new AddCommand(validPerson).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                commandResult.getFeedbackToUser());
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, commandResult.getFeedbackType());
        assertEquals(Arrays.asList(validPerson), modelStub.personsAdded);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person validPerson = new PersonBuilder().build();
        AddCommand addCommand = new AddCommand(validPerson);
        ModelStub modelStub = new ModelStubWithPerson(validPerson);

        assertThrows(CommandException.class,
                String.format(Messages.MESSAGE_DUPLICATE_PERSON, validPerson.getName(), validPerson.getEmail()), (
                ) -> addCommand.execute(modelStub));
    }

    @Test
    public void execute_withFieldFormatWarnings_warningFeedbackReturned() throws Exception {
        // EP: person with invalid name and email format — both warnings should appear in feedback
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();

        Person validPersonWithWarnings = new PersonBuilder()
                .withName(INVALID_NAME_WARN)
                .withPhone(INVALID_PHONE_WARN)
                .withEmail(INVALID_EMAIL_WARN)
                .withAddress(VALID_ADDRESS_BOB)
                .build();

        String warnings = Name.MESSAGE_WARN + SEPARATOR_NEW_LINE + Email.MESSAGE_WARN;
        AddCommand addCommand = new AddCommand(validPersonWithWarnings, warnings);

        CommandResult result = addCommand.execute(modelStub);

        String expectedMessage = String.format(
                AddCommand.MESSAGE_SUCCESS + SEPARATOR_NEW_LINE + warnings,
                Messages.format(validPersonWithWarnings));

        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    // -------------------------------------------------------------------------
    // Similar-name warnings
    // -------------------------------------------------------------------------

    @Test
    public void execute_similarName_warningShown() throws Exception {
        // EP: exact same words, different casing/spacing → similar name
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        Person existingPerson = new PersonBuilder().withName(NAME_JOHN_DOE).build();
        modelStub.addPerson(existingPerson);

        Person newPerson = new PersonBuilder()
                .withName(NAME_JOHN_DOE_LOWER_SPACED)
                .withEmail(EMAIL_DIFFERENT)
                .withPhone(PHONE_UNIQUE)
                .build();

        assertSimilarNameWarning(new AddCommand(newPerson).execute(modelStub), existingPerson);
    }

    @Test
    public void execute_partialNameMatch_warningShown() throws Exception {
        // EP: new name shares words in existing name → similar name
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        Person existingPerson = new PersonBuilder().withName(NAME_JOHN_DOE_SMITH).build();
        modelStub.addPerson(existingPerson);

        Person newPerson = new PersonBuilder()
                .withName(NAME_JOHN_DOE)
                .withEmail(EMAIL_DIFFERENT)
                .withPhone(PHONE_UNIQUE)
                .build();

        assertSimilarNameWarning(new AddCommand(newPerson).execute(modelStub), existingPerson);
    }

    @Test
    public void execute_multipleSimilarNames_onlyOneWarningAppended() throws Exception {
        // EP: multiple existing contacts match — warning should appear exactly once
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();

        Person existing1 = new PersonBuilder()
                .withName(NAME_JOHN_DOE).withEmail(EMAIL_JOHN_1).withPhone(PHONE_JOHN_1).build();
        Person existing2 = new PersonBuilder()
                .withName(NAME_JOHN_DOE_LOWER).withEmail(EMAIL_JOHN_2).withPhone(PHONE_JOHN_2).build();
        modelStub.addPerson(existing1);
        modelStub.addPerson(existing2);

        Person newPerson = new PersonBuilder()
                .withName(NAME_JOHN_DOE_UPPER_SPACED).withEmail(EMAIL_JOHN_NEW).withPhone(PHONE_JOHN_NEW).build();

        CommandResult result = new AddCommand(newPerson).execute(modelStub);
        assertExactlyOneWarning(result.getFeedbackToUser(),
                String.format(MESSAGE_SIMILAR_NAME, existing1.getName()),
                String.format(MESSAGE_SIMILAR_NAME, existing2.getName()));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    // -------------------------------------------------------------------------
    // Similar-address warnings
    // -------------------------------------------------------------------------

    @Test
    public void execute_similarAddress_warningShown() throws Exception {
        // EP: new address is a substring of existing address → similar address
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        Person existingPerson = new PersonBuilder()
                .withName(NAME_ALICE_ADDR).withAddress(ADDRESS_MAIN_STREET_FULL).build();
        modelStub.addPerson(existingPerson);

        Person newPerson = new PersonBuilder()
                .withName(NAME_DIFFERENT).withEmail(EMAIL_DIFFERENT)
                .withPhone(PHONE_UNIQUE).withAddress(ADDRESS_MAIN_STREET).build();

        assertSimilarAddressWarning(new AddCommand(newPerson).execute(modelStub), existingPerson);
    }

    @Test
    public void execute_multipleSimilarAddresses_onlyOneWarningAppended() throws Exception {
        // EP: multiple existing contacts match on address — warning should appear exactly once
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();

        Person existing1 = new PersonBuilder()
                .withName(NAME_ALICE_ADDR).withAddress(ADDRESS_MAIN_STREET_FULL)
                .withEmail(EMAIL_ALICE_1).withPhone(PHONE_ALICE_1).build();
        Person existing2 = new PersonBuilder()
                .withName(NAME_BOB_ADDR).withAddress(ADDRESS_MAIN_STREET)
                .withEmail(EMAIL_BOB_ADDR).withPhone(PHONE_BOB_ADDR).build();
        modelStub.addPerson(existing1);
        modelStub.addPerson(existing2);

        Person newPerson = new PersonBuilder()
                .withName(NAME_CHARLIE).withAddress(ADDRESS_MAIN_STREET)
                .withEmail(EMAIL_CHARLIE).withPhone(PHONE_CHARLIE).build();

        CommandResult result = new AddCommand(newPerson).execute(modelStub);
        assertExactlyOneWarning(result.getFeedbackToUser(),
                String.format(MESSAGE_SIMILAR_ADDRESS, existing1.getName(), existing1.getAddress()),
                String.format(MESSAGE_SIMILAR_ADDRESS, existing2.getName(), existing2.getAddress()));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    // -------------------------------------------------------------------------
    // Similar-phone warnings
    // -------------------------------------------------------------------------

    @Test
    public void execute_similarPhone_warningShown() throws Exception {
        // EP: phones share at least 3 consecutive digits → similar phone
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        Person existingPerson = new PersonBuilder()
                .withName(NAME_ALICE_SUPPLIES).withEmail(EMAIL_ALICE_SUPPLIES)
                .withPhone(PHONE_ALICE_SUPPLIES).withAddress(ADDRESS_ALPHA_STREET).build();
        modelStub.addPerson(existingPerson);

        Person newPerson = new PersonBuilder()
                .withName(NAME_BOB_TRADERS).withEmail(EMAIL_BOB_TRADERS)
                .withPhone(PHONE_BOB_TRADERS).withAddress(ADDRESS_BETA_AVENUE).build();

        assertSimilarPhoneWarning(new AddCommand(newPerson).execute(modelStub), existingPerson);
    }

    // -------------------------------------------------------------------------
    // Multiple warning types combined
    // -------------------------------------------------------------------------

    @Test
    public void execute_withSimilarNameAndAddressWarnings_bothWarningsPresent() throws Exception {
        // EP: new person triggers both a similar-name and a similar-address warning simultaneously
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();

        Person nameMatch = new PersonBuilder()
                .withName(NAME_JOHN_DOE_COMBINED).withPhone(PHONE_JOHN_DOE_COMBINED)
                .withEmail(EMAIL_JOHN_DOE_COMBINED).withAddress(ADDRESS_OLD_STREET).build();
        Person addressMatch = new PersonBuilder()
                .withName(NAME_MARY_JANE).withPhone(PHONE_MARY_JANE)
                .withEmail(EMAIL_MARY_JANE).withAddress(ADDRESS_CLEMENTI_ROAD).build();
        modelStub.addPerson(nameMatch);
        modelStub.addPerson(addressMatch);

        Person toAdd = new PersonBuilder()
                .withName(NAME_JOHN) // partial match → nameMatch
                .withPhone(PHONE_JOHN_NEW_COMBINED)
                .withEmail(EMAIL_JOHN_NEW_COMBINED)
                .withAddress(ADDRESS_CLEMENTI_ROAD) // exact match → addressMatch
                .build();

        CommandResult result = new AddCommand(toAdd).execute(modelStub);
        assertSimilarNameWarning(result, nameMatch);
        assertSimilarAddressWarning(result, addressMatch);
    }

    @Test
    public void getPendingConfirmation_returnsInactivePendingConfirmation() {
        Person validPerson = new PersonBuilder().build();
        AddCommand addCommand = new AddCommand(validPerson);

        PendingConfirmation pendingConfirmation = addCommand.getPendingConfirmation();
        assertFalse(pendingConfirmation.getNeedConfirmation());
    }

    @Test
    public void equals() {
        Person alice = new PersonBuilder().withName("Alice").build();
        Person bob = new PersonBuilder().withName("Bob").build();
        AddCommand addAliceCommand = new AddCommand(alice);
        AddCommand addBobCommand = new AddCommand(bob);

        // same object -> returns true
        assertTrue(addAliceCommand.equals(addAliceCommand));

        // same values -> returns true
        AddCommand addAliceCommandCopy = new AddCommand(alice);
        assertTrue(addAliceCommand.equals(addAliceCommandCopy));

        // different types -> returns false
        assertFalse(addAliceCommand.equals(1));

        // null -> returns false
        assertFalse(addAliceCommand.equals(null));

        // different person -> returns false
        assertFalse(addAliceCommand.equals(addBobCommand));
    }

    @Test
    public void toStringMethod() {
        AddCommand addCommand = new AddCommand(ALICE);
        String expected = AddCommand.class.getCanonicalName() + "{toAdd=" + ALICE + "}";
        assertEquals(expected, addCommand.toString());
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setProduct(Product target, Product editedProduct) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setInventory(ReadOnlyInventory inventory) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyInventory getInventory() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setVendorVault(ReadOnlyVendorVault vendorVault) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyVendorVault getVendorVault() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAliases(ReadOnlyAliases aliases) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAliases getAliases() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Person> findByEmail(Email email) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Person> findSimilarNameMatch(Person candidate, Person exclude) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Product> findSimilarNameMatch(Product candidate, Product exclude) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Person> findSimilarPhoneMatch(Person candidate, Person exclude) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Person> findSimilarAddressMatch(Person candidate, Person exclude) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasProduct(Product product) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteProduct(Product target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addProduct(Product product) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void archiveProduct(Product target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void restoreProduct(Product target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addAlias(Alias alias) throws DuplicateAliasException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Alias findAlias(String aliasStr) throws NoAliasFoundInAliasListException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void removeAlias(String aliasStr) throws NoAliasFoundInAliasListException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public List<Alias> getAliasList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Product> getFilteredProductList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredProductList(Predicate<Product> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void archivePerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void restorePerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void commitVendorVault(String actionSummary) {
            // allow commit in tests
        }

        @Override
        public String undoVendorVault() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canUndoVendorVault() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public String redoVendorVault() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canRedoVendorVault() {
            throw new AssertionError("This method should not be called.");
        }

    }

    /**
     * A Model stub that contains a single person.
     */
    private class ModelStubWithPerson extends ModelStub {
        private final Person person;

        ModelStubWithPerson(Person person) {
            requireNonNull(person);
            this.person = person;
        }

        @Override
        public boolean hasPerson(Person person) {
            requireNonNull(person);
            return this.person.isSamePerson(person);
        }

        @Override
        public Optional<Person> findByEmail(Email email) {
            requireNonNull(email);
            return this.person.getEmail().equals(email) ? Optional.of(this.person) : Optional.empty();
        }
    }

    /**
     * A Model stub that always accept the person being added.
     */
    private class ModelStubAcceptingPersonAdded extends ModelStub {
        final ArrayList<Person> personsAdded = new ArrayList<>();

        @Override
        public boolean hasPerson(Person person) {
            requireNonNull(person);
            return personsAdded.stream().anyMatch(person::isSamePerson);
        }

        @Override
        public void addPerson(Person person) {
            requireNonNull(person);
            personsAdded.add(person);
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            return javafx.collections.FXCollections.observableArrayList(personsAdded);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            AddressBook addressBook = new AddressBook();
            personsAdded.forEach(addressBook::addPerson);
            return addressBook;
        }

        @Override
        public Optional<Person> findSimilarNameMatch(Person candidate, Person exclude) {
            AddressBook addressBook = new AddressBook();
            personsAdded.forEach(addressBook::addPerson);
            return addressBook.findSimilarNameMatch(candidate, exclude);
        }

        @Override
        public Optional<Person> findSimilarPhoneMatch(Person candidate, Person exclude) {
            AddressBook addressBook = new AddressBook();
            personsAdded.forEach(addressBook::addPerson);
            return addressBook.findSimilarPhoneMatch(candidate, exclude);
        }

        @Override
        public Optional<Person> findSimilarAddressMatch(Person candidate, Person exclude) {
            AddressBook addressBook = new AddressBook();
            personsAdded.forEach(addressBook::addPerson);
            return addressBook.findSimilarAddressMatch(candidate, exclude);
        }
    }

}
