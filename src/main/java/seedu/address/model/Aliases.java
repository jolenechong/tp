package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.alias.Alias;
import seedu.address.model.alias.AliasList;
import seedu.address.model.alias.exceptions.DuplicateAliasException;
import seedu.address.model.alias.exceptions.NoAliasFoundInAliasListException;

/**
 * Wraps all data at the Aliases level
 */
public class Aliases implements ReadOnlyAliases {

    private AliasList aliasList;

    /*
     * The 'unusual' code block below is a non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     * among constructors.
     */
    {
        aliasList = new AliasList();
    }

    public Aliases() {}

    /**
     * Creates an Alias List using the aliases in the {@code toBeCopied}
     */
    public Aliases(ReadOnlyAliases toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    public void setAliases(List<Alias> aliases) {
        this.aliasList = new AliasList(aliases);
    }

    /**
     * Resets the existing data of this {@code Aliases} with {@code newData}.
     */
    public void resetData(ReadOnlyAliases newData) {
        requireNonNull(newData);

        setAliases(newData.getAliasList());
    }

    public void addAlias(Alias alias) throws DuplicateAliasException {
        this.aliasList.addAlias(alias);
    }

    public Alias findAlias(String aliasStr) throws NoAliasFoundInAliasListException {
        return this.aliasList.findAlias(aliasStr);
    }

    public void removeAlias(String aliasStr) throws NoAliasFoundInAliasListException {
        this.aliasList.removeAlias(aliasStr);
    }

    @Override
    public List<Alias> getAliasList() {
        return aliasList.toList();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("aliases", aliasList)
                .toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Aliases)) {
            return false;
        }

        Aliases otherAliases = (Aliases) other;
        return aliasList.equals(otherAliases.aliasList);
    }
}
