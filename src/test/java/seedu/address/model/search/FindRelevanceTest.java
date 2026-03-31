package seedu.address.model.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.model.search.FindRelevance.MatchTier.EXACT_TOKEN;
import static seedu.address.model.search.FindRelevance.MatchTier.PREFIX_TOKEN;
import static seedu.address.model.search.FindRelevance.MatchTier.SUBSTRING_TOKEN;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.search.FindRelevance.Score;

class FindRelevanceTest {

    @Test
    void scoreComparator_ordersByTierThenUnmatchedThenAlphabetical() {
        List<Score> scores = new ArrayList<>(List.of(
                new Score(SUBSTRING_TOKEN, 3, "Zed Alpha"),
                new Score(EXACT_TOKEN, 2, "Bravo"),
                new Score(EXACT_TOKEN, 1, "Charlie"),
                new Score(EXACT_TOKEN, 1, "Alpha"),
                new Score(PREFIX_TOKEN, 0, "Delta")
        ));

        scores.sort(FindRelevance.SCORE_COMPARATOR);

        assertEquals(List.of(
                new Score(EXACT_TOKEN, 1, "Alpha"),
                new Score(EXACT_TOKEN, 1, "Charlie"),
                new Score(EXACT_TOKEN, 2, "Bravo"),
                new Score(PREFIX_TOKEN, 0, "Delta"),
                new Score(SUBSTRING_TOKEN, 3, "Zed Alpha")
        ), scores);
    }

    @Test
    void scoreConstructor_negativeUnmatchedCharCount_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, ()
                -> new Score(PREFIX_TOKEN, -1, "Alice"));
    }
}
