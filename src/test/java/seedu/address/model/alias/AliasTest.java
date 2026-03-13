package seedu.address.model.alias;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
