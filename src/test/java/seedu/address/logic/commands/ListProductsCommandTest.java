package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.VendorVault;

public class ListProductsCommandTest {

    @Test
    public void execute_listProducts_success() {
        Model model = new ModelManager(new VendorVault(), new UserPrefs());
        ListProductsCommand command = new ListProductsCommand();

        CommandResult result = command.execute(model);

        assertEquals(ListProductsCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
    }

    @Test
    public void execute_nullModel_throwsException() {
        ListProductsCommand command = new ListProductsCommand();

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void getPendingConfirmation_returnsObject() {
        ListProductsCommand command = new ListProductsCommand();

        assertNotNull(command.getPendingConfirmation());
    }
}
