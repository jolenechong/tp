package seedu.address.logic.parser;

import java.util.ArrayList;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Utility class for detecting and handling confirmation flags in tokenized command inputs.
 */
public class ConfirmationFlagIndicator {

    /**
     * Checks whether the first element in the given token array contains the specified confirmation flag.
     */
    public static boolean containsConfirmationFlag(
            String[] tokens, String confirmationFlag, int tokenMinLength) throws ParseException {

        if (tokens.length <= tokenMinLength) {
            return false;
        }

        return tokens[0].equals(confirmationFlag);
    }

    /**
     * Returns a reconstructed command string with the confirmation flag removed.
     */
    public static String removeConfirmationFlag(String[] tokens, String confirmationFlag) {
        boolean removed = false;
        ArrayList<String> result = new ArrayList<>();
        for (String token : tokens) {
            if (token.equals(confirmationFlag) && !removed) {
                removed = true;
            } else {
                result.add(token);
            }
        }
        return String.join(" ", result);
    }
}
