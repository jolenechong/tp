package seedu.address.model.alias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import seedu.address.model.alias.exceptions.DuplicateAliasException;

public class AliasListTest {

    private final String aliasString = "ls";
    private final String originalCommand = "list";

    @Test
    public void constructor_empty_createsEmptyList() {
        AliasList aliasList = new AliasList();
        assertEquals(0, aliasList.getSize());
    }

    @Test
    public void constructor_withAliases_populateHashSetCorrectly() {
        ArrayList<Alias> aliases = new ArrayList<>();
        aliases.add(new Alias(aliasString, originalCommand));
        aliases.add(new Alias("a", "add"));

        AliasList aliasList = new AliasList(aliases);
        assertThrows(DuplicateAliasException.class, () ->
                aliasList.addAlias(new Alias(aliasString, originalCommand)));
    }

    @Test
    public void addAlias_uniqueAlias_addsSuccessfully() throws DuplicateAliasException {
        AliasList aliasList = new AliasList();
        aliasList.addAlias(new Alias(aliasString, originalCommand));
        assertEquals(1, aliasList.getSize());
    }

    @Test
    public void addAlias_duplicateAlias_throwsDuplicateAliasException() throws DuplicateAliasException {
        AliasList aliasList = new AliasList();
        aliasList.addAlias(new Alias(aliasString, originalCommand));
        assertThrows(DuplicateAliasException.class, () ->
                aliasList.addAlias(new Alias(aliasString, originalCommand)));
    }

    @Test
    public void addAlias_sameAliasNameDifferentCommand_throwsDuplicateException() throws DuplicateAliasException {
        AliasList aliasList = new AliasList();
        aliasList.addAlias(new Alias(aliasString, originalCommand));
        assertThrows(DuplicateAliasException.class, () ->
                aliasList.addAlias(new Alias(aliasString, "delete")));
    }
}
