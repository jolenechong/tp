package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PRODUCT_NAME_AIRPODS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_QUANTITY_IPHONE;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalProducts.OIL;
import static seedu.address.testutil.TypicalProducts.RICE;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.product.Product;
import seedu.address.model.product.exceptions.DuplicateProductException;
import seedu.address.model.product.exceptions.ProductNotFoundException;
import seedu.address.testutil.ProductBuilder;

public class InventoryTest {

    private final Inventory inventory = new Inventory();

    @Test
    public void constructor() {
        assertEquals(Collections.emptyList(), inventory.getProductList());
    }

    @Test
    public void constructor_withValidReadOnlyInventory_copiesData() {
        Inventory newData = getTypicalInventory();
        Inventory inventory = new Inventory(newData);
        assertEquals(newData, inventory);
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> inventory.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyInventory_replacesData() {
        Inventory newData = getTypicalInventory();
        inventory.resetData(newData);
        assertEquals(newData, inventory);
    }

    @Test
    public void resetData_withDuplicateProducts_throwsDuplicateProductException() {
        Product editedOil = new ProductBuilder(OIL).withName(VALID_PRODUCT_NAME_AIRPODS)
                .withQuantity(VALID_QUANTITY_IPHONE).build();
        List<Product> newProducts = Arrays.asList(OIL, editedOil);
        InventoryStub newData = new InventoryStub(newProducts);

        assertThrows(DuplicateProductException.class, () -> inventory.resetData(newData));
    }

    @Test
    public void hasProduct_nullProduct_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> inventory.hasProduct(null));
    }

    @Test
    public void hasProduct_productNotInInventory_returnsFalse() {
        assertFalse(inventory.hasProduct(OIL));
    }

    @Test
    public void hasProduct_productInInventory_returnsTrue() {
        inventory.addProduct(OIL);
        assertTrue(inventory.hasProduct(OIL));
    }

    @Test
    public void hasProduct_productWithSameIdentityFieldsInInventory_returnsTrue() {
        inventory.addProduct(OIL);
        Product editedOil = new ProductBuilder(OIL).withName(VALID_PRODUCT_NAME_AIRPODS)
                .withQuantity(VALID_QUANTITY_IPHONE).build();
        assertTrue(inventory.hasProduct(editedOil));
    }

    @Test
    public void setProduct_nullTargetProduct_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> inventory.setProduct(null, OIL));
    }

    @Test
    public void setProduct_nullEditedProduct_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> inventory.setProduct(OIL, null));
    }

    @Test
    public void setProduct_targetProductNotInInventory_throwsProductNotFoundException() {
        assertThrows(ProductNotFoundException.class, () -> inventory.setProduct(OIL, OIL));
    }

    @Test
    public void setProduct_editedProductHasSameIdentity_success() {
        inventory.addProduct(OIL);
        Product editedOil = new ProductBuilder(OIL).withName(VALID_PRODUCT_NAME_AIRPODS)
                .withQuantity(VALID_QUANTITY_IPHONE).build();

        inventory.setProduct(OIL, editedOil);
        Inventory expectedInventory = new Inventory();
        expectedInventory.addProduct(editedOil);

        assertEquals(expectedInventory, inventory);
    }

    @Test
    public void setProduct_editedProductHasDifferentIdentity_success() {
        inventory.addProduct(OIL);
        inventory.setProduct(OIL, RICE);
        Inventory expectedInventory = new Inventory();
        expectedInventory.addProduct(RICE);
        assertEquals(expectedInventory, inventory);
    }

    @Test
    public void setProduct_editedProductHasNonUniqueIdentity_throwsDuplicateProductException() {
        inventory.addProduct(OIL);
        inventory.addProduct(RICE);
        assertThrows(DuplicateProductException.class, () -> inventory.setProduct(RICE, OIL));
    }

    @Test
    public void removeProduct_nullProduct_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> inventory.removeProduct(null));
    }

    @Test
    public void removeProduct_missingProduct_throwsProductNotFoundException() {
        assertThrows(ProductNotFoundException.class, () -> inventory.removeProduct(OIL));
    }

    @Test
    public void removeProduct_existingProduct_removesProduct() {
        inventory.addProduct(OIL);
        inventory.removeProduct(OIL);
        Inventory expectedInventory = new Inventory();
        assertEquals(expectedInventory, inventory);
    }

    @Test
    public void getProductList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> inventory.getProductList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = Inventory.class.getCanonicalName() + "{products=" + inventory.getProductList() + "}";
        assertEquals(expected, inventory.toString());
    }

    /**
     * A stub ReadOnlyInventory whose products list can violate interface constraints.
     */
    private static class InventoryStub implements ReadOnlyInventory {
        private final ObservableList<Product> products = FXCollections.observableArrayList();

        InventoryStub(Collection<Product> products) {
            this.products.setAll(products);
        }

        @Override
        public ObservableList<Product> getProductList() {
            return products;
        }
    }

}
