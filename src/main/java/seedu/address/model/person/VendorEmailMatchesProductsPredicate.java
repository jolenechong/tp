package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.product.Product;

/**
 * Tests that a {@code Person}'s email is in the vendor-email set of the provided products.
 */
public class VendorEmailMatchesProductsPredicate implements Predicate<Person> {
    private final Set<Product> products;

    /**
     * Initializes a predicate that matches contacts whose emails are in the given list of products.
     *
     * @param products List of products whose vendor emails are used for matching.
     */
    public VendorEmailMatchesProductsPredicate(List<Product> products) {
        requireNonNull(products);
        this.products = Set.copyOf(products);
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);

        if (person.isArchived()) {
            return false;
        }

        return products.stream()
                .filter(product -> !product.isArchived())
                .anyMatch(product -> product.getVendorEmail()
                        .map(email -> person.getEmail().equals(email))
                        .orElse(false));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof VendorEmailMatchesProductsPredicate)) {
            return false;
        }

        VendorEmailMatchesProductsPredicate otherPredicate = (VendorEmailMatchesProductsPredicate) other;
        return products.equals(otherPredicate.products);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("products", products).toString();
    }
}
