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

import seedu.address.testutil.PersonBuilder;

class NameAndTagMatchesPredicateTest {

    @Test
    void constructor_nullPredicates_throwsNullPointerException() {
        NameContainsKeywordsScoredPredicate namePredicate =
                new NameContainsKeywordsScoredPredicate(Collections.singletonList("Ali"));
        PersonTagContainsKeywordsPredicate tagPredicate =
                new PersonTagContainsKeywordsPredicate(Collections.singletonList("vip"));

        assertThrows(NullPointerException.class, () -> new NameAndTagMatchesPredicate(null, tagPredicate));
        assertThrows(NullPointerException.class, () -> new NameAndTagMatchesPredicate(namePredicate, null));
    }

    @Test
    void test_requiresNameAndTagMatch() {
        NameContainsKeywordsScoredPredicate namePredicate =
                new NameContainsKeywordsScoredPredicate(Collections.singletonList("Ali"));
        PersonTagContainsKeywordsPredicate tagPredicate =
                new PersonTagContainsKeywordsPredicate(Collections.singletonList("vip"));
        NameAndTagMatchesPredicate predicate = new NameAndTagMatchesPredicate(namePredicate, tagPredicate);

        assertTrue(predicate.test(new PersonBuilder().withName("Alice").withTags("vip").build()));

        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withTags("lead").build()));
        assertFalse(predicate.test(new PersonBuilder().withName("Bob").withTags("vip").build()));
        assertFalse(predicate.test(new PersonBuilder().withName("Bob").withTags("lead").build()));
    }

    @Test
    void createPersonComparator_delegatesToNamePredicateComparator() {
        NameContainsKeywordsScoredPredicate namePredicate =
                new NameContainsKeywordsScoredPredicate(Collections.singletonList("ali"));
        PersonTagContainsKeywordsPredicate tagPredicate =
                new PersonTagContainsKeywordsPredicate(Collections.singletonList("vip"));
        NameAndTagMatchesPredicate predicate = new NameAndTagMatchesPredicate(namePredicate, tagPredicate);

        Person exact = new PersonBuilder().withName("Ali").withEmail("exact@example.com").withTags("vip").build();
        Person prefix = new PersonBuilder().withName("Alice").withEmail("prefix@example.com")
                .withTags("vip").build();
        Person substring = new PersonBuilder().withName("Mali").withEmail("substring@example.com")
                .withTags("vip").build();

        List<Person> expectedOrder = new ArrayList<>(Arrays.asList(substring, prefix, exact));
        expectedOrder.sort(namePredicate.createPersonComparator());

        List<Person> actualOrder = new ArrayList<>(Arrays.asList(substring, prefix, exact));
        actualOrder.sort(predicate.createPersonComparator());

        assertEquals(expectedOrder, actualOrder);
    }

    @Test
    void equals() {
        NameContainsKeywordsScoredPredicate firstNamePredicate =
                new NameContainsKeywordsScoredPredicate(Collections.singletonList("first"));
        NameContainsKeywordsScoredPredicate secondNamePredicate =
                new NameContainsKeywordsScoredPredicate(Collections.singletonList("second"));

        PersonTagContainsKeywordsPredicate firstTagPredicate =
                new PersonTagContainsKeywordsPredicate(Collections.singletonList("vip"));
        PersonTagContainsKeywordsPredicate secondTagPredicate =
                new PersonTagContainsKeywordsPredicate(Collections.singletonList("lead"));

        NameAndTagMatchesPredicate firstPredicate =
                new NameAndTagMatchesPredicate(firstNamePredicate, firstTagPredicate);
        NameAndTagMatchesPredicate secondPredicate =
                new NameAndTagMatchesPredicate(secondNamePredicate, secondTagPredicate);
        NameAndTagMatchesPredicate sameNameDifferentTag =
                new NameAndTagMatchesPredicate(firstNamePredicate, secondTagPredicate);

        assertTrue(firstPredicate.equals(firstPredicate));

        NameAndTagMatchesPredicate firstPredicateCopy =
                new NameAndTagMatchesPredicate(firstNamePredicate, firstTagPredicate);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        assertFalse(firstPredicate.equals(1));
        assertFalse(firstPredicate.equals(null));
        assertFalse(firstPredicate.equals(secondPredicate));
        assertFalse(firstPredicate.equals(sameNameDifferentTag));
    }

    @Test
    void toStringMethod() {
        NameContainsKeywordsScoredPredicate namePredicate =
                new NameContainsKeywordsScoredPredicate(Collections.singletonList("Ali"));
        PersonTagContainsKeywordsPredicate tagPredicate =
                new PersonTagContainsKeywordsPredicate(Collections.singletonList("vip"));
        NameAndTagMatchesPredicate predicate = new NameAndTagMatchesPredicate(namePredicate, tagPredicate);

        String expected = NameAndTagMatchesPredicate.class.getCanonicalName()
                + "{namePredicate=" + namePredicate + ", tagPredicate=" + tagPredicate + "}";
        assertEquals(expected, predicate.toString());
    }
}
