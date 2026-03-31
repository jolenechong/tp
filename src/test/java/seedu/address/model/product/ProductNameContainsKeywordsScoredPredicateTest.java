package seedu.address.model.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.search.FindRelevance.MatchTier;
import seedu.address.model.search.FindRelevance.Score;
import seedu.address.testutil.ProductBuilder;

class ProductNameContainsKeywordsScoredPredicateTest {

    @Test
    void constructor_nullKeywords_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ProductNameContainsKeywordsScoredPredicate(null));
    }

    @Test
    void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        ProductNameContainsKeywordsScoredPredicate firstPredicate =
                new ProductNameContainsKeywordsScoredPredicate(firstPredicateKeywordList);
        ProductNameContainsKeywordsScoredPredicate secondPredicate =
                new ProductNameContainsKeywordsScoredPredicate(secondPredicateKeywordList);

        assertTrue(firstPredicate.equals(firstPredicate));

        ProductNameContainsKeywordsScoredPredicate firstPredicateCopy =
                new ProductNameContainsKeywordsScoredPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        assertFalse(firstPredicate.equals(1));
        assertFalse(firstPredicate.equals(null));
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    void test_nameContainsKeywords_returnsTrue() {
        ProductNameContainsKeywordsScoredPredicate predicate =
                new ProductNameContainsKeywordsScoredPredicate(Collections.singletonList("ssd"));
        assertTrue(predicate.test(new ProductBuilder().withName("SSD 2TB").build()));

        predicate = new ProductNameContainsKeywordsScoredPredicate(Arrays.asList("xxx", "2tb"));
        assertTrue(predicate.test(new ProductBuilder().withName("SSD 2TB").build()));

        predicate = new ProductNameContainsKeywordsScoredPredicate(Collections.singletonList("SsD"));
        assertTrue(predicate.test(new ProductBuilder().withName("SSD 2TB").build()));
    }

    @Test
    void test_nameDoesNotContainKeywords_returnsFalse() {
        ProductNameContainsKeywordsScoredPredicate predicate =
                new ProductNameContainsKeywordsScoredPredicate(Collections.emptyList());
        assertFalse(predicate.test(new ProductBuilder().withName("SSD").build()));

        predicate = new ProductNameContainsKeywordsScoredPredicate(Collections.singletonList("RAM"));
        assertFalse(predicate.test(new ProductBuilder().withName("SSD 2TB").build()));

        predicate = new ProductNameContainsKeywordsScoredPredicate(
                Arrays.asList("DE/339", "support.rochor@yahoo.com", "10", "10"));
        assertFalse(predicate.test(new ProductBuilder().withName("NVMe SSD 2TB").withIdentifier("DE/339")
                .withQuantity("10").withThreshold("10").withVendorEmail("support.rochor@yahoo.com").build()));
    }

    @Test
    void computeScore_selectsBestMatchTierAndQuality() {
        ProductNameContainsKeywordsScoredPredicate predicate =
                new ProductNameContainsKeywordsScoredPredicate(Arrays.asList("sd", "ssd"));
        Product product = new ProductBuilder().withName("SSD 2TB").build();

        Score score = predicate.computeScore(product);

        assertEquals(MatchTier.EXACT_TOKEN, score.tier());
        assertEquals(0, score.unmatchedCharCount());
        assertEquals("SSD 2TB", score.sortKey());
    }

    @Test
    void createProductComparator_ordersByRelevanceThenAlphabetical() {
        ProductNameContainsKeywordsScoredPredicate predicate =
                new ProductNameContainsKeywordsScoredPredicate(Collections.singletonList("ali"));

        Product exactAlpha = new ProductBuilder().withIdentifier("SKU-B").withName("Ali").build();
        Product exactAlphaTie = new ProductBuilder().withIdentifier("SKU-A").withName("Ali").build();
        Product prefix = new ProductBuilder().withIdentifier("SKU-C").withName("Alice").build();
        Product substring = new ProductBuilder().withIdentifier("SKU-D").withName("Tali").build();

        List<Product> products = new ArrayList<>(Arrays.asList(substring, prefix, exactAlpha, exactAlphaTie));
        products.sort(predicate.createProductComparator());

        assertEquals(Arrays.asList(exactAlpha, exactAlphaTie, prefix, substring), products);
    }

    @Test
    void computeScore_multipleKeywords_usesBestKeywordAcrossAllTokens() {
        ProductNameContainsKeywordsScoredPredicate predicate =
                new ProductNameContainsKeywordsScoredPredicate(Arrays.asList("tb", "2tb"));
        Product product = new ProductBuilder().withName("SSD 2TB").build();

        Score score = predicate.computeScore(product);

        assertEquals(MatchTier.EXACT_TOKEN, score.tier());
        assertEquals(0, score.unmatchedCharCount());
    }

    @Test
    void createProductComparator_multiKeywordRanking_prefersHigherTierThenQuality() {
        ProductNameContainsKeywordsScoredPredicate predicate =
                new ProductNameContainsKeywordsScoredPredicate(Arrays.asList("ali", "cake"));

        Product exact = new ProductBuilder().withIdentifier("SKU-EXACT").withName("Cake Mix").build();
        Product prefix = new ProductBuilder().withIdentifier("SKU-PREFIX").withName("Alice Crackers").build();
        Product substring = new ProductBuilder().withIdentifier("SKU-SUB").withName("Tali Watch").build();

        List<Product> products = new ArrayList<>(Arrays.asList(substring, prefix, exact));
        products.sort(predicate.createProductComparator());

        assertEquals(Arrays.asList(exact, prefix, substring), products);
    }

    @Test
    void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        ProductNameContainsKeywordsScoredPredicate predicate = new ProductNameContainsKeywordsScoredPredicate(keywords);

        String expected = ProductNameContainsKeywordsScoredPredicate.class.getCanonicalName()
                + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
