package seedu.address.model.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_IDENTIFIER_AIRPODS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PRODUCT_NAME_IPAD;
import static seedu.address.logic.commands.CommandTestUtil.VALID_QUANTITY_IPHONE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_THRESHOLD_AIRPODS;
import static seedu.address.testutil.TypicalProducts.NOODLES;
import static seedu.address.testutil.TypicalProducts.OIL;
import static seedu.address.testutil.TypicalProducts.RICE;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Email;
import seedu.address.testutil.ProductBuilder;

public class ProductTest {

    @Test
    public void constructor_withVendorEmail_notArchivedByDefault() {
        Product product = new Product(
                RICE.getIdentifier(),
                RICE.getName(),
                RICE.getQuantity(),
                RICE.getRestockThreshold(),
                new Email(VALID_EMAIL_AMY));

        assertEquals(new Email(VALID_EMAIL_AMY), product.getVendorEmail().orElse(null));
        assertFalse(product.isArchived());
    }

    @Test
    public void constructor_withArchivedStatus_vendorEmailIsEmpty() {
        Product product = new Product(
                RICE.getIdentifier(),
                RICE.getName(),
                RICE.getQuantity(),
                RICE.getRestockThreshold(),
                true);

        assertTrue(product.getVendorEmail().isEmpty());
        assertTrue(product.isArchived());
    }

    @Test
    public void clearVendorEmail_withVendorEmail_clearsVendor() {
        Product productWithVendor = new ProductBuilder(RICE)
                .withVendorEmail(VALID_EMAIL_AMY)
                .build()
                .archive();

        Product clearedProduct = productWithVendor.clearVendorEmail();
        assertTrue(clearedProduct.getVendorEmail().isEmpty());
    }

    @Test
    public void isSameProduct() {
        // same object -> returns true
        assertTrue(RICE.isSameProduct(RICE));

        // null -> returns false
        assertFalse(RICE.isSameProduct(null));

        // same id, all other attributes different -> returns true
        Product editedRice = new ProductBuilder(RICE).withName(VALID_PRODUCT_NAME_IPAD)
                .withQuantity(VALID_QUANTITY_IPHONE).withThreshold(VALID_THRESHOLD_AIRPODS).build();
        assertTrue(RICE.isSameProduct(editedRice));

        // different id, all other attributes same -> returns false
        editedRice = new ProductBuilder(RICE).withIdentifier(VALID_IDENTIFIER_AIRPODS).build();
        assertFalse(RICE.isSameProduct(editedRice));

        // id differs in case, all other attributes same -> returns false
        Product editedOil = new ProductBuilder(OIL).withIdentifier(VALID_IDENTIFIER_AIRPODS.toLowerCase()).build();
        assertFalse(OIL.isSameProduct(editedOil));

        // id has trailing spaces, all other attributes same -> returns false
        String identifierWithTrailingSpaces = VALID_IDENTIFIER_AIRPODS + " ";
        editedOil = new ProductBuilder(OIL).withIdentifier(identifierWithTrailingSpaces).build();
        assertFalse(OIL.isSameProduct(editedOil));
    }

    @Test
    public void hashCode_sameProduct_sameHash() {
        Product product1 = new ProductBuilder(RICE).build();
        Product product2 = new ProductBuilder(RICE).build();

        assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    public void hashCode_differentArchivedStatus_notEqual() {
        Product product1 = new ProductBuilder().build();
        Product product2 = product1.archive();

        assertNotEquals(product1.hashCode(), product2.hashCode());
    }


    @Test
    public void equals() {
        // same values -> returns true
        Product riceCopy = new ProductBuilder(RICE).build();
        assertTrue(RICE.equals(riceCopy));

        // same object -> returns true
        assertTrue(RICE.equals(RICE));

        // null -> returns false
        assertFalse(RICE.equals(null));

        // different type -> returns false
        assertFalse(RICE.equals(5));

        // different product -> returns false
        assertFalse(RICE.equals(OIL));

        // different id -> returns false
        Product editedRice = new ProductBuilder(RICE).withIdentifier(VALID_IDENTIFIER_AIRPODS).build();
        assertFalse(RICE.equals(editedRice));

        // different name -> returns false
        editedRice = new ProductBuilder(RICE).withName(VALID_PRODUCT_NAME_IPAD).build();
        assertFalse(RICE.equals(editedRice));

        // different quantity -> returns false
        editedRice = new ProductBuilder(RICE).withQuantity(VALID_QUANTITY_IPHONE).build();
        assertFalse(RICE.equals(editedRice));

        // different threshold -> returns false
        editedRice = new ProductBuilder(RICE).withThreshold(VALID_THRESHOLD_AIRPODS).build();
        assertFalse(RICE.equals(editedRice));

        // different vendor email -> returns false
        editedRice = new ProductBuilder(RICE).withVendorEmail(VALID_EMAIL_AMY).build();
        assertFalse(RICE.equals(editedRice));
    }

    @Test
    public void equals_differentArchivedStatus_false() {
        Product product1 = new ProductBuilder().build();
        Product product2 = product1.archive();
        assertFalse(product1.equals(product2));
    }

    @Test
    public void toStringMethod() {
        String emailStr = RICE.getVendorEmail().map(Object::toString).orElse("-");

        String expected = String.format("%s; ID: %s; Qty: %s; Threshold: %s; Email: %s",
                RICE.getName(),
                RICE.getIdentifier(),
                RICE.getQuantity(),
                RICE.getRestockThreshold(),
                emailStr);

        assertEquals(expected, RICE.toString());
    }

    @Test
    public void isSameProductWarn_caseInsensitiveExactMatch_warningReturned() {
        Product other = new ProductBuilder()
                .withIdentifier("SKU-9998")
                .withName("brown rice 5kg")
                .build();

        assertTrue(RICE.generateDuplicateWarning(other).isDuplicated());
    }

    @Test
    public void isSameProductWarn_sharedToken_warningReturned() {
        Product other = new ProductBuilder()
                .withIdentifier("SKU-9997")
                .withName("Fried Noodles Beef")
                .build();

        assertTrue(NOODLES.generateDuplicateWarning(other).isDuplicated());
    }

    @Test
    public void isSameProductWarn_completelyDifferentNames_noWarning() {
        assertFalse(RICE.generateDuplicateWarning(OIL).isDuplicated());
    }

    @Test
    public void isSameProductWarn_partialSubstringNotTokenMatch_noWarning() {
        Product other = new ProductBuilder()
                .withIdentifier("SKU-9994")
                .withName("Ricecake Deluxe")
                .build();

        assertFalse(RICE.generateDuplicateWarning(other).isDuplicated());
    }
}
