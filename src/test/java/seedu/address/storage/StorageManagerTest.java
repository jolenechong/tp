package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.address.testutil.TypicalAliases.getTypicalAliases;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.AddressBook;
import seedu.address.model.Aliases;
import seedu.address.model.Inventory;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyAliases;
import seedu.address.model.ReadOnlyInventory;
import seedu.address.model.UserPrefs;

public class StorageManagerTest {

    private static final String ADDRESS_BOOK_FILE_PATH = "ab";
    private static final String USER_PREF_FILE_PATH = "prefs";
    private static final String INVENTORY_FILE_PATH = "products";
    private static final String ALIAS_FILE_PATH = "aliases";

    @TempDir
    public Path testFolder;

    private StorageManager storageManager;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(getTempFilePath(ADDRESS_BOOK_FILE_PATH));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(getTempFilePath(USER_PREF_FILE_PATH));
        JsonInventoryStorage inventoryStorage =
                new JsonInventoryStorage(getTempFilePath(INVENTORY_FILE_PATH));
        JsonAliasStorage aliasStorage = new JsonAliasStorage(getTempFilePath(ALIAS_FILE_PATH));

        storageManager = new StorageManager(addressBookStorage, userPrefsStorage, inventoryStorage, aliasStorage);
    }

    private Path getTempFilePath(String fileName) {
        return testFolder.resolve(fileName);
    }

    @Test
    public void prefsReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonUserPrefsStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonUserPrefsStorageTest} class.
         */
        UserPrefs original = new UserPrefs();
        original.setGuiSettings(new GuiSettings(300, 600, 4, 6));
        storageManager.saveUserPrefs(original);
        UserPrefs retrieved = storageManager.readUserPrefs().get();
        assertEquals(original, retrieved);
    }

    @Test
    public void addressBookReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonAddressBookStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonAddressBookStorageTest} class.
         */
        AddressBook original = getTypicalAddressBook();
        storageManager.saveAddressBook(original);
        ReadOnlyAddressBook retrieved = storageManager.readAddressBook().get();
        assertEquals(original, new AddressBook(retrieved));
    }

    @Test
    public void getAddressBookFilePath() {
        assertNotNull(storageManager.getAddressBookFilePath());
    }

    @Test
    public void getInventoryFilePath() {
        assertNotNull(storageManager.getInventoryFilePath());
        assertEquals(storageManager.getInventoryFilePath(), getTempFilePath(INVENTORY_FILE_PATH));
    }

    @Test
    public void inventoryReadSave() throws Exception {
        Inventory original = getTypicalInventory();
        storageManager.saveInventory(original);
        ReadOnlyInventory retrieved = storageManager.readInventory().get();
        assertEquals(original, new Inventory(retrieved));
    }

    @Test
    public void getAliasFilePath() {
        assertNotNull(storageManager.getAliasFilePath());
        assertEquals(storageManager.getAliasFilePath(), getTempFilePath(ALIAS_FILE_PATH));
    }

    @Test
    public void aliasReadSave() throws Exception {
        Aliases original = getTypicalAliases();
        storageManager.saveAliases(original);
        ReadOnlyAliases retrieved = storageManager.readAliases().get();
        assertEquals(original, new Aliases(retrieved));
    }
}
