package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.assertExactlyOneWarning;
import static seedu.address.logic.commands.CommandTestUtil.assertSimilarAddressWarning;
import static seedu.address.logic.commands.CommandTestUtil.assertSimilarNameWarning;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.model.person.warnings.DuplicatePersonWarning.MESSAGE_SIMILAR_ADDRESS;
import static seedu.address.model.person.warnings.DuplicatePersonWarning.MESSAGE_SIMILAR_NAME;
import static seedu.address.model.person.warnings.DuplicatePersonWarning.MESSAGE_SIMILAR_PHONE;
import static seedu.address.testutil.TestUtil.getProductByIdentifier;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Aliases;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.VendorVault;
import seedu.address.model.person.Email;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.product.Product;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.ProductBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private static final Email NON_EXISTENT_EMAIL = new Email("missing.person@example.com");
    private static final String NAME_TAG_KEEPER = "Tag Test Name";
    private static final String PHONE_TAG_KEEPER = "90000011";
    private static final String EMAIL_TAG_KEEPER = "tag.keeper@example.com";
    private static final String ADDRESS_TAG_KEEPER = "11 Tag Street";
    private static final String TAG_VIP = "vip";
    private static final String EDITED_CONTACT_PREFIX = "Edited Contact:";

    // -------------------------------------------------------------------------
    // Person field constants for warning tests
    // -------------------------------------------------------------------------
    private static final String PHONE_UNIQUE_A = "66666666";
    private static final String PHONE_UNIQUE_B = "66666667";
    private static final String EMAIL_ALICE = "alice.brown@example.com";
    private static final String EMAIL_JOHN = "john.doe@example.com";
    private static final String NAME_ALICE = "Alice Brown";
    private static final String NAME_JOHN_1 = "John Doe";
    private static final String NAME_JOHN_2 = "John Smith";
    private static final String NAME_JOHN_EDIT_TARGET = "John Kurz";
    private static final String NAME_ALICE_EDIT_TARGET = "Alice Kurz";
    private static final String ADDRESS_JURONG_WEST = "Jurong West";
    private static final String ADDRESS_JURONG_WEST_AVE5 = "Jurong West Ave 5";
    private static final String ADDRESS_NOWHERE = "99 Nowhere Street";
    private static final String ADDRESS_UNIQUE = "some address far away";

    private static final String PRESET_WARNING = "Pre-existing warning message";

    private Model model = new ModelManager(new VendorVault(
            getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedPerson = new PersonBuilder().withTags(TAG_VIP).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(firstPerson.getEmail(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new VendorVault(model.getVendorVault()), new UserPrefs(), new Aliases());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(lastPerson.getEmail(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new VendorVault(model.getVendorVault()), new UserPrefs(), new Aliases());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(firstPerson.getEmail(), new EditPersonDescriptor());
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new VendorVault(model.getVendorVault()), new UserPrefs(), new Aliases());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(personInFilteredList.getEmail(),
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new VendorVault(model.getVendorVault()), new UserPrefs(), new Aliases());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person secondPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        EditCommand editCommand = new EditCommand(secondPerson.getEmail(), descriptor);

        assertCommandFailure(editCommand, model,
                String.format(Messages.MESSAGE_DUPLICATE_PERSON, firstPerson.getName(), firstPerson.getEmail()));
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in address book
        Person personInList = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(personInFilteredList.getEmail(),
                new EditPersonDescriptorBuilder(personInList).build());

        assertCommandFailure(editCommand, model,
                String.format(Messages.MESSAGE_DUPLICATE_PERSON, personInList.getName(), personInList.getEmail()));
    }

    @Test
    public void execute_invalidPersonEmailUnfilteredList_failure() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(NON_EXISTENT_EMAIL, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_EMAIL);
    }

    /**
     * Edit filtered list with a target email that does not exist in the model.
     */
    @Test
    public void execute_invalidPersonEmailFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        EditCommand editCommand = new EditCommand(NON_EXISTENT_EMAIL,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_EMAIL);
    }

    @Test
    public void getPendingConfirmation_returnsInactivePendingConfirmation() {
        Person editedPerson = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(firstPerson.getEmail(), descriptor);

        PendingConfirmation pendingConfirmation = editCommand.getPendingConfirmation();
        assertFalse(pendingConfirmation.getNeedConfirmation());
    }

    // =========================================================================
    // Clear-tags confirmation tests
    // =========================================================================

    @Test
    public void execute_clearTags_requiresConfirmation() throws Exception {
        // EP: t/ should require confirmation
        Person personWithTag = buildPersonWithTagAndAdd(
                NAME_TAG_KEEPER, PHONE_TAG_KEEPER, EMAIL_TAG_KEEPER, ADDRESS_TAG_KEEPER);

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();
        EditCommand editCommand = new EditCommand(personWithTag.getEmail(), descriptor);

        CommandResult result = editCommand.execute(model);

        assertEquals(EditCommand.CONFIRMATION_CLEAR_TAGS_MESSAGE, result.getFeedbackToUser());
        assertTrue(editCommand.getPendingConfirmation().getNeedConfirmation());
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(personWithTag.getEmail(), model.getFilteredPersonList().get(0).getEmail());
        assertTrue(model.findByEmail(personWithTag.getEmail()).orElseThrow().getTags().contains(new Tag(TAG_VIP)));
    }

    @Test
    public void execute_clearTagsConfirm_tagsAreCleared() throws Exception {
        Person personWithTag = buildPersonWithTagAndAdd(
                NAME_TAG_KEEPER, PHONE_TAG_KEEPER, EMAIL_TAG_KEEPER, ADDRESS_TAG_KEEPER);
        int expectedSize = model.getFilteredPersonList().size();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();
        EditCommand editCommand = new EditCommand(personWithTag.getEmail(), descriptor);
        editCommand.execute(model);
        assertEquals(1, model.getFilteredPersonList().size());

        PendingConfirmation pendingConfirmation = editCommand.getPendingConfirmation();
        CommandResult confirmResult = new ConfirmCommand(pendingConfirmation.getOnConfirm()).execute(model);

        assertTrue(confirmResult.getFeedbackToUser().startsWith(EDITED_CONTACT_PREFIX));
        assertTrue(model.findByEmail(personWithTag.getEmail()).orElseThrow().getTags().isEmpty());
        assertEquals(expectedSize, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_clearTagsCancel_tagsUnchanged() throws Exception {
        Person personWithTag = buildPersonWithTagAndAdd(
                NAME_TAG_KEEPER, PHONE_TAG_KEEPER, EMAIL_TAG_KEEPER, ADDRESS_TAG_KEEPER);
        int expectedSize = model.getFilteredPersonList().size();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();
        EditCommand editCommand = new EditCommand(personWithTag.getEmail(), descriptor);
        editCommand.execute(model);
        assertEquals(1, model.getFilteredPersonList().size());

        PendingConfirmation pendingConfirmation = editCommand.getPendingConfirmation();
        CommandResult cancelResult = new CancelCommand(pendingConfirmation.getOnCancel()).execute(model);

        assertEquals(EditCommand.MESSAGE_CLEAR_TAGS_CANCELLED, cancelResult.getFeedbackToUser());
        assertTrue(model.findByEmail(personWithTag.getEmail()).orElseThrow().getTags().contains(new Tag(TAG_VIP)));
        assertEquals(expectedSize, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_clearTagsSkipConfirmation_tagsClearedImmediately() throws Exception {
        Person personWithTag = buildPersonWithTagAndAdd(
                NAME_TAG_KEEPER, PHONE_TAG_KEEPER, EMAIL_TAG_KEEPER, ADDRESS_TAG_KEEPER);

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();
        EditCommand editCommand = new EditCommand(personWithTag.getEmail(), descriptor, false);

        CommandResult result = editCommand.execute(model);

        assertTrue(result.getFeedbackToUser().startsWith(EDITED_CONTACT_PREFIX));
        assertFalse(editCommand.getPendingConfirmation().getNeedConfirmation());
        assertTrue(model.findByEmail(personWithTag.getEmail()).orElseThrow().getTags().isEmpty());
    }

    @Test
    public void execute_confirmFailure_throwsAssertionError() throws Exception {
        Model throwingModel = new ModelManager(new VendorVault(
                getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases()) {
            @Override
            public void setPerson(Person target, Person editedPerson) throws DuplicatePersonException {
                throw new DuplicatePersonException();
            }
        };

        Person targetPerson = throwingModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();
        EditCommand editCommand = new EditCommand(targetPerson.getEmail(), descriptor);

        editCommand.execute(throwingModel);
        PendingConfirmation pendingConfirmation = editCommand.getPendingConfirmation();

        AssertionError assertionError = assertThrows(AssertionError.class, () ->
                new ConfirmCommand(pendingConfirmation.getOnConfirm()).execute(throwingModel));
        assertEquals(EditCommand.MESSAGE_FAILURE, assertionError.getMessage());
        assertTrue(assertionError.getCause() instanceof CommandException);
        assertEquals(Messages.MESSAGE_DUPLICATE_PERSON, assertionError.getCause().getMessage());
    }

    @Test
    public void equals() {
        Email firstEmail = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()).getEmail();
        Email secondEmail = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased()).getEmail();
        final EditCommand standardCommand = new EditCommand(firstEmail, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(firstEmail, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different email -> returns false
        assertFalse(standardCommand.equals(new EditCommand(secondEmail, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(firstEmail, DESC_BOB)));
    }

    @Test
    public void execute_editEmailToMatchAnotherPerson_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person secondPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());

        // edit second person's email to match first person's email
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withEmail(firstPerson.getEmail().value).build();
        EditCommand editCommand = new EditCommand(secondPerson.getEmail(), descriptor);

        assertCommandFailure(editCommand, model,
                String.format(Messages.MESSAGE_DUPLICATE_PERSON, firstPerson.getName(), firstPerson.getEmail()));
    }

    @Test
    public void execute_editEmailWithLinkedProducts_updatesActiveAndArchived() throws Exception {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Email oldEmail = personToEdit.getEmail();
        String newEmailValue = "updated.vendor@example.com";

        Product linkedActive = new ProductBuilder()
                .withIdentifier("SKU-700")
                .withName("Linked Active")
                .withVendorEmail(oldEmail.value)
                .build();
        Product linkedArchived = new ProductBuilder()
                .withIdentifier("SKU-701")
                .withName("Linked Archived")
                .withVendorEmail(oldEmail.value)
                .build()
                .archive();
        Product unlinked = new ProductBuilder()
                .withIdentifier("SKU-702")
                .withName("Unlinked")
                .withVendorEmail("someone@example.com")
                .build();

        model.addProduct(linkedActive);
        model.addProduct(linkedArchived);
        model.addProduct(unlinked);

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withEmail(newEmailValue).build();
        CommandResult result = new EditCommand(oldEmail, descriptor).execute(model);

        Product updatedActive = getProductByIdentifier(model, "SKU-700");
        Product updatedArchived = getProductByIdentifier(model, "SKU-701");
        Product unchangedUnlinked = getProductByIdentifier(model, "SKU-702");

        assertEquals(newEmailValue, updatedActive.getVendorEmail().orElseThrow().value);
        assertEquals(newEmailValue, updatedArchived.getVendorEmail().orElseThrow().value);
        assertTrue(updatedArchived.isArchived());
        assertEquals("someone@example.com", unchangedUnlinked.getVendorEmail().orElseThrow().value);
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, result.getFeedbackType());
    }

    @Test
    public void execute_editEmailWithLinkedProducts_undoRevertsPersonAndProducts() throws Exception {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Email oldEmail = personToEdit.getEmail();
        String newEmailValue = "atomic.undo@example.com";

        Product linkedActive = new ProductBuilder()
                .withIdentifier("SKU-710")
                .withName("Linked Active Undo")
                .withVendorEmail(oldEmail.value)
                .build();
        Product linkedArchived = new ProductBuilder()
                .withIdentifier("SKU-711")
                .withName("Linked Archived Undo")
                .withVendorEmail(oldEmail.value)
                .build()
                .archive();
        model.addProduct(linkedActive);
        model.addProduct(linkedArchived);
        model.commitVendorVault("Setup for undo test");

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withEmail(newEmailValue).build();
        new EditCommand(oldEmail, descriptor).execute(model);

        assertTrue(model.findByEmail(new Email(newEmailValue)).isPresent());

        model.undoVendorVault();

        assertTrue(model.findByEmail(oldEmail).isPresent());
        assertFalse(model.findByEmail(new Email(newEmailValue)).isPresent());
    }

    @Test
    public void execute_editEmailWithTagClearConfirmation_updatesLinkedProductsOnConfirm() throws Exception {
        Person personWithTag = new PersonBuilder()
                .withName("Cole")
                .withPhone("90001001")
                .withEmail("cole@example.com")
                .withAddress("101 Cole Street")
                .withTags("vip")
                .build();
        model.addPerson(personWithTag);

        Product linkedProduct = new ProductBuilder()
                .withIdentifier("SKU-703")
                .withName("Linked Confirmation")
                .withVendorEmail(personWithTag.getEmail().value)
                .build();
        model.addProduct(linkedProduct);

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withEmail("cole.updated@example.com")
                .withTags()
                .build();
        EditCommand editCommand = new EditCommand(personWithTag.getEmail(), descriptor);

        editCommand.execute(model);
        PendingConfirmation pendingConfirmation = editCommand.getPendingConfirmation();
        new ConfirmCommand(pendingConfirmation.getOnConfirm()).execute(model);

        Product updatedLinked = getProductByIdentifier(model, "SKU-703");
        assertEquals("cole.updated@example.com", updatedLinked.getVendorEmail().orElseThrow().value);
    }

    @Test
    public void execute_editWithoutEmailChange_linkedProductsRemainUnchanged() throws Exception {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Product linkedProduct = new ProductBuilder()
                .withIdentifier("SKU-704")
                .withName("Linked No Change")
                .withVendorEmail(personToEdit.getEmail().value)
                .build();
        model.addProduct(linkedProduct);

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone("90001002").build();
        new EditCommand(personToEdit.getEmail(), descriptor).execute(model);

        Product unchangedLinked = getProductByIdentifier(model, "SKU-704");
        assertEquals(personToEdit.getEmail(), unchangedLinked.getVendorEmail().orElseThrow());
    }

    @Test
    public void toStringMethod() {
        Email email = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()).getEmail();
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(email, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{email=" + email + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

    // =========================================================================
    // Warning-related tests
    // =========================================================================

    @Test
    public void execute_editPhoneToMatchAnotherPerson_warn() {
        // EP: edited phone is identical to another existing contact's phone → similar-phone warning
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person secondPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone(firstPerson.getPhone().value).build();
        EditCommand editCommand = new EditCommand(secondPerson.getEmail(), descriptor);

        Person editedPerson = new PersonBuilder(secondPerson)
                .withPhone(firstPerson.getPhone().value)
                .build();
        String warningMessage = String.format(MESSAGE_SIMILAR_PHONE, firstPerson.getName(), firstPerson.getPhone());
        String expectedMessage = String.format(
                EditCommand.MESSAGE_EDIT_PERSON_SUCCESS + "\n" + warningMessage,
                Messages.format(editedPerson));
        CommandResult expectedCommandResult = new CommandResult(expectedMessage, CommandResult.FEEDBACK_TYPE_WARN);

        Model expectedModel = new ModelManager(new VendorVault(model.getVendorVault()), new UserPrefs(), new Aliases());
        expectedModel.setPerson(secondPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_constructorWithPresetWarnings_warningsIncludedInFeedback() throws Exception {
        // EP: preset warning injected at construction time → appears as is in feedback
        Index indexFiona = Index.fromOneBased(6);
        Person fiona = model.getFilteredPersonList().get(indexFiona.getZeroBased());

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone(PHONE_UNIQUE_A)
                .build();

        CommandResult result = new EditCommand(fiona.getEmail(), descriptor, PRESET_WARNING).execute(model);

        assertTrue(result.getFeedbackToUser().contains(PRESET_WARNING));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_editNameToSimilarName_warningShown() throws Exception {
        // EP: new name shares a token with an existing contact's name → similar-name warning
        // "alice" in NAME_ALICE_EDIT_TARGET overlaps with "alice" in ALICE's name "Alice Pauline"
        Person thirdPerson = model.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName(NAME_ALICE_EDIT_TARGET)
                .build();

        CommandResult result = new EditCommand(thirdPerson.getEmail(), descriptor).execute(model);

        assertSimilarNameWarning(result, ALICE);
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_editAddressToSimilarAddress_warningShown() throws Exception {
        // EP: new address is a substring of an existing contact's address → similar-address warning
        Person thirdPerson = model.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withAddress(ADDRESS_JURONG_WEST)
                .build();

        CommandResult result = new EditCommand(thirdPerson.getEmail(), descriptor).execute(model);

        assertSimilarAddressWarning(result, ALICE);
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_editWithoutNameChange_noNameWarning() throws Exception {
        // EP: name field absent from descriptor (nameChanged = false) → no name warning,
        //     even when the existing name shares a token with another contact
        Person aliceSimilar = new PersonBuilder()
                .withName(NAME_ALICE)
                .withPhone(PHONE_UNIQUE_A)
                .withEmail(EMAIL_ALICE)
                .withAddress(ADDRESS_UNIQUE)
                .build();
        model.addPerson(aliceSimilar);

        Person lastPerson = lastPersonInModel();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone(PHONE_UNIQUE_A)
                .build();

        CommandResult result = new EditCommand(lastPerson.getEmail(), descriptor).execute(model);

        assertFalse(result.getFeedbackToUser().contains(String.format(MESSAGE_SIMILAR_NAME, ALICE.getName())));
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, result.getFeedbackType());
    }

    @Test
    public void execute_editWithoutAddressChange_noAddressWarning() throws Exception {
        // EP: address field absent from descriptor (addressChanged = false) → no address warning,
        //     even when the existing address is similar to another contact's
        Person personWithSimilarAddress = new PersonBuilder()
                .withName(NAME_ALICE)
                .withPhone(PHONE_UNIQUE_A)
                .withEmail(EMAIL_ALICE)
                .withAddress(ADDRESS_JURONG_WEST)
                .build();
        model.addPerson(personWithSimilarAddress);

        Person lastPerson = lastPersonInModel();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone(PHONE_UNIQUE_B)
                .build();

        CommandResult result = new EditCommand(lastPerson.getEmail(), descriptor).execute(model);

        assertFalse(result.getFeedbackToUser().contains(
                String.format(MESSAGE_SIMILAR_ADDRESS, ALICE.getName(), ALICE.getAddress())));
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, result.getFeedbackType());
    }

    @Test
    public void execute_editNameToMultipleSimilar_onlyOneWarning() throws Exception {
        // EP: new name matches multiple existing contacts → exactly one name warning (deduplication)
        Person similar1 = buildPerson(NAME_JOHN_1, PHONE_UNIQUE_A, EMAIL_ALICE, null);
        Person similar2 = buildPerson(NAME_JOHN_2, PHONE_UNIQUE_B, EMAIL_JOHN, null);
        model.addPerson(similar1);
        model.addPerson(similar2);

        Person thirdPerson = model.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName(NAME_JOHN_EDIT_TARGET)
                .build();

        CommandResult result = new EditCommand(thirdPerson.getEmail(), descriptor).execute(model);

        assertExactlyOneWarning(result.getFeedbackToUser(),
                String.format(MESSAGE_SIMILAR_NAME, similar1.getName()),
                String.format(MESSAGE_SIMILAR_NAME, similar2.getName()));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_editNameAndAddress_bothWarningsShown() throws Exception {
        // EP: both name and address fields changed and each matches a different contact
        //     → both name warning and address warning appear in feedback
        Person nameSimilar = buildPerson(NAME_JOHN_1, PHONE_UNIQUE_A, EMAIL_JOHN, ADDRESS_NOWHERE);
        model.addPerson(nameSimilar);

        Person thirdPerson = model.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName(NAME_JOHN_EDIT_TARGET)
                .withAddress(ADDRESS_JURONG_WEST)
                .build();

        CommandResult result = new EditCommand(thirdPerson.getEmail(), descriptor).execute(model);

        assertSimilarNameWarning(result, nameSimilar);
        assertSimilarAddressWarning(result, ALICE);
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_editAddressToMultipleSimilar_onlyOneWarning() throws Exception {
        // EP: new address matches multiple existing contacts → exactly one address warning (deduplication)
        Person anotherJurong = buildPerson(NAME_JOHN_1, PHONE_UNIQUE_A,
                EMAIL_JOHN, ADDRESS_JURONG_WEST_AVE5);
        model.addPerson(anotherJurong);

        Person thirdPerson = model.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withAddress(ADDRESS_JURONG_WEST)
                .build();

        CommandResult result = new EditCommand(thirdPerson.getEmail(), descriptor).execute(model);

        assertExactlyOneWarning(result.getFeedbackToUser(),
                String.format(MESSAGE_SIMILAR_ADDRESS, ALICE.getName(), ALICE.getAddress()),
                String.format(MESSAGE_SIMILAR_ADDRESS, anotherJurong.getName(), anotherJurong.getAddress()));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    /**
     * Builds a person with one {@value TAG_VIP} tag, adds them to the model, and returns the built person.
     * Used by the clear-tags confirmation tests to avoid repeating the same PersonBuilder block.
     */
    private Person buildPersonWithTagAndAdd(String name, String phone, String email, String address) {
        Person person = new PersonBuilder()
                .withName(name).withPhone(phone).withEmail(email).withAddress(address)
                .withTags(TAG_VIP).build();
        model.addPerson(person);
        return person;
    }

    /** Returns the last person currently in the filtered list. */
    private Person lastPersonInModel() {
        int lastIndex = model.getFilteredPersonList().size();
        return model.getFilteredPersonList().get(Index.fromOneBased(lastIndex).getZeroBased());
    }

    /**
     * Builds {@link Person} with default address when {@code address} is null.
     */
    private static Person buildPerson(String name, String phone, String email, String address) {
        PersonBuilder builder = new PersonBuilder()
                .withName(name)
                .withPhone(phone)
                .withEmail(email);
        if (address != null) {
            builder = builder.withAddress(address);
        }
        return builder.build();
    }

}
