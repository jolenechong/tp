package seedu.address.model.alias;

/**
 * Represents an alternative name for a command. Each alias maps to a user-defined shorthand
 * to an original command keyword.
 */
public class Alias {

    private final String alias;
    private final String originalCommand;

    /**
     * Constructs an Alias that maps the specific alias name to an original command.
     *
     * @param alias The shorthand alias name.
     * @param originalCommand The original command keword this alias represents.
     */
    public Alias(String alias, String originalCommand) {
        this.alias = alias;
        this.originalCommand = originalCommand;
    }

    public String getAlias() {
        return this.alias;
    }

    public String getOriginalCommand() {
        return this.originalCommand;
    }


}
