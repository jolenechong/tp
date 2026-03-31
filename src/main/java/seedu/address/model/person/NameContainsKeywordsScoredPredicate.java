package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.search.FindRelevance.SCORE_COMPARATOR;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.search.FindRelevance.MatchTier;
import seedu.address.model.search.FindRelevance.Score;

/**
 * Tests that a {@code Person}'s {@code Name} matches any keyword using token-level partial matching.
 */
public class NameContainsKeywordsScoredPredicate implements RankedPersonPredicate {
    private static final String WHITESPACE_REGEX = "\\s+";

    private final List<String> keywords;

    /**
     * Creates a predicate that uses token-level partial matching and supports scoring.
     *
     * @param keywords cannot be null
     */
    public NameContainsKeywordsScoredPredicate(List<String> keywords) {
        requireNonNull(keywords);
        this.keywords = List.copyOf(keywords);
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);
        return computeScore(person).tier() != MatchTier.NO_MATCH;
    }

    /**
     * Computes the best relevance score for the given contact based on all keyword-token pairs.
     */
    public Score computeScore(Person person) {
        requireNonNull(person);

        String fullName = person.getName().fullName;
        Score bestScore = new Score(MatchTier.NO_MATCH, Integer.MAX_VALUE, fullName);

        String[] nameTokens = fullName.trim().split(WHITESPACE_REGEX);
        for (String keyword : keywords) {
            for (String token : nameTokens) {
                Score candidate = toScore(token, keyword, fullName);
                if (isBetterScore(candidate, bestScore)) {
                    bestScore = candidate;
                }
            }
        }

        return bestScore;
    }

    /**
     * Returns a comparator that ranks contacts according to scoring rules.
     */
    public Comparator<Person> createPersonComparator() {
        Map<Person, Score> scoreCache = new HashMap<>();
        return Comparator.comparing((Person person)
                -> scoreCache.computeIfAbsent(person, this::computeScore), SCORE_COMPARATOR);
    }

    private Score toScore(String token, String keyword, String fullName) {
        int matchScore = StringUtil.getWordPartialMatchScoreIgnoreCase(token, keyword);
        MatchTier tier = toMatchTier(matchScore);
        if (tier == MatchTier.NO_MATCH) {
            return new Score(MatchTier.NO_MATCH, Integer.MAX_VALUE, fullName);
        }

        int unmatchedCharCount = token.trim().length() - keyword.trim().length();
        return new Score(tier, unmatchedCharCount, fullName);
    }

    private static MatchTier toMatchTier(int score) {
        return switch (score) {
        case StringUtil.WORD_MATCH_SCORE_EXACT -> MatchTier.EXACT_TOKEN;
        case StringUtil.WORD_MATCH_SCORE_PREFIX -> MatchTier.PREFIX_TOKEN;
        case StringUtil.WORD_MATCH_SCORE_SUBSTRING -> MatchTier.SUBSTRING_TOKEN;
        default -> MatchTier.NO_MATCH;
        };
    }

    private boolean isBetterScore(Score candidate, Score currentBest) {
        return SCORE_COMPARATOR.compare(candidate, currentBest) < 0;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof NameContainsKeywordsScoredPredicate)) {
            return false;
        }

        NameContainsKeywordsScoredPredicate otherPredicate = (NameContainsKeywordsScoredPredicate) other;
        return keywords.equals(otherPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
