package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.AliasCommand.MESSAGE_ALIAS_CONTAINS_SPACE;
import static seedu.address.logic.commands.AliasCommand.MESSAGE_ALIAS_IS_A_PREDEFINED_COMMAND;
import static seedu.address.logic.commands.AliasCommand.MESSAGE_ORIGINAL_COMMAND_DOES_NOT_EXISTS;
import static seedu.address.storage.JsonAdaptedAlias.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalAliases.LIST;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.parser.exceptions.ParseException;

public class JsonAdaptedAliasTest {
    private static final String INVALID_ALIAS_ONLY_SPACE = " ";
    private static final String INVALID_ORIGINAL_COMMAND_NOT_VALID = "lst";
    private static final String INVALID_ORIGINAL_COMMAND_NOT_VALID_WITH_SPACE = "lst g";

    private static final String VALID_ALIAS = LIST.getAlias();
    private static final String VALID_ORIGINAL_COMMAND = LIST.getOriginalCommand();

    @Test
    public void toModelType_validAliasDetails_returnsAlias() throws Exception {
        JsonAdaptedAlias alias = new JsonAdaptedAlias(LIST);
        assertEquals(LIST, alias.toModelType());
    }

    @Test
    public void toModelType_nullAlias_throwsIllegalValueException() {
        JsonAdaptedAlias alias = new JsonAdaptedAlias(null, VALID_ORIGINAL_COMMAND);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "alias");
        assertThrows(IllegalValueException.class, expectedMessage, alias::toModelType);
    }

    @Test
    public void toModelType_nullOriginalCommand_throwsIllegalValueException() {
        JsonAdaptedAlias alias = new JsonAdaptedAlias(VALID_ALIAS, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "original command");
        assertThrows(IllegalValueException.class, expectedMessage, alias::toModelType);
    }

    @Test
    public void toModelType_originalCommandNotValid_throwsParseException() {
        JsonAdaptedAlias alias = new JsonAdaptedAlias(VALID_ALIAS, INVALID_ORIGINAL_COMMAND_NOT_VALID);
        assertThrows(ParseException.class,
                String.format(MESSAGE_ORIGINAL_COMMAND_DOES_NOT_EXISTS, INVALID_ORIGINAL_COMMAND_NOT_VALID),
                alias::toModelType);

        JsonAdaptedAlias alias1 = new JsonAdaptedAlias(VALID_ALIAS, INVALID_ORIGINAL_COMMAND_NOT_VALID_WITH_SPACE);
        assertThrows(ParseException.class,
                String.format(MESSAGE_ORIGINAL_COMMAND_DOES_NOT_EXISTS, INVALID_ORIGINAL_COMMAND_NOT_VALID_WITH_SPACE),
                alias1::toModelType);
    }

    @Test
    public void toModelType_invalidAliasOnlySpaces_throwsParseException() {
        JsonAdaptedAlias alias = new JsonAdaptedAlias(INVALID_ALIAS_ONLY_SPACE, VALID_ORIGINAL_COMMAND);
        assertThrows(ParseException.class, MESSAGE_ALIAS_CONTAINS_SPACE, alias::toModelType);
    }

    @Test
    public void toModelType_aliasIsAValidCommand_throwsParseException() {
        JsonAdaptedAlias alias = new JsonAdaptedAlias(VALID_ORIGINAL_COMMAND, VALID_ORIGINAL_COMMAND);
        assertThrows(ParseException.class, MESSAGE_ALIAS_IS_A_PREDEFINED_COMMAND, alias::toModelType);
    }
}
