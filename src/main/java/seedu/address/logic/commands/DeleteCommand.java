package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE_PERSONS;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Email;
import seedu.address.model.person.NameEqualsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.product.Identifier;
import seedu.address.model.product.Product;

/**
 * Deletes a person identified using it's displayed index from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by email used in the displayed person list.\n"
            + "Parameters: Email \n"
            + "Example: " + COMMAND_WORD + " irfam@example.com";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    public static final String MESSAGE_PRODUCTS_DELINKED =
            "⚠ Warning: %1$d product(s) became unassociated from contact (%2$s).";

    public static final String CONFIRMATION_DELETE_PERSON_MESSAGE =
            "Confirm (y) you want to delete the following person shown below:";

    public static final String MESSAGE_DELETE_FAILURE = "Did not delete person";

    private PendingConfirmation pendingConfirmation = new PendingConfirmation();

    private final Email targetEmail;
    private final boolean needsConfirmation;

    /**
     * Creates a DeleteCommand to delete the person at the specified {@code targetIndexString}.
     *
     */
    public DeleteCommand(Email targetEmail, boolean needsConfirmation) {
        this.targetEmail = targetEmail;
        this.needsConfirmation = needsConfirmation;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        Person personToDelete = model.findByEmail(targetEmail)
                .orElseThrow(() ->
                        new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_EMAIL));

        if (!this.needsConfirmation) {
            return this.deletePerson(model, personToDelete);
        }

        pendingConfirmation = new PendingConfirmation(() ->
                this.onConfirm(model, personToDelete), () -> this.onCancel(model));

        NameEqualsKeywordsPredicate predicate = new NameEqualsKeywordsPredicate(personToDelete);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(CONFIRMATION_DELETE_PERSON_MESSAGE);
    }

    /**
     * Deletes the specified person from the model and commits the change.
     * After deletion, the list will show all the contacts.
     *
     */
    public CommandResult deletePerson(Model model, Person personToDelete) {
        List<Product> linkedProducts = collectLinkedProducts(model, personToDelete);

        // Unlink products first
        for (Product linkedProduct : linkedProducts) {
            Product unlinkedProduct = linkedProduct.clearVendorEmail();
            model.setProduct(linkedProduct, unlinkedProduct);
        }

        model.deletePerson(personToDelete);

        model.commitVendorVault();
        model.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS);

        String successMessage = String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete));
        if (linkedProducts.isEmpty()) {
            return new CommandResult(successMessage);
        }

        String linkedProductIds = linkedProducts.stream()
                .map(Product::getIdentifier)
                .map(Identifier::toString)
                .collect(Collectors.joining(", "));
        String warning = String.format(MESSAGE_PRODUCTS_DELINKED, linkedProducts.size(), linkedProductIds);

        return new CommandResult(successMessage + "/n" + warning, CommandResult.FEEDBACK_TYPE_WARN);
    }

    /**
     * Returns products currently linked to the given person via vendor email.
     */
    List<Product> collectLinkedProducts(Model model, Person personToDelete) {
        List<Product> linkedProducts = new ArrayList<>();
        ObservableList<Product> productList = model.getInventory().getProductList();

        for (Product product : productList) {
            if (product.getVendorEmail().isPresent()) {
                Email vendorEmail = product.getVendorEmail().get();
                if (vendorEmail.equals(personToDelete.getEmail())) {
                    linkedProducts.add(product);
                }
            }
        }

        return linkedProducts;
    }

    /**
     * Executes the deletion of specified person from model upon confirmation
     * Resets the displayed person list to show all the person after deletion
     *
     */
    public Optional<CommandResult> onConfirm(Model model, Person personToDelete) {
        return Optional.of(this.deletePerson(model, personToDelete));
    }

    /**
     * Cancels the deletion and reset the displayed person list to show all persons.
     *
     */
    public Optional<CommandResult> onCancel(Model model) {
        model.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS);
        return Optional.of(new CommandResult(
                String.format(MESSAGE_DELETE_FAILURE)));
    }

    @Override
    public PendingConfirmation getPendingConfirmation() {
        return this.pendingConfirmation;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand)) {
            return false;
        }

        DeleteCommand otherDeleteCommand = (DeleteCommand) other;
        return targetEmail.equals(otherDeleteCommand.targetEmail);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetEmail", targetEmail)
                .toString();
    }
}
