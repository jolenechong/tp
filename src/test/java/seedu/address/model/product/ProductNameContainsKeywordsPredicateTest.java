package seedu.address.model.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.ProductBuilder;

public class ProductNameContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        ProductNameContainsKeywordsPredicate firstPredicate =
                new ProductNameContainsKeywordsPredicate(firstPredicateKeywordList);
        ProductNameContainsKeywordsPredicate secondPredicate =
                new ProductNameContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        ProductNameContainsKeywordsPredicate firstPredicateCopy =
                new ProductNameContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different person -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // One keyword
        ProductNameContainsKeywordsPredicate predicate =
                new ProductNameContainsKeywordsPredicate(Collections.singletonList("SSD"));
        assertTrue(predicate.test(new ProductBuilder().withName("SSD 2TB").build()));

        // Multiple keywords
        predicate = new ProductNameContainsKeywordsPredicate(Arrays.asList("SSD", "2TB"));
        assertTrue(predicate.test(new ProductBuilder().withName("SSD 2TB").build()));

        // Only one matching keyword
        predicate = new ProductNameContainsKeywordsPredicate(Arrays.asList("RAM", "2TB"));
        assertTrue(predicate.test(new ProductBuilder().withName("SSD 2TB").build()));

        // Mixed-case keywords
        predicate = new ProductNameContainsKeywordsPredicate(Arrays.asList("sSd", "2tB"));
        assertTrue(predicate.test(new ProductBuilder().withName("SSD 2TB").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        ProductNameContainsKeywordsPredicate predicate =
                new ProductNameContainsKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(new ProductBuilder().withName("SSD").build()));

        // Non-matching keyword
        predicate = new ProductNameContainsKeywordsPredicate(Arrays.asList("RAM"));
        assertFalse(predicate.test(new ProductBuilder().withName("SSD 2TB").build()));

        // Keywords match id, email, threshold and quantity, but does not match name
        predicate = new ProductNameContainsKeywordsPredicate(
                Arrays.asList("DE/339", "support.rochor@yahoo.com", "10", "10"));
        assertFalse(predicate.test(new ProductBuilder().withName("NVMe SSD 2TB").withIdentifier("DE/339")
                .withQuantity("10").withThreshold("10").withVendorEmail("support.rochor@yahoo.com").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        ProductNameContainsKeywordsPredicate predicate = new ProductNameContainsKeywordsPredicate(keywords);

        String expected = ProductNameContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
