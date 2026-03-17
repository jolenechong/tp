package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditProductCommand.EditProductDescriptor;
import seedu.address.testutil.EditProductDescriptorBuilder;

public class EditProductDescriptorTest {

    @Test
    public void equals() {
        EditProductDescriptor descriptor =
                new EditProductDescriptorBuilder().withName("iPad").build();

        // same object
        assertTrue(descriptor.equals(descriptor));

        // same values
        EditProductDescriptor copy =
                new EditProductDescriptorBuilder(descriptor).build();
        assertTrue(descriptor.equals(copy));

        // null
        assertFalse(descriptor.equals(null));

        // different type
        assertFalse(descriptor.equals(5));
    }
}
