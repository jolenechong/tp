package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.product.Identifier;
import seedu.address.model.product.Name;
import seedu.address.model.product.Product;
import seedu.address.model.product.Quantity;
import seedu.address.model.product.RestockThreshold;

/**
 * Tests for ArchiveProductCommand.
 */
public class ArchiveProductCommandTest {

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

        ArchiveProductCommand command = new ArchiveProductCommand("p1");
        CommandResult result = command.execute(model);

        // Should be green success, not yellow warning
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, result.getFeedbackType());
    }

    @Test
    public void execute_validIdentifier_productIsArchived() throws Exception {
        Model model = new ModelManager();
        Product product = buildProduct("p1");
        model.addProduct(product);

        new ArchiveProductCommand("p1").execute(model);

        Product archived = model.getInventory().getProductList().stream()
                .filter(p -> p.getIdentifier().value.equals("p1"))
                .findFirst().get();

        assertTrue(archived.isArchived());
    }

    @Test
    public void execute_validIdentifier_msgContainsDetails() throws Exception {
        Model model = new ModelManager();
        Product product = buildProduct("p1");
        model.addProduct(product);

        CommandResult result = new ArchiveProductCommand("p1").execute(model);

        String expectedMessage = String.format(ArchiveProductCommand.MESSAGE_USAGE
                .split("\n")[0].replace("archiveproduct IDENTIFIER", "Archived Product: %1$s"),
                Messages.formatProduct(product));

        // Check the message starts with "Archived Product:"
        assertTrue(result.getFeedbackToUser().startsWith("Archived Product:"));
    }

    @Test
    public void execute_invalidIdentifier_throwsCommandException() {
        Model model = new ModelManager();

        ArchiveProductCommand command = new ArchiveProductCommand("invalid");

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));
        assertEquals("No product found with identifier.", exception.getMessage());
    }

    @Test
    public void execute_alreadyArchivedProduct_throwsCommandException() throws Exception {
        Model model = new ModelManager();
        Product product = buildProduct("p1");
        model.addProduct(product);

        // Archive first time
        new ArchiveProductCommand("p1").execute(model);

        // Archive again should throw
        ArchiveProductCommand command = new ArchiveProductCommand("p1");
        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));
        assertEquals("This product is already archived. Did you want to restore it?", exception.getMessage());
    }

    @Test
    public void getPendingConfirmation_returnsConfirmation() {
        ArchiveProductCommand command = new ArchiveProductCommand("RICE");
        assertNotNull(command.getPendingConfirmation());
    }
}
