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
public class VersionedVendorVault {

    private static final int INITIAL_STATE = 0;
    private static final int HISTORY_STEP = 1;
    private static final Logger logger = LogsCenter.getLogger(VersionedVendorVault.class);

    private final List<VendorVault> vendorVaultStateList; // list elements mutability is intended
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
        this.currentStatePointer = INITIAL_STATE;

        checkInvariant();
    }

    /**
     * Saves a copy of the current VendorVault state to the history.
     */
    public void commit(VendorVault currentState) {
        requireNonNull(currentState);

        // to discard all irrelevant states
        vendorVaultStateList.subList(currentStatePointer + HISTORY_STEP, vendorVaultStateList.size()).clear();
        vendorVaultStateList.add(new VendorVault(currentState));

        currentStatePointer++;

        logger.info("Committed new state. Current state pointer: " + currentStatePointer);
        checkInvariant();
    }

    /**
     * Restores the previous VendorVault state.
     */
    public void undo(VendorVault currentState) {
        requireNonNull(currentState);

        if (!canUndo()) {
            throw new IllegalStateException(UndoCommand.MESSAGE_FAILURE);
        }
        currentStatePointer--;
        currentState.resetData(vendorVaultStateList.get(currentStatePointer));

        logger.info("Undo performed. Current state pointer: " + currentStatePointer);
        checkInvariant();
    }

    /**
     * Restores the next VendorVault state after an undo.
     */
    public void redo(VendorVault currentState) {
        requireNonNull(currentState);

        if (!canRedo()) {
            throw new IllegalStateException(RedoCommand.MESSAGE_FAILURE);
        }
        currentStatePointer++;
        currentState.resetData(vendorVaultStateList.get(currentStatePointer));

        logger.info("Redo performed. Current state pointer: " + currentStatePointer);
        checkInvariant();
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

        assert currentStatePointer >= INITIAL_STATE
                && currentStatePointer < vendorVaultStateList.size()
                : "Invalid state pointer: " + currentStatePointer;
    }

}
