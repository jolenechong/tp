package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.product.Identifier;
import seedu.address.model.product.Name;
import seedu.address.model.product.Product;
import seedu.address.model.product.Quantity;

public class RestoreProductCommandTest {

    @Test
    public void execute_validIdentifier_success() throws Exception {

        Model model = new ModelManager();

        Product product = new Product(
                new Identifier("p1"),
                new Name("Coffee"),
                new Quantity("10")
        );

        model.addProduct(product);
        model.archiveProduct(product);

        RestoreProductCommand command = new RestoreProductCommand("p1");

        CommandResult result = command.execute(model);

        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, result.getFeedbackType());
    }

    @Test
    public void execute_invalidIdentifier_throwsCommandException() {

        Model model = new ModelManager();

        RestoreProductCommand command = new RestoreProductCommand("invalid");

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_missingIdentifier_throwsCommandException() {
        Model model = new ModelManager();

        RestoreProductCommand command = new RestoreProductCommand("");

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_productNotFound_throwsCommandException() {
        Model model = new ModelManager();
        RestoreProductCommand command = new RestoreProductCommand("unknown");

        assertThrows(CommandException.class, () -> command.execute(model));
    }
}
