package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalAliases.RESTORE;
import static seedu.address.testutil.TypicalAliases.UNDO;
import static seedu.address.testutil.TypicalAliases.getTypicalAliases;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.Aliases;
import seedu.address.model.ReadOnlyAliases;

public class JsonAliasStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAliasStorageTest");

    private static final String MISSING_FILE = "NonExistentFile.json";
    private static final String NO_COMMAND_FOUND_FILE = "noCommandFoundAliases.json";

    @TempDir
    public Path testFolder;

    private Optional<ReadOnlyAliases> readAliasFile(String filePath) throws Exception {
        return new JsonAliasStorage(Paths.get(filePath)).readAliases(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    private void saveAliasFile(ReadOnlyAliases aliases, String filePath) {
        try {
            new JsonAliasStorage(Paths.get(filePath))
                    .saveAliases(aliases, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAliasFile(MISSING_FILE).isPresent());
    }

    @Test
    public void readAlias_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readAliasFile(null));
    }

    @Test
    public void readAndSaveAddressBook_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempInventory.json");
        Aliases original = getTypicalAliases();
        JsonAliasStorage jsonAliasStorage = new JsonAliasStorage(filePath);

        // Save in new file and read back
        jsonAliasStorage.saveAliases(original, filePath);
        ReadOnlyAliases readBack = jsonAliasStorage.readAliases(filePath).get();
        assertEquals(original, new Aliases(readBack));

        // Modify data, overwrite exiting file, and read back
        original.addAlias(RESTORE);
        jsonAliasStorage.saveAliases(original, filePath);
        readBack = jsonAliasStorage.readAliases(filePath).get();
        assertEquals(original, new Aliases(readBack));

        // Save and read without specifying file path
        original.addAlias(UNDO);
        jsonAliasStorage.saveAliases(original);
        readBack = jsonAliasStorage.readAliases().get();
        assertEquals(original, new Aliases(readBack));
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAliasFile("notJsonFormatAliases.json"));
    }

    @Test
    public void read_invalidAliasNameJson_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAliasFile("invalidAliasAliases.json"));
    }

    @Test
    public void read_noCommandFoundAliasJson_exceptionMessageContainsInvalidCommand() {
        DataLoadingException exception = org.junit.jupiter.api.Assertions.assertThrows(DataLoadingException.class, () ->
                readAliasFile(NO_COMMAND_FOUND_FILE));

        assertTrue(exception.getCause().getMessage().contains("Command 'addproducts' does not exist."));
    }

    @Test
    public void saveAliasFile_nullAliasFile_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAliasFile(null, "SomeFile.json"));
    }

    @Test
    public void saveAliasFile_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAliasFile(new Aliases(), null));
    }
}
