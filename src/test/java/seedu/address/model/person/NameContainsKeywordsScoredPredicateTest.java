package seedu.address.model.person;

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
import seedu.address.testutil.PersonBuilder;

class NameContainsKeywordsScoredPredicateTest {

    private static final String NAME_ALICE_BOB = "Alice Bob";
    private static final String NAME_ALICE = "Alice";

    @Test
    void constructor_nullKeywords_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new NameContainsKeywordsScoredPredicate(null));
    }

    @Test
    void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        NameContainsKeywordsScoredPredicate firstPredicate =
                new NameContainsKeywordsScoredPredicate(firstPredicateKeywordList);
        NameContainsKeywordsScoredPredicate secondPredicate =
                new NameContainsKeywordsScoredPredicate(secondPredicateKeywordList);

        assertTrue(firstPredicate.equals(firstPredicate));

        NameContainsKeywordsScoredPredicate firstPredicateCopy =
                new NameContainsKeywordsScoredPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        assertFalse(firstPredicate.equals(1));
        assertFalse(firstPredicate.equals(null));
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    void test_nameContainsKeywords_returnsTrue() {
        assertMatches(NAME_ALICE_BOB, "Ali");
        assertMatches(NAME_ALICE_BOB, "xxx", "bo");
        assertMatches(NAME_ALICE_BOB, "aLI");
    }

    @Test
    void test_nameDoesNotContainKeywords_returnsFalse() {
        assertDoesNotMatch(NAME_ALICE, Collections.emptyList());
        assertDoesNotMatch(NAME_ALICE_BOB, Collections.singletonList("Carol"));

        NameContainsKeywordsScoredPredicate predicate = createPredicate("12345", "alice@email.com", "Main", "Street");
        assertFalse(predicate.test(new PersonBuilder().withName(NAME_ALICE).withPhone("12345")
                .withEmail("alice@email.com").withAddress("Main Street").build()));
    }

    @Test
    void computeScore_selectsBestMatchTierAndQuality() {
        // EP: best score should come from the strongest keyword-token pair.
        NameContainsKeywordsScoredPredicate predicate = createPredicate("li", "alice");
        Person person = buildPersonWithName(NAME_ALICE_BOB);

        Score score = predicate.computeScore(person);

        assertExactMatchScore(score);
        assertEquals(NAME_ALICE_BOB, score.sortKey());
    }

    @Test
    void createPersonComparator_ordersByRelevanceThenAlphabetical() {
        // EP: ties on relevance should be broken by alphabetical sort key, not insertion order.
        NameContainsKeywordsScoredPredicate predicate = createPredicate("ali");

        Person exactAliAlpha = buildPersonWithName("Ali Alpha", "alpha@example.com");
        Person exactAliBeta = buildPersonWithName("Ali Beta", "beta@example.com");
        Person prefix = buildPersonWithName("Alice", "prefix@example.com");
        Person substring = buildPersonWithName("Mali", "substring@example.com");

        List<Person> persons = new ArrayList<>(Arrays.asList(substring, prefix, exactAliBeta, exactAliAlpha));
        persons.sort(predicate.createPersonComparator());

        assertEquals(Arrays.asList(exactAliAlpha, exactAliBeta, prefix, substring), persons);
    }

    @Test
    void computeScore_multipleKeywords_usesBestKeywordAcrossAllTokens() {
        NameContainsKeywordsScoredPredicate predicate = createPredicate("li", "mali");
        Person person = buildPersonWithName("Mali Tan");

        Score score = predicate.computeScore(person);

        assertExactMatchScore(score);
    }

    @Test
    void createPersonComparator_multiKeywordRanking_prefersHigherTierThenQuality() {
        NameContainsKeywordsScoredPredicate predicate = createPredicate("ali", "tan");

        Person exact = buildPersonWithName("Tan Lee", "exact@example.com");
        Person prefix = buildPersonWithName("Alice Wong", "prefix@example.com");
        Person substring = buildPersonWithName("Mali Ong", "substring@example.com");

        List<Person> persons = new ArrayList<>(Arrays.asList(substring, prefix, exact));
        persons.sort(predicate.createPersonComparator());

        assertEquals(Arrays.asList(exact, prefix, substring), persons);
    }

    @Test
    void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        NameContainsKeywordsScoredPredicate predicate = new NameContainsKeywordsScoredPredicate(keywords);

        String expected = NameContainsKeywordsScoredPredicate.class.getCanonicalName()
                + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }

    private NameContainsKeywordsScoredPredicate createPredicate(String... keywords) {
        return new NameContainsKeywordsScoredPredicate(Arrays.asList(keywords));
    }

    private Person buildPersonWithName(String name) {
        return new PersonBuilder().withName(name).build();
    }

    private Person buildPersonWithName(String name, String email) {
        return new PersonBuilder().withName(name).withEmail(email).build();
    }

    private void assertMatches(String name, String... keywords) {
        assertTrue(createPredicate(keywords).test(buildPersonWithName(name)));
    }

    private void assertDoesNotMatch(String name, List<String> keywords) {
        assertFalse(new NameContainsKeywordsScoredPredicate(keywords).test(buildPersonWithName(name)));
    }

    private void assertExactMatchScore(Score score) {
        assertEquals(MatchTier.EXACT_TOKEN, score.tier());
        assertEquals(0, score.unmatchedCharCount());
    }
}
