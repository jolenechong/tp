package seedu.address;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalProducts.RICE;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.Config;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.ConfigUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.Aliases;
import seedu.address.model.Inventory;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyAliases;
import seedu.address.model.ReadOnlyInventory;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.product.Product;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.Storage;
import seedu.address.storage.UserPrefsStorage;
import seedu.address.testutil.ProductBuilder;

public class MainAppTest {

    public static final String ADDRESSBOOK_JSON = "addressbook.json";
    public static final String INVENTORY_JSON = "inventory.json";
    public static final String ALIAS_JSON = "alias.json";

    private static final int expectedCallCount = 1;
    private static final String CONFIG_FILE = "config.json";
    private static final String INVALID_CONFIG_CONTENT = "{ invalid-json ";
    private static final String PREFS_FILE = "prefs.json";
    private static final String CUSTOM_PREFS_FILE = "custom-prefs.json";
    private static final String SAVE_FAILURE_MESSAGE = "save failed";
    private static final String READ_FAILURE_MESSAGE = "read failed";
    private static final String ILLEGAL_VALUE_MESSAGE = "illegal value";
    private static final String UNKNOWN_VENDOR_PRODUCT_IDENTIFIER = "SKU-UNKNOWN";
    private static final String UNKNOWN_VENDOR_EMAIL = "missing@example.com";
    private static final String METHOD_LOAD_INITIAL_ADDRESS_BOOK = "loadInitialAddressBook";
    private static final String METHOD_LOAD_INITIAL_INVENTORY = "loadInitialInventory";
    private static final String METHOD_VALIDATE_INITIAL_INVENTORY = "validateInitialInventory";
    private static final String METHOD_LOAD_INITIAL_ALIASES = "loadInitialAliases";
    private static final String METHOD_GET_ILLEGAL_VALUE_DETAILS = "getIllegalValueDetails";

    @TempDir
    public Path testFolder;

    @Test
    public void initConfig_missingCustomPath_returnsDefaultConfigAndCreatesFile() {
        TestableMainApp app = new TestableMainApp();
        Path configPath = testFolder.resolve(CONFIG_FILE);

        Config config = app.callInitConfig(configPath);

        assertEquals(new Config(), config);
        assertTrue(Files.exists(configPath));
    }

    @Test
    public void initConfig_validCustomFile_returnsConfiguredValues() throws Exception {
        TestableMainApp app = new TestableMainApp();
        Path configPath = testFolder.resolve(CONFIG_FILE);

        Config expected = new Config();
        expected.setLogLevel(Level.SEVERE);
        expected.setUserPrefsFilePath(testFolder.resolve(CUSTOM_PREFS_FILE));
        ConfigUtil.saveConfig(expected, configPath);

        Config actual = app.callInitConfig(configPath);

        assertEquals(expected, actual);
    }

    @Test
    public void initConfig_invalidCustomFile_returnsDefaultConfig() throws Exception {
        TestableMainApp app = new TestableMainApp();
        Path configPath = testFolder.resolve(CONFIG_FILE);
        Files.writeString(configPath, INVALID_CONFIG_CONTENT);

        Config config = app.callInitConfig(configPath);

        assertEquals(new Config(), config);
        assertTrue(Files.exists(configPath));
    }

    @Test
    public void initPrefs_readReturnsEmpty_returnsDefaultAndSaves() {
        TestableMainApp app = new TestableMainApp();
        UserPrefsStorageStub storageStub = new UserPrefsStorageStub(testFolder.resolve(PREFS_FILE));

        UserPrefs prefs = app.callInitPrefs(storageStub);

        assertEquals(new UserPrefs(), prefs);
        assertEquals(expectedCallCount, storageStub.saveUserPrefsCallCount);
        assertEquals(new UserPrefs(storageStub.savedUserPrefs), prefs);
    }

    @Test
    public void initPrefs_readThrowsDataLoadingException_returnsDefaultAndSaves() {
        TestableMainApp app = new TestableMainApp();
        UserPrefsStorageStub storageStub = new UserPrefsStorageStub(testFolder.resolve(PREFS_FILE));
        storageStub.readException = new DataLoadingException(new IOException(READ_FAILURE_MESSAGE));

        UserPrefs prefs = app.callInitPrefs(storageStub);

        assertEquals(new UserPrefs(), prefs);
        assertEquals(expectedCallCount, storageStub.saveUserPrefsCallCount);
    }

    @Test
    public void initPrefs_saveThrowsIoException_doesNotThrowAndReturnsReadPrefs() {
        TestableMainApp app = new TestableMainApp();
        UserPrefsStorageStub storageStub = new UserPrefsStorageStub(testFolder.resolve(PREFS_FILE));

        UserPrefs expected = new UserPrefs();
        expected.setAddressBookFilePath(testFolder.resolve(ADDRESSBOOK_JSON));
        expected.setProductsFilePath(testFolder.resolve(INVENTORY_JSON));
        expected.setAliasFilePath(testFolder.resolve(ALIAS_JSON));

        storageStub.readUserPrefsResult = Optional.of(expected);
        storageStub.saveException = new IOException(SAVE_FAILURE_MESSAGE);

        UserPrefs actual = assertDoesNotThrow(() -> app.callInitPrefs(storageStub));

        assertEquals(expected, actual);
        assertEquals(expectedCallCount, storageStub.saveUserPrefsCallCount);
    }

    @Test
    public void stop_saveUserPrefsSuccess_noException() {
        TestableMainApp app = new TestableMainApp();
        StorageStub storageStub = new StorageStub();
        Model model = new ModelManager();
        app.setModelAndStorage(model, storageStub);

        assertDoesNotThrow(app::stop);
        assertEquals(expectedCallCount, storageStub.saveUserPrefsCallCount);
        assertEquals(new UserPrefs(model.getUserPrefs()), new UserPrefs(storageStub.savedUserPrefs));
    }

    @Test
    public void stop_saveUserPrefsIoException_noException() {
        TestableMainApp app = new TestableMainApp();
        StorageStub storageStub = new StorageStub();
        storageStub.saveException = new IOException(SAVE_FAILURE_MESSAGE);
        app.setModelAndStorage(new ModelManager(), storageStub);

        assertDoesNotThrow(app::stop);
        assertEquals(expectedCallCount, storageStub.saveUserPrefsCallCount);
    }

    @Test
    public void loadInitialAddressBook_emptyOptional_returnsSampleAddressBook() throws Exception {
        TestableMainApp app = new TestableMainApp();
        ReadableStorageStub storageStub = new ReadableStorageStub();
        storageStub.addressBookReadResult = Optional.empty();

        ReadOnlyAddressBook initialData = invokeLoadInitialAddressBook(app, storageStub);

        assertEquals(new AddressBook(SampleDataUtil.getSampleAddressBook()), new AddressBook(initialData));
    }

    @Test
    public void loadInitialAddressBook_dataLoadingException_returnsEmptyAddressBook() throws Exception {
        TestableMainApp app = new TestableMainApp();
        ReadableStorageStub storageStub = new ReadableStorageStub();
        storageStub.addressBookReadException = new DataLoadingException(new IOException(READ_FAILURE_MESSAGE));

        ReadOnlyAddressBook initialData = invokeLoadInitialAddressBook(app, storageStub);

        assertEquals(new AddressBook(), new AddressBook(initialData));
    }

    @Test
    public void loadInitialInventory_emptyOptional_returnsSampleInventory() throws Exception {
        TestableMainApp app = new TestableMainApp();
        ReadableStorageStub storageStub = new ReadableStorageStub();
        storageStub.inventoryReadResult = Optional.empty();

        ReadOnlyInventory initialInventory = invokeLoadInitialInventory(
                app, storageStub, SampleDataUtil.getSampleAddressBook());

        assertEquals(new Inventory(SampleDataUtil.getSampleInventory()), new Inventory(initialInventory));
    }

    @Test
    public void loadInitialInventory_dataLoadingException_returnsEmptyInventory() throws Exception {
        TestableMainApp app = new TestableMainApp();
        ReadableStorageStub storageStub = new ReadableStorageStub();
        storageStub.inventoryReadException = new DataLoadingException(new IOException(READ_FAILURE_MESSAGE));

        ReadOnlyInventory initialInventory = invokeLoadInitialInventory(app, storageStub, new AddressBook());

        assertEquals(new Inventory(), new Inventory(initialInventory));
    }

    @Test
    public void validateInitialInventory_illegalValue_returnsEmptyInventory() throws Exception {
        TestableMainApp app = new TestableMainApp();
        ReadableStorageStub storageStub = new ReadableStorageStub();

        Product unknownVendorProduct = new ProductBuilder(RICE)
                .withIdentifier(UNKNOWN_VENDOR_PRODUCT_IDENTIFIER)
                .withVendorEmail(UNKNOWN_VENDOR_EMAIL)
                .build();
        Inventory inventory = new Inventory();
        inventory.addProduct(unknownVendorProduct);

        ReadOnlyInventory validatedInventory = invokeValidateInitialInventory(
                app, storageStub, new AddressBook(), inventory);

        assertEquals(new Inventory(), new Inventory(validatedInventory));
    }

    @Test
    public void loadInitialAliases_emptyOptional_returnsEmptyAliases() throws Exception {
        TestableMainApp app = new TestableMainApp();
        ReadableStorageStub storageStub = new ReadableStorageStub();
        storageStub.aliasesReadResult = Optional.empty();

        ReadOnlyAliases initialAliases = invokeLoadInitialAliases(app, storageStub);

        assertEquals(new Aliases(), new Aliases(initialAliases));
    }

    @Test
    public void loadInitialAliases_dataLoadingException_returnsEmptyAliases() throws Exception {
        TestableMainApp app = new TestableMainApp();
        ReadableStorageStub storageStub = new ReadableStorageStub();
        storageStub.aliasesReadException = new DataLoadingException(new IOException(READ_FAILURE_MESSAGE));

        ReadOnlyAliases initialAliases = invokeLoadInitialAliases(app, storageStub);

        assertEquals(new Aliases(), new Aliases(initialAliases));
    }

    @Test
    public void getIllegalValueDetails_illegalValueCause_returnsDetails() throws Exception {
        TestableMainApp app = new TestableMainApp();
        DataLoadingException exception = new DataLoadingException(new IllegalValueException(ILLEGAL_VALUE_MESSAGE));

        Optional<String> details = invokeGetIllegalValueDetails(app, exception);

        assertEquals(Optional.of(ILLEGAL_VALUE_MESSAGE), details);
    }

    @Test
    public void getIllegalValueDetails_nonIllegalCause_returnsEmpty() throws Exception {
        TestableMainApp app = new TestableMainApp();
        DataLoadingException exception = new DataLoadingException(new IOException(READ_FAILURE_MESSAGE));

        Optional<String> details = invokeGetIllegalValueDetails(app, exception);

        assertEquals(Optional.empty(), details);
    }

    private ReadOnlyAddressBook invokeLoadInitialAddressBook(MainApp app, Storage storage) throws Exception {
        return (ReadOnlyAddressBook) invokePrivateMethod(
                app, METHOD_LOAD_INITIAL_ADDRESS_BOOK, new Class<?>[]{Storage.class}, storage);
    }

    private ReadOnlyInventory invokeLoadInitialInventory(MainApp app,
                                                         Storage storage,
                                                         ReadOnlyAddressBook initialData) throws Exception {
        return (ReadOnlyInventory) invokePrivateMethod(
                app,
                METHOD_LOAD_INITIAL_INVENTORY,
                new Class<?>[]{Storage.class, ReadOnlyAddressBook.class},
                storage,
                initialData);
    }

    private ReadOnlyInventory invokeValidateInitialInventory(MainApp app,
                                                             Storage storage,
                                                             ReadOnlyAddressBook initialData,
                                                             ReadOnlyInventory initialInventory) throws Exception {
        return (ReadOnlyInventory) invokePrivateMethod(
                app,
                METHOD_VALIDATE_INITIAL_INVENTORY,
                new Class<?>[]{Storage.class, ReadOnlyAddressBook.class, ReadOnlyInventory.class},
                storage,
                initialData,
                initialInventory);
    }

    private ReadOnlyAliases invokeLoadInitialAliases(MainApp app, Storage storage) throws Exception {
        return (ReadOnlyAliases) invokePrivateMethod(
                app, METHOD_LOAD_INITIAL_ALIASES, new Class<?>[]{Storage.class}, storage);
    }

    @SuppressWarnings("unchecked")
    private Optional<String> invokeGetIllegalValueDetails(MainApp app, DataLoadingException exception)
            throws Exception {
        return (Optional<String>) invokePrivateMethod(
                app,
                METHOD_GET_ILLEGAL_VALUE_DETAILS,
                new Class<?>[]{DataLoadingException.class},
                exception);
    }

    private Object invokePrivateMethod(MainApp app, String methodName, Class<?>[] parameterTypes, Object... args)
            throws Exception {
        Method method = MainApp.class.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(app, args);
    }

    private static class TestableMainApp extends MainApp {
        Config callInitConfig(Path configFilePath) {
            return initConfig(configFilePath);
        }

        UserPrefs callInitPrefs(UserPrefsStorage storage) {
            return initPrefs(storage);
        }

        void setModelAndStorage(Model model, Storage storage) {
            this.model = model;
            this.storage = storage;
        }
    }

    private static class UserPrefsStorageStub implements UserPrefsStorage {
        private final Path filePath;

        private Optional<UserPrefs> readUserPrefsResult = Optional.empty();
        private DataLoadingException readException;
        private IOException saveException;
        private int saveUserPrefsCallCount;
        private ReadOnlyUserPrefs savedUserPrefs;

        UserPrefsStorageStub(Path filePath) {
            this.filePath = filePath;
        }

        @Override
        public Path getUserPrefsFilePath() {
            return filePath;
        }

        @Override
        public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
            if (readException != null) {
                throw readException;
            }
            return readUserPrefsResult;
        }

        @Override
        public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
            saveUserPrefsCallCount++;
            savedUserPrefs = userPrefs;
            if (saveException != null) {
                throw saveException;
            }
        }
    }

    private static class StorageStub implements Storage {
        private int saveUserPrefsCallCount;
        private ReadOnlyUserPrefs savedUserPrefs;
        private IOException saveException;

        @Override
        public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
            saveUserPrefsCallCount++;
            savedUserPrefs = userPrefs;
            if (saveException != null) {
                throw saveException;
            }
        }

        @Override
        public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getUserPrefsFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getInventoryFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<ReadOnlyInventory> readInventory() throws DataLoadingException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<ReadOnlyInventory> readInventory(Path filePath) throws DataLoadingException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void saveInventory(ReadOnlyInventory inventory) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void saveInventory(ReadOnlyInventory inventory, Path filePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAliasFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<ReadOnlyAliases> readAliases() throws DataLoadingException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<ReadOnlyAliases> readAliases(Path filePath) throws DataLoadingException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void saveAliases(ReadOnlyAliases aliases) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void saveAliases(ReadOnlyAliases aliases, Path filePath) {
            throw new AssertionError("This method should not be called.");
        }
    }

    private static class ReadableStorageStub extends StorageStub {
        private Path addressBookFilePath = Path.of(ADDRESSBOOK_JSON);
        private Path inventoryFilePath = Path.of(INVENTORY_JSON);
        private Path aliasFilePath = Path.of(ALIAS_JSON);

        private Optional<ReadOnlyAddressBook> addressBookReadResult = Optional.of(new AddressBook());
        private Optional<ReadOnlyInventory> inventoryReadResult = Optional.of(new Inventory());
        private Optional<ReadOnlyAliases> aliasesReadResult = Optional.of(new Aliases());

        private DataLoadingException addressBookReadException;
        private DataLoadingException inventoryReadException;
        private DataLoadingException aliasesReadException;

        @Override
        public Path getAddressBookFilePath() {
            return addressBookFilePath;
        }

        @Override
        public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
            if (addressBookReadException != null) {
                throw addressBookReadException;
            }
            return addressBookReadResult;
        }

        @Override
        public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
            return readAddressBook();
        }

        @Override
        public Path getInventoryFilePath() {
            return inventoryFilePath;
        }

        @Override
        public Optional<ReadOnlyInventory> readInventory() throws DataLoadingException {
            if (inventoryReadException != null) {
                throw inventoryReadException;
            }
            return inventoryReadResult;
        }

        @Override
        public Optional<ReadOnlyInventory> readInventory(Path filePath) throws DataLoadingException {
            return readInventory();
        }

        @Override
        public Path getAliasFilePath() {
            return aliasFilePath;
        }

        @Override
        public Optional<ReadOnlyAliases> readAliases() throws DataLoadingException {
            if (aliasesReadException != null) {
                throw aliasesReadException;
            }
            return aliasesReadResult;
        }

        @Override
        public Optional<ReadOnlyAliases> readAliases(Path filePath) throws DataLoadingException {
            return readAliases();
        }
    }
}
