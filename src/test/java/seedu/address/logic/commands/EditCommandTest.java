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
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.model.person.warnings.DuplicatePersonWarning.MESSAGE_SIMILAR_ADDRESS;
import static seedu.address.model.person.warnings.DuplicatePersonWarning.MESSAGE_SIMILAR_NAME;
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
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private static final Email NON_EXISTENT_EMAIL = new Email("missing.person@example.com");

    private Model model = new ModelManager(new VendorVault(
            getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedPerson = new PersonBuilder().withTags("vip").build();
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

        assertCommandFailure(editCommand, model, Messages.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in address book
        Person personInList = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(personInFilteredList.getEmail(),
                new EditPersonDescriptorBuilder(personInList).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_DUPLICATE_PERSON);
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

    @Test
    public void execute_clearTags_requiresConfirmation() throws Exception {
        // check confirmation is shown
        Person personWithTag = new PersonBuilder()
                .withName("Tag Keeper")
                .withPhone("90000011")
                .withEmail("tag.keeper@example.com")
                .withAddress("11 Tag Street")
                .withTags("vip")
                .build();
        model.addPerson(personWithTag);
        int expectedSizeAfterReset = model.getFilteredPersonList().size();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();
        EditCommand editCommand = new EditCommand(personWithTag.getEmail(), descriptor);

        CommandResult result = editCommand.execute(model);

        assertEquals(EditCommand.CONFIRMATION_CLEAR_TAGS_MESSAGE, result.getFeedbackToUser());
        assertTrue(editCommand.getPendingConfirmation().getNeedConfirmation());
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(personWithTag.getEmail(), model.getFilteredPersonList().get(0).getEmail());
        assertTrue(model.findByEmail(personWithTag.getEmail()).orElseThrow().getTags().contains(new Tag("vip")));

        // sanity check: cancelling should restore the list
        new CancelCommand(editCommand.getPendingConfirmation().getOnCancel()).execute(model);
        assertEquals(expectedSizeAfterReset, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_clearTagsConfirm_tagsAreCleared() throws Exception {
        // check confirmation is shown and confirming clears the tags
        Person personWithTag = new PersonBuilder()
                .withName("Tag Confirm")
                .withPhone("90000012")
                .withEmail("tag.confirm@example.com")
                .withAddress("12 Tag Street")
                .withTags("vip")
                .build();
        model.addPerson(personWithTag);
        int expectedSizeAfterReset = model.getFilteredPersonList().size();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();
        EditCommand editCommand = new EditCommand(personWithTag.getEmail(), descriptor);
        editCommand.execute(model);
        assertEquals(1, model.getFilteredPersonList().size());

        PendingConfirmation pendingConfirmation = editCommand.getPendingConfirmation();
        CommandResult confirmResult = new ConfirmCommand(pendingConfirmation.getOnConfirm()).execute(model);

        assertTrue(confirmResult.getFeedbackToUser().startsWith("Edited Contact:"));
        assertTrue(model.findByEmail(personWithTag.getEmail()).orElseThrow().getTags().isEmpty());
        assertEquals(expectedSizeAfterReset, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_clearTagsCancel_tagsUnchanged() throws Exception {
        // check cancelling confirmation leaves tags unchanged
        Person personWithTag = new PersonBuilder()
                .withName("Tag Cancel")
                .withPhone("90000013")
                .withEmail("tag.cancel@example.com")
                .withAddress("13 Tag Street")
                .withTags("vip")
                .build();
        model.addPerson(personWithTag);
        int expectedSizeAfterReset = model.getFilteredPersonList().size();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();
        EditCommand editCommand = new EditCommand(personWithTag.getEmail(), descriptor);
        editCommand.execute(model);
        assertEquals(1, model.getFilteredPersonList().size());

        PendingConfirmation pendingConfirmation = editCommand.getPendingConfirmation();
        CommandResult cancelResult = new CancelCommand(pendingConfirmation.getOnCancel()).execute(model);

        assertEquals(EditCommand.MESSAGE_CLEAR_TAGS_CANCELLED, cancelResult.getFeedbackToUser());
        assertTrue(model.findByEmail(personWithTag.getEmail()).orElseThrow().getTags().contains(new Tag("vip")));
        assertEquals(expectedSizeAfterReset, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_clearTagsSkipConfirmation_tagsClearedImmediately() throws Exception {
        // check skip confirmation
        Person personWithTag = new PersonBuilder()
                .withName("Tag Skip")
                .withPhone("90000014")
                .withEmail("tag.skip@example.com")
                .withAddress("14 Tag Street")
                .withTags("vip")
                .build();
        model.addPerson(personWithTag);

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();
        EditCommand editCommand = new EditCommand(personWithTag.getEmail(), descriptor, false);

        CommandResult result = editCommand.execute(model);

        assertTrue(result.getFeedbackToUser().startsWith("Edited Contact:"));
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
    public void execute_editPhoneToMatchAnotherPerson_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person secondPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());

        // edit second person's phone to match first person's phone
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone(firstPerson.getPhone().value).build();
        EditCommand editCommand = new EditCommand(secondPerson.getEmail(), descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_editEmailToMatchAnotherPerson_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person secondPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());

        // edit second person's email to match first person's email
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withEmail(firstPerson.getEmail().value).build();
        EditCommand editCommand = new EditCommand(secondPerson.getEmail(), descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_DUPLICATE_PERSON);
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

    // ==================== Warning-related tests ====================

    @Test
    public void execute_constructorWithPresetWarnings_warningsIncludedInFeedback() throws Exception {
        // Edit FIONA (index 6) with only a phone change and a preset warning.
        // FIONA's name "Fiona Kunz" has no similarity with any typical person, so no extra
        // warnings are appended — the only warning in the result is the preset one.
        String presetWarning = "Pre-existing warning message";
        Index indexFiona = Index.fromOneBased(6);
        Person fiona = model.getFilteredPersonList().get(indexFiona.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone("55555500")
                .build();
        EditCommand editCommand = new EditCommand(fiona.getEmail(), descriptor, presetWarning);

        CommandResult result = editCommand.execute(model);

        assertTrue(result.getFeedbackToUser().contains(presetWarning));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_editNameToSimilarName_warningShown() throws Exception {
        // Edit CARL (INDEX_THIRD_PERSON) name to "Alice Kurz".
        // "alice" in "Alice Kurz" overlaps with "alice" in ALICE's name "Alice Pauline"
        // so similar name warning should be appended.
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName("Alice Kurz")
                .build();
        Person thirdPerson = model.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(thirdPerson.getEmail(), descriptor);

        CommandResult result = editCommand.execute(model);

        assertTrue(result.getFeedbackToUser().contains(
                String.format(MESSAGE_SIMILAR_NAME, ALICE.getName())));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_editAddressToSimilarAddress_warningShown() throws Exception {
        // Edit CARL (INDEX_THIRD_PERSON) address to "Jurong West".
        // ALICE's address "123, Jurong West Ave 6, #08-111" contains "Jurong West" as a
        // substring so similar address warning should be appended.
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withAddress("Jurong West")
                .build();
        Person thirdPerson = model.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(thirdPerson.getEmail(), descriptor);

        CommandResult result = editCommand.execute(model);

        assertTrue(result.getFeedbackToUser().contains(
                String.format(MESSAGE_SIMILAR_ADDRESS, ALICE.getName(), ALICE.getAddress())));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_editWithoutNameChange_noNameWarning() throws Exception {
        // Add a person whose name is similar to ALICE ("alice" token shared).
        Person aliceSimilar = new PersonBuilder()
                .withName("Alice Brown")
                .withPhone("55555555")
                .withEmail("alice.brown@example.com")
                .withAddress("some address far away")
                .build();
        model.addPerson(aliceSimilar);

        // Edit only the phone (nameChanged = false).
        // Even though "Alice Brown" shares a name token with ALICE
        // there should be no name warning since the name field was not part of the edit descriptor.
        Index indexLast = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLast.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone("66666666")
                .build();
        EditCommand editCommand = new EditCommand(lastPerson.getEmail(), descriptor);

        CommandResult result = editCommand.execute(model);

        assertFalse(result.getFeedbackToUser().contains(String.format(MESSAGE_SIMILAR_NAME, ALICE.getName())));
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, result.getFeedbackType());
    }

    @Test
    public void execute_editWithoutAddressChange_noAddressWarning() throws Exception {
        // Add a person whose address is similar to ALICE's ("Jurong West" ⊂ ALICE's address).
        Person personWithSimilarAddress = new PersonBuilder()
                .withName("Unique XYZ Person")
                .withPhone("55555556")
                .withEmail("unique.xyz@example.com")
                .withAddress("Jurong West")
                .build();
        model.addPerson(personWithSimilarAddress);

        // Edit only the phone (addressChanged = false).
        // Even though "Jurong West" is similar to ALICE's address, no address warning should
        // fire because the address field was not part of the edit descriptor.
        Index indexLast = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLast.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone("66666667")
                .build();
        EditCommand editCommand = new EditCommand(lastPerson.getEmail(), descriptor);

        CommandResult result = editCommand.execute(model);

        assertFalse(result.getFeedbackToUser().contains(
                String.format(MESSAGE_SIMILAR_ADDRESS, ALICE.getName(), ALICE.getAddress())));
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, result.getFeedbackType());
    }

    @Test
    public void execute_editNameToMultipleSimilar_onlyOneWarning() throws Exception {
        // Add two persons whose names share "john" with the new name we will edit to.
        Person similar1 = new PersonBuilder()
                .withName("John Doe")
                .withPhone("55555561")
                .withEmail("john.doe.extra@example.com")
                .build();
        Person similar2 = new PersonBuilder()
                .withName("John Smith")
                .withPhone("55555562")
                .withEmail("john.smith.extra@example.com")
                .build();
        model.addPerson(similar1);
        model.addPerson(similar2);

        // Edit CARL's name to "John Kurz" — "john" overlaps with both similar1 and similar2.
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName("John Kurz")
                .build();
        Person thirdPerson = model.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(thirdPerson.getEmail(), descriptor);

        CommandResult result = editCommand.execute(model);
        String feedback = result.getFeedbackToUser();

        // Exactly one of the two possible warnings appears
        assertTrue(feedback.contains(String.format(MESSAGE_SIMILAR_NAME, similar1.getName()))
                || feedback.contains(String.format(MESSAGE_SIMILAR_NAME, similar2.getName())));
        // but not both (deduplication must work).
        assertFalse(feedback.contains(String.format(MESSAGE_SIMILAR_NAME, similar1.getName()))
                && feedback.contains(String.format(MESSAGE_SIMILAR_NAME, similar2.getName())));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_editNameAndAddress_bothWarningsShown() throws Exception {
        // Add a person to trigger a name warning ("john" token shared with the new name).
        Person nameSimilar = new PersonBuilder()
                .withName("John Doe")
                .withPhone("55555570")
                .withEmail("johndoe.extra@example.com")
                .withAddress("99 Nowhere Street")
                .build();
        model.addPerson(nameSimilar);

        // Edit CARL: new name "John Kurz" so name warning (shares "john" with "John Doe");
        //            new address "Jurong West" so address warning (contained in ALICE's address).
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName("John Kurz")
                .withAddress("Jurong West")
                .build();
        Person thirdPerson = model.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(thirdPerson.getEmail(), descriptor);

        CommandResult result = editCommand.execute(model);

        assertTrue(result.getFeedbackToUser().contains(
                String.format(MESSAGE_SIMILAR_NAME, nameSimilar.getName())));
        assertTrue(result.getFeedbackToUser().contains(
                String.format(MESSAGE_SIMILAR_ADDRESS, ALICE.getName(), ALICE.getAddress())));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_editAddressToMultipleSimilar_onlyOneWarning() throws Exception {
        // ALICE already has "123, Jurong West Ave 6, #08-111" in the typical model.
        // Add a second person whose address also contains "Jurong West".
        Person anotherJurong = new PersonBuilder()
                .withName("Another Person")
                .withPhone("55555581")
                .withEmail("another.jurong@example.com")
                .withAddress("Jurong West Ave 5")
                .build();
        model.addPerson(anotherJurong);

        // Edit CARL's address to "Jurong West" — both ALICE and anotherJurong are similar.
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withAddress("Jurong West")
                .build();
        Person thirdPerson = model.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(thirdPerson.getEmail(), descriptor);

        CommandResult result = editCommand.execute(model);
        String feedback = result.getFeedbackToUser();

        // Exactly one address warning should appear — not both.
        assertTrue(feedback.contains(String.format(MESSAGE_SIMILAR_ADDRESS, ALICE.getName(), ALICE.getAddress()))
                || feedback.contains(String.format(MESSAGE_SIMILAR_ADDRESS,
                        anotherJurong.getName(), anotherJurong.getAddress())));
        assertFalse(feedback.contains(String.format(MESSAGE_SIMILAR_ADDRESS, ALICE.getName(), ALICE.getAddress()))
                && feedback.contains(String.format(MESSAGE_SIMILAR_ADDRESS,
                        anotherJurong.getName(), anotherJurong.getAddress())));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

}
