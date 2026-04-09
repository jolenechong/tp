package seedu.address.model.util;

import static javafx.collections.FXCollections.observableArrayList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.model.util.VendorVaultConsistencyUtil.MESSAGE_DUPLICATE_PRODUCT_IDENTIFIER;
import static seedu.address.model.util.VendorVaultConsistencyUtil.MESSAGE_DUPLICATE_PRODUCT_IDENTIFIER_WITH_LINES;
import static seedu.address.model.util.VendorVaultConsistencyUtil.MESSAGE_UNKNOWN_VENDOR_LINK;
import static seedu.address.model.util.VendorVaultConsistencyUtil.MESSAGE_UNKNOWN_VENDOR_LINK_WITH_LINES;
import static seedu.address.model.util.VendorVaultConsistencyUtil.findUnknownVendorLinksFromJson;
import static seedu.address.model.util.VendorVaultConsistencyUtil.validateOrThrow;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalProducts.OIL;
import static seedu.address.testutil.TypicalProducts.RICE;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.ReadOnlyInventory;
import seedu.address.model.product.Product;
import seedu.address.testutil.ProductBuilder;

public class VendorVaultConsistencyUtilTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "VendorVaultConsistencyUtilTest");
    private static final Path UNKNOWN_VENDOR_SINGLE_FILE =
            TEST_DATA_FOLDER.resolve("unknownVendorSingleProductInventory.json");
    private static final Path UNKNOWN_VENDOR_MIXED_FILE =
            TEST_DATA_FOLDER.resolve("unknownVendorMixedInventory.json");
    private static final Path DUPLICATE_IDENTIFIER_FILE =
            Paths.get("src", "test", "data", "JsonInventoryStorageTest", "duplicateProductIDInventory.json");
    private static final Path MISSING_FILE = TEST_DATA_FOLDER.resolve("missingInventoryFile.json");
    private static final String SKU_UNKNOWN = "SKU-UNKNOWN";
    private static final String SKU_DUP = "SKU-DUP";
    private static final String VENDOR_EMAIL_MISSING = "missing@example.com";
    private static final String VENDOR_EMAIL_NOT_IN_FILE = "not-in-file@example.com";
    private static final String SKU_KNOWN = "SKU-KNOWN";
    private static final String SKU_NO_EMAIL = "SKU-NO-EMAIL";
    private static final String LINE_REFERENCE_SINGLE = "line 6";
    private static final String LINE_REFERENCE_MULTIPLE = "lines 7, 13";
    private static final String ISSUE_UNKNOWN_VENDOR_WITH_LINE = "'missing@example.com' at line 13";
    private static final String REFLECTION_TO_LINE_REFERENCE_METHOD = "toLineReference";
    private static final String LINE_NUMBERS_SINGLE = "6";
    private static final String LINE_NUMBERS_WITH_EMPTY_PARTS = "6, , 13";
    private static final String LINE_REFERENCE_WITH_EMPTY_PARTS = "lines 6, , 13";

    @Test
    public void validateOrThrow_nullAddressBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                validateOrThrow(null, getTypicalInventory()));
    }

    @Test
    public void validateOrThrow_nullInventory_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                validateOrThrow(getTypicalAddressBook(), null));
    }

    @Test
    public void validateOrThrow_validInventory_success() {
        assertDoesNotThrow(() -> validateOrThrow(
                getTypicalAddressBook(),
                getTypicalInventory()));
    }

    @Test
    public void validateOrThrow_unknownVendorEmail_throwsIllegalValueException() {
        Product unlinkedProduct = new ProductBuilder(RICE)
                .withIdentifier(SKU_UNKNOWN)
                .withVendorEmail(VENDOR_EMAIL_MISSING)
                .build();

        ReadOnlyInventory inventory = inventoryWithProducts(unlinkedProduct);

        assertThrows(IllegalValueException.class, String.format(MESSAGE_UNKNOWN_VENDOR_LINK,
                SKU_UNKNOWN, VENDOR_EMAIL_MISSING), () ->
                validateOrThrow(getTypicalAddressBook(), inventory));
    }

    @Test
    public void validateOrThrow_unknownVendorEmailWithSourcePath_includesLineNumbers() {
        Product unlinkedProduct = new ProductBuilder(RICE)
                .withIdentifier(SKU_UNKNOWN)
                .withVendorEmail(VENDOR_EMAIL_MISSING)
                .build();

        ReadOnlyInventory inventory = inventoryWithProducts(unlinkedProduct);

        assertThrows(IllegalValueException.class, String.format(MESSAGE_UNKNOWN_VENDOR_LINK_WITH_LINES,
                VENDOR_EMAIL_MISSING, LINE_REFERENCE_SINGLE), () ->
                validateOrThrow(getTypicalAddressBook(), inventory, UNKNOWN_VENDOR_SINGLE_FILE));
    }

    @Test
    public void findUnknownVendorLinksFromJson_withUnknownEmail_returnsLineAwareIssue() {
        List<String> issues = findUnknownVendorLinksFromJson(getTypicalAddressBook(), UNKNOWN_VENDOR_MIXED_FILE);

        assertEquals(1, issues.size());
        assertEquals(ISSUE_UNKNOWN_VENDOR_WITH_LINE, issues.get(0));
    }

    @Test
    public void findUnknownVendorLinksFromJson_missingFile_returnsEmptyList() {
        List<String> issues = findUnknownVendorLinksFromJson(getTypicalAddressBook(), MISSING_FILE);

        assertEquals(0, issues.size());
    }

    @Test
    public void validateOrThrow_duplicateIdentifier_throwsIllegalValueException() {
        Product first = new ProductBuilder(RICE)
                .withIdentifier(SKU_DUP)
                .build();
        Product second = new ProductBuilder(OIL)
                .withIdentifier(SKU_DUP)
                .build();

        // EP: Use test double to bypass model-level uniqueness guards.
        ReadOnlyInventory inventory = inventoryWithProducts(first, second);

        assertThrows(IllegalValueException.class, MESSAGE_DUPLICATE_PRODUCT_IDENTIFIER, () ->
                validateOrThrow(getTypicalAddressBook(), inventory));
    }

    @Test
    public void validateOrThrow_duplicateIdentifierWithPathButNoLineMatch_throwsGenericDuplicateMessage() {
        Product first = new ProductBuilder(RICE)
                .withIdentifier(SKU_DUP)
                .build();
        Product second = new ProductBuilder(OIL)
                .withIdentifier(SKU_DUP)
                .build();

        ReadOnlyInventory inventory = inventoryWithProducts(first, second);

        assertThrows(IllegalValueException.class, MESSAGE_DUPLICATE_PRODUCT_IDENTIFIER, () ->
                validateOrThrow(getTypicalAddressBook(), inventory, UNKNOWN_VENDOR_SINGLE_FILE));
    }

    @Test
    public void validateOrThrow_duplicateIdentifierWithMissingPath_throwsGenericDuplicateMessage() {
        Product first = new ProductBuilder(RICE)
                .withIdentifier(SKU_DUP)
                .build();
        Product second = new ProductBuilder(OIL)
                .withIdentifier(SKU_DUP)
                .build();

        ReadOnlyInventory inventory = inventoryWithProducts(first, second);

        assertThrows(IllegalValueException.class, MESSAGE_DUPLICATE_PRODUCT_IDENTIFIER, () ->
                validateOrThrow(getTypicalAddressBook(), inventory, MISSING_FILE));
    }

    @Test
    public void validateOrThrow_duplicateIdentifierWithLineMatch_throwsLineAwareDuplicateMessage() {
        Product first = new ProductBuilder(RICE)
                .build();
        Product second = new ProductBuilder(OIL)
                .withIdentifier(RICE.getIdentifier().value)
                .build();

        ReadOnlyInventory inventory = inventoryWithProducts(first, second);

        assertThrows(IllegalValueException.class,
                String.format(MESSAGE_DUPLICATE_PRODUCT_IDENTIFIER_WITH_LINES,
                        RICE.getIdentifier().value, LINE_REFERENCE_MULTIPLE), () ->
                        validateOrThrow(getTypicalAddressBook(), inventory, DUPLICATE_IDENTIFIER_FILE));
    }

    @Test
    public void validateOrThrow_unknownVendorEmailWithPathButNoLineMatch_throwsIdentifierBasedMessage() {
        Product unlinkedProduct = new ProductBuilder(RICE)
                .withIdentifier(SKU_UNKNOWN)
                .withVendorEmail(VENDOR_EMAIL_NOT_IN_FILE)
                .build();

        ReadOnlyInventory inventory = inventoryWithProducts(unlinkedProduct);

        assertThrows(IllegalValueException.class, String.format(MESSAGE_UNKNOWN_VENDOR_LINK, SKU_UNKNOWN,
                VENDOR_EMAIL_NOT_IN_FILE), () ->
                validateOrThrow(getTypicalAddressBook(), inventory, UNKNOWN_VENDOR_SINGLE_FILE));
    }

    @Test
    public void validateOrThrow_knownVendorEmailWithSourcePath_success() {
        Product linkedProduct = new ProductBuilder(RICE)
                .withIdentifier(SKU_KNOWN)
                .withVendorEmail(ALICE.getEmail().value)
                .build();

        ReadOnlyInventory inventory = inventoryWithProducts(linkedProduct);

        assertDoesNotThrow(() -> validateOrThrow(getTypicalAddressBook(), inventory, UNKNOWN_VENDOR_SINGLE_FILE));
    }

    @Test
    public void validateOrThrow_withoutVendorEmail_success() {
        Product productWithoutVendor = new ProductBuilder(RICE)
                .withIdentifier(SKU_NO_EMAIL)
                .withoutVendorEmail()
                .build();

        ReadOnlyInventory inventory = inventoryWithProducts(productWithoutVendor);

        assertDoesNotThrow(() -> validateOrThrow(getTypicalAddressBook(), inventory));
    }

    @Test
    public void toLineReference_singleLine_returnsSingularPrefix() throws Exception {
        assertEquals(LINE_REFERENCE_SINGLE, invokeToLineReference(LINE_NUMBERS_SINGLE));
    }

    @Test
    public void toLineReference_withEmptyParts_countsOnlyNonEmptyParts() throws Exception {
        assertEquals(LINE_REFERENCE_WITH_EMPTY_PARTS, invokeToLineReference(LINE_NUMBERS_WITH_EMPTY_PARTS));
    }

    private String invokeToLineReference(String lineNumbers) throws Exception {
        java.lang.reflect.Method method = VendorVaultConsistencyUtil.class
                .getDeclaredMethod(REFLECTION_TO_LINE_REFERENCE_METHOD, String.class);
        method.setAccessible(true);
        return (String) method.invoke(null, lineNumbers);
    }

    private ReadOnlyInventory inventoryWithProducts(Product... products) {
        ObservableList<Product> productList = observableArrayList(products);

        return new ReadOnlyInventory() {
            @Override
            public ObservableList<Product> getProductList() {
                return productList;
            }

            @Override
            public Optional<Product> findSimilarNameMatch(Product candidate, Product exclude) {
                return Optional.empty();
            }
        };
    }
}
