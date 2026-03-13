package seedu.address.model.alias;

import java.util.ArrayList;
import java.util.HashSet;

import seedu.address.model.alias.exceptions.DuplicateAliasException;

/**
 * Manages a collection of command aliases. Maintains both an ArrayList to retrieve the Alias
 * and a HashSet for efficient duplicate detection.
 */
public class AliasList {

    private final ArrayList<Alias> aliases;
    private final HashSet<String> aliasHashSet;

    /**
     * Constructs an empty AliasList with no aliases
     */
    public AliasList() {
        this.aliases = new ArrayList<>();
        this.aliasHashSet = new HashSet<>();
    }

    /**
     * Constructs an AliasList populated with the provided ArrayList of aliases.
     * Automatically populates the internal HashSet for efficient lookup and duplicate detection.
     *
     * @param aliases The ArrayList of Alias objects to initialise the list with.
     */
    public AliasList(ArrayList<Alias> aliases) {
        this.aliases = aliases;
        this.aliasHashSet = populateHashSetUsingAliases();
    }

    /**
     * Populates a HashSet with all alias names from the current aliases list.
     * Used for efficient duplicate detection during alias addition.
     *
     * @return A HashSet containing all alias names from the current list.
     */
    public HashSet<String> populateHashSetUsingAliases() {
        HashSet<String> hs = new HashSet<>();
        for (Alias alias : aliases) {
            hs.add(alias.getAlias());
        }
        return hs;
    }

    /**
     * Adds a new alias to the list if its name is unique.
     * The alias name must not already exist in the list.
     *
     * @param alias The Alias object to add.
     * @throws DuplicateAliasException If an alias with the same name already exists.
     */
    public void addAlias(Alias alias) throws DuplicateAliasException {
        if (aliasHashSet.contains(alias.getAlias())) {
            throw new DuplicateAliasException();
        }

        this.aliasHashSet.add(alias.getAlias());
        this.aliases.add(alias);
    }
}
