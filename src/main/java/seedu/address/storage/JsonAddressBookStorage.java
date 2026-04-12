package seedu.address.storage;

import static java.util.Objects.requireNonNull;
import static seedu.address.storage.JsonLineReferenceUtil.findFieldLineNumbers;
import static seedu.address.storage.JsonLineReferenceUtil.formatLineReference;
import static seedu.address.storage.JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyAddressBook;

/**
 * A class to access AddressBook data stored as a json file on the hard disk.
 */
public class JsonAddressBookStorage implements AddressBookStorage {

    private static final String EMAIL_FIELD = "email";
    private static final String MESSAGE_DUPLICATE_CONTACT_EMAIL = "Duplicate contact email '";
    private static final String MESSAGE_QUOTE_SUFFIX = "'";
    private static final String MESSAGE_QUOTE_AT = "' at ";
    private static final String PERIOD = ".";

    private final Path filePath;

    public JsonAddressBookStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getAddressBookFilePath() {
        return filePath;
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
        return readAddressBook(filePath);
    }

    /**
     * Similar to {@link #readAddressBook()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableAddressBook> jsonAddressBook = JsonUtil.readJsonFile(
                filePath, JsonSerializableAddressBook.class);
        if (!jsonAddressBook.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonAddressBook.get().toModelType());
        } catch (IllegalValueException ive) {
            if (MESSAGE_DUPLICATE_PERSON.equals(ive.getMessage())) {
                String detailedMessage = jsonAddressBook.get().findFirstDuplicateEmail()
                        .map(email -> buildDuplicateEmailErrorMessage(filePath, email))
                        .orElse(MESSAGE_DUPLICATE_PERSON);

                throw new DataLoadingException(new IllegalValueException(detailedMessage, ive));
            }

            throw new DataLoadingException(ive);
        }
    }

    private String buildDuplicateEmailErrorMessage(Path filePath, String duplicateEmail) {
        List<Integer> lineNumbers = findFieldLineNumbers(filePath, EMAIL_FIELD, duplicateEmail);

        if (lineNumbers.isEmpty()) {
            return MESSAGE_DUPLICATE_CONTACT_EMAIL + duplicateEmail + MESSAGE_QUOTE_SUFFIX + PERIOD;
        }

        return MESSAGE_DUPLICATE_CONTACT_EMAIL + duplicateEmail + MESSAGE_QUOTE_AT
                + formatLineReference(lineNumbers) + PERIOD;
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, filePath);
    }

    /**
     * Similar to {@link #saveAddressBook(ReadOnlyAddressBook)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
        requireNonNull(addressBook);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableAddressBook(addressBook), filePath);
    }

}
