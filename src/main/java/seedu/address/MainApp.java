package seedu.address;

import static seedu.address.model.util.VendorVaultConsistencyUtil.validateOrThrow;
import static seedu.address.ui.Messages.MESSAGE_COULD_NOT_LOAD_STARTING_EMPTY_ADDRESS_BOOK;
import static seedu.address.ui.Messages.MESSAGE_COULD_NOT_LOAD_STARTING_EMPTY_ALIAS;
import static seedu.address.ui.Messages.MESSAGE_COULD_NOT_LOAD_STARTING_EMPTY_INVENTORY;
import static seedu.address.ui.Messages.MESSAGE_CREATING_NEW_DATA_FILE;
import static seedu.address.ui.Messages.MESSAGE_DATA_FILE_AT;
import static seedu.address.ui.Messages.MESSAGE_ILLEGAL_VALUES_FOUND_IN;
import static seedu.address.ui.Messages.MESSAGE_LOG_SEPARATOR;
import static seedu.address.ui.Messages.MESSAGE_POPULATED_EMPTY_ALIAS_FILE;
import static seedu.address.ui.Messages.MESSAGE_POPULATED_SAMPLE_ADDRESS_BOOK;
import static seedu.address.ui.Messages.MESSAGE_POPULATED_SAMPLE_INVENTORY;
import static seedu.address.ui.Messages.NEWLINE;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.address.commons.core.Config;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.Version;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.ConfigUtil;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
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
import seedu.address.model.VendorVault;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.AddressBookStorage;
import seedu.address.storage.AliasStorage;
import seedu.address.storage.InventoryStorage;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonAliasStorage;
import seedu.address.storage.JsonInventoryStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.storage.UserPrefsStorage;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(1, 5, 1, true);
    public static final String LOG_HEADER =
            "=============================[ Initializing VendorVault ]===========================";
    public static final String LOG_FOOTER =
            "============================ [ Stopping VendorVault ] =============================";

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Config config;

    @Override
    public void init() throws Exception {
        logger.info(LOG_HEADER);
        super.init();

        AppParameters appParameters = AppParameters.parse(getParameters());
        config = initConfig(appParameters.getConfigPath());
        initLogging(config);

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        AddressBookStorage addressBookStorage = new JsonAddressBookStorage(userPrefs.getAddressBookFilePath());
        InventoryStorage inventoryStorage = new JsonInventoryStorage(userPrefs.getProductsFilePath());
        AliasStorage aliasStorage = new JsonAliasStorage(userPrefs.getAliasFilePath());
        storage = new StorageManager(addressBookStorage, userPrefsStorage, inventoryStorage, aliasStorage);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s address book and {@code userPrefs}. <br>
     * The data from the sample address book will be used instead if {@code storage}'s address book is not found,
     * or an empty address book will be used instead if errors occur when reading {@code storage}'s address book.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getAddressBookFilePath());

        ReadOnlyAddressBook initialData = loadInitialAddressBook(storage);
        ReadOnlyInventory initialInventory = loadInitialInventory(storage, initialData);
        ReadOnlyAliases initialAliases = loadInitialAliases(storage);

        VendorVault initialVV = new VendorVault(initialData, initialInventory);
        return new ModelManager(initialVV, userPrefs, initialAliases);
    }

    private ReadOnlyAddressBook loadInitialAddressBook(Storage storage) {
        Path addressBookFilePath = storage.getAddressBookFilePath();

        try {
            Optional<ReadOnlyAddressBook> addressBookOptional = storage.readAddressBook();
            logDataFileInitializationIfMissing(addressBookOptional, addressBookFilePath,
                    MESSAGE_POPULATED_SAMPLE_ADDRESS_BOOK);

            return addressBookOptional.orElseGet(SampleDataUtil::getSampleAddressBook);
        } catch (DataLoadingException e) {
            logLoadingIssue(addressBookFilePath, e);
            logger.warning(buildCouldNotLoadWarning(addressBookFilePath,
                    MESSAGE_COULD_NOT_LOAD_STARTING_EMPTY_ADDRESS_BOOK));

            return new AddressBook();
        }
    }

    private ReadOnlyInventory loadInitialInventory(Storage storage, ReadOnlyAddressBook initialData) {
        Path inventoryFilePath = storage.getInventoryFilePath();

        try {
            Optional<ReadOnlyInventory> inventoryOptional = storage.readInventory();
            logDataFileInitializationIfMissing(inventoryOptional, inventoryFilePath,
                    MESSAGE_POPULATED_SAMPLE_INVENTORY);

            ReadOnlyInventory initialInventory = inventoryOptional.orElseGet(SampleDataUtil::getSampleInventory);
            return validateInitialInventory(inventoryFilePath, initialData, initialInventory);
        } catch (IllegalValueException e) {
            logIllegalValueIssue(inventoryFilePath, e.getMessage());
            logger.warning(buildCouldNotLoadWarning(inventoryFilePath,
                    MESSAGE_COULD_NOT_LOAD_STARTING_EMPTY_INVENTORY));

            return new Inventory();
        } catch (DataLoadingException e) {
            logInventoryLoadingIssue(inventoryFilePath, e, initialData);
            logger.warning(buildCouldNotLoadWarning(inventoryFilePath,
                    MESSAGE_COULD_NOT_LOAD_STARTING_EMPTY_INVENTORY));

            return new Inventory();
        }
    }

    private ReadOnlyInventory validateInitialInventory(Path inventoryFilePath,
                                                       ReadOnlyAddressBook initialData,
                                                       ReadOnlyInventory initialInventory)
            throws IllegalValueException {
        validateOrThrow(initialData, initialInventory, inventoryFilePath);
        return initialInventory;
    }

    private void logInventoryLoadingIssue(Path inventoryFilePath, DataLoadingException exception,
                                          ReadOnlyAddressBook initialData) {
        Optional<String> illegalValueDetails = logLoadingIssue(inventoryFilePath, exception);
        if (!illegalValueDetails.isPresent()) {
            return;
        }

        String details = illegalValueDetails.get();
    }

    private ReadOnlyAliases loadInitialAliases(Storage storage) {
        Path aliasFilePath = storage.getAliasFilePath();

        try {
            Optional<ReadOnlyAliases> aliasesOptional = storage.readAliases();
            logDataFileInitializationIfMissing(aliasesOptional, aliasFilePath, MESSAGE_POPULATED_EMPTY_ALIAS_FILE);

            return aliasesOptional.orElseGet(Aliases::new);
        } catch (DataLoadingException e) {
            logLoadingIssue(aliasFilePath, e);
            logger.warning(buildCouldNotLoadWarning(aliasFilePath,
                    MESSAGE_COULD_NOT_LOAD_STARTING_EMPTY_ALIAS));

            return new Aliases();
        }
    }

    private void logDataFileInitializationIfMissing(Optional<?> dataOptional, Path filePath, String populationMessage) {
        if (!dataOptional.isPresent()) {
            logger.info(MESSAGE_CREATING_NEW_DATA_FILE + filePath + populationMessage);
        }
    }

    private String buildCouldNotLoadWarning(Path filePath, String fallbackMessage) {
        return MESSAGE_DATA_FILE_AT + filePath + fallbackMessage;
    }

    private Optional<String> logLoadingIssue(Path filePath, DataLoadingException exception) {
        Optional<String> illegalValueDetails = getIllegalValueDetails(exception);
        illegalValueDetails.ifPresent(details -> logIllegalValueIssue(filePath, details));
        return illegalValueDetails;
    }

    private Optional<String> getIllegalValueDetails(DataLoadingException exception) {
        Throwable cause = exception.getCause();

        if (!(cause instanceof IllegalValueException)) {
            return Optional.empty();
        }

        String details = cause.getMessage();
        return details == null ? Optional.empty() : Optional.of(details);
    }

    private void logIllegalValueIssue(Path filePath, String details) {
        logger.warning(MESSAGE_ILLEGAL_VALUES_FOUND_IN + filePath + MESSAGE_LOG_SEPARATOR
                + details.replace(NEWLINE, " "));
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    /**
     * Returns a {@code Config} using the file at {@code configFilePath}. <br>
     * The default file path {@code Config#DEFAULT_CONFIG_FILE} will be used instead
     * if {@code configFilePath} is null.
     */
    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed;

        configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            if (!configOptional.isPresent()) {
                logger.info("Creating new config file " + configFilePathUsed);
            }
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataLoadingException e) {
            logger.warning("Config file at " + configFilePathUsed + " could not be loaded."
                    + " Using default config properties.");
            initializedConfig = new Config();
        }

        // Update config file in case it was missing to begin with or there are new/unused fields
        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (!prefsOptional.isPresent()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        // Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting VendorVault " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info(LOG_FOOTER);
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
