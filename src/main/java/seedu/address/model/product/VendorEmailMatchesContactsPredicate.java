package seedu.address.model.product;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;

/**
 * Tests that a {@code Product}'s vendor email is in the provided email list.
 */
public class VendorEmailMatchesContactsPredicate implements Predicate<Product> {
    private final Set<Person> contacts;

    /**
     * Initializes a predicate that matches products whose vendor emails are in the given list of contacts.
     *
     * @param contacts List of contacts whose emails are used for matching.
     */
    public VendorEmailMatchesContactsPredicate(List<Person> contacts) {
        requireNonNull(contacts);
        this.contacts = Set.copyOf(contacts);
    }

    @Override
    public boolean test(Product product) {
        requireNonNull(product);

        if (product.isArchived()) {
            return false;
        }

        return product.getVendorEmail()
                .map(email -> contacts.stream()
                        .anyMatch(contact -> contact.getEmail().equals(email)))
                .orElse(false);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof VendorEmailMatchesContactsPredicate)) {
            return false;
        }

        VendorEmailMatchesContactsPredicate otherPredicate = (VendorEmailMatchesContactsPredicate) other;
        return contacts.equals(otherPredicate.contacts);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("emails", contacts
        ).toString();
    }
}
