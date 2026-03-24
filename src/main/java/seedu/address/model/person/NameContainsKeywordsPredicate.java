package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} matches any of the full-word keywords given.
 */
public class NameContainsKeywordsPredicate implements Predicate<Person> {
    private static final String WHITESPACE_REGEX = "\\s+";

    private final List<String> keywords;

    /**
     * Creates a predicate that matches any full-word keyword in a person's name.
     *
     * @param keywords cannot be null
     */
    public NameContainsKeywordsPredicate(List<String> keywords) {
        requireNonNull(keywords);
        this.keywords = List.copyOf(keywords);
    }

    @Override
    public boolean test(Person person) {
        String[] nameTokens = person.getName().fullName.trim().split(WHITESPACE_REGEX);

        return keywords.stream().anyMatch(keyword -> Arrays.stream(nameTokens).anyMatch(token ->
                StringUtil.getWordPartialMatchScoreIgnoreCase(token, keyword) > StringUtil.WORD_MATCH_SCORE_NO_MATCH));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NameContainsKeywordsPredicate)) {
            return false;
        }

        NameContainsKeywordsPredicate otherNameContainsKeywordsPredicate = (NameContainsKeywordsPredicate) other;
        return keywords.equals(otherNameContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
