package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");
    private static final String DUPLICATE_EMAIL_IN_TYPICAL_DATA = ALICE.getEmail().value;
    private static final String PERSON_ONE_NAME = "One";
    private static final String PERSON_TWO_NAME = "Two";
    private static final String PERSON_THREE_NAME = "Three";
    private static final String PERSON_ONE_PHONE = "91234567";
    private static final String PERSON_TWO_PHONE = "91234568";
    private static final String PERSON_THREE_PHONE = "91234569";
    private static final String PERSON_ONE_ADDRESS = "Address One";
    private static final String PERSON_TWO_ADDRESS = "Address Two";
    private static final String PERSON_THREE_ADDRESS = "Address Three";
    private static final String UNIQUE_EMAIL_IN_UNIQUE_EMAILS_TEST = "one@example.com";
    private static final String DUPLICATE_EMAIL_IN_NULL_EMAIL_TEST = "two@example.com";

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

    @Test
    public void findDuplicateEmails_duplicatePersonsFile_returnsDuplicateEmail() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();

        assertEquals(List.of(DUPLICATE_EMAIL_IN_TYPICAL_DATA), dataFromFile.findDuplicateEmails());
    }

    @Test
    public void findDuplicateEmails_nullEmail_ignoresNullEmail() {
        List<JsonAdaptedPerson> personList = List.of(
                new JsonAdaptedPerson(PERSON_ONE_NAME, PERSON_ONE_PHONE, null, PERSON_ONE_ADDRESS, List.of(), false),
                new JsonAdaptedPerson(PERSON_TWO_NAME, PERSON_TWO_PHONE, DUPLICATE_EMAIL_IN_NULL_EMAIL_TEST,
                        PERSON_TWO_ADDRESS, List.of(), false),
                new JsonAdaptedPerson(PERSON_THREE_NAME, PERSON_THREE_PHONE, DUPLICATE_EMAIL_IN_NULL_EMAIL_TEST,
                        PERSON_THREE_ADDRESS, List.of(), false));

        JsonSerializableAddressBook data = new JsonSerializableAddressBook(personList);

        assertEquals(List.of(DUPLICATE_EMAIL_IN_NULL_EMAIL_TEST), data.findDuplicateEmails());
    }

    @Test
    public void findDuplicateEmails_uniqueEmails_returnsEmptyList() {
        List<JsonAdaptedPerson> personList = List.of(
                new JsonAdaptedPerson(PERSON_ONE_NAME, PERSON_ONE_PHONE, UNIQUE_EMAIL_IN_UNIQUE_EMAILS_TEST,
                        PERSON_ONE_ADDRESS, List.of(), false),
                new JsonAdaptedPerson(PERSON_TWO_NAME, PERSON_TWO_PHONE, DUPLICATE_EMAIL_IN_NULL_EMAIL_TEST,
                        PERSON_TWO_ADDRESS, List.of(), false),
                new JsonAdaptedPerson(PERSON_THREE_NAME, PERSON_THREE_PHONE, null, PERSON_THREE_ADDRESS,
                        List.of(), false));

        JsonSerializableAddressBook data = new JsonSerializableAddressBook(personList);

        assertEquals(List.of(), data.findDuplicateEmails());
    }

    @Test
    public void findFirstDuplicateEmail_multipleDuplicateEmails_returnsFirstDuplicateEncountered() {
        List<JsonAdaptedPerson> personList = List.of(
                new JsonAdaptedPerson(PERSON_ONE_NAME, PERSON_ONE_PHONE,
                        UNIQUE_EMAIL_IN_UNIQUE_EMAILS_TEST, PERSON_ONE_ADDRESS, List.of(), false),
                new JsonAdaptedPerson(PERSON_TWO_NAME, PERSON_TWO_PHONE,
                        DUPLICATE_EMAIL_IN_NULL_EMAIL_TEST, PERSON_TWO_ADDRESS, List.of(), false),
                new JsonAdaptedPerson(PERSON_THREE_NAME, PERSON_THREE_PHONE,
                        UNIQUE_EMAIL_IN_UNIQUE_EMAILS_TEST, PERSON_THREE_ADDRESS, List.of(), false),
                new JsonAdaptedPerson("Four", "91234570", DUPLICATE_EMAIL_IN_NULL_EMAIL_TEST,
                        "Address Four", List.of(), false)
        );

        JsonSerializableAddressBook data = new JsonSerializableAddressBook(personList);

        assertEquals(Optional.of(UNIQUE_EMAIL_IN_UNIQUE_EMAILS_TEST), data.findFirstDuplicateEmail());
    }

    @Test
    public void findFirstDuplicateEmail_noDuplicateEmails_returnsEmptyOptional() {
        List<JsonAdaptedPerson> personList = List.of(
                new JsonAdaptedPerson(PERSON_ONE_NAME, PERSON_ONE_PHONE,
                        UNIQUE_EMAIL_IN_UNIQUE_EMAILS_TEST, PERSON_ONE_ADDRESS, List.of(), false),
                new JsonAdaptedPerson(PERSON_TWO_NAME, PERSON_TWO_PHONE,
                        DUPLICATE_EMAIL_IN_NULL_EMAIL_TEST, PERSON_TWO_ADDRESS, List.of(), false),
                new JsonAdaptedPerson(PERSON_THREE_NAME, PERSON_THREE_PHONE,
                        null, PERSON_THREE_ADDRESS, List.of(), false)
        );

        JsonSerializableAddressBook data = new JsonSerializableAddressBook(personList);

        assertEquals(Optional.empty(), data.findFirstDuplicateEmail());
    }

}
