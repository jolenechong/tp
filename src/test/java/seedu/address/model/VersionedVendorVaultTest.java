package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class VersionedVendorVaultTest {

    private static final String EMPTY_SUMMARY = "";
    private static final String SAMPLE_ACTION_SUMMARY = "New contact added: Alice Pauline";
    private static final String SECOND_PERSON_NAME = "John";

    private VendorVault vendorVault;
    private VersionedVendorVault versionedVendorVault;

    @BeforeEach
    public void setUp() {
        vendorVault = new VendorVault();
        versionedVendorVault = new VersionedVendorVault(vendorVault);
    }

    // ======================== commit ========================

    @Test
    public void commit_addNewState_pointerMovesForward() {
        // EP: any commit from initial state should enable undo
        versionedVendorVault.commit(new VendorVault(), EMPTY_SUMMARY);

        assertTrue(versionedVendorVault.canUndo());
    }

    // ======================== undo ========================

    @Test
    public void undo_afterTwoCommits_restoresPreviousState() {
        // EP: undo from a non-initial state (typical case)
        VendorVault state1 = buildStateWith(new PersonBuilder().build());
        VendorVault state2 = buildStateWith(new PersonBuilder().withName(SECOND_PERSON_NAME).build());
        commitTwoStates(state1, state2);

        versionedVendorVault.undo(state2);

        assertEquals(state1, state2);
        assertTrue(versionedVendorVault.canRedo());
    }

    @Test
    public void undoWithSummary_afterSummaryCommit_returnsCommittedSummary() {
        // EP: undo should return the action summary of the undone state
        VendorVault state = new VendorVault();
        versionedVendorVault.commit(state, SAMPLE_ACTION_SUMMARY);

        assertEquals(SAMPLE_ACTION_SUMMARY, versionedVendorVault.undo(state));
    }

    @Test
    public void undo_atInitialState_throwsException() {
        assertThrows(IllegalStateException.class, () -> versionedVendorVault.undo(vendorVault));
    }

    // ======================== redo ========================

    @Test
    public void redo_afterUndo_restoresNextState() {
        // EP: redo from a state that was previously undone (typical case)
        VendorVault state1 = buildStateWith(new PersonBuilder().build());
        VendorVault state2 = buildStateWith(new PersonBuilder().withName(SECOND_PERSON_NAME).build());
        commitTwoStates(state1, state2);
        VendorVault expectedStateAfterRedo = new VendorVault(state2);

        versionedVendorVault.undo(state2);
        versionedVendorVault.redo(state2);

        assertEquals(expectedStateAfterRedo, state2);
    }

    @Test
    public void redoWithSummary_afterUndo_returnsSummaryOfRedoneState() {
        // EP: redo should return the action summary of the restored state
        VendorVault state = new VendorVault();
        versionedVendorVault.commit(state, SAMPLE_ACTION_SUMMARY);
        versionedVendorVault.undo(state);

        assertEquals(SAMPLE_ACTION_SUMMARY, versionedVendorVault.redo(state));
    }

    @Test
    public void redo_atLatestState_throwsException() {
        assertThrows(IllegalStateException.class, () -> versionedVendorVault.redo(vendorVault));
    }

    // ======================== helpers ========================

    /**
     * Commits two states sequentially into {@code versionedVendorVault}.
     * Used to set up a two-entry history without repeating boilerplate across tests.
     */
    private void commitTwoStates(VendorVault first, VendorVault second) {
        versionedVendorVault.commit(first, EMPTY_SUMMARY);
        versionedVendorVault.commit(second, EMPTY_SUMMARY);
    }

    /**
     * Returns a new {@code VendorVault} with the given person added to its address book.
     */
    private VendorVault buildStateWith(Person person) {
        VendorVault state = new VendorVault();
        state.getAddressBook().addPerson(person);
        return state;
    }
}
