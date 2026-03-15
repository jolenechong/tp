package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalProducts.AIRPODS;
import static seedu.address.testutil.TypicalProducts.IPAD;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.Inventory;
import seedu.address.model.ReadOnlyInventory;


public class JsonInventoryStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonInventoryStorageTest");

    private static final String MISSING_FILE = "NonExistentFile.json";

    @TempDir
    public Path testFolder;

    @Test
    public void readInventory_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readInventory(null));
    }

    private Optional<ReadOnlyInventory> readInventory(String filePath) throws Exception {
        return new JsonInventoryStorage(Paths.get(filePath)).readInventory(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readInventory(MISSING_FILE).isPresent());
    }

    @Test
    public void readAndSaveAddressBook_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempInventory.json");
        Inventory original = getTypicalInventory();
        JsonInventoryStorage jsonInventoryStorage = new JsonInventoryStorage(filePath);

        // Save in new file and read back
        jsonInventoryStorage.saveInventory(original, filePath);
        ReadOnlyInventory readBack = jsonInventoryStorage.readInventory(filePath).get();
        assertEquals(original, new Inventory(readBack));

        // Modify data, overwrite exiting file, and read back
        original.addProduct(AIRPODS);
        jsonInventoryStorage.saveInventory(original, filePath);
        readBack = jsonInventoryStorage.readInventory(filePath).get();
        assertEquals(original, new Inventory(readBack));

        // Save and read without specifying file path
        original.addProduct(IPAD);
        jsonInventoryStorage.saveInventory(original);
        readBack = jsonInventoryStorage.readInventory().get();
        assertEquals(original, new Inventory(readBack));
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readInventory("notJsonFormatInventory.json"));
    }

    @Test
    public void read_invalidNameInventoryJson_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readInventory("invalidNameInventory.json"));
    }

    @Test
    public void saveInventory_nullInventory_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveInventory(null, "SomeFile.json"));
    }

    private void saveInventory(ReadOnlyInventory inventory, String filePath) {
        try {
            new JsonInventoryStorage(Paths.get(filePath))
                    .saveInventory(inventory, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveInventory_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveInventory(new Inventory(), null));
    }
}
