package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PRODUCT_NAME_IPAD;
import static seedu.address.logic.commands.CommandTestUtil.VALID_QUANTITY_IPHONE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_THRESHOLD_AIRPODS;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.product.Product;
import seedu.address.testutil.EditProductDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.ProductBuilder;

public class EditProductCommandTest {

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Model model = new ModelManager();
        model.setAddressBook(getTypicalAddressBook());
        model.setInventory(getTypicalInventory());

        Person vendor = new PersonBuilder().withEmail(VALID_EMAIL_AMY).build();
        model.addPerson(vendor);

        Product firstProduct = model.getFilteredProductList().get(0);
        Product editedProduct = new ProductBuilder(firstProduct)
                .withName(VALID_PRODUCT_NAME_IPAD)
                .withQuantity(VALID_QUANTITY_IPHONE)
                .withThreshold(VALID_THRESHOLD_AIRPODS)
                .withVendorEmail(VALID_EMAIL_AMY)
                .build();

        EditProductCommand.EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withName(VALID_PRODUCT_NAME_IPAD)
                .withQuantity(VALID_QUANTITY_IPHONE)
                .withThreshold(VALID_THRESHOLD_AIRPODS)
                .withVendorEmail(VALID_EMAIL_AMY)
                .build();

        EditProductCommand command =
                new EditProductCommand(firstProduct.getIdentifier().value, descriptor);

        Model expectedModel = new ModelManager(
                model.getVendorVault(),
                new UserPrefs(),
                model.getAliases());
        expectedModel.setProduct(firstProduct, editedProduct);
        expectedModel.updateFilteredProductList(Model.PREDICATE_SHOW_ACTIVE_PRODUCTS);
        expectedModel.commitVendorVault();

        assertCommandSuccess(command, model,
                String.format(EditProductCommand.MESSAGE_EDIT_PRODUCT_SUCCESS,
                        Messages.formatProduct(editedProduct)),
                expectedModel);
    }

    @Test
    public void execute_partialFieldsSpecified_success() {
        Model model = new ModelManager();
        model.setAddressBook(getTypicalAddressBook());
        model.setInventory(getTypicalInventory());

        Product firstProduct = model.getFilteredProductList().get(0);
        EditProductCommand.EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withQuantity(VALID_QUANTITY_IPHONE)
                .build();
        EditProductCommand command = new EditProductCommand(firstProduct.getIdentifier().value, descriptor);

        Product editedProduct = new ProductBuilder(firstProduct)
                .withQuantity(VALID_QUANTITY_IPHONE)
                .build();

        Model expectedModel = new ModelManager(
                model.getVendorVault(),
                new UserPrefs(),
                model.getAliases());
        expectedModel.setProduct(firstProduct, editedProduct);
        expectedModel.updateFilteredProductList(Model.PREDICATE_SHOW_ACTIVE_PRODUCTS);
        expectedModel.commitVendorVault();

        assertCommandSuccess(command, model,
                String.format(EditProductCommand.MESSAGE_EDIT_PRODUCT_SUCCESS, Messages.formatProduct(editedProduct)),
                expectedModel);
    }

    @Test
    public void execute_clearVendorEmail_success() {
        Model model = new ModelManager();
        model.setAddressBook(getTypicalAddressBook());
        model.setInventory(getTypicalInventory());
        Product originalProduct = model.getFilteredProductList().get(0);
        Product productWithVendor = new ProductBuilder(originalProduct)
                .withVendorEmail(VALID_EMAIL_AMY)
                .build();
        model.setProduct(originalProduct, productWithVendor);

        EditProductCommand.EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withoutVendorEmail()
                .build();
        EditProductCommand command = new EditProductCommand(productWithVendor.getIdentifier().value, descriptor);

        Product editedProduct = new ProductBuilder(productWithVendor)
                .withoutVendorEmail()
                .build();

        Model expectedModel = new ModelManager(
                model.getVendorVault(),
                new UserPrefs(),
                model.getAliases());
        expectedModel.setProduct(productWithVendor, editedProduct);
        expectedModel.updateFilteredProductList(Model.PREDICATE_SHOW_ACTIVE_PRODUCTS);
        expectedModel.commitVendorVault();

        assertCommandSuccess(command, model,
                String.format(EditProductCommand.MESSAGE_EDIT_PRODUCT_SUCCESS, Messages.formatProduct(editedProduct)),
                expectedModel);
    }

    @Test
    public void execute_nonExistentVendorEmail_throwsCommandException() {
        Model model = new ModelManager();
        model.setAddressBook(getTypicalAddressBook());
        model.setInventory(getTypicalInventory());

        Product firstProduct = model.getFilteredProductList().get(0);
        EditProductCommand.EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withVendorEmail("ghost@example.com")
                .build();
        EditProductCommand command = new EditProductCommand(firstProduct.getIdentifier().value, descriptor);

        assertCommandFailure(command, model, EditProductCommand.MESSAGE_VENDOR_EMAIL_NOT_FOUND);
    }

    @Test
    public void execute_invalidProductIdentifier_throwsCommandException() {
        Model model = new ModelManager();
        model.setAddressBook(getTypicalAddressBook());
        model.setInventory(getTypicalInventory());

        EditProductCommand.EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withName(VALID_PRODUCT_NAME_IPAD)
                .build();
        EditProductCommand command = new EditProductCommand("MISSING-ID", descriptor);

        assertCommandFailure(command, model, EditProductCommand.MESSAGE_INVALID_PRODUCT_ID);
    }

    @Test
    public void equals() {
        String targetIdentifier = "SKU-1001";
        EditProductCommand.EditProductDescriptor firstDescriptor = new EditProductDescriptorBuilder()
                .withName(VALID_PRODUCT_NAME_IPAD)
                .build();
        EditProductCommand.EditProductDescriptor secondDescriptor = new EditProductDescriptorBuilder()
                .withQuantity(VALID_QUANTITY_IPHONE)
                .build();

        EditProductCommand editFirstCommand = new EditProductCommand(targetIdentifier, firstDescriptor);
        EditProductCommand editSecondCommand = new EditProductCommand("SKU-1002", secondDescriptor);

        assertTrue(editFirstCommand.equals(editFirstCommand));
        assertTrue(editFirstCommand.equals(new EditProductCommand(targetIdentifier, firstDescriptor)));
        assertFalse(editFirstCommand.equals(1));
        assertFalse(editFirstCommand.equals(null));
        assertFalse(editFirstCommand.equals(editSecondCommand));
    }

    @Test
    public void toStringMethod() {
        EditProductCommand.EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withName(VALID_PRODUCT_NAME_IPAD)
                .withVendorEmail(VALID_EMAIL_BOB)
                .build();
        EditProductCommand command = new EditProductCommand("SKU-1001", descriptor);

        String expected = EditProductCommand.class.getCanonicalName()
                + "{targetIdentifier=SKU-1001, editProductDescriptor=" + descriptor + "}";
        assertEquals(expected, command.toString());
    }
}
