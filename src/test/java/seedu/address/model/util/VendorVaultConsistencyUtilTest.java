package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
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

    @Test
    public void validateOrThrow_nullAddressBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                VendorVaultConsistencyUtil.validateOrThrow(null, getTypicalInventory()));
    }

    @Test
    public void validateOrThrow_nullInventory_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                VendorVaultConsistencyUtil.validateOrThrow(getTypicalAddressBook(), null));
    }

    @Test
    public void validateOrThrow_validInventory_success() {
        assertDoesNotThrow(() -> VendorVaultConsistencyUtil.validateOrThrow(
                getTypicalAddressBook(),
                getTypicalInventory()));
    }

    @Test
    public void validateOrThrow_unknownVendorEmail_throwsIllegalValueException() {
        Product unlinkedProduct = new ProductBuilder()
                .withIdentifier("SKU-UNKNOWN")
                .withName("Unlinked Product")
                .withQuantity("1")
                .withThreshold("0")
                .withVendorEmail("missing@example.com")
                .build();

        ReadOnlyInventory inventory = inventoryWithProducts(unlinkedProduct);

        assertThrows(IllegalValueException.class, String.format(VendorVaultConsistencyUtil.MESSAGE_UNKNOWN_VENDOR_LINK,
                "SKU-UNKNOWN", "missing@example.com"), () ->
                VendorVaultConsistencyUtil.validateOrThrow(getTypicalAddressBook(), inventory));
    }

    @Test
    public void validateOrThrow_unknownVendorEmailWithSourcePath_includesLineNumbers() {
        Product unlinkedProduct = new ProductBuilder()
                .withIdentifier("SKU-UNKNOWN")
                .withName("Unlinked Product")
                .withQuantity("1")
                .withThreshold("0")
                .withVendorEmail("missing@example.com")
                .build();

        ReadOnlyInventory inventory = inventoryWithProducts(unlinkedProduct);

        assertThrows(IllegalValueException.class,
                String.format(VendorVaultConsistencyUtil.MESSAGE_UNKNOWN_VENDOR_LINK_WITH_LINES,
                        "missing@example.com", "line 6"), () ->
                        VendorVaultConsistencyUtil.validateOrThrow(getTypicalAddressBook(), inventory,
                                UNKNOWN_VENDOR_SINGLE_FILE));
    }

    @Test
    public void findUnknownVendorLinksFromJson_withUnknownEmail_returnsLineAwareIssue() {
        List<String> issues = VendorVaultConsistencyUtil.findUnknownVendorLinksFromJson(
                getTypicalAddressBook(),
                UNKNOWN_VENDOR_MIXED_FILE);

        org.junit.jupiter.api.Assertions.assertEquals(1, issues.size());
        org.junit.jupiter.api.Assertions.assertEquals("'missing@example.com' at line 13", issues.get(0));
    }

    @Test
    public void validateOrThrow_duplicateIdentifier_throwsIllegalValueException() {
        Product first = new ProductBuilder()
                .withIdentifier("SKU-DUP")
                .withName("First")
                .withQuantity("1")
                .withThreshold("0")
                .build();
        Product second = new ProductBuilder()
                .withIdentifier("SKU-DUP")
                .withName("Second")
                .withQuantity("2")
                .withThreshold("0")
                .build();

        // EP: Use test double to bypass model-level uniqueness guards.
        ReadOnlyInventory inventory = inventoryWithProducts(first, second);

        assertThrows(IllegalValueException.class, VendorVaultConsistencyUtil.MESSAGE_DUPLICATE_PRODUCT_IDENTIFIER, () ->
                VendorVaultConsistencyUtil.validateOrThrow(getTypicalAddressBook(), inventory));
    }

    @Test
    public void validateOrThrow_withoutVendorEmail_success() {
        Product productWithoutVendor = new ProductBuilder()
                .withIdentifier("SKU-NO-EMAIL")
                .withName("No Vendor")
                .withQuantity("3")
                .withThreshold("1")
                .withoutVendorEmail()
                .build();

        ReadOnlyInventory inventory = inventoryWithProducts(productWithoutVendor);

        assertDoesNotThrow(() -> VendorVaultConsistencyUtil.validateOrThrow(getTypicalAddressBook(), inventory));
    }

    private ReadOnlyInventory inventoryWithProducts(Product... products) {
        ObservableList<Product> productList = FXCollections.observableArrayList(products);

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
