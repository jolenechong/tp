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
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE_PERSONS;
import static seedu.address.model.person.warnings.DuplicatePersonWarning.MESSAGE_SIMILAR_ADDRESS;
import static seedu.address.model.person.warnings.DuplicatePersonWarning.formatNameWarning;
import static seedu.address.model.person.warnings.DuplicatePersonWarning.formatPhoneWarning;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.NameEqualsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.product.Product;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";
    public static final String COMMAND_USAGE = COMMAND_WORD + " EMAIL"
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_TAG + "TAG]...";
    public static final String COMMAND_DESCRIPTION = "Edits a contact's details.";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the vendor contact identified "
            + "by contact email. "
            + "Add '-y' to skip confirmation when clearing all tags. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: TARGET_EMAIL (must exist in displayed list) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " johndoe@example.com "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Contact: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";

    /**
     * If tags weren't meant to be cleared, it's likely that they entered the Edit command wrong,
     * so we cancel the operation instead of continuing with the rest of the edit.
     */
    public static final String CONFIRMATION_CLEAR_TAGS_MESSAGE =
            "Confirm (y) you want to clear ALL tags for this contact, otherwise the edit operation will be cancelled:";

    public static final String MESSAGE_CLEAR_TAGS_CANCELLED = "Edit cancelled and tags were not cleared.";
    public static final String MESSAGE_FAILURE = "Confirmed edit unexpectedly failed";

    private final Email email;
    private final EditPersonDescriptor editPersonDescriptor;
    private final boolean needsConfirmation;
    private final String initialWarnings;
    private PendingConfirmation pendingConfirmation = new PendingConfirmation();

    /**
     * @param email of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Email email, EditPersonDescriptor editPersonDescriptor) {
        this(email, editPersonDescriptor, EMPTY_STRING, true);
    }

    /**
     * @param email of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     * @param needsConfirmation whether this command should prompt before clearing all tags
     */
    public EditCommand(Email email, EditPersonDescriptor editPersonDescriptor, boolean needsConfirmation) {
        this(email, editPersonDescriptor, EMPTY_STRING, needsConfirmation);
    }

    /**
     * Constructor for EditCommand with warnings to show after success.
     *
     * @param email of the person in the filtered person list to edit.
     * @param editPersonDescriptor details to edit the person with.
     * @param warnings warnings to show after success.
     */
    public EditCommand(Email email, EditPersonDescriptor editPersonDescriptor, String warnings) {
        this(email, editPersonDescriptor, warnings, true);
    }

    /**
     * Full constructor for EditCommand with warnings and confirmation control.
     */
    public EditCommand(Email email, EditPersonDescriptor editPersonDescriptor,
                       String initialWarnings, boolean needsConfirmation) {
        requireNonNull(email);
        requireNonNull(editPersonDescriptor);
        requireNonNull(initialWarnings);

        this.email = email;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
        this.initialWarnings = initialWarnings;
        this.needsConfirmation = needsConfirmation;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Person personToEdit = model.findByEmail(email)
                .orElseThrow(() ->
                        new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_EMAIL));
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        validateNoDuplicate(personToEdit, editedPerson, model);

        String allWarnings = buildWarnings(editedPerson, personToEdit, model, initialWarnings);

        if (shouldConfirmTagClear() && needsConfirmation) {
            return createConfirmationPrompt(model, personToEdit, editedPerson, allWarnings);
        }

        applyEdit(model, personToEdit, editedPerson);

        return buildCommandResult(editedPerson, allWarnings);
    }

    private CommandResult buildCommandResult(Person editedPerson, String allWarnings) {
        String formattedWarnings = allWarnings.isEmpty() ? "" : SEPARATOR_NEW_LINE + allWarnings;
        String feedbackType = allWarnings.isEmpty()
                ? CommandResult.FEEDBACK_TYPE_SUCCESS
                : CommandResult.FEEDBACK_TYPE_WARN;

        return new CommandResult(
                formatSuccessPart(editedPerson) + formattedWarnings,
                feedbackType);
    }

    private CommandResult createConfirmationPrompt(Model model, Person personToEdit,
                                                   Person editedPerson, String allWarnings) {
        pendingConfirmation = new PendingConfirmation(()
                -> onConfirm(model, personToEdit, editedPerson, allWarnings.toString()), ()
                -> onCancel(model));

        NameEqualsKeywordsPredicate predicate = new NameEqualsKeywordsPredicate(personToEdit);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(CONFIRMATION_CLEAR_TAGS_MESSAGE);
    }

    private String buildWarnings(Person editedPerson, Person personToEdit, Model model, String originalWarnings) {
        StringBuilder warningsBuilder = new StringBuilder(originalWarnings);
        appendSimilarContactWarnings(editedPerson, personToEdit, model, warningsBuilder, editPersonDescriptor);
        return warningsBuilder.toString();
    }

    private void validateNoDuplicate(Person personToEdit, Person editedPerson, Model model) throws CommandException {
        for (Person existingPerson : model.getFilteredPersonList()) {
            if (existingPerson.equals(personToEdit)) {
                continue;
            }
            if (editedPerson.isSamePerson(existingPerson)) {
                throw new CommandException(MESSAGE_DUPLICATE_PERSON);
            }
        }
    }

    private boolean shouldConfirmTagClear() {
        return editPersonDescriptor.getTags().isPresent() && editPersonDescriptor.getTags().get().isEmpty();
    }

    private Optional<CommandResult> onConfirm(Model model, Person personToEdit, Person editedPerson,
                                              String allWarnings) {
        // all user errors should have already been validated before confirmation
        try {
            applyEdit(model, personToEdit, editedPerson);
            return Optional.of(buildCommandResult(editedPerson, allWarnings));
        } catch (CommandException e) {
            throw new AssertionError(MESSAGE_FAILURE, e);
        }
    }

    private Optional<CommandResult> onCancel(Model model) {
        model.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS);
        return Optional.of(new CommandResult(MESSAGE_CLEAR_TAGS_CANCELLED));
    }

    private void applyEdit(Model model, Person personToEdit, Person editedPerson)
            throws CommandException {

        try {
            model.setPerson(personToEdit, editedPerson);
        } catch (DuplicatePersonException e) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        Email oldEmail = personToEdit.getEmail();
        Email newEmail = editedPerson.getEmail();
        if (!oldEmail.equals(newEmail)) {
            List<Product> linkedProducts = VendorProductLinkUtil.collectLinkedProducts(model, oldEmail);
            VendorProductLinkUtil.updateVendorEmail(model, linkedProducts, newEmail);
        }

        model.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS);
        model.commitVendorVault(formatSuccessPart(editedPerson));
    }

    private String formatSuccessPart(Person editedPerson) {
        return String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return pendingConfirmation;
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());

        Set<Tag> finalTags = new HashSet<>(updatedTags);

        if (personToEdit.isArchived()) {
            finalTags.add(new Tag("archived"));
        }

        return new Person(updatedName, updatedPhone, updatedEmail, updatedAddress, finalTags);
    }

    /**
     * Checks for similar contacts in the model and appends warnings if found.
     * Only checks for warnings on fields that were actually edited.
     *
     * @param editedPerson The person after editing
     * @param personToEdit The original person being edited (to skip during comparison)
     * @param model The model containing the list of persons
     * @param warnings StringBuilder to append warnings to
     * @param descriptor The descriptor containing which fields were edited
     */
    private void appendSimilarContactWarnings(Person editedPerson, Person personToEdit, Model model,
                                              StringBuilder warnings, EditPersonDescriptor descriptor) {
        if (descriptor.getName().isPresent()) {
            model.findSimilarNameMatch(editedPerson, personToEdit).ifPresent(match ->
                    appendWarning(warnings, formatNameWarning(match.getName())));
        }

        if (descriptor.getPhone().isPresent()) {
            model.findSimilarPhoneMatch(editedPerson, personToEdit).ifPresent(match ->
                    appendWarning(warnings, formatPhoneWarning(match.getName(), match.getPhone())));
        }

        if (descriptor.getAddress().isPresent()) {
            model.findSimilarAddressMatch(editedPerson, personToEdit).ifPresent(match ->
                    appendWarning(warnings, String.format(
                            MESSAGE_SIMILAR_ADDRESS, match.getName(), match.getAddress())));
        }
    }

    private void appendWarning(StringBuilder warnings, String message) {
        if (warnings.length() > 0) {
            warnings.append(SEPARATOR_NEW_LINE);
        }
        warnings.append(message);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return email.equals(otherEditCommand.email)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("email", email)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Set<Tag> tags;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            EditPersonDescriptor otherEditPersonDescriptor = (EditPersonDescriptor) other;
            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(address, otherEditPersonDescriptor.address)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("tags", tags)
                    .toString();
        }
    }
}
