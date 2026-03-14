package seedu.address.model;

import java.util.List;

import seedu.address.model.alias.Alias;

/**
 * Unmodifiable view of the alias list
 */
public interface ReadOnlyAliases {

    /**
     * Returns an unmodifiable view of the alias list.
     *
     */
    List<Alias> getAliasList();
}
