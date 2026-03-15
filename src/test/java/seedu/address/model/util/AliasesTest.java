package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalAliases.getTypicalAliases;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import seedu.address.model.Aliases;
import seedu.address.model.alias.Alias;
import seedu.address.model.alias.exceptions.DuplicateAliasException;
import seedu.address.model.alias.exceptions.NoAliasFoundInAliasListException;

public class AliasesTest {

    private final Aliases aliases = new Aliases();

    @Test
    public void constructor() {
        assertEquals(Collections.emptyList(), aliases.getAliasList());
    }

    @Test
    public void constructor_withValidReadOnlyAliases_copiesData() {
        Aliases newData = getTypicalAliases();
        Aliases inventory = new Aliases(newData);
        assertEquals(newData, inventory);
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> aliases.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAliases_replacesData() {
        Aliases newData = getTypicalAliases();
        aliases.resetData(newData);
        assertEquals(newData, aliases);
    }

    @Test
    public void setAliases_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> aliases.setAliases(null));
    }

    @Test
    public void setAliases_withValidList_replacesAliasList() {
        Aliases typicalAliases = getTypicalAliases();
        aliases.setAliases(typicalAliases.getAliasList());
        assertEquals(typicalAliases, aliases);
    }

    @Test
    public void setAliases_withEmptyList_clearsAliasList() {
        Aliases typicalAliases = getTypicalAliases();
        typicalAliases.setAliases(Collections.emptyList());
        assertEquals(Collections.emptyList(), typicalAliases.getAliasList());
    }

    @Test
    public void addAlias_nullTargetAlias_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> aliases.addAlias(null));
    }

    @Test
    public void addAlias_validAlias_success() throws DuplicateAliasException {
        Alias alias = new Alias("ls", "list");
        aliases.addAlias(alias);
        assertTrue(aliases.getAliasList().contains(alias));
    }

    @Test
    public void addAlias_duplicateAlias_throwsDuplicateAliasException() throws DuplicateAliasException {
        Alias alias = new Alias("ls", "list");
        aliases.addAlias(alias);
        assertThrows(DuplicateAliasException.class, () -> aliases.addAlias(alias));
    }

    @Test
    public void findAlias_existingAlias_returnsAlias()
            throws DuplicateAliasException, NoAliasFoundInAliasListException {
        Alias alias = new Alias("ls", "list");
        aliases.addAlias(alias);
        assertEquals(alias, aliases.findAlias("ls"));
    }

    @Test
    public void findAlias_nonExistentAlias_throwsNoAliasFoundException() {
        assertThrows(NoAliasFoundInAliasListException.class, () -> aliases.findAlias("nonexistent"));
    }

    @Test
    public void findAlias_nullAlias_throwsNoAliasFoundException() {
        assertThrows(NoAliasFoundInAliasListException.class, () -> aliases.findAlias(null));
    }

    @Test
    public void getAliasList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () ->
                aliases.getAliasList().add(new Alias("ls", "list")));
    }

    @Test
    public void equals() {
        assertTrue(aliases.equals(aliases));
        assertFalse(aliases.equals(null));
        assertFalse(aliases.equals(5));
        Aliases other = new Aliases();
        assertTrue(aliases.equals(other));

    }

    @Test
    public void toString_containsAliasesInfo() {
        String str = aliases.toString();
        assertTrue(str.contains("aliases"));
    }
}
