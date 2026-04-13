package seedu.address;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalProducts.RICE;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;

import seedu.address.commons.core.Config;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.ConfigUtil;
import seedu.address.commons.util.JsonUtil;
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
    private static final String DUPLICATE_EMAIL_DETAILS =
        "Duplicate contact email 'alice@example.com' at lines 5, 11.";
    private static final String UNKNOWN_VENDOR_PRODUCT_IDENTIFIER = "SKU-UNKNOWN";
    private static final String UNKNOWN_VENDOR_EMAIL = "missing@example.com";
    private static final String METHOD_LOAD_INITIAL_ADDRESS_BOOK = "loadInitialAddressBook";
    private static final String METHOD_LOAD_INITIAL_INVENTORY = "loadInitialInventory";
    private static final String METHOD_LOAD_INITIAL_ALIASES = "loadInitialAliases";
    private static final String METHOD_EXTRACT_VALIDATION_OR_PARSING_DETAILS =
            "extractValidationOrParsingDetails";
    private static final String METHOD_EXTRACT_ROOT_CAUSE_SUMMARY = "extractRootCauseSummary";
    private static final String METHOD_FORMAT_JSON_PARSING_DETAILS = "formatJsonParsingDetails";
    private static final String METHOD_NORMALIZE_JSON_PARSING_MESSAGE = "normalizeJsonParsingMessage";
    private static final String METHOD_EXTRACT_FIRST_LINE = "extractFirstLine";
    private static final String METHOD_INIT_MODEL_MANAGER = "initModelManager";
    private static final String METHOD_LOG_DATA_VALIDATION_ISSUE = "logDataValidationIssue";
    private static final String METHOD_INIT_LOGGING = "initLogging";
    private static final String IO_EXCEPTION_DETAILS = "IOException: " + READ_FAILURE_MESSAGE;
    private static final String DETAILS_WITH_NEWLINE = "line one\nline two";
    private static final String INVALID_JSON_WITH_UNQUOTED_TOKEN = "{\"vendorEmail\": hello@synapse.sg}";
    private static final String INVALID_JSON_GENERIC_PREFIX = "Invalid JSON format";
    private static final String INVALID_JSON_TOKEN_DETAILS = "Unrecognized token 'hello'.";
    private static final String INVALID_JSON_LINE_PREFIX = "Invalid JSON format at line 1: ";
    private static final String JSON_PARSE_FALLBACK_MESSAGE = "Malformed JSON.";
    private static final String GENERIC_JSON_PARSE_MESSAGE = "Unexpected close marker";
    private static final String INVALID_JSON_GENERIC_DETAILS =
            INVALID_JSON_GENERIC_PREFIX + ": " + GENERIC_JSON_PARSE_MESSAGE;
    private static final JsonLocation UNKNOWN_LOCATION = new JsonLocation("source", -1L, -1L, 0, 1);
    private static final String CONFIG_FILE_PREFIX = "Config file at ";
    private static final String PREFS_FILE_PREFIX = "Preference file at ";
    private static final String READ_DATA_PREFIX = "Could not read data in ";
    private static final String FAILED_SAVE_CONFIG_PREFIX = "Failed to save config file :";
    private static final Path LOG_TEST_FILE = Path.of("src", "test", "data",
            "VendorVaultConsistencyUtilTest", "unknownVendorMixedInventory.json");

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
    public void initConfig_invalidCustomFile_logsFriendlyJsonParseDetails() throws Exception {
        TestableMainApp app = new TestableMainApp();
        Path configPath = testFolder.resolve(CONFIG_FILE);
        Files.writeString(configPath, INVALID_CONFIG_CONTENT);

        withMainAppLogger(handler -> {
            app.callInitConfig(configPath);
            assertTrue(handler.contains(CONFIG_FILE_PREFIX + configPath + ": " + INVALID_JSON_GENERIC_PREFIX));
        });
    }

    @Test
    public void initConfig_saveConfigThrowsIoException_logsWarningAndReturnsConfig() throws Exception {
        TestableMainApp app = new TestableMainApp();
        Path directoryPath = Files.createDirectories(testFolder.resolve("config-directory"));

        withMainAppLogger(handler -> {
            Config config = app.callInitConfig(directoryPath);

            assertEquals(new Config(), config);
            assertTrue(handler.contains(FAILED_SAVE_CONFIG_PREFIX));
        });
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
    public void initPrefs_jsonParseFailure_logsFriendlyJsonParseDetailsAndReturnsDefault() {
        TestableMainApp app = new TestableMainApp();
        UserPrefsStorageStub storageStub = new UserPrefsStorageStub(testFolder.resolve(PREFS_FILE));
        storageStub.readException = new DataLoadingException(createMalformedJsonIoException());

        withMainAppLogger(handler -> {
            UserPrefs prefs = app.callInitPrefs(storageStub);

            assertEquals(new UserPrefs(), prefs);
            assertEquals(expectedCallCount, storageStub.saveUserPrefsCallCount);
            assertTrue(handler.contains(PREFS_FILE_PREFIX + storageStub.getUserPrefsFilePath() + ": "
                    + INVALID_JSON_LINE_PREFIX));
            assertTrue(handler.contains(INVALID_JSON_TOKEN_DETAILS));
        });
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
    public void loadInitialAddressBook_dataLoadingException_logsConciseCause() throws Exception {
        TestableMainApp app = new TestableMainApp();
        ReadableStorageStub storageStub = new ReadableStorageStub();
        storageStub.addressBookReadException = new DataLoadingException(new IOException(READ_FAILURE_MESSAGE));

        withMainAppLogger(handler -> {
            invokeLoadInitialAddressBook(app, storageStub);
            assertTrue(handler.contains(READ_DATA_PREFIX + storageStub.getAddressBookFilePath()
                    + ": " + IO_EXCEPTION_DETAILS));
        });
    }

    @Test
    public void loadInitialAddressBook_duplicateEmailDetails_logsAndReturnsEmptyAddressBook() throws Exception {
        TestableMainApp app = new TestableMainApp();
        ReadableStorageStub storageStub = new ReadableStorageStub();
        storageStub.addressBookReadException = new DataLoadingException(new IllegalValueException(
                DUPLICATE_EMAIL_DETAILS));

        withMainAppLogger(handler -> {
            ReadOnlyAddressBook initialData = invokeLoadInitialAddressBook(app, storageStub);

            assertEquals(new AddressBook(), new AddressBook(initialData));
            assertTrue(handler.contains(DUPLICATE_EMAIL_DETAILS));
        });
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
    public void loadInitialInventory_illegalValue_returnsEmptyInventory() throws Exception {
        TestableMainApp app = new TestableMainApp();
        ReadableStorageStub storageStub = new ReadableStorageStub();

        Product unknownVendorProduct = new ProductBuilder(RICE)
                .withIdentifier(UNKNOWN_VENDOR_PRODUCT_IDENTIFIER)
                .withVendorEmail(UNKNOWN_VENDOR_EMAIL)
                .build();
        Inventory inventory = new Inventory();
        inventory.addProduct(unknownVendorProduct);
        storageStub.inventoryReadResult = Optional.of(inventory);

        ReadOnlyInventory validatedInventory = invokeLoadInitialInventory(app, storageStub, new AddressBook());

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
    public void extractValidationOrParsingDetails_illegalValueCause_returnsDetails() throws Exception {
        TestableMainApp app = new TestableMainApp();
        DataLoadingException exception = new DataLoadingException(new IllegalValueException(ILLEGAL_VALUE_MESSAGE));

        Optional<String> details = invokeExtractValidationOrParsingDetails(app, exception);

        assertEquals(Optional.of(ILLEGAL_VALUE_MESSAGE), details);
    }

    @Test
    public void extractValidationOrParsingDetails_nonIllegalCause_returnsEmpty() throws Exception {
        TestableMainApp app = new TestableMainApp();
        DataLoadingException exception = new DataLoadingException(new IOException(READ_FAILURE_MESSAGE));

        Optional<String> details = invokeExtractValidationOrParsingDetails(app, exception);

        assertEquals(Optional.empty(), details);
    }

    @Test
    public void extractValidationOrParsingDetails_illegalValueCauseWithNullMessage_returnsEmpty() throws Exception {
        TestableMainApp app = new TestableMainApp();
        DataLoadingException exception = new DataLoadingException(new IllegalValueException((String) null));

        Optional<String> details = invokeExtractValidationOrParsingDetails(app, exception);

        assertEquals(Optional.empty(), details);
    }

    @Test
    public void extractValidationOrParsingDetails_jsonParseCause_returnsFriendlyLineMessage() throws Exception {
        TestableMainApp app = new TestableMainApp();
        DataLoadingException exception = new DataLoadingException(createMalformedJsonIoException());

        Optional<String> details = invokeExtractValidationOrParsingDetails(app, exception);

        assertTrue(details.isPresent());
        assertTrue(details.get().startsWith(INVALID_JSON_LINE_PREFIX));
        assertTrue(details.get().contains(INVALID_JSON_TOKEN_DETAILS));
    }

    @Test
    public void extractValidationOrParsingDetails_nestedJsonParseCause_returnsFriendlyLineMessage() throws Exception {
        TestableMainApp app = new TestableMainApp();
        DataLoadingException exception = new DataLoadingException(
                new IOException(READ_FAILURE_MESSAGE, createMalformedJsonIoException()));

        Optional<String> details = invokeExtractValidationOrParsingDetails(app, exception);

        assertTrue(details.isPresent());
        assertTrue(details.get().startsWith(INVALID_JSON_LINE_PREFIX));
        assertTrue(details.get().contains(INVALID_JSON_TOKEN_DETAILS));
    }

    @Test
    public void extractRootCauseSummary_nullCause_returnsEmpty() throws Exception {
        TestableMainApp app = new TestableMainApp();

        Optional<String> details = invokeExtractRootCauseSummary(app, null);

        assertEquals(Optional.empty(), details);
    }

    @Test
    public void extractRootCauseSummary_nestedCauseWithNullMessage_returnsRootClassName() throws Exception {
        TestableMainApp app = new TestableMainApp();
        Throwable root = new IllegalArgumentException((String) null);
        Throwable mid = new RuntimeException("mid", root);
        Throwable top = new IOException("top", mid);

        Optional<String> details = invokeExtractRootCauseSummary(app, top);

        assertEquals(Optional.of("IllegalArgumentException"), details);
    }

    @Test
    public void extractRootCauseSummary_blankRootMessage_returnsRootClassName() throws Exception {
        TestableMainApp app = new TestableMainApp();

        Optional<String> details = invokeExtractRootCauseSummary(app, new IllegalStateException("   "));

        assertEquals(Optional.of("IllegalStateException"), details);
    }

    @Test
    public void extractRootCauseSummary_nestedCauseWithMultilineMessage_returnsClassAndFirstLine() throws Exception {
        TestableMainApp app = new TestableMainApp();
        Throwable root = new IOException("root message\nextra details");
        Throwable top = new RuntimeException("wrapper", root);

        Optional<String> details = invokeExtractRootCauseSummary(app, top);

        assertEquals(Optional.of("IOException: root message"), details);
    }

    @Test
    public void formatJsonParsingDetails_nullLocation_returnsGenericFormatMessage() throws Exception {
        TestableMainApp app = new TestableMainApp();
        JsonProcessingException exception = createJsonProcessingException(GENERIC_JSON_PARSE_MESSAGE, null);

        String details = invokeFormatJsonParsingDetails(app, exception);

        assertEquals(INVALID_JSON_GENERIC_DETAILS, details);
    }

    @Test
    public void formatJsonParsingDetails_locationLineNotPositive_returnsGenericFormatMessage() throws Exception {
        TestableMainApp app = new TestableMainApp();
        JsonProcessingException exception = createJsonProcessingException(GENERIC_JSON_PARSE_MESSAGE, UNKNOWN_LOCATION);

        String details = invokeFormatJsonParsingDetails(app, exception);

        assertEquals(INVALID_JSON_GENERIC_DETAILS, details);
    }

    @Test
    public void normalizeJsonParsingMessage_emptyOriginalMessage_returnsFallback() throws Exception {
        TestableMainApp app = new TestableMainApp();
        JsonProcessingException exception = createJsonProcessingException("", null);

        String message = invokeNormalizeJsonParsingMessage(app, exception);

        assertEquals(JSON_PARSE_FALLBACK_MESSAGE, message);
    }

    @Test
    public void extractFirstLine_nullMessage_returnsEmptyString() throws Exception {
        TestableMainApp app = new TestableMainApp();

        String firstLine = invokeExtractFirstLine(app, null);

        assertEquals("", firstLine);
    }

    @Test
    public void initModelManager_emptyStorageData_returnsModelWithSampleData() throws Exception {
        TestableMainApp app = new TestableMainApp();
        ReadableStorageStub storageStub = new ReadableStorageStub();
        storageStub.addressBookReadResult = Optional.empty();
        storageStub.inventoryReadResult = Optional.empty();
        storageStub.aliasesReadResult = Optional.empty();

        Model model = invokeInitModelManager(app, storageStub, new UserPrefs());

        int samplePersonListSize = SampleDataUtil.getSampleAddressBook().getPersonList().size();
        int modelPersonListSize = model.getAddressBook().getPersonList().size();
        int sampleProductListSize = SampleDataUtil.getSampleInventory().getProductList().size();
        int modelProductListSize = model.getInventory().getProductList().size();

        assertEquals(samplePersonListSize, modelPersonListSize);
        assertEquals(sampleProductListSize, modelProductListSize);
        assertEquals(new Aliases(), new Aliases(model.getAliases()));
    }

    @Test
    public void logDataValidationIssue_withNewLineInDetails_noException() {
        TestableMainApp app = new TestableMainApp();

        assertDoesNotThrow(() -> invokeLogDataValidationIssue(app, LOG_TEST_FILE, DETAILS_WITH_NEWLINE));
    }

    @Test
    public void initLogging_withConfig_noException() {
        TestableMainApp app = new TestableMainApp();

        assertDoesNotThrow(() -> invokeInitLogging(app, new Config()));
    }

    private Model invokeInitModelManager(MainApp app, Storage storage, ReadOnlyUserPrefs userPrefs) throws Exception {
        return (Model) invokePrivateMethod(
                app,
                METHOD_INIT_MODEL_MANAGER,
                // Reflection needs an explicit parameter-type signature to resolve the private overload:
                new Class<?>[]{Storage.class, ReadOnlyUserPrefs.class},
                storage,
                userPrefs);
    }

    private void invokeLogDataValidationIssue(MainApp app, Path filePath, String details) throws Exception {
        invokePrivateMethod(
                app,
                METHOD_LOG_DATA_VALIDATION_ISSUE,
                new Class<?>[]{Path.class, String.class},
                filePath,
                details);
    }

    private void invokeInitLogging(MainApp app, Config config) throws Exception {
        invokePrivateMethod(
                app,
                METHOD_INIT_LOGGING,
                new Class<?>[]{Config.class},
                config);
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

    private ReadOnlyAliases invokeLoadInitialAliases(MainApp app, Storage storage) throws Exception {
        return (ReadOnlyAliases) invokePrivateMethod(
                app, METHOD_LOAD_INITIAL_ALIASES, new Class<?>[]{Storage.class}, storage);
    }

    @SuppressWarnings("unchecked")
    private Optional<String> invokeExtractValidationOrParsingDetails(MainApp app, DataLoadingException exception)
            throws Exception {
        return (Optional<String>) invokePrivateMethod(
                app,
                METHOD_EXTRACT_VALIDATION_OR_PARSING_DETAILS,
                new Class<?>[]{DataLoadingException.class},
                exception);
    }

    @SuppressWarnings("unchecked")
    private Optional<String> invokeExtractRootCauseSummary(MainApp app, Throwable cause) throws Exception {
        return (Optional<String>) invokePrivateMethod(
                app,
                METHOD_EXTRACT_ROOT_CAUSE_SUMMARY,
                new Class<?>[]{Throwable.class},
                cause);
    }

    private String invokeFormatJsonParsingDetails(MainApp app, JsonProcessingException exception) throws Exception {
        return (String) invokePrivateMethod(
                app,
                METHOD_FORMAT_JSON_PARSING_DETAILS,
                new Class<?>[]{JsonProcessingException.class},
                exception);
    }

    private String invokeNormalizeJsonParsingMessage(MainApp app, JsonProcessingException exception) throws Exception {
        return (String) invokePrivateMethod(
                app,
                METHOD_NORMALIZE_JSON_PARSING_MESSAGE,
                new Class<?>[]{JsonProcessingException.class},
                exception);
    }

    private String invokeExtractFirstLine(MainApp app, String message) throws Exception {
        return (String) invokePrivateMethod(
                app,
                METHOD_EXTRACT_FIRST_LINE,
                new Class<?>[]{String.class},
                message);
    }

    private Object invokePrivateMethod(MainApp app, String methodName, Class<?>[] parameterTypes, Object... args)
            throws Exception {
        Method method = MainApp.class.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(app, args);
    }

    private IOException createMalformedJsonIoException() {
        try {
            JsonUtil.fromJsonString(INVALID_JSON_WITH_UNQUOTED_TOKEN, Object.class);
            throw new AssertionError("Expected malformed JSON to throw IOException");
        } catch (IOException ioe) {
            return ioe;
        }
    }

    private JsonProcessingException createJsonProcessingException(String message, JsonLocation location) {
        return new JsonProcessingException(message, location) {
            private static final long serialVersionUID = 1L;
        };
    }

    private void withMainAppLogger(ThrowingConsumer<CapturingLogHandler> assertion) {
        Logger mainAppLogger = LogsCenter.getLogger(MainApp.class);
        CapturingLogHandler handler = new CapturingLogHandler();
        mainAppLogger.addHandler(handler);

        try {
            assertion.accept(handler);
        } catch (Exception e) {
            throw new AssertionError("Unexpected exception during log assertion", e);
        } finally {
            mainAppLogger.removeHandler(handler);
        }
    }

    @FunctionalInterface
    private interface ThrowingConsumer<T> {
        void accept(T value) throws Exception;
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
        private static final String METHOD_SHOULD_NOT_BE_CALLED_MESSAGE = "This method should not be called.";

        private int saveUserPrefsCallCount;
        private ReadOnlyUserPrefs savedUserPrefs;
        private IOException saveException;

        private AssertionError methodShouldNotBeCalledError() {
            return new AssertionError(METHOD_SHOULD_NOT_BE_CALLED_MESSAGE);
        }

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
            throw methodShouldNotBeCalledError();
        }

        @Override
        public Path getUserPrefsFilePath() {
            throw methodShouldNotBeCalledError();
        }

        @Override
        public Path getAddressBookFilePath() {
            throw methodShouldNotBeCalledError();
        }

        @Override
        public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
            throw methodShouldNotBeCalledError();
        }

        @Override
        public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
            throw methodShouldNotBeCalledError();
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook) {
            throw methodShouldNotBeCalledError();
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) {
            throw methodShouldNotBeCalledError();
        }

        @Override
        public Path getInventoryFilePath() {
            throw methodShouldNotBeCalledError();
        }

        @Override
        public Optional<ReadOnlyInventory> readInventory() throws DataLoadingException {
            throw methodShouldNotBeCalledError();
        }

        @Override
        public Optional<ReadOnlyInventory> readInventory(Path filePath) throws DataLoadingException {
            throw methodShouldNotBeCalledError();
        }

        @Override
        public void saveInventory(ReadOnlyInventory inventory) {
            throw methodShouldNotBeCalledError();
        }

        @Override
        public void saveInventory(ReadOnlyInventory inventory, Path filePath) {
            throw methodShouldNotBeCalledError();
        }

        @Override
        public Path getAliasFilePath() {
            throw methodShouldNotBeCalledError();
        }

        @Override
        public Optional<ReadOnlyAliases> readAliases() throws DataLoadingException {
            throw methodShouldNotBeCalledError();
        }

        @Override
        public Optional<ReadOnlyAliases> readAliases(Path filePath) throws DataLoadingException {
            throw methodShouldNotBeCalledError();
        }

        @Override
        public void saveAliases(ReadOnlyAliases aliases) {
            throw methodShouldNotBeCalledError();
        }

        @Override
        public void saveAliases(ReadOnlyAliases aliases, Path filePath) {
            throw methodShouldNotBeCalledError();
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

    private static class CapturingLogHandler extends Handler {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            if (record != null && record.getMessage() != null) {
                messages.add(record.getMessage());
            }
        }

        @Override
        public void flush() {}

        @Override
        public void close() throws SecurityException {}

        boolean contains(String snippet) {
            return messages.stream().anyMatch(message -> message.contains(snippet));
        }
    }
}
