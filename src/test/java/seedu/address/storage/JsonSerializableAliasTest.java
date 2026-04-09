package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.AliasCommand.MESSAGE_ALIAS_CANNOT_BE_EMPTY;
import static seedu.address.logic.commands.AliasCommand.MESSAGE_ORIGINAL_COMMAND_DOES_NOT_EXISTS;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.address.commons.util.JsonUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Aliases;
import seedu.address.model.alias.exceptions.DuplicateAliasException;
import seedu.address.testutil.TypicalAliases;

public class JsonSerializableAliasTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAliasTest");
    private static final Path TYPICAL_ALIAS_FILE = TEST_DATA_FOLDER.resolve("typicalAlias.json");
    private static final Path INVALID_ALIAS_NAME_FILE = TEST_DATA_FOLDER.resolve("invalidAliasNameAlias.json");
    private static final Path DUPLICATE_ALIAS_NAME_FILE = TEST_DATA_FOLDER.resolve("duplicateAliasNameAlias.json");
    private static final Path NO_COMMAND_FOUND_FILE = TEST_DATA_FOLDER.resolve("noCommandFoundAlias.json");

    @Test
    public void toModelType_typicalAliasFile_success() throws Exception {
        JsonSerializableAlias dataFromFile = JsonUtil.readJsonFile(TYPICAL_ALIAS_FILE,
                JsonSerializableAlias.class).get();

        Aliases aliasesFromFile = dataFromFile.toModelType();
        Aliases typicalAlias = TypicalAliases.getTypicalAliases();
        assertEquals(aliasesFromFile, typicalAlias);
    }

    @Test
    public void toModelType_invalidAliasNameAlias_throwsParseException() throws Exception {
        JsonSerializableAlias dataFromFile = JsonUtil.readJsonFile(INVALID_ALIAS_NAME_FILE,
                JsonSerializableAlias.class).get();

        assertThrows(ParseException.class, MESSAGE_ALIAS_CANNOT_BE_EMPTY,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateAliasNameAlias_throwsDuplicateAliasException() throws Exception {
        JsonSerializableAlias dataFromFile = JsonUtil.readJsonFile(DUPLICATE_ALIAS_NAME_FILE,
                JsonSerializableAlias.class).get();

        assertThrows(DuplicateAliasException.class, "Operation would result in duplicate alias",
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_noCommandFoundAlias_throwsParseException() throws Exception {
        JsonSerializableAlias dataFromFile = JsonUtil.readJsonFile(NO_COMMAND_FOUND_FILE,
                JsonSerializableAlias.class).get();

        assertThrows(ParseException.class, String.format(MESSAGE_ORIGINAL_COMMAND_DOES_NOT_EXISTS, "lst"),
                dataFromFile::toModelType);
    }
}
