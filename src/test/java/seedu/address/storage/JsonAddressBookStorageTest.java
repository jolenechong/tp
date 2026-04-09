package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.storage.JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.HOON;
import static seedu.address.testutil.TypicalPersons.IDA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;

public class JsonAddressBookStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAddressBookStorageTest");
    private static final String MISSING_FILE = "NonExistentFile.json";
    private static final String DUPLICATE_EMAIL_FILE = "duplicateEmailAddressBook.json";
    private static final String NOT_JSON_FORMAT_FILE = "notJsonFormatAddressBook.json";
    private static final String INVALID_PERSON_FILE = "invalidPersonAddressBook.json";
    private static final String INVALID_AND_VALID_PERSON_FILE = "invalidAndValidPersonAddressBook.json";
    private static final String SAMPLE_FILE_NAME = "SomeFile.json";
    private static final String DUPLICATE_EMAIL = "alice@example.com";
    private static final String DUPLICATE_CONTACT_EMAIL_PREFIX = "Duplicate contact email '";
    private static final String QUOTE_SUFFIX = "'";
    private static final String DUPLICATE_CONTACT_EMAIL_MESSAGE_SUFFIX = "'.";
    private static final String LINE_REFERENCE_KEYWORD = "lines";
    private static final String REFLECTION_BUILD_DUPLICATE_EMAIL_MESSAGE_METHOD = "buildDuplicateEmailErrorMessage";

    @TempDir
    public Path testFolder;

    @Test
    public void readAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readAddressBook(null));
    }

    private java.util.Optional<ReadOnlyAddressBook> readAddressBook(String filePath) throws Exception {
        return new JsonAddressBookStorage(Paths.get(filePath)).readAddressBook(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        if (prefsFileInTestDataFolder == null) {
            return null;
        }

        return TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder);
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAddressBook(MISSING_FILE).isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAddressBook(NOT_JSON_FORMAT_FILE));
    }

    @Test
    public void readAddressBook_invalidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook(INVALID_PERSON_FILE));
    }

    @Test
    public void readAddressBook_invalidAndValidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook(INVALID_AND_VALID_PERSON_FILE));
    }

    @Test
    public void readAddressBook_duplicateEmailAddressBook_throwDataLoadingExceptionWithLineNumbers() {
        DataLoadingException exception = org.junit.jupiter.api.Assertions.assertThrows(
                DataLoadingException.class, () -> readAddressBook(DUPLICATE_EMAIL_FILE));

        String message = exception.getCause().getMessage();
        assertTrue(message.contains(DUPLICATE_CONTACT_EMAIL_PREFIX + DUPLICATE_EMAIL + QUOTE_SUFFIX));
        assertTrue(message.contains(LINE_REFERENCE_KEYWORD));
    }

    @Test
    public void buildDuplicateEmailErrorMessage_missingFile_returnsEmailOnlyMessage() throws Exception {
        Path resolvedDupeEmailFilePath = TEST_DATA_FOLDER.resolve(DUPLICATE_EMAIL_FILE);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(resolvedDupeEmailFilePath);

        Path resolvedMissingFilePath = TEST_DATA_FOLDER.resolve(MISSING_FILE);
        String message = invokeBuildDuplicateEmailErrorMessage(
            storage,
            resolvedMissingFilePath,
            List.of(DUPLICATE_EMAIL));

        assertEquals(DUPLICATE_CONTACT_EMAIL_PREFIX + DUPLICATE_EMAIL + DUPLICATE_CONTACT_EMAIL_MESSAGE_SUFFIX,
            message);
    }

    @Test
    public void buildDuplicateEmailErrorMessage_noDuplicateEmails_returnsDefaultDuplicatePersonMessage()
            throws Exception {
        Path resolvedDupeEmailFilePath = TEST_DATA_FOLDER.resolve(DUPLICATE_EMAIL_FILE);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(resolvedDupeEmailFilePath);

        String message = invokeBuildDuplicateEmailErrorMessage(
                storage,
                resolvedDupeEmailFilePath,
                List.of());

        assertEquals(MESSAGE_DUPLICATE_PERSON, message);
    }

    @Test
    public void readAndSaveAddressBook_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempAddressBook.json");
        AddressBook original = getTypicalAddressBook();
        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);

        // Save in new file and read back
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Modify data, overwrite exiting file, and read back
        original.addPerson(HOON);
        original.removePerson(ALICE);
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Save and read without specifying file path
        original.addPerson(IDA);
        jsonAddressBookStorage.saveAddressBook(original); // file path not specified
        readBack = jsonAddressBookStorage.readAddressBook().get(); // file path not specified
        assertEquals(original, new AddressBook(readBack));

    }

    @Test
    public void saveAddressBook_nullAddressBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(null, SAMPLE_FILE_NAME));
    }

    /**
     * Saves {@code addressBook} at the specified {@code filePath}.
     */
    private void saveAddressBook(ReadOnlyAddressBook addressBook, String filePath) {
        try {
            new JsonAddressBookStorage(Paths.get(filePath))
                    .saveAddressBook(addressBook, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(new AddressBook(), null));
    }

    private String invokeBuildDuplicateEmailErrorMessage(JsonAddressBookStorage storage,
                                                         Path filePath,
                                                         List<String> duplicateEmails) throws Exception {
        java.lang.reflect.Method method = JsonAddressBookStorage.class
                .getDeclaredMethod(REFLECTION_BUILD_DUPLICATE_EMAIL_MESSAGE_METHOD, Path.class, List.class);
        method.setAccessible(true);
        return (String) method.invoke(storage, filePath, duplicateEmails);
    }
}
