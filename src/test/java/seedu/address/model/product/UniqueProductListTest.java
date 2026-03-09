package seedu.address.model.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PRODUCT_NAME_AIRPODS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_QUANTITY_IPHONE;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalProducts.OIL;
import static seedu.address.testutil.TypicalProducts.RICE;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.product.exceptions.DuplicateProductException;
import seedu.address.model.product.exceptions.ProductNotFoundException;
import seedu.address.testutil.ProductBuilder;

public class UniqueProductListTest {

    private final UniqueProductList uniqueProductList = new UniqueProductList();

    @Test
    public void contains_nullProduct_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueProductList.contains(null));
    }

    @Test
    public void contains_productNotInList_returnsFalse() {
        assertFalse(uniqueProductList.contains(RICE));
    }

    @Test
    public void contains_productInList_returnsTrue() {
        uniqueProductList.add(RICE);
        assertTrue(uniqueProductList.contains(RICE));
    }

    @Test
    public void contains_productWithSameIdentityFieldsInList_returnsTrue() {
        uniqueProductList.add(RICE);
        Product editedRice = new ProductBuilder(RICE).withName(VALID_PRODUCT_NAME_AIRPODS).build();
        assertTrue(uniqueProductList.contains(editedRice));
    }

    @Test
    public void add_nullProduct_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueProductList.add(null));
    }

    @Test
    public void add_duplicateProduct_throwsDuplicateProductException() {
        uniqueProductList.add(RICE);
        assertThrows(DuplicateProductException.class, () -> uniqueProductList.add(RICE));
    }

    @Test
    public void setProduct_nullTargetProduct_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueProductList.setProduct(null, RICE));
    }

    @Test
    public void setProduct_nullEditedProduct_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueProductList.setProduct(RICE, null));
    }

    @Test
    public void setProduct_targetProductNotInList_throwsProductNotFoundException() {
        assertThrows(ProductNotFoundException.class, () -> uniqueProductList.setProduct(RICE, RICE));
    }

    @Test
    public void setProduct_editedProductIsSameProduct_success() {
        uniqueProductList.add(RICE);
        uniqueProductList.setProduct(RICE, RICE);
        UniqueProductList expectedUniqueProductList = new UniqueProductList();
        expectedUniqueProductList.add(RICE);
        assertEquals(expectedUniqueProductList, uniqueProductList);
    }

    @Test
    public void setProduct_editedProductHasSameIdentity_success() {
        uniqueProductList.add(RICE);
        Product editedRice = new ProductBuilder(RICE).withQuantity(VALID_QUANTITY_IPHONE).build();
        uniqueProductList.setProduct(RICE, editedRice);
        UniqueProductList expectedUniqueProductList = new UniqueProductList();
        expectedUniqueProductList.add(editedRice);
        assertEquals(expectedUniqueProductList, uniqueProductList);
    }

    @Test
    public void setProduct_editedProductHasDifferentIdentity_success() {
        uniqueProductList.add(RICE);
        uniqueProductList.setProduct(RICE, OIL);
        UniqueProductList expectedUniqueProductList = new UniqueProductList();
        expectedUniqueProductList.add(OIL);
        assertEquals(expectedUniqueProductList, uniqueProductList);
    }

    @Test
    public void setProduct_editedProductHasNonUniqueIdentity_throwsDuplicateProductException() {
        uniqueProductList.add(RICE);
        uniqueProductList.add(OIL);
        assertThrows(DuplicateProductException.class, () -> uniqueProductList.setProduct(RICE, OIL));
    }

    @Test
    public void remove_nullProduct_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueProductList.remove(null));
    }

    @Test
    public void remove_productDoesNotExist_throwsProductNotFoundException() {
        assertThrows(ProductNotFoundException.class, () -> uniqueProductList.remove(RICE));
    }

    @Test
    public void remove_existingProduct_removesProduct() {
        uniqueProductList.add(RICE);
        uniqueProductList.remove(RICE);
        UniqueProductList expectedUniqueProductList = new UniqueProductList();
        assertEquals(expectedUniqueProductList, uniqueProductList);
    }

    @Test
    public void setProducts_nullUniqueProductList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueProductList.setProducts((UniqueProductList) null));
    }

    @Test
    public void setProducts_uniqueProductList_replacesOwnListWithProvidedUniqueProductList() {
        uniqueProductList.add(RICE);
        UniqueProductList expectedUniqueProductList = new UniqueProductList();
        expectedUniqueProductList.add(OIL);
        uniqueProductList.setProducts(expectedUniqueProductList);
        assertEquals(expectedUniqueProductList, uniqueProductList);
    }

    @Test
    public void setProducts_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueProductList.setProducts((List<Product>) null));
    }

    @Test
    public void setProducts_list_replacesOwnListWithProvidedList() {
        uniqueProductList.add(RICE);
        List<Product> productList = Collections.singletonList(OIL);
        uniqueProductList.setProducts(productList);
        UniqueProductList expectedUniqueProductList = new UniqueProductList();
        expectedUniqueProductList.add(OIL);
        assertEquals(expectedUniqueProductList, uniqueProductList);
    }

    @Test
    public void setProducts_listWithDuplicateProducts_throwsDuplicateProductException() {
        List<Product> listWithDuplicateProducts = Arrays.asList(RICE, RICE);
        assertThrows(DuplicateProductException.class, () -> uniqueProductList.setProducts(listWithDuplicateProducts));
    }

    @Test
    public void archiveProduct_success() {
        UniqueProductList list = new UniqueProductList();
        Product product = new ProductBuilder().build();

        list.add(product);
        list.archiveProduct(product);

        assertTrue(list.asUnmodifiableObservableList().get(0).isArchived());
    }

    @Test
    public void restoreProduct_success() {
        UniqueProductList list = new UniqueProductList();
        Product product = new ProductBuilder().build();
        product = product.archive();
        list.add(product);
        list.restoreProduct(product);

        assertFalse(list.asUnmodifiableObservableList().get(0).isArchived());
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, ()
                -> uniqueProductList.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void toStringMethod() {
        assertEquals(uniqueProductList.asUnmodifiableObservableList().toString(), uniqueProductList.toString());
    }
}
