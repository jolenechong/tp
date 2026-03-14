package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.product.Identifier;
import seedu.address.model.product.Quantity;
import seedu.address.model.product.RestockThreshold;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_IDENTIFIER = "identifier";
    public static final String FIELD_PRODUCT_NAME = "product name";
    public static final String COMMA_SEPARATOR = ", ";
    public static final String SPACE_SEPARATOR = " ";
    public static final String NEWLINE = "\n";
    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";
    public static final String MESSAGE_NOT_A_NUMBER = "Index is not a number.";

    /**
     * Parses a string index into an integer
     *
     */
    public static int parseIndexToInteger(String index) throws ParseException {
        String trimmedIndex = index.trim();
        if (!StringUtil.isValidInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_NOT_A_NUMBER);
        }

        return Integer.parseInt(trimmedIndex);
    }

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     *
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    private static Optional<String> getNameWarning(String name) throws ParseException {
        if (name.isBlank()) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }

        if (name.length() > Name.MAX_LENGTH) {
            throw new ParseException(Name.MESSAGE_LENGTH_CONSTRAINTS);
        }

        if (!Name.isValidNameWarn(name)) {
            return Optional.of(Name.MESSAGE_WARN);
        }

        return Optional.empty();
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static ParseResult<Name> parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        Optional<String> warnings = getNameWarning(trimmedName);
        return new ParseResult<>(new Name(trimmedName), warnings);
    }

    private static Optional<String> getPhoneWarning(String phone) throws ParseException {
        if (phone.isBlank() || !Phone.isValidPhone(phone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }

        if (!Phone.isValidPhoneWarn(phone)) {
            return Optional.of(Phone.MESSAGE_WARN);
        }

        return Optional.empty();
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static ParseResult<Phone> parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        Optional<String> warnings = getPhoneWarning(trimmedPhone);
        return new ParseResult<>(new Phone(trimmedPhone), warnings);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (trimmedAddress.length() > Address.MAX_LENGTH) {
            throw new ParseException(Address.MESSAGE_LENGTH_CONSTRAINTS);
        }

        if (trimmedAddress.isBlank()) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }

        return new Address(trimmedAddress);
    }

    private static Optional<String> getEmailWarning(String email) throws ParseException {
        if (email.isBlank()) {
            throw new ParseException(Email.MESSAGE_BLANK);
        }

        if (!Email.isValidEmail(email)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }

        if (!Email.isValidEmailWarn(email)) {
            return Optional.of(Email.MESSAGE_WARN);
        }

        return Optional.empty();
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static ParseResult<Email> parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        Optional<String> warnings = getEmailWarning(trimmedEmail);
        return new ParseResult<>(new Email(trimmedEmail), warnings);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    private static Optional<String> getIdentifierWarning(String identifier) throws ParseException {
        if (identifier.isBlank()) {
            throw new ParseException(Identifier.MESSAGE_CONSTRAINTS);
        }

        if (identifier.length() > Identifier.MAX_LENGTH) {
            throw new ParseException(Identifier.MESSAGE_LENGTH_CONSTRAINTS);
        }

        if (!Identifier.isValidIdentifierWarn(identifier)) {
            return Optional.of(Identifier.MESSAGE_WARN);
        }

        return Optional.empty();
    }

    /**
     * Parses a product {@code String identifier} into {@code Identifier}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code identifier} is invalid.
     */
    public static ParseResult<Identifier> parseIdentifier(String identifier) throws ParseException {
        requireNonNull(identifier);
        String trimmedIdentifier = identifier.trim();
        Optional<String> warning = getIdentifierWarning(trimmedIdentifier);
        return new ParseResult<>(new Identifier(trimmedIdentifier), warning);
    }

    private static Optional<String> getProductNameWarning(String name) throws ParseException {
        if (name.isBlank()) {
            throw new ParseException(seedu.address.model.product.Name.MESSAGE_CONSTRAINTS);
        }

        if (name.length() > seedu.address.model.product.Name.MAX_LENGTH) {
            throw new ParseException(seedu.address.model.product.Name.MESSAGE_LENGTH_CONSTRAINTS);
        }

        if (!seedu.address.model.product.Name.isValidNameWarn(name)) {
            return Optional.of(seedu.address.model.product.Name.MESSAGE_WARN);
        }

        return Optional.empty();
    }

    /**
     * Parses a product {@code String name} into a {@code seedu.address.model.product.Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static ParseResult<seedu.address.model.product.Name> parseProductName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        Optional<String> warning = getProductNameWarning(trimmedName);
        return new ParseResult<>(new seedu.address.model.product.Name(trimmedName), warning);
    }

    /**
     * Parses a product {@code String quantity} into {@code Quantity}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code quantity} is invalid.
     */
    public static ParseResult<Quantity> parseQuantity(String quantity) throws ParseException {
        requireNonNull(quantity);
        String trimmedQuantity = quantity.trim();

        if (!Quantity.isValidQuantity(trimmedQuantity)) {
            throw new ParseException(Quantity.MESSAGE_CONSTRAINTS);
        }

        return new ParseResult<>(new Quantity(trimmedQuantity), Optional.empty());
    }

    /**
     * Parses a product {@code String threshold} into {@code RestockThreshold}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code threshold} is invalid.
     */
    public static ParseResult<RestockThreshold> parseThreshold(String threshold) throws ParseException {
        requireNonNull(threshold);
        String trimmedThreshold = threshold.trim();

        if (!RestockThreshold.isValidRestockThreshold(trimmedThreshold)) {
            throw new ParseException(RestockThreshold.MESSAGE_CONSTRAINTS);
        }

        return new ParseResult<>(new RestockThreshold(trimmedThreshold), Optional.empty());
    }
}
