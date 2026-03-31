package seedu.address.model.product;

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
 * Tests that a {@code Product}'s {@code Name} matches any keyword using token-level partial matching.
 */
public class ProductNameContainsKeywordsScoredPredicate implements RankedProductPredicate {
    private static final String WHITESPACE_REGEX = "\\s+";

    private final List<String> keywords;

    /**
     * Creates a predicate that uses token-level partial matching and supports scoring.
     *
     * @param keywords cannot be null
     */
    public ProductNameContainsKeywordsScoredPredicate(List<String> keywords) {
        requireNonNull(keywords);
        this.keywords = List.copyOf(keywords);
    }

    @Override
    public boolean test(Product product) {
        requireNonNull(product);
        return computeScore(product).tier() != MatchTier.NO_MATCH;
    }

    /**
     * Computes the best relevance score for the given product based on all keyword-token pairs.
     */
    public Score computeScore(Product product) {
        requireNonNull(product);

        String productName = product.getName().fullName;
        Score bestScore = new Score(MatchTier.NO_MATCH, Integer.MAX_VALUE, productName);

        String[] nameTokens = productName.trim().split(WHITESPACE_REGEX);
        for (String keyword : keywords) {
            for (String token : nameTokens) {
                Score candidate = toScore(token, keyword, productName);
                if (isBetterScore(candidate, bestScore)) {
                    bestScore = candidate;
                }
            }
        }

        return bestScore;
    }

    /**
     * Returns a comparator that ranks products according to scoring rules.
     */
    public Comparator<Product> createProductComparator() {
        Map<Product, Score> scoreCache = new HashMap<>();
        return Comparator.comparing((Product product)
                -> scoreCache.computeIfAbsent(product, this::computeScore), SCORE_COMPARATOR);
    }

    private Score toScore(String token, String keyword, String productName) {
        int matchScore = StringUtil.getWordPartialMatchScoreIgnoreCase(token, keyword);
        MatchTier tier = toMatchTier(matchScore);
        if (tier == MatchTier.NO_MATCH) {
            return new Score(MatchTier.NO_MATCH, Integer.MAX_VALUE, productName);
        }

        int unmatchedCharCount = token.trim().length() - keyword.trim().length();
        return new Score(tier, unmatchedCharCount, productName);
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

        if (!(other instanceof ProductNameContainsKeywordsScoredPredicate)) {
            return false;
        }

        ProductNameContainsKeywordsScoredPredicate otherPredicate = (ProductNameContainsKeywordsScoredPredicate) other;
        return keywords.equals(otherPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
