package seedu.address.model.alias;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import seedu.address.model.alias.exceptions.DuplicateAliasException;
import seedu.address.model.alias.exceptions.NoAliasFoundInAliasListException;

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
    public AliasList(List<Alias> aliases) {
        this.aliases = new ArrayList<>(aliases);
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

    public int getSize() {
        return this.aliases.size();
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

    /**
     * Removes the alias with matching aliasStr from the list.
     *
     * @param aliasStr The alias name to search for and delete.
     * @throws NoAliasFoundInAliasListException If no alias with the specified name is found.
     */
    public void removeAlias(String aliasStr) throws NoAliasFoundInAliasListException {
        if (!aliasHashSet.contains(aliasStr)) {
            throw new NoAliasFoundInAliasListException();
        }

        Alias deleteAlias = findAlias(aliasStr);
        this.aliasHashSet.remove(aliasStr);
        this.aliases.remove(deleteAlias);
    }

    public List<Alias> toList() {
        return this.aliases.stream().toList();
    }

    /**
     * Searches for and returns the Alias object matching the provided alias name.
     * Performs a linear search through the aliases list.
     *
     * @param aliasStr The alias name to search for.
     * @return The Alias object with the matching name.
     * @throws NoAliasFoundInAliasListException If no alias with the specified name is found.
     */
    public Alias findAlias(String aliasStr) throws NoAliasFoundInAliasListException {
        for (Alias currAlias : this.aliases) {
            if (currAlias.getAlias().equals(aliasStr)) {
                return currAlias;
            }
        }
        throw new NoAliasFoundInAliasListException();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AliasList)) {
            return false;
        }

        AliasList otherAliasList = (AliasList) other;
        return aliases.equals(otherAliasList.aliases)
                && aliasHashSet.equals(otherAliasList.aliasHashSet);
    }
}
