package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyAliases;
import seedu.address.model.ReadOnlyInventory;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;

/**
 * Manages storage of AddressBook data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private AddressBookStorage addressBookStorage;
    private UserPrefsStorage userPrefsStorage;
    private InventoryStorage inventoryStorage;
    private AliasStorage aliasStorage;

    /**
     * Creates a {@code StorageManager} with the given {@code AddressBookStorage} and {@code UserPrefStorage}.
     */
    public StorageManager(AddressBookStorage addressBookStorage,
                          UserPrefsStorage userPrefsStorage,
                          InventoryStorage inventoryStorage,
                          AliasStorage aliasStorage) {
        this.addressBookStorage = addressBookStorage;
        this.userPrefsStorage = userPrefsStorage;
        this.inventoryStorage = inventoryStorage;
        this.aliasStorage = aliasStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ AddressBook methods ==============================

    @Override
    public Path getAddressBookFilePath() {
        return addressBookStorage.getAddressBookFilePath();
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
        return readAddressBook(addressBookStorage.getAddressBookFilePath());
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
        logger.fine("Attempting to read Address Book data from file: " + filePath);
        return addressBookStorage.readAddressBook(filePath);
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, addressBookStorage.getAddressBookFilePath());
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
        logger.fine("Attempting to write to Address Book data file: " + filePath);
        addressBookStorage.saveAddressBook(addressBook, filePath);
    }

    // ================ Inventory methods ==============================
    @Override
    public Path getInventoryFilePath() {
        return inventoryStorage.getInventoryFilePath();
    }

    @Override
    public Optional<ReadOnlyInventory> readInventory() throws DataLoadingException {
        return readInventory(inventoryStorage.getInventoryFilePath());
    }

    @Override
    public Optional<ReadOnlyInventory> readInventory(Path filePath) throws DataLoadingException {
        logger.fine("Attempting to read Inventory data from file: " + filePath);
        return inventoryStorage.readInventory(filePath);
    }

    @Override
    public void saveInventory(ReadOnlyInventory inventory) throws IOException {
        saveInventory(inventory, inventoryStorage.getInventoryFilePath());
    }

    @Override
    public void saveInventory(ReadOnlyInventory inventory, Path filePath) throws IOException {
        logger.fine("Attempting to write to Inventory data file: " + filePath);
        inventoryStorage.saveInventory(inventory, filePath);
    }

    @Override
    public Path getAliasFilePath() {
        return aliasStorage.getAliasFilePath();
    }

    @Override
    public Optional<ReadOnlyAliases> readAliases() throws DataLoadingException {
        return readAliases(aliasStorage.getAliasFilePath());
    }

    @Override
    public Optional<ReadOnlyAliases> readAliases(Path filePath) throws DataLoadingException {
        logger.fine("Attempting to read Alias data from file: " + filePath);
        return aliasStorage.readAliases(filePath);
    }

    @Override
    public void saveAliases(ReadOnlyAliases aliases) throws IOException {
        saveAliases(aliases, aliasStorage.getAliasFilePath());
    }

    @Override
    public void saveAliases(ReadOnlyAliases aliases, Path filePath) throws IOException {
        logger.fine("Attempting to write to Alias data file: " + filePath);
        aliasStorage.saveAliases(aliases, filePath);
    }

}
