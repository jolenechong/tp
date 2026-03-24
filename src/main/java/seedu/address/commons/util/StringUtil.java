package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Locale;

/**
 * Helper functions for handling strings.
 */
public class StringUtil {

    public static final int WORD_MATCH_SCORE_NO_MATCH = 0;
    public static final int WORD_MATCH_SCORE_SUBSTRING = 1;
    public static final int WORD_MATCH_SCORE_PREFIX = 2;
    public static final int WORD_MATCH_SCORE_EXACT = 3;

    private static final String ERROR_SENTENCE_WORD_EMPTY = "Sentence word parameter cannot be empty";
    private static final String ERROR_WORD_EMPTY = "Word parameter cannot be empty";
    private static final String ERROR_SENTENCE_WORD_SINGLE = "Sentence word parameter should be a single word";
    private static final String ERROR_WORD_SINGLE = "Word parameter should be a single word";

    private static final int DP_OFFSET = 1;
    private static final int NO_MATCH = 0;

    /**
     * Returns true if the {@code sentence} contains the {@code word}.
     *   Ignores case, but a full word match is required.
     *   <br>examples:<pre>
     *       containsWordIgnoreCase("ABc def", "abc") == true
     *       containsWordIgnoreCase("ABc def", "DEF") == true
     *       containsWordIgnoreCase("ABc def", "AB") == false //not a full word match
     *       </pre>
     * @param sentence cannot be null
     * @param word cannot be null, cannot be empty, must be a single word
     */
    public static boolean containsWordIgnoreCase(String sentence, String word) {
        requireNonNull(sentence);
        requireNonNull(word);

        String preppedWord = word.trim();
        checkArgument(!preppedWord.isEmpty(), "Word parameter cannot be empty");
        checkArgument(preppedWord.split("\\s+").length == 1, "Word parameter should be a single word");

        String preppedSentence = sentence;
        String[] wordsInPreppedSentence = preppedSentence.split("\\s+");

        return Arrays.stream(wordsInPreppedSentence)
                .anyMatch(preppedWord::equalsIgnoreCase);
    }

    /**
     * Returns a token-level partial match score between {@code sentenceWord} and {@code word}, ignoring case.
     * Score ordering is: exact (3) &gt; prefix (2) &gt; substring (1) &gt; no match (0).
     *
     * @param sentenceWord cannot be null, cannot be empty, must be a single word
     * @param word cannot be null, cannot be empty, must be a single word
     */
    public static int getWordPartialMatchScoreIgnoreCase(String sentenceWord, String word) {
        String preppedSentenceWord = normalizeAndValidateSingleWord(sentenceWord, ERROR_SENTENCE_WORD_EMPTY,
                ERROR_SENTENCE_WORD_SINGLE);
        String preppedWord = normalizeAndValidateSingleWord(word, ERROR_WORD_EMPTY, ERROR_WORD_SINGLE);

        String sentenceWordLower = preppedSentenceWord.toLowerCase(Locale.ROOT);
        String wordLower = preppedWord.toLowerCase(Locale.ROOT);

        if (sentenceWordLower.equals(wordLower)) {
            return WORD_MATCH_SCORE_EXACT;
        }
        if (sentenceWordLower.startsWith(wordLower)) {
            return WORD_MATCH_SCORE_PREFIX;
        }
        if (sentenceWordLower.contains(wordLower)) {
            return WORD_MATCH_SCORE_SUBSTRING;
        }
        return WORD_MATCH_SCORE_NO_MATCH;
    }

    private static String normalizeAndValidateSingleWord(String value, String emptyMessage, String singleWordMessage) {
        requireNonNull(value);

        String preppedValue = value.trim();
        checkArgument(!preppedValue.isEmpty(), emptyMessage);
        checkArgument(preppedValue.split("\\s+").length == 1, singleWordMessage);

        return preppedValue;
    }

    /**
     * Returns a detailed message of the t, including the stack trace.
     */
    public static String getDetails(Throwable t) {
        requireNonNull(t);
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return t.getMessage() + "\n" + sw.toString();
    }

    /**
     * Returns true if {@code s} represents a non-zero unsigned integer
     * e.g. 1, 2, 3, ..., {@code Integer.MAX_VALUE} <br>
     * Will return false for any other non-null string input
     * e.g. empty string, "-1", "0", "+1", and " 2 " (untrimmed), "3 0" (contains whitespace), "1 a" (contains letters)
     * @throws NullPointerException if {@code s} is null.
     */
    public static boolean isNonZeroUnsignedInteger(String s) {
        requireNonNull(s);

        try {
            int value = Integer.parseInt(s);
            return value > 0 && !s.startsWith("+"); // "+1" is successfully parsed by Integer#parseInt(String)
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Returns true if {@code s} can be parsed as an integer.
     *
     */
    public static boolean isValidInteger(String s) {
        requireNonNull(s);

        try {
            int value = Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Returns the length of the longest contiguous substring common to both strings,
     * ignoring case differences.
     *
     * @param a The first string to compare.
     * @param b The second string to compare.
     * @return The length of the longest common contiguous substring.
     */
    public static int longestContiguousMatch(String a, String b) {
        String normalizedA = a.toLowerCase();
        String normalizedB = b.toLowerCase();

        // Two rows is enough because each cell depends only on the row above,
        int[] previousRow = new int[normalizedB.length() + DP_OFFSET];
        int[] currentRow = new int[normalizedB.length() + DP_OFFSET];

        int longestMatch = NO_MATCH;

        for (int i = DP_OFFSET; i <= normalizedA.length(); i++) {
            for (int j = DP_OFFSET; j <= normalizedB.length(); j++) {
                boolean charsMatch = normalizedA.charAt(i - DP_OFFSET) == normalizedB.charAt(j - DP_OFFSET);
                currentRow[j] = charsMatch ? previousRow[j - DP_OFFSET] + DP_OFFSET : NO_MATCH;
                longestMatch = Math.max(longestMatch, currentRow[j]);
            }

            int[] temp = previousRow;
            previousRow = currentRow;
            currentRow = temp;
            Arrays.fill(currentRow, NO_MATCH);
        }

        return longestMatch;
    }

}
