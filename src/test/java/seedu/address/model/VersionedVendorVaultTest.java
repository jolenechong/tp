package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class VersionedVendorVaultTest {

    private VendorVault vendorVault;
    private VersionedVendorVault versionedVendorVault;

    @BeforeEach
    public void setUp() {
        vendorVault = new VendorVault();
        versionedVendorVault = new VersionedVendorVault(vendorVault);
    }

    @Test
    public void commit_addNewState_pointerMovesForward() {
        VendorVault newState = new VendorVault();
        versionedVendorVault.commit(newState, "");

        assertTrue(versionedVendorVault.canUndo()); // should now be able to undo
    }

    @Test
    public void undo_afterCommit_restoresPreviousState() {
        // initial commit
        VendorVault state1 = new VendorVault();
        state1.getAddressBook().addPerson(new PersonBuilder().build());
        versionedVendorVault.commit(state1, "");

        // commit second state
        VendorVault state2 = new VendorVault();
        state2.getAddressBook().addPerson(new PersonBuilder().withName("John").build());
        versionedVendorVault.commit(state2, "");

        // undo to previous state
        versionedVendorVault.undo(state2);

        // state2 should now match state1
        assertEquals(state1, state2);
        assertTrue(versionedVendorVault.canRedo());
    }

    @Test
    public void undoWithSummary_afterSummaryCommit_returnsCommittedSummary() {
        VendorVault state = new VendorVault();
        String actionSummary = "New contact added: Alice Pauline";

        versionedVendorVault.commit(state, actionSummary);

        assertEquals(actionSummary, versionedVendorVault.undo(state));
    }

    @Test
    public void redo_afterUndo_restoresNextState() {
        // initial commit
        VendorVault state1 = new VendorVault();
        state1.getAddressBook().addPerson(new PersonBuilder().build());
        versionedVendorVault.commit(state1, "");

        // commit second state
        VendorVault state2 = new VendorVault();
        state2.getAddressBook().addPerson(new PersonBuilder().withName("John").build());
        versionedVendorVault.commit(state2, "");
        VendorVault expectedStateAfterRedo = new VendorVault(state2);

        // undo
        versionedVendorVault.undo(state2);

        // redo
        versionedVendorVault.redo(state2);

        assertEquals(expectedStateAfterRedo, state2);
    }

    @Test
    public void redoWithSummary_afterUndo_returnsSummaryOfRedoneState() {
        VendorVault state = new VendorVault();
        String actionSummary = "New contact added: Alice Pauline";

        versionedVendorVault.commit(state, actionSummary);
        versionedVendorVault.undo(state);

        assertEquals(actionSummary, versionedVendorVault.redo(state));
    }

    @Test
    public void undo_atInitialState_throwsException() {
        assertThrows(IllegalStateException.class, () -> versionedVendorVault.undo(vendorVault));
    }

    @Test
    public void redo_atLatestState_throwsException() {
        assertThrows(IllegalStateException.class, () -> versionedVendorVault.redo(vendorVault));
    }
}
