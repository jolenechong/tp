package seedu.address.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.model.Aliases;
import seedu.address.model.alias.Alias;

/**
 * A Utility class containing a list of {@Code Alias} objects to be used in
 * tests.
 */
public class TypicalAliases {

    public static final Alias LIST = new Alias("ls", "list");
    public static final Alias ADD = new Alias("a", "add");
    public static final Alias EDIT = new Alias("ed", "edit");
    public static final Alias EXIT = new Alias("ex", "exit");
    public static final Alias FIND = new Alias("f", "find");
    public static final Alias REDO = new Alias("r", "redo");

    // Manually added
    public static final Alias RESTORE =
            new Alias("rst", "restore");
    public static final Alias UNDO =
            new Alias("u", "undo");


    private TypicalAliases() {
    } //Prevents instantiation

    /**
     * Returns an {@Code Aliases} with all  the typical alias.
     */
    public static Aliases getTypicalAliases() {
        Aliases aliases = new Aliases();
        for (Alias alias : getTypicalAlias()) {
            aliases.addAlias(alias);
        }
        return aliases;
    }

    public static List<Alias> getTypicalAlias() {
        return new ArrayList<>(Arrays.asList(LIST, ADD, EDIT, EXIT, FIND, REDO));
    }
}
