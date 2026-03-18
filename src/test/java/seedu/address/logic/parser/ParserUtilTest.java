package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.product.Identifier;
import seedu.address.model.product.Quantity;
import seedu.address.model.product.RestockThreshold;
import seedu.address.model.tag.Tag;

public class ParserUtilTest {
    private static final String INVALID_NAME = " ";
    private static final String INVALID_PHONE = " ";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_BLANK_EMAIL = " ";
    private static final String INVALID_TAG = "#friend";

    private static final String INVALID_SHORT_PHONE = "12 ";
    private static final String INVALID_LONG_NAME = "This name is way too long and exceeds the "
            + "maximum length allowed for a name in the vendor vault application. "
            + "This name is way too long and exceeds the maximum length allowed for"
            + "a name in the vendor vault application. This name is way too long "
            + "and exceeds the maximum length allowed for a name in the vendor vault application.";
    private static final String INVALID_LONG_ADDRESS = "This address is way too long and exceeds the"
            + "maximum length allowed for an address in the vendor vault application. This "
            + "address is way too long and exceeds the maximum length allowed for an "
            + "address in the vendor vault application. This address is way too long "
            + "and exceeds the maximum length allowed for an address in the vendor vault "
            + "application. This address is way too long and exceeds the maximum length "
            + "allowed for an address in the vendor vault application. This address is"
            + "way too long and exceeds the maximum length allowed for an address in the vendor vault application. ";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";
    private static final String VALID_ADDRESS = "123 Main Street #0505";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";

    private static final String VALID_IDENTIFIER = "SKU-1001";
    private static final String VALID_PRODUCT_NAME = "Brown Rice 5kg";
    private static final String VALID_QUANTITY = "24";
    private static final String VALID_THRESHOLD = "0";
    private static final String VALID_IDENTIFIER_WARN = "SKU 1001";
    private static final String VALID_PRODUCT_NAME_WARN = "apple juice 5% sugar";

    private static final String INVALID_IDENTIFIER = " ";
    private static final String INVALID_PRODUCT_NAME = " ";
    private static final String INVALID_QUANTITY = "-1";
    private static final String INVALID_THRESHOLD = "-1";

    private static final String INVALID_LONG_IDENTIFIER = "a".repeat(Identifier.MAX_LENGTH + 1);
    private static final String INVALID_LONG_PRODUCT_NAME = "a".repeat(
            seedu.address.model.product.Name.MAX_LENGTH + 1);

    private static final String WHITESPACE = " \t\r\n";

    private static final int INDEX_FIRST_PERSON_INT = 1;

    @Test
    public void parseIndexToInteger_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndexToInteger("10 a"));
    }

    @Test
    public void parseIndexToInteger_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON_INT, ParserUtil.parseIndexToInteger("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON_INT, ParserUtil.parseIndexToInteger("  1  "));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME).getValue());
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace).getValue());
    }

    @Test
    public void parseName_invalidLongName_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_LONG_NAME));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE).getValue());
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace).getValue());
    }

    @Test
    public void parsePhone_invalidShortPhone_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_SHORT_PHONE));
    }

    @Test
    public void parsePhone_multiplePhonesWithTrailingEmpty_returnsPhoneWithWarning() throws Exception {
        String multiplePhones = "61234567,";
        ParseResult<Phone> result = ParserUtil.parsePhone(multiplePhones);
        assertEquals(new Phone(multiplePhones), result.getValue());
        assertFalse(result.getWarning().isEmpty());
    }

    @Test
    public void parsePhone_multiplePhonesWithMiddleEmpty_returnsPhoneWithWarning() throws Exception {
        String multiplePhones = "12345678,,12345679";
        ParseResult<Phone> result = ParserUtil.parsePhone(multiplePhones);
        assertEquals(new Phone(multiplePhones), result.getValue());
        assertFalse(result.getWarning().isEmpty());
    }

    @Test
    public void parsePhone_oneTooShort_throwsParseException() {
        // test for multiple phones, one short
        String multiplePhones = "61234567, 12";
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(multiplePhones));
    }

    @Test
    public void parseAddress_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseAddress((String) null));
    }

    @Test
    public void parseAddress_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseAddress(INVALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithoutWhitespace_returnsAddress() throws Exception {
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(VALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithWhitespace_returnsTrimmedAddress() throws Exception {
        String addressWithWhitespace = WHITESPACE + VALID_ADDRESS + WHITESPACE;
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(addressWithWhitespace));
    }

    @Test
    public void parseAddress_blankAddress_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseAddress("   "));
    }

    @Test
    public void parseAddress_emptyString_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseAddress(""));
    }

    @Test
    public void parseAddress_invalidLongAddress_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseAddress(INVALID_LONG_ADDRESS));
    }

    @Test
    public void parseEmail_blankEmail_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_BLANK_EMAIL));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL).getValue());
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace).getValue());
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(Arrays.asList(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(Collections.emptyList()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(Arrays.asList(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = new HashSet<Tag>(Arrays.asList(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2)));

        assertEquals(expectedTagSet, actualTagSet);
    }

    @Test
    public void parseIdentifier_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseIdentifier((String) null));
    }

    @Test
    public void parseIdentifier_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIdentifier(INVALID_IDENTIFIER));
    }

    @Test
    public void parseIdentifier_validValueWithoutWhitespace_returnsIdentifier() throws Exception {
        Identifier expectedIdentifier = new Identifier(VALID_IDENTIFIER);
        assertEquals(expectedIdentifier, ParserUtil.parseIdentifier(VALID_IDENTIFIER).getValue());
    }

    @Test
    public void parseIdentifier_validValueWithWhitespace_returnsTrimmedIdentifier() throws Exception {
        String identifierWithWhitespace = WHITESPACE + VALID_IDENTIFIER + WHITESPACE;
        Identifier expectedIdentifier = new Identifier(VALID_IDENTIFIER);
        assertEquals(expectedIdentifier, ParserUtil.parseIdentifier(identifierWithWhitespace).getValue());
    }

    @Test
    public void parseIdentifier_warnValue_returnsWarning() throws Exception {
        assertEquals(Identifier.MESSAGE_WARN,
                ParserUtil.parseIdentifier(VALID_IDENTIFIER_WARN).getWarning().orElse(""));
    }

    @Test
    public void parseIdentifier_invalidLongIdentifier_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIdentifier(INVALID_LONG_IDENTIFIER));
    }

    @Test
    public void parseProductName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseProductName((String) null));
    }

    @Test
    public void parseProductName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseProductName(INVALID_PRODUCT_NAME));
    }

    @Test
    public void parseProductName_validValueWithoutWhitespace_returnsName() throws Exception {
        seedu.address.model.product.Name expectedName = new seedu.address.model.product.Name(VALID_PRODUCT_NAME);
        assertEquals(expectedName, ParserUtil.parseProductName(VALID_PRODUCT_NAME).getValue());
    }

    @Test
    public void parseProductName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String productNameWithWhitespace = WHITESPACE + VALID_PRODUCT_NAME + WHITESPACE;
        seedu.address.model.product.Name expectedName = new seedu.address.model.product.Name(VALID_PRODUCT_NAME);
        assertEquals(expectedName, ParserUtil.parseProductName(productNameWithWhitespace).getValue());
    }

    @Test
    public void parseProductName_warnValue_returnsWarning() throws Exception {
        assertEquals(seedu.address.model.product.Name.MESSAGE_WARN,
                ParserUtil.parseProductName(VALID_PRODUCT_NAME_WARN).getWarning().orElse(""));
    }

    @Test
    public void parseProductName_invalidLongProductName_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseProductName(INVALID_LONG_PRODUCT_NAME));
    }

    @Test
    public void parseQuantity_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseQuantity((String) null));
    }

    @Test
    public void parseQuantity_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseQuantity(INVALID_QUANTITY));
    }

    @Test
    public void parseQuantity_validValueWithoutWhitespace_returnsQuantity() throws Exception {
        Quantity expectedQuantity = new Quantity(VALID_QUANTITY);
        assertEquals(expectedQuantity, ParserUtil.parseQuantity(VALID_QUANTITY).getValue());
    }

    @Test
    public void parseQuantity_validValueWithWhitespace_returnsTrimmedQuantity() throws Exception {
        String quantityWithWhitespace = WHITESPACE + VALID_QUANTITY + WHITESPACE;
        Quantity expectedQuantity = new Quantity(VALID_QUANTITY);
        assertEquals(expectedQuantity, ParserUtil.parseQuantity(quantityWithWhitespace).getValue());
    }

    @Test
    public void parseQuantity_validValue_returnsNoWarnings() throws Exception {
        assertTrue(ParserUtil.parseQuantity(VALID_QUANTITY).getWarning().isEmpty());
    }

    @Test
    public void parseThreshold_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseThreshold((String) null));
    }

    @Test
    public void parseThreshold_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseThreshold(INVALID_THRESHOLD));
    }

    @Test
    public void parseThreshold_validValueWithoutWhitespace_returnsQuantity() throws Exception {
        RestockThreshold expectedThreshold = new RestockThreshold(VALID_THRESHOLD);
        assertEquals(expectedThreshold, ParserUtil.parseThreshold(VALID_THRESHOLD).getValue());
    }

    @Test
    public void parseThreshold_validValueWithWhitespace_returnsTrimmedThreshold() throws Exception {
        String thresholdWithWhitespace = WHITESPACE + VALID_THRESHOLD + WHITESPACE;
        RestockThreshold expectedThreshold = new RestockThreshold(VALID_THRESHOLD);
        assertEquals(expectedThreshold, ParserUtil.parseThreshold(thresholdWithWhitespace).getValue());
    }

    @Test
    public void parseThreshold_validValue_returnsNoWarnings() throws Exception {
        assertTrue(ParserUtil.parseThreshold(VALID_THRESHOLD).getWarning().isEmpty());
    }
}
