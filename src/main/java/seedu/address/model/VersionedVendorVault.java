package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;

/**
 * Represents the version history of whole of VendorVault.
 */
public class VersionedVendorVault extends VendorVault {
    // Solution inspired by https://se-education.org/addressbook-level3/DeveloperGuide.html#proposed-undoredo-feature

    private static final int INITIAL_STATE = 0;
    private static final int HISTORY_STEP = 1;
    private static final Logger logger = LogsCenter.getLogger(VersionedVendorVault.class);

    private final List<VendorVault> vendorVaultStateList; // list elements mutability is intended
    private final List<String> stateActionSummaryList;
    private int currentStatePointer;

    /**
     * Creates a VersionedVendorVault with the given initial state.
     *
     * @param vendorVault Initial state of the VendorVault.
     */
    public VersionedVendorVault(ReadOnlyVendorVault vendorVault) {
        requireNonNull(vendorVault);

        this.vendorVaultStateList = new ArrayList<>();
        this.vendorVaultStateList.add(new VendorVault(vendorVault));
        this.stateActionSummaryList = new ArrayList<>();
        this.stateActionSummaryList.add("");
        this.currentStatePointer = INITIAL_STATE;

        checkInvariant();
    }

    /**
     * Saves a copy of the current VendorVault state to the history with an action summary.
     */
    public void commit(VendorVault currentState, String actionSummary) {
        requireNonNull(currentState);
        requireNonNull(actionSummary);

        // to discard all irrelevant states
        vendorVaultStateList.subList(currentStatePointer + HISTORY_STEP, vendorVaultStateList.size()).clear();
        stateActionSummaryList.subList(currentStatePointer + HISTORY_STEP, stateActionSummaryList.size()).clear();
        vendorVaultStateList.add(new VendorVault(currentState));
        stateActionSummaryList.add(actionSummary);

        currentStatePointer++;

        logger.info("Committed new state. Current state pointer: " + currentStatePointer);
        checkInvariant();
    }

    /**
     * Restores the previous VendorVault state and returns the summary of what was undone.
     */
    public String undo(VendorVault currentState) {
        requireNonNull(currentState);

        if (!canUndo()) {
            throw new IllegalStateException(UndoCommand.MESSAGE_FAILURE);
        }
        String undoneActionSummary = stateActionSummaryList.get(currentStatePointer);
        currentStatePointer--;
        currentState.resetData(vendorVaultStateList.get(currentStatePointer));

        logger.info("Undo performed. Current state pointer: " + currentStatePointer);
        checkInvariant();
        return undoneActionSummary;
    }

    /**
     * Restores the next VendorVault state after an undo and returns the summary of what was redone.
     */
    public String redo(VendorVault currentState) {
        requireNonNull(currentState);

        if (!canRedo()) {
            throw new IllegalStateException(RedoCommand.MESSAGE_FAILURE);
        }
        currentStatePointer++;
        currentState.resetData(vendorVaultStateList.get(currentStatePointer));

        logger.info("Redo performed. Current state pointer: " + currentStatePointer);
        checkInvariant();
        return stateActionSummaryList.get(currentStatePointer);
    }

    public boolean canUndo() {
        return currentStatePointer > INITIAL_STATE;
    }

    public boolean canRedo() {
        return currentStatePointer < vendorVaultStateList.size() - HISTORY_STEP;
    }

    /**
     * Checks the internal invariants of this VersionedVendorVault.
     */
    private void checkInvariant() {
        assert !vendorVaultStateList.isEmpty()
                : "History must never be empty";

        assert vendorVaultStateList.size() == stateActionSummaryList.size()
                : "State history and summary history must stay aligned";

        assert currentStatePointer >= INITIAL_STATE
                && currentStatePointer < vendorVaultStateList.size()
                : "Invalid state pointer: " + currentStatePointer;
    }

}
