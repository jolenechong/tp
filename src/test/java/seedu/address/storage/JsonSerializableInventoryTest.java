package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.commons.util.JsonUtil.readJsonFile;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.Inventory;
import seedu.address.testutil.TypicalProducts;



public class JsonSerializableInventoryTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableInventoryTest");
    private static final Path TYPICAL_PRODUCT_FILE = TEST_DATA_FOLDER.resolve("typicalProductInventory.json");
    private static final Path INVALID_PRODUCT_NAME_FILE = TEST_DATA_FOLDER.resolve("invalidNameInventory.json");
    private static final Path DUPLICATE_IDENTIFIER_FILE = TEST_DATA_FOLDER.resolve("duplicateProductIDInventory.json");
    private static final Path NULL_IDENTIFIER_FILE = TEST_DATA_FOLDER.resolve("nullIdentifierInventory.json");
    private static final String DUPLICATE_IDENTIFIER = "SKU-2001";

    @Test
    public void toModelType_typicalInventoryFile_success() throws Exception {
        JsonSerializableInventory dataFromFile = readJsonFile(TYPICAL_PRODUCT_FILE,
                JsonSerializableInventory.class).get();

        Inventory inventoryFromFile = dataFromFile.toModelType();
        Inventory typicalInventory = TypicalProducts.getTypicalInventory();
        assertEquals(inventoryFromFile, typicalInventory);
    }

    @Test
    public void toModelType_invalidNameInventory_throwsIllegalValueException() throws Exception {
        JsonSerializableInventory dataFromFile = readJsonFile(INVALID_PRODUCT_NAME_FILE,
                JsonSerializableInventory.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateIdentifier_throwsIllegalValueException() throws Exception {
        JsonSerializableInventory dataFromFile = readJsonFile(DUPLICATE_IDENTIFIER_FILE,
                JsonSerializableInventory.class).get();

        assertThrows(IllegalValueException.class, JsonSerializableInventory.MESSAGE_DUPLICATE_PRODUCT,
                dataFromFile::toModelType);
    }

    @Test
    public void findDuplicateIdentifiers_nullIdentifier_ignoresNullIdentifier() throws Exception {
        JsonSerializableInventory dataFromFile = readJsonFile(NULL_IDENTIFIER_FILE,
                JsonSerializableInventory.class).get();

        assertEquals(List.of(DUPLICATE_IDENTIFIER), dataFromFile.findDuplicateIdentifiers());
    }

    @Test
    public void findDuplicateIdentifiers_allUnique_returnsEmptyList() throws Exception {
        JsonSerializableInventory dataFromFile = readJsonFile(TYPICAL_PRODUCT_FILE,
                JsonSerializableInventory.class).get();

        assertEquals(List.of(), dataFromFile.findDuplicateIdentifiers());
    }
}
