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
        NameContainsKeywordsScoredPredicate predicate =
                new NameContainsKeywordsScoredPredicate(Collections.singletonList("Ali"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        predicate = new NameContainsKeywordsScoredPredicate(Arrays.asList("xxx", "bo"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        predicate = new NameContainsKeywordsScoredPredicate(Collections.singletonList("aLI"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    @Test
    void test_nameDoesNotContainKeywords_returnsFalse() {
        NameContainsKeywordsScoredPredicate predicate =
                new NameContainsKeywordsScoredPredicate(Collections.emptyList());
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").build()));

        predicate = new NameContainsKeywordsScoredPredicate(Collections.singletonList("Carol"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        predicate = new NameContainsKeywordsScoredPredicate(
                Arrays.asList("12345", "alice@email.com", "Main", "Street"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withPhone("12345")
                .withEmail("alice@email.com").withAddress("Main Street").build()));
    }

    @Test
    void computeScore_selectsBestMatchTierAndQuality() {
        NameContainsKeywordsScoredPredicate predicate =
                new NameContainsKeywordsScoredPredicate(Arrays.asList("li", "alice"));
        Person person = new PersonBuilder().withName("Alice Bob").build();

        Score score = predicate.computeScore(person);

        assertEquals(MatchTier.EXACT_TOKEN, score.tier());
        assertEquals(0, score.unmatchedCharCount());
        assertEquals("Alice Bob", score.sortKey());
    }

    @Test
    void createPersonComparator_ordersByRelevanceThenAlphabetical() {
        NameContainsKeywordsScoredPredicate predicate =
                new NameContainsKeywordsScoredPredicate(Collections.singletonList("ali"));

        Person exactAlpha = new PersonBuilder().withName("Ali").withEmail("b@example.com").build();
        Person exactAlphaEmailTie = new PersonBuilder().withName("Ali").withEmail("a@example.com").build();
        Person prefix = new PersonBuilder().withName("Alice").withEmail("c@example.com").build();
        Person substring = new PersonBuilder().withName("Mali").withEmail("d@example.com").build();

        List<Person> persons = new ArrayList<>(Arrays.asList(substring, prefix, exactAlpha, exactAlphaEmailTie));
        persons.sort(predicate.createPersonComparator());

        assertEquals(Arrays.asList(exactAlpha, exactAlphaEmailTie, prefix, substring), persons);
    }

    @Test
    void computeScore_multipleKeywords_usesBestKeywordAcrossAllTokens() {
        NameContainsKeywordsScoredPredicate predicate =
                new NameContainsKeywordsScoredPredicate(Arrays.asList("li", "mali"));
        Person person = new PersonBuilder().withName("Mali Tan").build();

        Score score = predicate.computeScore(person);

        assertEquals(MatchTier.EXACT_TOKEN, score.tier());
        assertEquals(0, score.unmatchedCharCount());
    }

    @Test
    void createPersonComparator_multiKeywordRanking_prefersHigherTierThenQuality() {
        NameContainsKeywordsScoredPredicate predicate =
                new NameContainsKeywordsScoredPredicate(Arrays.asList("ali", "tan"));

        Person exact = new PersonBuilder().withName("Tan Lee").withEmail("exact@example.com").build();
        Person prefix = new PersonBuilder().withName("Alice Wong").withEmail("prefix@example.com").build();
        Person substring = new PersonBuilder().withName("Mali Ong").withEmail("substring@example.com").build();

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
}
