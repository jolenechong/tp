package seedu.address.model.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalProducts.OIL;
import static seedu.address.testutil.TypicalProducts.RICE;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.ProductBuilder;

public class ProductTest {

    private static final String VALID_IDENTIFIER_OIL = "SKU-1000";
    private static final String VALID_NAME_OIL = "Cooking Oil 1L";
    private static final String VALID_QUANTITY_OIL = "20";

    @Test
    public void isSameProduct() {
        // same object -> returns true
        assertTrue(RICE.isSameProduct(RICE));

        // null -> returns false
        assertFalse(RICE.isSameProduct(null));

        // same id, all other attributes different -> returns true
        Product editedRice = new ProductBuilder(RICE).withName(VALID_NAME_OIL).withQuantity(VALID_QUANTITY_OIL).build();
        assertTrue(RICE.isSameProduct(editedRice));

        // different id, all other attributes same -> returns false
        editedRice = new ProductBuilder(RICE).withIdentifier(VALID_IDENTIFIER_OIL).build();
        assertFalse(RICE.isSameProduct(editedRice));

        // id differs in case, all other attributes same -> returns false
        Product editedOil = new ProductBuilder(OIL).withIdentifier(VALID_IDENTIFIER_OIL.toLowerCase()).build();
        assertFalse(OIL.isSameProduct(editedOil));

        // id has trailing spaces, all other attributes same -> returns false
        String identifierWithTrailingSpaces = VALID_IDENTIFIER_OIL + " ";
        editedOil = new ProductBuilder(OIL).withIdentifier(identifierWithTrailingSpaces).build();
        assertFalse(OIL.isSameProduct(editedOil));
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
        Product editedRice = new ProductBuilder(RICE).withIdentifier(VALID_IDENTIFIER_OIL).build();
        assertFalse(RICE.equals(editedRice));

        // different name -> returns false
        editedRice = new ProductBuilder(RICE).withName(VALID_NAME_OIL).build();
        assertFalse(RICE.equals(editedRice));

        // different quantity -> returns false
        editedRice = new ProductBuilder(RICE).withQuantity(VALID_QUANTITY_OIL).build();
        assertFalse(RICE.equals(editedRice));
    }

    @Test
    public void toStringMethod() {
        String expected =
                Product.class.getCanonicalName() + "{identifier=" + RICE.getIdentifier() + ", name=" + RICE.getName()
                + ", quantity=" + RICE.getQuantity() + "}";
        assertEquals(expected, RICE.toString());
    }
}
