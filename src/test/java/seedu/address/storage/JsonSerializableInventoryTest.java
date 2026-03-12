package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.Inventory;
import seedu.address.testutil.TypicalProducts;



public class JsonSerializableInventoryTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableInventoryTest");
    private static final Path TYPICAL_PRODUCT_FILE = TEST_DATA_FOLDER.resolve("typicalProductInventory.json");
    private static final Path INVALID_PRODUCT_NAME_FILE = TEST_DATA_FOLDER.resolve("invalidNameInventory.json");
    private static final Path DUPLICATE_IDENTIFIER_FILE = TEST_DATA_FOLDER.resolve("duplicateProductIDInventory.json");

    @Test
    public void toModelType_typicalInventoryFile_success() throws Exception {
        JsonSerializableInventory dataFromFile = JsonUtil.readJsonFile(TYPICAL_PRODUCT_FILE,
                JsonSerializableInventory.class).get();

        Inventory inventoryFromFile = dataFromFile.toModelType();
        Inventory typicalInventory = TypicalProducts.getTypicalInventory();
        assertEquals(inventoryFromFile, typicalInventory);
    }

    @Test
    public void toModelType_invalidNameInventory_throwsIllegalValueException() throws Exception {
        JsonSerializableInventory dataFromFile = JsonUtil.readJsonFile(INVALID_PRODUCT_NAME_FILE,
                JsonSerializableInventory.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateIdentifier_throwsIllegalValueException() throws Exception {
        JsonSerializableInventory dataFromFile = JsonUtil.readJsonFile(DUPLICATE_IDENTIFIER_FILE,
                JsonSerializableInventory.class).get();

        assertThrows(IllegalValueException.class, JsonSerializableInventory.MESSAGE_DUPLICATE_PRODUCT,
                dataFromFile::toModelType);
    }
}
