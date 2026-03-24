package seedu.address.model.product;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Product}'s {@code Name} matches any of the keywords given.
 */
public class ProductNameContainsKeywordsPredicate implements Predicate<Product> {
    private final List<String> keywords;

    /**
     * Creates a predicate that matches any full-word keyword in a product name.
     *
     */
    public ProductNameContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = List.copyOf(keywords);
    }

    @Override
    public boolean test(Product product) {
        return keywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(product.getName().fullName, keyword));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ProductNameContainsKeywordsPredicate)) {
            return false;
        }

        ProductNameContainsKeywordsPredicate otherProductNameContainsKeywordsPredicate =
                (ProductNameContainsKeywordsPredicate) other;

        return keywords.equals(otherProductNameContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }

}
