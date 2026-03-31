package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Aliases;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.VendorVault;
import seedu.address.model.product.Identifier;
import seedu.address.model.product.Name;
import seedu.address.model.product.Product;
import seedu.address.model.product.Quantity;
import seedu.address.model.product.RestockThreshold;
import seedu.address.testutil.ProductBuilder;

/**
 * Tests for RestoreProductCommand.
 */
public class RestoreProductCommandTest {

    private Product buildProduct(String id) {
        return new Product(
                new Identifier(id),
                new Name("Coffee"),
                new Quantity("10"),
                new RestockThreshold("5")
        );
    }

    @Test
    public void execute_validIdentifier_success() throws Exception {
        Model model = new ModelManager();
        Product product = buildProduct("p1");
        model.addProduct(product);
        model.archiveProduct(product);

        RestoreProductCommand command = new RestoreProductCommand("p1");
        CommandResult result = command.execute(model);

        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, result.getFeedbackType());
    }

    @Test
    public void execute_validIdentifier_productIsRestored() throws Exception {
        Model model = new ModelManager();
        Product product = buildProduct("p1");
        model.addProduct(product);
        model.archiveProduct(product);

        new RestoreProductCommand("p1").execute(model);

        Product restored = model.getInventory().getProductList().stream()
                .filter(p -> p.getIdentifier().value.equals("p1"))
                .findFirst().get();

        assertFalse(restored.isArchived());
    }

    @Test
    public void execute_noIdentifier_returnsNoneArchived() throws Exception {
        Model model = new ModelManager();

        RestoreProductCommand command = new RestoreProductCommand(null);
        CommandResult result = command.execute(model);

        assertEquals(RestoreProductCommand.MESSAGE_NONE_ARCHIVED, result.getFeedbackToUser());
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, result.getFeedbackType());
    }

    @Test
    public void execute_noIdentifier_returnsListArchived() throws Exception {
        Model model = new ModelManager();
        Product product = buildProduct("p1");
        model.addProduct(product);
        model.archiveProduct(product);

        RestoreProductCommand command = new RestoreProductCommand(null);
        CommandResult result = command.execute(model);

        assertEquals(RestoreProductCommand.MESSAGE_LIST_ARCHIVED, result.getFeedbackToUser());
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, result.getFeedbackType());
    }

    @Test
    public void execute_emptyIdentifier_returnsNoneArchived() throws Exception {
        Model model = new ModelManager();

        RestoreProductCommand command = new RestoreProductCommand("");
        CommandResult result = command.execute(model);

        assertEquals(RestoreProductCommand.MESSAGE_NONE_ARCHIVED, result.getFeedbackToUser());
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, result.getFeedbackType());
    }

    @Test
    public void execute_invalidIdentifier_throwsCommandException() {
        Model model = new ModelManager();

        RestoreProductCommand command = new RestoreProductCommand("invalid");

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_productNotFound_throwsCommandException() {
        Model model = new ModelManager();
        RestoreProductCommand command = new RestoreProductCommand("unknown");

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void restoreProduct_updatesFilteredList() {
        ModelManager model = new ModelManager(new VendorVault(), new UserPrefs(), new Aliases());
        Product product = new ProductBuilder().build();

        model.addProduct(product);
        model.archiveProduct(product);

        Product archivedProduct = model.getInventory().getProductList().get(0);
        model.restoreProduct(archivedProduct);

        assertFalse(model.getFilteredProductList().get(0).isArchived());
    }

    @Test
    public void execute_successMessageStartsWithRestoredProduct() throws Exception {
        Model model = new ModelManager();
        Product product = buildProduct("p1");
        model.addProduct(product);
        model.archiveProduct(product);

        CommandResult result = new RestoreProductCommand("p1").execute(model);

        assertTrue(result.getFeedbackToUser().startsWith("Restored Product:"));
    }
}
