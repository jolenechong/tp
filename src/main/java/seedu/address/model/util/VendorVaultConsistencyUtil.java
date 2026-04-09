package seedu.address.model.util;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyInventory;
import seedu.address.model.person.Email;
import seedu.address.model.product.Identifier;
import seedu.address.model.product.Product;

/**
 * Encapsulates validation of data consistency between contacts and inventory.
 */
public final class VendorVaultConsistencyUtil {

    public static final String MESSAGE_DUPLICATE_PRODUCT_IDENTIFIER =
            "Products list contains duplicate product id(s).";
    public static final String MESSAGE_UNKNOWN_VENDOR_LINK =
            "Product with identifier '%s' references unknown vendor email '%s'.";
    public static final String MESSAGE_DUPLICATE_PRODUCT_IDENTIFIER_WITH_LINES =
            "Duplicate product identifier '%s' at %s.";
    public static final String MESSAGE_UNKNOWN_VENDOR_LINK_WITH_LINES =
            "Unknown vendor email '%s' at %s.";
    private static final String IDENTIFIER_FIELD = "identifier";
    private static final String VENDOR_EMAIL_FIELD = "vendorEmail";
    private static final String CAPTURED_FIELD_PATTERN_TEMPLATE = "\"%s\"\\s*:\\s*\"([^\"]+)\"";
    private static final String EXACT_FIELD_PATTERN_TEMPLATE = "\"%s\"\\s*:\\s*\"%s\"";

    private VendorVaultConsistencyUtil() {}

    /**
     * Validates that product identifiers are unique and vendor links resolve to existing contacts.
     *
     * @throws IllegalValueException if a duplicate identifier or unresolved vendor link is found.
     */
    public static void validateOrThrow(ReadOnlyAddressBook addressBook, ReadOnlyInventory inventory)
            throws IllegalValueException {
        validateOrThrow(addressBook, inventory, null);
    }

    /**
     * Validates consistency and enriches messages with line numbers when the source file path is available.
     */
    public static void validateOrThrow(ReadOnlyAddressBook addressBook, ReadOnlyInventory inventory,
                                       Path inventoryFilePath)
            throws IllegalValueException {
        requireAllNonNull(addressBook, inventory);

        Set<Email> knownContactEmails = getKnownContactEmails(addressBook);

        Set<Identifier> seenIdentifiers = new HashSet<>();
        for (Product product : inventory.getProductList()) {
            validateIdentifierUniqueness(product, seenIdentifiers, inventoryFilePath);
            validateVendorLink(product, knownContactEmails, inventoryFilePath);
        }
    }

    /**
     * Returns vendor-link violations found directly from inventory JSON lines without requiring inventory
     * deserialization.
     */
    public static List<String> findUnknownVendorLinksFromJson(ReadOnlyAddressBook addressBook, Path inventoryFilePath) {
        requireAllNonNull(addressBook, inventoryFilePath);

        Set<String> knownContactEmails = getKnownContactEmailValues(addressBook);
        Pattern vendorEmailPattern = buildCapturedFieldPattern(VENDOR_EMAIL_FIELD);

        try {
            return collectUnknownVendorIssues(readAllLines(inventoryFilePath), vendorEmailPattern, knownContactEmails);
        } catch (IOException e) {
            return List.of();
        }
    }

    private static Set<Email> getKnownContactEmails(ReadOnlyAddressBook addressBook) {
        return addressBook.getPersonList().stream()
                .map(person -> person.getEmail())
                .collect(Collectors.toSet());
    }

    private static Set<String> getKnownContactEmailValues(ReadOnlyAddressBook addressBook) {
        return addressBook.getPersonList().stream()
                .map(person -> person.getEmail().value)
                .collect(Collectors.toSet());
    }

    private static void validateIdentifierUniqueness(Product product, Set<Identifier> seenIdentifiers,
                                                     Path inventoryFilePath) throws IllegalValueException {
        Identifier identifier = product.getIdentifier();
        if (seenIdentifiers.add(identifier)) {
            return;
        }

        if (inventoryFilePath == null) {
            throw new IllegalValueException(MESSAGE_DUPLICATE_PRODUCT_IDENTIFIER);
        }

        String lineNumbers = resolveLineNumbers(inventoryFilePath, IDENTIFIER_FIELD, identifier.value);
        if (lineNumbers.isEmpty()) {
            throw new IllegalValueException(MESSAGE_DUPLICATE_PRODUCT_IDENTIFIER);
        }

        throw new IllegalValueException(String.format(
                MESSAGE_DUPLICATE_PRODUCT_IDENTIFIER_WITH_LINES,
                identifier.value,
                toLineReference(lineNumbers)));
    }

    private static void validateVendorLink(Product product, Set<Email> knownContactEmails, Path inventoryFilePath)
            throws IllegalValueException {
        if (product.getVendorEmail().isEmpty()) {
            return;
        }

        String vendorEmail = product.getVendorEmail().get().value;
        if (knownContactEmails.contains(product.getVendorEmail().get())) {
            return;
        }

        if (inventoryFilePath == null) {
            throw new IllegalValueException(String.format(
                    MESSAGE_UNKNOWN_VENDOR_LINK,
                    product.getIdentifier().value,
                    vendorEmail));
        }

        String lineNumbers = resolveLineNumbers(inventoryFilePath, VENDOR_EMAIL_FIELD, vendorEmail);
        if (lineNumbers.isEmpty()) {
            throw new IllegalValueException(String.format(
                    MESSAGE_UNKNOWN_VENDOR_LINK,
                    product.getIdentifier().value,
                    vendorEmail));
        }

        throw new IllegalValueException(String.format(
                MESSAGE_UNKNOWN_VENDOR_LINK_WITH_LINES,
                vendorEmail,
                toLineReference(lineNumbers)));
    }

    private static String toLineReference(String lineNumbers) {
        long lineCount = Stream.of(lineNumbers.split(","))
                .map(String::trim)
                .filter(part -> !part.isEmpty())
                .count();

        return (lineCount <= 1 ? "line " : "lines ") + lineNumbers;
    }

    private static String resolveLineNumbers(Path filePath, String jsonField, String fieldValue) {
        Pattern pattern = buildExactFieldPattern(jsonField, fieldValue);

        try {
            Set<Integer> matchedLineNumbers = findMatchingLineNumbers(readAllLines(filePath), pattern);
            return joinLineNumbers(matchedLineNumbers);
        } catch (IOException e) {
            return "";
        }
    }

    private static Pattern buildCapturedFieldPattern(String jsonField) {
        return Pattern.compile(CAPTURED_FIELD_PATTERN_TEMPLATE.formatted(Pattern.quote(jsonField)));
    }

    private static Pattern buildExactFieldPattern(String jsonField, String fieldValue) {
        return Pattern.compile(EXACT_FIELD_PATTERN_TEMPLATE.formatted(
                Pattern.quote(jsonField), Pattern.quote(fieldValue)));
    }

    private static List<String> readAllLines(Path filePath) throws IOException {
        return Files.readAllLines(filePath, StandardCharsets.UTF_8);
    }

    private static List<String> collectUnknownVendorIssues(List<String> lines, Pattern vendorEmailPattern,
                                                           Set<String> knownContactEmails) {
        List<String> violations = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            collectUnknownVendorIssuesFromLine(violations, lines.get(i), i + 1, vendorEmailPattern, knownContactEmails);
        }
        return violations;
    }

    private static void collectUnknownVendorIssuesFromLine(List<String> violations, String line, int lineNumber,
                                                           Pattern vendorEmailPattern, Set<String> knownContactEmails) {
        Matcher matcher = vendorEmailPattern.matcher(line);
        while (matcher.find()) {
            String vendorEmail = matcher.group(1);
            if (!knownContactEmails.contains(vendorEmail.toLowerCase(Locale.ROOT))) {
                violations.add("'" + vendorEmail + "' at line " + lineNumber);
            }
        }
    }

    private static Set<Integer> findMatchingLineNumbers(List<String> lines, Pattern pattern) {
        Set<Integer> matchedLineNumbers = new LinkedHashSet<>();
        for (int i = 0; i < lines.size(); i++) {
            collectMatchingLineNumbersFromLine(matchedLineNumbers, lines.get(i), i + 1, pattern);
        }
        return matchedLineNumbers;
    }

    private static void collectMatchingLineNumbersFromLine(Set<Integer> matchedLineNumbers, String line, int lineNumber,
                                                           Pattern pattern) {
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            matchedLineNumbers.add(lineNumber);
        }
    }

    private static String joinLineNumbers(Set<Integer> lineNumbers) {
        return lineNumbers.stream()
                .map(String::valueOf)
                .reduce((left, right) -> left + ", " + right)
                .orElse("");
    }
}
