package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import seedu.address.logic.commands.EditProductCommand.EditProductDescriptor;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
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

        EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withName(VALID_PRODUCT_NAME_IPAD)
                .withQuantity(VALID_QUANTITY_IPHONE)
                .withThreshold(VALID_THRESHOLD_AIRPODS)
                .withVendorEmail(VALID_EMAIL_AMY)
                .build();

        EditProductCommand command =
                new EditProductCommand(firstProduct.getIdentifier().value, descriptor);

        Model expectedModel = new ModelManager();
        expectedModel.setVendorVault(model.getVendorVault());
        expectedModel.setProduct(firstProduct, editedProduct);
        expectedModel.updateFilteredProductList(Model.PREDICATE_SHOW_ACTIVE_PRODUCTS);
        String successMessage = String.format(EditProductCommand.MESSAGE_EDIT_PRODUCT_SUCCESS, editedProduct);
        expectedModel.commitVendorVault(successMessage);

        CommandResult result;
        try {
            result = command.execute(model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertTrue(result.getFeedbackToUser().contains(
                String.format(EditProductCommand.MESSAGE_EDIT_PRODUCT_SUCCESS, editedProduct)));
        assertTrue(model.getFilteredProductList().contains(editedProduct));
        assertEquals(expectedModel, model);
    }
    @Test
    public void execute_similarNameWarning_success() throws Exception {
        Model model = new ModelManager();
        model.setAddressBook(getTypicalAddressBook());
        model.setInventory(getTypicalInventory());

        Product existing = model.getFilteredProductList().get(0);
        Product another = model.getFilteredProductList().get(1);

        // force same name as another product
        EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withName(another.getName().toString())
                .build();

        EditProductCommand command =
                new EditProductCommand(existing.getIdentifier().value, descriptor);

        CommandResult result = command.execute(model);

        assertTrue(result.getFeedbackToUser().contains("Warning"));
    }

    @Test
    public void execute_lowStockWarning_success() throws Exception {
        Model model = new ModelManager();
        model.setAddressBook(getTypicalAddressBook());
        model.setInventory(getTypicalInventory());

        Product product = model.getFilteredProductList().get(0);

        EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withQuantity("1")
                .withThreshold("10")
                .build();

        EditProductCommand command =
                new EditProductCommand(product.getIdentifier().value, descriptor);

        CommandResult result = command.execute(model);

        assertTrue(result.getFeedbackToUser().contains("below threshold"));
    }

    @Test
    public void execute_multipleWarnings_success() throws Exception {
        Model model = new ModelManager();
        model.setAddressBook(getTypicalAddressBook());
        model.setInventory(getTypicalInventory());

        Product product = model.getFilteredProductList().get(0);
        Product another = model.getFilteredProductList().get(1);

        EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withName(another.getName().toString())
                .withQuantity("1")
                .withThreshold("10")
                .build();

        EditProductCommand command =
                new EditProductCommand(product.getIdentifier().value, descriptor);

        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();

        assertTrue(feedback.contains("similar name"));
        assertTrue(feedback.contains("below threshold"));
    }

    @Test
    public void execute_noWarnings_success() throws Exception {
        Model model = new ModelManager();
        model.setAddressBook(getTypicalAddressBook());
        model.setInventory(getTypicalInventory());

        Product product = model.getFilteredProductList().get(0);

        EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withName("UniqueName123")
                .build();

        EditProductCommand command =
                new EditProductCommand(product.getIdentifier().value, descriptor);

        CommandResult result = command.execute(model);

        assertFalse(result.getFeedbackToUser().contains("Warning"));
    }

    @Test
    public void execute_partialFieldsSpecified_success() {
        Model model = new ModelManager();
        model.setAddressBook(getTypicalAddressBook());
        model.setInventory(getTypicalInventory());

        Product firstProduct = model.getFilteredProductList().get(0);

        EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withQuantity(VALID_QUANTITY_IPHONE)
                .build();

        EditProductCommand command =
                new EditProductCommand(firstProduct.getIdentifier().value, descriptor);

        Product editedProduct = new ProductBuilder(firstProduct)
                .withQuantity(VALID_QUANTITY_IPHONE)
                .build();

        Model expectedModel = new ModelManager();
        expectedModel.setVendorVault(model.getVendorVault());
        expectedModel.setProduct(firstProduct, editedProduct);
        expectedModel.updateFilteredProductList(Model.PREDICATE_SHOW_ACTIVE_PRODUCTS);
        String successMessage = String.format(EditProductCommand.MESSAGE_EDIT_PRODUCT_SUCCESS, editedProduct);
        expectedModel.commitVendorVault(successMessage);
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

        EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withoutVendorEmail()
                .build();

        EditProductCommand command =
                new EditProductCommand(productWithVendor.getIdentifier().value, descriptor);

        Product editedProduct = new ProductBuilder(productWithVendor)
                .withoutVendorEmail()
                .build();

        Model expectedModel = new ModelManager();
        expectedModel.setVendorVault(model.getVendorVault());
        expectedModel.setProduct(productWithVendor, editedProduct);
        expectedModel.updateFilteredProductList(Model.PREDICATE_SHOW_ACTIVE_PRODUCTS);
        String successMessage = String.format(EditProductCommand.MESSAGE_EDIT_PRODUCT_SUCCESS, editedProduct);
        expectedModel.commitVendorVault(successMessage);

        assertCommandSuccess(command, model,
                String.format(EditProductCommand.MESSAGE_EDIT_PRODUCT_SUCCESS,
                        editedProduct),
                expectedModel);
    }

    @Test
    public void execute_nonExistentVendorEmail_throwsCommandException() {
        Model model = new ModelManager();
        model.setAddressBook(getTypicalAddressBook());
        model.setInventory(getTypicalInventory());

        Product firstProduct = model.getFilteredProductList().get(0);

        EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withVendorEmail("ghost@example.com")
                .build();

        EditProductCommand command =
                new EditProductCommand(firstProduct.getIdentifier().value, descriptor);

        assertCommandFailure(command, model,
                EditProductCommand.MESSAGE_VENDOR_EMAIL_NOT_FOUND);
    }

    @Test
    public void execute_invalidProductIdentifier_throwsCommandException() {
        Model model = new ModelManager();
        model.setAddressBook(getTypicalAddressBook());
        model.setInventory(getTypicalInventory());

        EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withName(VALID_PRODUCT_NAME_IPAD)
                .build();

        EditProductCommand command =
                new EditProductCommand("MISSING-ID", descriptor);

        assertCommandFailure(command, model,
                EditProductCommand.MESSAGE_INVALID_PRODUCT_ID);
    }

    @Test
    public void constructor_noFieldsEdited_throwsException() {
        EditProductDescriptor descriptor =
                new EditProductDescriptor();
        assertThrows(IllegalArgumentException.class, ()
                -> new EditProductCommand("SKU-1", descriptor));
    }

    @Test
    public void equals() {
        String targetIdentifier = "SKU-1001";

        EditProductDescriptor firstDescriptor =
                new EditProductDescriptorBuilder().withName(VALID_PRODUCT_NAME_IPAD).build();

        EditProductDescriptor secondDescriptor =
                new EditProductDescriptorBuilder().withQuantity(VALID_QUANTITY_IPHONE).build();

        EditProductCommand editFirstCommand =
                new EditProductCommand(targetIdentifier, firstDescriptor);

        EditProductCommand editSecondCommand =
                new EditProductCommand("SKU-1002", secondDescriptor);

        assertTrue(editFirstCommand.equals(editFirstCommand));
        assertTrue(editFirstCommand.equals(
                new EditProductCommand(targetIdentifier, firstDescriptor)));
        assertFalse(editFirstCommand.equals(1));
        assertFalse(editFirstCommand.equals(null));
        assertFalse(editFirstCommand.equals(editSecondCommand));
    }

    @Test
    public void execute_editIdentifier_success() {
        Model model = new ModelManager();
        model.setAddressBook(getTypicalAddressBook());
        model.setInventory(getTypicalInventory());

        Product product = model.getFilteredProductList().get(0);

        EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withIdentifier("NEW-ID")
                .build();

        EditProductCommand command =
                new EditProductCommand(product.getIdentifier().value, descriptor);

        Product editedProduct = new ProductBuilder(product)
                .withIdentifier("NEW-ID")
                .build();

        Model expectedModel = new ModelManager();
        expectedModel.setVendorVault(model.getVendorVault());
        expectedModel.setProduct(product, editedProduct);
        expectedModel.updateFilteredProductList(Model.PREDICATE_SHOW_ACTIVE_PRODUCTS);
        String successMessage = String.format(EditProductCommand.MESSAGE_EDIT_PRODUCT_SUCCESS, editedProduct);
        expectedModel.commitVendorVault(successMessage);

        assertCommandSuccess(command, model,
                String.format(EditProductCommand.MESSAGE_EDIT_PRODUCT_SUCCESS,
                        editedProduct),
                expectedModel);
    }

    @Test
    public void execute_duplicateIdentifier_failure() {
        Model model = new ModelManager();
        model.setAddressBook(getTypicalAddressBook());
        model.setInventory(getTypicalInventory());

        Product first = model.getFilteredProductList().get(0);
        Product second = model.getFilteredProductList().get(1);

        EditProductDescriptor descriptor = new EditProductDescriptorBuilder()
                .withIdentifier(second.getIdentifier().value)
                .build();

        EditProductCommand command =
                new EditProductCommand(first.getIdentifier().value, descriptor);

        assertCommandFailure(command, model, Messages.MESSAGE_DUPLICATE_PRODUCT);
    }

    @Test
    public void getPendingConfirmation_returnsPendingConfirmation() {
        EditProductCommand.EditProductDescriptor descriptor =
                new EditProductDescriptorBuilder().withName(VALID_PRODUCT_NAME_IPAD).build();

        EditProductCommand command = new EditProductCommand("SKU-1001", descriptor);

        assertNotNull(command.getPendingConfirmation());
    }

    @Test
    public void equals_sameIdentifierDifferentDescriptor_false() {
        String targetIdentifier = "SKU-1001";

        EditProductDescriptor descriptor1 =
                new EditProductDescriptorBuilder().withName("iPad").build();

        EditProductDescriptor descriptor2 =
                new EditProductDescriptorBuilder().withQuantity("5").build();

        EditProductCommand command1 =
                new EditProductCommand(targetIdentifier, descriptor1);

        EditProductCommand command2 =
                new EditProductCommand(targetIdentifier, descriptor2);

        assertFalse(command1.equals(command2));
    }

    @Test
    public void equals_differentIdentifier_false() {
        EditProductCommand.EditProductDescriptor descriptor =
                new EditProductDescriptorBuilder()
                        .withName(VALID_PRODUCT_NAME_IPAD)
                        .build();

        EditProductCommand command1 =
                new EditProductCommand("SKU-1001", descriptor);

        EditProductCommand command2 =
                new EditProductCommand("SKU-2002", descriptor);

        assertFalse(command1.equals(command2));
    }

    @Test
    public void equals_null_false() {
        EditProductCommand.EditProductDescriptor descriptor =
                new EditProductDescriptorBuilder()
                        .withName(VALID_PRODUCT_NAME_IPAD)
                        .build();

        EditProductCommand command =
                new EditProductCommand("SKU-1001", descriptor);

        assertFalse(command.equals(null));
    }

    @Test
    public void equals_differentType_false() {
        EditProductCommand.EditProductDescriptor descriptor =
                new EditProductDescriptorBuilder()
                        .withName(VALID_PRODUCT_NAME_IPAD)
                        .build();

        EditProductCommand command =
                new EditProductCommand("SKU-1001", descriptor);

        assertFalse(command.equals(5));
    }

    @Test
    public void equals_differentName_false() {
        EditProductDescriptor descriptor1 =
                new EditProductDescriptorBuilder().withName("iPad").withQuantity(VALID_QUANTITY_IPHONE).build();
        EditProductDescriptor descriptor2 =
                new EditProductDescriptorBuilder().withName("iPhone").withQuantity(VALID_QUANTITY_IPHONE).build();
        assertFalse(descriptor1.equals(descriptor2));
    }

    @Test
    public void equals_differentQuantity_false() {
        EditProductDescriptor descriptor1 =
                new EditProductDescriptorBuilder().withName("iPad").withQuantity("5").build();
        EditProductDescriptor descriptor2 =
                new EditProductDescriptorBuilder().withName("iPad").withQuantity("10").build();
        assertFalse(descriptor1.equals(descriptor2));
    }

    @Test
    public void equals_differentThreshold_false() {
        EditProductDescriptor descriptor1 =
                new EditProductDescriptorBuilder().withName("iPad").withThreshold(VALID_THRESHOLD_AIRPODS).build();
        EditProductDescriptor descriptor2 =
                new EditProductDescriptorBuilder().withName("iPad").withThreshold("99").build();
        assertFalse(descriptor1.equals(descriptor2));
    }

    @Test
        public void equals_sameFields_true() {
        EditProductDescriptor descriptor1 =
                new EditProductDescriptorBuilder().withName("iPad").withQuantity(VALID_QUANTITY_IPHONE).build();
        EditProductDescriptor descriptor2 =
                new EditProductDescriptorBuilder().withName("iPad").withQuantity(VALID_QUANTITY_IPHONE).build();
        assertTrue(descriptor1.equals(descriptor2));
    }

    @Test
    public void equals_differentVendorEmail_false() {
        EditProductDescriptor descriptor1 = new EditProductDescriptorBuilder()
                .withoutVendorEmail()
                .build();
        EditProductDescriptor descriptor2 = new EditProductDescriptorBuilder()
                .withName("iPad")
                .build();
        assertFalse(descriptor1.equals(descriptor2));
    }

    @Test
    public void equals_sameVendorEmail_true() {
        EditProductDescriptor descriptor1 = new EditProductDescriptorBuilder()
                .withVendorEmail(VALID_EMAIL_AMY)
                .build();
        EditProductDescriptor descriptor2 = new EditProductDescriptorBuilder()
                .withVendorEmail(VALID_EMAIL_AMY)
                .build();
        assertTrue(descriptor1.equals(descriptor2));
    }

    @Test
    public void equals_sameIdentifier_true() {
        EditProductDescriptor descriptor1 = new EditProductDescriptorBuilder()
                .withIdentifier("SKU-9999")
                .build();
        EditProductDescriptor descriptor2 = new EditProductDescriptorBuilder()
                .withIdentifier("SKU-9999")
                .build();

        assertTrue(descriptor1.equals(descriptor2));
    }

    @Test
    public void equals_differentIdentifierDescriptor_false() {
        EditProductDescriptor descriptor1 = new EditProductDescriptorBuilder()
                .withIdentifier("SKU-1111")
                .build();
        EditProductDescriptor descriptor2 = new EditProductDescriptorBuilder()
                .withIdentifier("SKU-9999")
                .build();
        assertFalse(descriptor1.equals(descriptor2));
    }

    @Test
    public void equals_bothVendorEmailAndIdentifierDiffer_false() {
        EditProductDescriptor descriptor1 = new EditProductDescriptorBuilder()
                .withIdentifier("SKU-1111")
                .withVendorEmail(VALID_EMAIL_AMY)
                .build();
        EditProductDescriptor descriptor2 = new EditProductDescriptorBuilder()
                .withIdentifier("SKU-9999")
                .withVendorEmail(VALID_EMAIL_BOB)
                .build();
        assertFalse(descriptor1.equals(descriptor2));
    }

    @Test
    public void toStringMethod() {
        EditProductDescriptor descriptor =
                new EditProductDescriptorBuilder()
                        .withName(VALID_PRODUCT_NAME_IPAD)
                        .withVendorEmail(VALID_EMAIL_BOB)
                        .build();

        EditProductCommand command =
                new EditProductCommand("SKU-1001", descriptor);

        String expected = EditProductCommand.class.getCanonicalName()
                + "{targetIdentifier=SKU-1001, editProductDescriptor=" + descriptor + "}";

        assertEquals(expected, command.toString());
    }
}
