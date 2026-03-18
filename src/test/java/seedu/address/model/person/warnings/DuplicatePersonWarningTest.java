package seedu.address.model.person.warnings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class DuplicatePersonWarningTest {

    @Test
    public void constructor_withMessage_getWarningReturnsSameMessage() {
        String warning = "warning message";

        DuplicatePersonWarning duplicatePersonWarning = new DuplicatePersonWarning(warning);

        assertEquals(warning, duplicatePersonWarning.getWarning());
    }

    @Test
    public void constructor_withNull_getWarningReturnsNull() {
        DuplicatePersonWarning duplicatePersonWarning = new DuplicatePersonWarning(null);

        assertNull(duplicatePersonWarning.getWarning());
    }
}

