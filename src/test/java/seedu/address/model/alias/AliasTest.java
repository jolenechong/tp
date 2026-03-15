package seedu.address.model.alias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class AliasTest {

    private final String aliasString = "ls";
    private final String originalCommand = "list";

    @Test
    public void getAlias_validAlias_returnsCorrectAlias() {
        Alias alias = new Alias(aliasString, originalCommand);
        assertEquals(aliasString, alias.getAlias());
    }

    @Test
    public void getOriginalCommand_validAlias_returnsCorrectCommand() {
        Alias alias = new Alias(aliasString, originalCommand);
        assertEquals(originalCommand, alias.getOriginalCommand());
    }

    @Test
    public void equals() {
        Alias alias = new Alias(aliasString, originalCommand);
        assertTrue(alias.equals(alias));

        assertFalse(alias.equals(null));

        assertFalse(alias.equals("some string"));

        Alias alias1 = new Alias(aliasString, originalCommand);
        assertTrue(alias.equals(alias1));

        alias1 = new Alias("differentAlias", originalCommand);
        assertFalse(alias.equals(alias1));

        alias1 = new Alias(aliasString, "differentCommand");
        assertFalse(alias.equals(alias1));

        alias1 = new Alias("differentAlias", "differentCommand");
        assertFalse(alias.equals(alias1));
    }
}
