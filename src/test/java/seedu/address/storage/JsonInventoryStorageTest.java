package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.storage.JsonSerializableInventory.MESSAGE_DUPLICATE_PRODUCT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalProducts.AIRPODS;
import static seedu.address.testutil.TypicalProducts.IPAD;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.Inventory;
import seedu.address.model.ReadOnlyInventory;

public class JsonInventoryStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonInventoryStorageTest");

    private static final String MISSING_FILE = "NonExistentFile.json";
    private static final String DUPLICATE_IDENTIFIER_FILE = "duplicateProductIDInventory.json";
    private static final String NOT_JSON_FORMAT_FILE = "notJsonFormatInventory.json";
    private static final String INVALID_NAME_FILE = "invalidNameInventory.json";
    private static final String SAMPLE_FILE_NAME = "SomeFile.json";
    private static final String DUPLICATE_IDENTIFIER = "SKU-1001";
    private static final String DUPLICATE_IDENTIFIER_WITHOUT_LINES = "SKU-404";
    private static final String LINE_REFERENCE_MULTIPLE = "lines 7, 13";
    private static final String LINE_REFERENCE_SINGLE = "line 5";
    private static final String REFLECTION_BUILD_DUPLICATE_MESSAGE_METHOD = "buildDuplicateIdentifierErrorMessage";
    private static final String REFLECTION_FORMAT_LINE_REFERENCE_METHOD = "formatLineReference";

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
        assertThrows(DataLoadingException.class, () -> readInventory(NOT_JSON_FORMAT_FILE));
    }

    @Test
    public void read_invalidNameInventoryJson_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readInventory(INVALID_NAME_FILE));
    }

    @Test
    public void read_duplicateIdentifierInventoryJson_exceptionThrown() {
        DataLoadingException exception = org.junit.jupiter.api.Assertions.assertThrows(
                DataLoadingException.class, () ->
                        readInventory(DUPLICATE_IDENTIFIER_FILE));

        assertTrue(exception.getCause().getMessage().contains(
                "Duplicate product identifier '" + DUPLICATE_IDENTIFIER + "'"));
        assertTrue(exception.getCause().getMessage().contains(LINE_REFERENCE_MULTIPLE));
    }

    @Test
    public void buildDuplicateIdentifierError_noDuplicateIdentifiers_returnsDefaultMessage() throws Exception {
        JsonInventoryStorage storage = new JsonInventoryStorage(TEST_DATA_FOLDER.resolve(DUPLICATE_IDENTIFIER_FILE));

        String message = invokeBuildDuplicateIdentifierErrorMessage(storage,
                TEST_DATA_FOLDER.resolve(DUPLICATE_IDENTIFIER_FILE), List.of());

        assertEquals(MESSAGE_DUPLICATE_PRODUCT, message);
    }

    @Test
    public void buildDuplicateIdentifierError_missingFilePath_returnsMessageWithoutLineNumbers() throws Exception {
        JsonInventoryStorage storage = new JsonInventoryStorage(TEST_DATA_FOLDER.resolve(DUPLICATE_IDENTIFIER_FILE));

        String message = invokeBuildDuplicateIdentifierErrorMessage(storage,
                TEST_DATA_FOLDER.resolve(MISSING_FILE), List.of(DUPLICATE_IDENTIFIER_WITHOUT_LINES));

        assertEquals("Duplicate product identifier '" + DUPLICATE_IDENTIFIER_WITHOUT_LINES + "'.", message);
    }

    @Test
    public void formatLineReference_singleLineNumber_returnsSingularPrefix() throws Exception {
        JsonInventoryStorage storage = new JsonInventoryStorage(TEST_DATA_FOLDER.resolve(DUPLICATE_IDENTIFIER_FILE));

        String lineReference = invokeFormatLineReference(storage, List.of(5));

        assertEquals(LINE_REFERENCE_SINGLE, lineReference);
    }

    private String invokeBuildDuplicateIdentifierErrorMessage(JsonInventoryStorage storage, Path filePath,
                                                              List<String> duplicateIdentifiers) throws Exception {
        java.lang.reflect.Method method = JsonInventoryStorage.class
                .getDeclaredMethod(REFLECTION_BUILD_DUPLICATE_MESSAGE_METHOD, Path.class, List.class);
        method.setAccessible(true);
        return (String) method.invoke(storage, filePath, duplicateIdentifiers);
    }

    private String invokeFormatLineReference(JsonInventoryStorage storage, List<Integer> lineNumbers) throws Exception {
        java.lang.reflect.Method method = JsonInventoryStorage.class
                .getDeclaredMethod(REFLECTION_FORMAT_LINE_REFERENCE_METHOD, List.class);
        method.setAccessible(true);
        return (String) method.invoke(storage, lineNumbers);
    }

    @Test
    public void saveInventory_nullInventory_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveInventory(null, SAMPLE_FILE_NAME));
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
