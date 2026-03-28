package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_WARN;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_WARN;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_WARN;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandUtil.SEPARATOR_NEW_LINE;
import static seedu.address.model.person.warnings.DuplicatePersonWarning.MESSAGE_SIMILAR_ADDRESS;
import static seedu.address.model.person.warnings.DuplicatePersonWarning.MESSAGE_SIMILAR_NAME;
import static seedu.address.model.person.warnings.DuplicatePersonWarning.MESSAGE_SIMILAR_PHONE;
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

        assertThrows(CommandException.class, Messages.MESSAGE_DUPLICATE_PERSON, () -> addCommand.execute(modelStub));
    }

    @Test
    public void execute_withWarnings_success() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();

        Person validPersonWithWarnings = new PersonBuilder()
                .withName(INVALID_NAME_WARN)
                .withPhone(INVALID_PHONE_WARN)
                .withEmail(INVALID_EMAIL_WARN)
                .withAddress(VALID_ADDRESS_BOB)
                .build();

        String warnings =
                Name.MESSAGE_WARN + SEPARATOR_NEW_LINE
                        + Email.MESSAGE_WARN;
        AddCommand addCommand = new AddCommand(validPersonWithWarnings, warnings);

        CommandResult result = addCommand.execute(modelStub);

        String expectedMessage = String.format(
                AddCommand.MESSAGE_SUCCESS + SEPARATOR_NEW_LINE + warnings,
                Messages.format(validPersonWithWarnings));

        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_similarName_warningShown() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();

        // Add a person first
        Person existingPerson = new PersonBuilder().withName("John Doe").build();
        modelStub.addPerson(existingPerson);

        // Try to add a person with similar name (different case/spacing)
        Person newPerson = new PersonBuilder()
                .withName("john  doe")
                .withEmail("different@example.com")
                .withPhone("99999999")
                .build();
        AddCommand addCommand = new AddCommand(newPerson);

        CommandResult result = addCommand.execute(modelStub);

        assertTrue(result.getFeedbackToUser().contains(String.format(MESSAGE_SIMILAR_NAME, existingPerson.getName())));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_partialNameMatch_warningShown() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();

        // Add a person first
        Person existingPerson = new PersonBuilder().withName("John Doe Smith").build();
        modelStub.addPerson(existingPerson);

        // Try to add a person with partial name match
        Person newPerson = new PersonBuilder()
                .withName("John Doe")
                .withEmail("different@example.com")
                .withPhone("99999999")
                .build();
        AddCommand addCommand = new AddCommand(newPerson);

        CommandResult result = addCommand.execute(modelStub);

        assertTrue(result.getFeedbackToUser().contains(String.format(MESSAGE_SIMILAR_NAME, existingPerson.getName())));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_withSimilarNameAndAddressWarnings_success() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        String duplicateWarnName = "John Doe";
        String duplicateWarnAddressName = "Mary Jane";
        String duplicateWarnAddress = "123 Clementi Road";

        // Existing contact for similar-name warning
        modelStub.addPerson(new PersonBuilder()
                .withName(duplicateWarnName)
                .withPhone("11111111")
                .withEmail("john.doe@example.com")
                .withAddress("1 Old Street")
                .build());

        // Existing contact for similar-address warning
        modelStub.addPerson(new PersonBuilder()
                .withName(duplicateWarnAddressName)
                .withPhone("22222222")
                .withEmail("mary.jane@example.com")
                .withAddress(duplicateWarnAddress)
                .build());

        Person toAdd = new PersonBuilder()
                .withName("John")
                .withPhone("33333333")
                .withEmail("john.new@example.com")
                .withAddress("123 Clementi Road")
                .build();

        CommandResult result = new AddCommand(toAdd).execute(modelStub);

        assertTrue(result.getFeedbackToUser().contains(String.format(MESSAGE_SIMILAR_NAME, duplicateWarnName)));
        assertTrue(result.getFeedbackToUser().contains(
                String.format(MESSAGE_SIMILAR_ADDRESS, duplicateWarnAddressName, duplicateWarnAddress)));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_multipleSimilarName_onlyOneWarningAppended() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();

        // Existing contacts
        Person existing1 = new PersonBuilder()
                .withName("John Doe")
                .withEmail("john1@example.com")
                .withPhone("11111111")
                .build();
        Person existing2 = new PersonBuilder()
                .withName("john doe") // same name, different casing
                .withEmail("john2@example.com")
                .withPhone("22222222")
                .build();
        modelStub.addPerson(existing1);
        modelStub.addPerson(existing2);

        // New person with different email and phone
        Person newPerson = new PersonBuilder()
                .withName("John  Doe") // similar name
                .withEmail("newjohn@example.com") // different
                .withPhone("33333333") // different
                .build();

        AddCommand addCommand = new AddCommand(newPerson);
        CommandResult result = addCommand.execute(modelStub);

        String feedback = result.getFeedbackToUser();

        // Warning should appear only once
        assertTrue(feedback.contains(String.format(MESSAGE_SIMILAR_NAME, existing1.getName()))
                || feedback.contains(String.format(MESSAGE_SIMILAR_NAME, existing2.getName())));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_multipleSimilarAddress_onlyOneWarningAppended() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();

        // Existing contacts with similar addresses
        Person existing1 = new PersonBuilder()
                .withName("Alice")
                .withAddress("123 Main Street Block A")
                .withEmail("alice1@example.com")
                .withPhone("11111111")
                .build();
        Person existing2 = new PersonBuilder()
                .withName("Bob")
                .withAddress("123 Main Street") // partial match
                .withEmail("bob@example.com")
                .withPhone("22222222")
                .build();
        modelStub.addPerson(existing1);
        modelStub.addPerson(existing2);

        // New person with different email and phone
        Person newPerson = new PersonBuilder()
                .withName("Charlie")
                .withAddress("123 Main Street") // similar address
                .withEmail("charlie@example.com") // different
                .withPhone("33333333") // different
                .build();

        AddCommand addCommand = new AddCommand(newPerson);
        CommandResult result = addCommand.execute(modelStub);

        String feedback = result.getFeedbackToUser();

        // Warning should appear only once
        assertTrue(
                feedback.contains(
                        String.format(MESSAGE_SIMILAR_ADDRESS, existing1.getName(), existing1.getAddress()))
                || feedback.contains(
                        String.format(MESSAGE_SIMILAR_ADDRESS, existing2.getName(), existing2.getAddress())));

        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_similarAddress_warningShown() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();

        Person existingPerson = new PersonBuilder()
                .withName("Alice")
                .withAddress("123 Main Street Block A")
                .build();
        modelStub.addPerson(existingPerson);

        // Try to add a person with similar address (partial match)
        Person newPerson = new PersonBuilder()
                .withName("Different Name")
                .withEmail("different@example.com")
                .withPhone("99999999")
                .withAddress("123 Main Street")
                .build();
        AddCommand addCommand = new AddCommand(newPerson);

        CommandResult result = addCommand.execute(modelStub);

        assertTrue(result.getFeedbackToUser().contains(String.format(
                MESSAGE_SIMILAR_ADDRESS, existingPerson.getName(), existingPerson.getAddress())));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_similarPhone_warningShown() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();

        Person existingPerson = new PersonBuilder()
                .withName("Alice Supplies")
                .withEmail("alice@example.com")
                .withPhone("91234567")
                .withAddress("1 Alpha Street")
                .build();
        modelStub.addPerson(existingPerson);

        Person newPerson = new PersonBuilder()
                .withName("Bob Traders")
                .withEmail("bob@example.com")
                .withPhone("00123456")
                .withAddress("99 Beta Avenue")
                .build();

        CommandResult result = new AddCommand(newPerson).execute(modelStub);

        assertTrue(result.getFeedbackToUser().contains(String.format(
                MESSAGE_SIMILAR_PHONE, existingPerson.getName(), existingPerson.getPhone())));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
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
            return Optional.empty();
        }

        @Override
        public Optional<Product> findSimilarNameMatch(Product candidate, Product exclude) {
            return Optional.empty();
        }

        @Override
        public Optional<Person> findSimilarPhoneMatch(Person candidate, Person exclude) {
            return Optional.empty();
        }

        @Override
        public Optional<Person> findSimilarAddressMatch(Person candidate, Person exclude) {
            return Optional.empty();
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
            // stub method
        }

        @Override
        public Optional<String> undoVendorVault() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canUndoVendorVault() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<String> redoVendorVault() {
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
