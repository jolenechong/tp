package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.StringUtil.WORD_MATCH_SCORE_EXACT;
import static seedu.address.commons.util.StringUtil.WORD_MATCH_SCORE_NO_MATCH;
import static seedu.address.commons.util.StringUtil.WORD_MATCH_SCORE_PREFIX;
import static seedu.address.commons.util.StringUtil.WORD_MATCH_SCORE_SUBSTRING;

import java.util.Comparator;
import java.util.Locale;

/**
 * This class defines the ranking contract for find results:
 * exact token match &gt; prefix token match &gt; substring token match,
 * then fewer unmatched characters, then alphabetical by full name.
 */
public final class FindRelevance {
    /**
     * Comparator for descending relevance with tie-breakers.
     */
    public static final Comparator<Score> SCORE_COMPARATOR =
            Comparator.comparingInt((Score score) -> score.tier().getWeight()).reversed()
                    .thenComparingInt(Score::unmatchedChars)
                    .thenComparing(score -> score.fullName().toLowerCase(Locale.ROOT));

    private static final String MESSAGE_UNMATCHED_CHARS_NEGATIVE = "unmatchedChars must be non-negative";

    /**
     * Match tier for one keyword against one name token.
     */
    public enum MatchTier {
        NO_MATCH(WORD_MATCH_SCORE_NO_MATCH),
        SUBSTRING_TOKEN(WORD_MATCH_SCORE_SUBSTRING),
        PREFIX_TOKEN(WORD_MATCH_SCORE_PREFIX),
        EXACT_TOKEN(WORD_MATCH_SCORE_EXACT);

        private final int weight;

        MatchTier(int weight) {
            this.weight = weight;
        }

        /**
         * Returns weight for this tier.
         */
        public int getWeight() {
            return weight;
        }
    }

    /**
     * Immutable relevance score.
     */
    public record Score(MatchTier tier, int unmatchedChars, String fullName) {

        /**
         * Creates a relevance score tuple.
         */
        public Score {
            requireNonNull(tier);
            requireNonNull(fullName);
            if (unmatchedChars < 0) {
                throw new IllegalArgumentException(MESSAGE_UNMATCHED_CHARS_NEGATIVE);
            }
        }
    }

    /**
     * Utility class.
     */
    private FindRelevance() {
    }
}
