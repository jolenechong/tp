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

    private static final String NAME_SSD_2TB = "SSD 2TB";

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
        assertMatches(NAME_SSD_2TB, "ssd");
        assertMatches(NAME_SSD_2TB, "xxx", "2tb");
        assertMatches(NAME_SSD_2TB, "SsD");
    }

    @Test
    void test_nameDoesNotContainKeywords_returnsFalse() {
        assertDoesNotMatch("SSD", Collections.emptyList());
        assertDoesNotMatch(NAME_SSD_2TB, Collections.singletonList("RAM"));

        ProductNameContainsKeywordsScoredPredicate predicate =
                predicate("DE/339", "support.rochor@yahoo.com", "10", "10");
        assertFalse(predicate.test(new ProductBuilder().withName("NVMe SSD 2TB").withIdentifier("DE/339")
                .withQuantity("10").withThreshold("10").withVendorEmail("support.rochor@yahoo.com").build()));
    }

    @Test
    void computeScore_selectsBestMatchTierAndQuality() {
        // EP: strongest keyword-token pair should determine final score.
        ProductNameContainsKeywordsScoredPredicate predicate = predicate("sd", "ssd");
        Product product = productWithName(NAME_SSD_2TB);

        Score score = predicate.computeScore(product);

        assertExactMatchScore(score);
        assertEquals(NAME_SSD_2TB, score.sortKey());
    }

    @Test
    void createProductComparator_ordersByRelevanceThenAlphabetical() {
        // EP: ties on relevance should break using alphabetical sort key, not insertion order.
        ProductNameContainsKeywordsScoredPredicate predicate = predicate("ali");

        Product exactAliAlpha = productWithNameAndIdentifier("Ali Alpha", "SKU-B");
        Product exactAliBeta = productWithNameAndIdentifier("Ali Beta", "SKU-A");
        Product prefix = productWithNameAndIdentifier("Alice", "SKU-C");
        Product substring = productWithNameAndIdentifier("Tali", "SKU-D");

        List<Product> products = new ArrayList<>(Arrays.asList(substring, prefix, exactAliBeta, exactAliAlpha));
        products.sort(predicate.createProductComparator());

        assertEquals(Arrays.asList(exactAliAlpha, exactAliBeta, prefix, substring), products);
    }

    @Test
    void computeScore_multipleKeywords_usesBestKeywordAcrossAllTokens() {
        // EP: multi-keyword scoring should select the strongest exact match across all keywords/tokens.
        ProductNameContainsKeywordsScoredPredicate predicate = predicate("tb", "2tb");
        Product product = productWithName(NAME_SSD_2TB);

        Score score = predicate.computeScore(product);

        assertExactMatchScore(score);
    }

    @Test
    void createProductComparator_multiKeywordRanking_prefersHigherTierThenQuality() {
        // EP: comparator should prioritize higher tier, then better quality (fewer unmatched chars).
        ProductNameContainsKeywordsScoredPredicate predicate = predicate("ali", "cake");

        Product exact = productWithNameAndIdentifier("Cake Mix", "SKU-EXACT");
        Product prefix = productWithNameAndIdentifier("Alice Crackers", "SKU-PREFIX");
        Product substring = productWithNameAndIdentifier("Tali Watch", "SKU-SUB");

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

    private ProductNameContainsKeywordsScoredPredicate predicate(String... keywords) {
        return new ProductNameContainsKeywordsScoredPredicate(Arrays.asList(keywords));
    }

    private Product productWithName(String name) {
        return new ProductBuilder().withName(name).build();
    }

    private Product productWithNameAndIdentifier(String name, String identifier) {
        return new ProductBuilder().withIdentifier(identifier).withName(name).build();
    }

    private void assertMatches(String productName, String... keywords) {
        assertTrue(predicate(keywords).test(productWithName(productName)));
    }

    private void assertDoesNotMatch(String productName, List<String> keywords) {
        assertFalse(new ProductNameContainsKeywordsScoredPredicate(keywords).test(productWithName(productName)));
    }

    private void assertExactMatchScore(Score score) {
        assertEquals(MatchTier.EXACT_TOKEN, score.tier());
        assertEquals(0, score.unmatchedCharCount());
    }
}
