package seedu.address.model.alias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import seedu.address.model.alias.exceptions.DuplicateAliasException;
import seedu.address.model.alias.exceptions.NoAliasFoundInAliasListException;

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

    @Test
    public void findAlias_existingAlias_returnsCorrectAlias() throws NoAliasFoundInAliasListException {
        AliasList aliasList = new AliasList();
        aliasList.addAlias(new Alias(aliasString, originalCommand));

        Alias result = aliasList.findAlias(aliasString);
        assertEquals(aliasString, result.getAlias());
    }

    @Test
    public void findAlias_nonExistentAlias_throwsException() {
        AliasList aliasList = new AliasList();
        aliasList.addAlias(new Alias(aliasString, originalCommand));

        assertThrows(NoAliasFoundInAliasListException.class, () -> {
            aliasList.findAlias("nonexistent");
        });
    }

    @Test
    public void findAlias_emptyList_throwsException() {
        AliasList emptyList = new AliasList();

        assertThrows(NoAliasFoundInAliasListException.class, () -> {
            emptyList.findAlias("anything");
        });
    }

    @Test
    public void removeAlias_existingAlias_removesSuccessfully() throws Exception {
        AliasList aliasList = new AliasList();
        aliasList.addAlias(new Alias(aliasString, originalCommand));
        aliasList.removeAlias(aliasString);
        assertEquals(0, aliasList.getSize());
    }

    @Test
    public void removeAlias_nonExistentAlias_throwsNoAliasFoundInAliasListException() {
        AliasList aliasList = new AliasList();
        aliasList.addAlias(new Alias(aliasString, originalCommand));
        assertThrows(NoAliasFoundInAliasListException.class, () ->
                aliasList.removeAlias("nonexistent"));
    }

    @Test
    public void removeAlias_emptyList_throwsNoAliasFoundInAliasListException() {
        AliasList aliasList = new AliasList();
        assertThrows(NoAliasFoundInAliasListException.class, () ->
                aliasList.removeAlias(aliasString));
    }

    @Test
    public void removeAlias_alreadyRemovedAlias_throwsNoAliasFoundInAliasListException() throws Exception {
        AliasList aliasList = new AliasList();
        aliasList.addAlias(new Alias(aliasString, originalCommand));
        aliasList.removeAlias(aliasString);
        assertThrows(NoAliasFoundInAliasListException.class, () ->
                aliasList.removeAlias(aliasString));
    }

    @Test
    public void removeAlias_onlyTargetAliasRemoved_otherAliasesUnaffected() throws Exception {
        AliasList aliasList = new AliasList();

        aliasList.addAlias(new Alias(aliasString, originalCommand));
        aliasList.addAlias(new Alias("cl", "clear"));
        aliasList.removeAlias(aliasString);

        assertEquals(1, aliasList.getSize());
        assertEquals("cl", aliasList.findAlias("cl").getAlias());
    }

    @Test
    public void equals() {
        AliasList aliasList = new AliasList();
        // same object -> returns true
        assertTrue(aliasList.equals(aliasList));

        // null -> returns false
        assertFalse(aliasList.equals(null));

        // different type -> returns false
        assertFalse(aliasList.equals("some string"));

        AliasList aliasList1 = new AliasList();
        // different aliasList with no content -> returns true
        assertTrue(aliasList.equals(aliasList1));

        aliasList.addAlias(new Alias(aliasString, originalCommand));
        aliasList1.addAlias(new Alias(aliasString, originalCommand));
        // different aliasList with same content -> returns true
        assertTrue(aliasList.equals(aliasList1));

        AliasList aliasList2 = new AliasList();
        // different aliasList with different content -> returns false
        assertFalse(aliasList1.equals(aliasList2));

        // different aliasList with different content -> returns false
        aliasList2.addAlias(new Alias("different", "delete"));
        assertFalse(aliasList1.equals(aliasList2));
    }

}
