package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_IDENTIFIER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_QUANTITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_THRESHOLD;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.product.Product;
import seedu.address.testutil.EditPersonDescriptorBuilder;

/**
 * Contains helper methods for testing commands.
 */
public class CommandTestUtil {

    public static final String VALID_NAME_AMY = "Amy Bee";
    public static final String VALID_NAME_BOB = "Bob Choo";
    public static final String VALID_PHONE_AMY = "11111111";
    public static final String VALID_PHONE_BOB = "33333333";
    public static final String VALID_EMAIL_AMY = "amy@example.com";
    public static final String VALID_EMAIL_BOB = "bob@example.com";
    public static final String VALID_ADDRESS_AMY = "Block 312, Amy Street 1";
    public static final String VALID_ADDRESS_BOB = "Block 123, Bobby Street 3";
    public static final String VALID_TAG_HUSBAND = "husband";
    public static final String VALID_TAG_FRIEND = "friend";

    public static final String VALID_IDENTIFIER_IPAD = "TAB-1001";
    public static final String VALID_IDENTIFIER_AIRPODS = "AUD/3301";
    public static final String VALID_PRODUCT_NAME_IPAD = "iPad Air 11";
    public static final String VALID_PRODUCT_NAME_AIRPODS = "AirPods Pro 2";
    public static final String VALID_QUANTITY_IPAD = "0";
    public static final String VALID_QUANTITY_IPHONE = "30";
    public static final String VALID_QUANTITY_AIRPODS = "45";
    public static final String VALID_THRESHOLD_IPAD = "0";
    public static final String VALID_THRESHOLD_AIRPODS = "45";

    public static final String NAME_DESC_AMY = " " + PREFIX_NAME + VALID_NAME_AMY;
    public static final String NAME_DESC_BOB = " " + PREFIX_NAME + VALID_NAME_BOB;
    public static final String PHONE_DESC_AMY = " " + PREFIX_PHONE + VALID_PHONE_AMY;
    public static final String PHONE_DESC_BOB = " " + PREFIX_PHONE + VALID_PHONE_BOB;
    public static final String EMAIL_DESC_AMY = " " + PREFIX_EMAIL + VALID_EMAIL_AMY;
    public static final String EMAIL_DESC_BOB = " " + PREFIX_EMAIL + VALID_EMAIL_BOB;
    public static final String ADDRESS_DESC_AMY = " " + PREFIX_ADDRESS + VALID_ADDRESS_AMY;
    public static final String ADDRESS_DESC_BOB = " " + PREFIX_ADDRESS + VALID_ADDRESS_BOB;
    public static final String TAG_DESC_FRIEND = " " + PREFIX_TAG + VALID_TAG_FRIEND;
    public static final String TAG_DESC_HUSBAND = " " + PREFIX_TAG + VALID_TAG_HUSBAND;

    public static final String IDENTIFIER_DESC_IPAD = " " + PREFIX_IDENTIFIER + VALID_IDENTIFIER_IPAD;
    public static final String IDENTIFIER_DESC_AIRPODS = " " + PREFIX_IDENTIFIER + VALID_IDENTIFIER_AIRPODS;
    public static final String PRODUCT_NAME_DESC_IPAD = " " + PREFIX_NAME + VALID_PRODUCT_NAME_IPAD;
    public static final String PRODUCT_NAME_DESC_AIRPODS = " " + PREFIX_NAME + VALID_PRODUCT_NAME_AIRPODS;
    public static final String QUANTITY_DESC_IPAD = " " + PREFIX_QUANTITY + VALID_QUANTITY_IPAD;
    public static final String QUANTITY_DESC_AIRPODS = " " + PREFIX_QUANTITY + VALID_QUANTITY_AIRPODS;
    public static final String THRESHOLD_DESC_IPAD = " " + PREFIX_THRESHOLD + VALID_THRESHOLD_IPAD;
    public static final String THRESHOLD_DESC_AIRPODS = " " + PREFIX_THRESHOLD + VALID_THRESHOLD_AIRPODS;

    public static final String INVALID_NAME_DESC = " " + PREFIX_NAME; // empty string not allowed in names
    public static final String INVALID_PHONE_DESC = " " + PREFIX_PHONE + "12"; // phone less than 3 characters
    public static final String INVALID_EMAIL_DESC = " " + PREFIX_EMAIL + "bob!yahoo"; // missing '@' symbol
    public static final String INVALID_ADDRESS_DESC = " " + PREFIX_ADDRESS; // empty string not allowed for addresses
    public static final String INVALID_TAG_DESC = " " + PREFIX_TAG; // empty tags nt allowed during add
    public static final String INVALID_TAG_DESC_LONG = " " + PREFIX_TAG
            + "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxs"; // tags over 50 characters not allowed

    public static final String INVALID_IDENTIFIER_DESC = " " + PREFIX_IDENTIFIER; // empty identifier not allowed
    public static final String INVALID_PRODUCT_NAME_DESC = " " + PREFIX_NAME; // empty product name not allowed
    public static final String INVALID_QUANTITY_DESC = " " + PREFIX_QUANTITY; // empty quantity not allowed
    public static final String INVALID_THRESHOLD_DESC = " " + PREFIX_THRESHOLD; // empty threshold not allowed

    public static final String INVALID_NAME_WARN = "James-Doe";
    public static final String INVALID_PHONE_WARN = "1234 5678 (HP) 1111-3333 (Office)";
    public static final String INVALID_EMAIL_WARN = "johndoe1234567890abcdefghijklmnopqrstuvwxyz."
            + "johndoe1234567890abcdefghijklmnopqrstuvwxyz.johndoe1234567890abcdefghijklmnopqrstuvwxyz."
            + "johndoe1234567890abcdefghijklmnopqrstuvwxyz.johndoe1234567890abcdefghijklmnopqrstuvwxyz"
            + "@valid-domain-for-testing-1234567890abcdefghijklmnopqrstuvwxyz.com";
    public static final String INVALID_NAME_DESC_WARN = " " + PREFIX_NAME + INVALID_NAME_WARN; // with warning
    public static final String INVALID_PHONE_DESC_WARN = " " + PREFIX_PHONE + INVALID_PHONE_WARN; // with warning
    public static final String INVALID_EMAIL_DESC_WARN = " " + PREFIX_EMAIL + INVALID_EMAIL_WARN; // missing '@' symbol

    public static final String INVALID_IDENTIFIER_WARN = "A B";
    public static final String INVALID_PRODUCT_NAME_WARN = "apple juice 5% sugar";
    public static final String INVALID_IDENTIFIER_DESC_WARN = " " + PREFIX_IDENTIFIER + INVALID_IDENTIFIER_WARN;
    public static final String INVALID_PRODUCT_NAME_DESC_WARN = " " + PREFIX_NAME + INVALID_PRODUCT_NAME_WARN;

    public static final String PREAMBLE_WHITESPACE = "\t  \r  \n";
    public static final String PREAMBLE_NON_EMPTY = "NonEmptyPreamble";

    public static final EditCommand.EditPersonDescriptor DESC_AMY;
    public static final EditCommand.EditPersonDescriptor DESC_BOB;

    static {
        DESC_AMY = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_AMY).withEmail(VALID_EMAIL_AMY).withAddress(VALID_ADDRESS_AMY)
                .withTags(VALID_TAG_FRIEND).build();
        DESC_BOB = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB).withAddress(VALID_ADDRESS_BOB)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - the returned {@link CommandResult} matches {@code expectedCommandResult} <br>
     * - the {@code actualModel} matches {@code expectedModel}
     */
    public static void assertCommandSuccess(Command command, Model actualModel, CommandResult expectedCommandResult,
            Model expectedModel) {
        try {
            CommandResult result = command.execute(actualModel);
            assertEquals(expectedCommandResult, result);
            assertEquals(expectedModel, actualModel);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Convenience wrapper to {@link #assertCommandSuccess(Command, Model, CommandResult, Model)}
     * that takes a string {@code expectedMessage}.
     */
    public static void assertCommandSuccess(Command command, Model actualModel, String expectedMessage,
            Model expectedModel) {
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        assertCommandSuccess(command, actualModel, expectedCommandResult, expectedModel);
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - a {@code CommandException} is thrown <br>
     * - the CommandException message matches {@code expectedMessage} <br>
     * - the address book, filtered person list and selected person in {@code actualModel} remain unchanged
     */
    public static void assertCommandFailure(Command command, Model actualModel, String expectedMessage) {
        // we are unable to defensively copy the model for comparison later, so we can
        // only do so by copying its components.
        AddressBook expectedAddressBook = new AddressBook(actualModel.getAddressBook());
        List<Person> expectedFilteredList = new ArrayList<>(actualModel.getFilteredPersonList());
        List<Product> expectedFilteredProductList = new ArrayList<>(actualModel.getFilteredProductList());

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(actualModel));
        assertEquals(expectedAddressBook, actualModel.getAddressBook());
        assertEquals(expectedFilteredList, actualModel.getFilteredPersonList());
        assertEquals(expectedFilteredProductList, actualModel.getFilteredProductList());
    }

    /**
     * Updates {@code model}'s filtered list to show only the person at the given {@code targetIndex} in the
     * {@code model}'s address book.
     */
    public static void showPersonAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredPersonList().size());

        Person person = model.getFilteredPersonList().get(targetIndex.getZeroBased());
        final String[] splitName = person.getName().fullName.split("\\s+");
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(Arrays.asList(splitName[0])));

        assertEquals(1, model.getFilteredPersonList().size());
    }

}
