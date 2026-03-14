package seedu.address.model.alias.exceptions;

/**
 * Signals that there is no such alias in the alias list.
 */
public class NoAliasFoundInAliasListException extends RuntimeException {
    public NoAliasFoundInAliasListException() {
        super("No alias found in AliasList");
    }

}
