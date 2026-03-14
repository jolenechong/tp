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
import seedu.address.model.product.RestockThreshold;

public class ArchiveProductCommandTest {

    @Test
    public void execute_validIdentifier_success() throws Exception {

        Model model = new ModelManager();

        Product product = new Product(
                new Identifier("p1"),
                new Name("Coffee"),
                new Quantity("10"),
                new RestockThreshold("5")
        );

        model.addProduct(product);

        ArchiveProductCommand command = new ArchiveProductCommand("p1");

        CommandResult result = command.execute(model);

        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_invalidIdentifier_throwsCommandException() {

        Model model = new ModelManager();

        ArchiveProductCommand command = new ArchiveProductCommand("invalid");

        assertThrows(CommandException.class, () -> command.execute(model));
    }
}
