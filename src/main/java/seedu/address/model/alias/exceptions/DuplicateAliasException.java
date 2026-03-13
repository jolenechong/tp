package seedu.address.model.alias.exceptions;

/**
 * Signals that the operation will result in duplicate Alias (Alias are consider duplicates if the alias already exists
 * in the text file).
 */
public class DuplicateAliasException extends RuntimeException {
    public DuplicateAliasException() {
        super("Operation would result in duplicate alias");
    }
}
