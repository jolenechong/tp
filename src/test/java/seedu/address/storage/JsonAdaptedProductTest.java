package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.storage.JsonAdaptedProduct.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.TypicalProducts.RICE;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.product.Identifier;
import seedu.address.model.product.Name;
import seedu.address.model.product.Product;
import seedu.address.model.product.Quantity;

public class JsonAdaptedProductTest {

    private static final String INVALID_NAME = " ";
    private static final String INVALID_QUANTITY = "-99";

    private static final String VALID_IDENTIFIER = RICE.getIdentifier().toString();
    private static final String VALID_NAME = RICE.getName().fullName;
    private static final String VALID_QUANTITY = RICE.getQuantity().toString();
    private static final boolean IS_ARCHIVED = true;
    private static final boolean NOT_ARCHIVED = false;

    @Test
    public void jsonCreatorConstructor_validInputs_fieldsStoredCorrectly() throws Exception {
        Product product = RICE;

        assertEquals(VALID_IDENTIFIER, product.getIdentifier().toString());
        assertEquals(VALID_NAME, product.getName().fullName);
        assertEquals(VALID_QUANTITY, product.getQuantity().toString());
    }

    @Test
    public void jsonCreatorConstructor_isArchivedTrue_storedCorrectly() throws Exception {
        JsonAdaptedProduct adapted = new JsonAdaptedProduct(
                VALID_IDENTIFIER, VALID_NAME, VALID_QUANTITY, IS_ARCHIVED);
        Product product = adapted.toModelType();

        assertTrue(product.isArchived());
    }

    @Test
    public void productConstructor_validProduct_roundTripSucceeds() throws Exception {
        Product original = RICE;

        JsonAdaptedProduct adapted = new JsonAdaptedProduct(original);
        Product result = adapted.toModelType();

        assertEquals(original.getIdentifier().toString(), result.getIdentifier().toString());
        assertEquals(original.getName().fullName, result.getName().fullName);
        assertEquals(original.getQuantity().toString(), result.getQuantity().toString());
    }

    @Test
    public void toModelType_nullIdentifier_throwsIllegalValueException() {
        JsonAdaptedProduct adapted = new JsonAdaptedProduct(
                null, VALID_NAME, VALID_QUANTITY, NOT_ARCHIVED);

        IllegalValueException ex = assertThrows(IllegalValueException.class,
                adapted::toModelType);

        assertEquals(ex.getMessage(), String.format(MISSING_FIELD_MESSAGE_FORMAT,
                Identifier.class.getSimpleName()));
    }

    @Test
    public void toModelType_invalidIdentifier_throwsIllegalValueException() {
        JsonAdaptedProduct adapted = new JsonAdaptedProduct(
                INVALID_NAME, VALID_NAME, VALID_QUANTITY, NOT_ARCHIVED);

        IllegalValueException ex = assertThrows(IllegalValueException.class,
                adapted::toModelType);

        assertEquals(Identifier.MESSAGE_CONSTRAINTS, ex.getMessage());
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedProduct adapted = new JsonAdaptedProduct(
                VALID_IDENTIFIER, null, VALID_QUANTITY, NOT_ARCHIVED);

        IllegalValueException ex = assertThrows(IllegalValueException.class,
                adapted::toModelType);

        assertEquals(ex.getMessage(), String.format(MISSING_FIELD_MESSAGE_FORMAT,
                Name.class.getSimpleName()));
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedProduct adapted = new JsonAdaptedProduct(
                VALID_IDENTIFIER, INVALID_NAME, VALID_QUANTITY, NOT_ARCHIVED);

        IllegalValueException ex = assertThrows(IllegalValueException.class,
                adapted::toModelType);

        assertEquals(Name.MESSAGE_CONSTRAINTS, ex.getMessage());
    }

    @Test
    public void toModelType_nullQuantity_throwsIllegalValueException() {
        JsonAdaptedProduct adapted = new JsonAdaptedProduct(
                VALID_IDENTIFIER, VALID_NAME, null, NOT_ARCHIVED);

        IllegalValueException ex = assertThrows(IllegalValueException.class,
                adapted::toModelType);

        assertEquals(ex.getMessage(), String.format(MISSING_FIELD_MESSAGE_FORMAT,
                Quantity.class.getSimpleName()));
    }

    @Test
    public void toModelType_invalidQuantity_throwsIllegalValueException() {
        JsonAdaptedProduct adapted = new JsonAdaptedProduct(
                VALID_IDENTIFIER, VALID_NAME, INVALID_QUANTITY, NOT_ARCHIVED);

        IllegalValueException ex = assertThrows(IllegalValueException.class,
                adapted::toModelType);

        assertEquals(Quantity.MESSAGE_CONSTRAINTS, ex.getMessage());
    }
}
