package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

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

    public static final String SEPARATOR_COMMA = ", ";
    public static final String SEPARATOR_SPACE = "\\s+";
    public static final String SEPARATOR_NEW_LINE = "\n";

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

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static ParseResult<Name> parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        Optional<String> warnings = validateName(trimmedName);
        return new ParseResult<>(new Name(trimmedName), warnings);
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
        Optional<String> warnings = validatePhone(trimmedPhone);
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

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static ParseResult<Email> parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        Optional<String> warnings = validateEmail(trimmedEmail);
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
        if (trimmedTag.length() > Tag.MAX_LENGTH) {
            throw new ParseException(Tag.MESSAGE_LENGTH_CONSTRAINTS);
        }
        if (trimmedTag.isBlank()) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
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

    /**
     * Parses a product {@code String identifier} into {@code Identifier}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code identifier} is invalid.
     */
    public static ParseResult<Identifier> parseIdentifier(String identifier) throws ParseException {
        requireNonNull(identifier);
        String trimmedIdentifier = identifier.trim();
        Optional<String> warnings = validateIdentifier(trimmedIdentifier);
        return new ParseResult<>(new Identifier(trimmedIdentifier), warnings);
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
        Optional<String> warnings = validateProductName(trimmedName);
        return new ParseResult<>(new seedu.address.model.product.Name(trimmedName), warnings);
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

    // ============ Helper Functions for Warnings ============

    private static Optional<String> validateName(String name) throws ParseException {
        return validate(
            name, () -> new ParseException(Name.MESSAGE_BLANK),
            Name.MAX_LENGTH, ()
                        -> new ParseException(Name.MESSAGE_LENGTH_CONSTRAINTS),
            Name::isValidName, () -> new ParseException(Name.MESSAGE_CONSTRAINTS),
            Name::isValidNameWarn, Name.MESSAGE_WARN
        );

    }

    private static Optional<String> validatePhone(String phone) throws ParseException {
        return validate(
                phone, () -> new ParseException(Phone.MESSAGE_BLANK),
                0, null,
                Phone::isValidPhone, () -> new ParseException(Phone.MESSAGE_CONSTRAINTS),
                Phone::isValidPhoneWarn, Phone.MESSAGE_WARN
        );
    }

    private static Optional<String> validateEmail(String email) throws ParseException {
        return validate(
                email, () -> new ParseException(Email.MESSAGE_BLANK),
                Email.MAX_LENGTH, () -> new ParseException(Email.MESSAGE_LENGTH_CONSTRAINTS),
                Email::isValidEmail, () -> new ParseException(Email.MESSAGE_CONSTRAINTS),
                Email::isValidEmailWarn, Email.MESSAGE_WARN
        );
    }

    private static Optional<String> validateIdentifier(String identifier) throws ParseException {
        return validate(
                identifier, () -> new ParseException(Identifier.MESSAGE_BLANK),
                Identifier.MAX_LENGTH, ()
                        -> new ParseException(Identifier.MESSAGE_LENGTH_CONSTRAINTS),
                Identifier::isValidIdentifier, () -> new ParseException(Identifier.MESSAGE_CONSTRAINTS),
                Identifier::isValidIdentifierWarn, Identifier.MESSAGE_WARN
        );
    }

    private static Optional<String> validateProductName(String name) throws ParseException {
        return validate(
                name, () -> new ParseException(seedu.address.model.product.Name.MESSAGE_BLANK),
                seedu.address.model.product.Name.MAX_LENGTH, ()
                        -> new ParseException(seedu.address.model.product.Name.MESSAGE_LENGTH_CONSTRAINTS),
                seedu.address.model.product.Name::isValidName, ()
                        -> new ParseException(seedu.address.model.product.Name.MESSAGE_CONSTRAINTS),
                seedu.address.model.product.Name::isValidNameWarn,
                seedu.address.model.product.Name.MESSAGE_WARN
        );
    }

    // ============ Generic Warning Helper ============
    @FunctionalInterface
    interface Validator {
        boolean validate(String input);
    }

    @FunctionalInterface
    interface ExceptionSupplier {
        ParseException get();
    }

    /**
     * Checks only blank, max length and warnings
     */
    private static Optional<String> validate(
            String value,
            ExceptionSupplier blankException,
            int maxLength,
            ExceptionSupplier lengthException,
            Validator errorValidator,
            ExceptionSupplier errorException,
            Validator warningValidator,
            String warningMessage
    ) throws ParseException {
        // error validation is optional
        requireAllNonNull(value, blankException, errorValidator, errorException, warningValidator, warningMessage);

        if (value.isBlank()) {
            throw blankException.get();
        }

        if (lengthException != null) {
            if (value.length() > maxLength) {
                throw lengthException.get();
            }
        }

        if (!errorValidator.validate(value)) {
            throw errorException.get();
        }

        if (!warningValidator.validate(value)) {
            return Optional.of(warningMessage);
        }

        return Optional.empty();
    }
}
