package seedu.address.model;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;

/**
 * Represents the version history of whole of VendorVault.
 */
public class VersionedVendorVault extends VendorVault {

    private final List<VendorVault> vendorVaultStateList;
    private int currentStatePointer;

    /**
     * Creates a VersionedVendorVault with the given initial state.
     *
     * @param vendorVault Initial state of the VendorVault.
     */
    public VersionedVendorVault(ReadOnlyVendorVault vendorVault) {
        this.vendorVaultStateList = new ArrayList<>();
        this.vendorVaultStateList.add(new VendorVault(vendorVault));
        this.currentStatePointer = 0;
    }

    /**
     * Saves a copy of the current VendorVault state to the history.
     */
    public void commit(VendorVault currentState) {
        vendorVaultStateList.subList(currentStatePointer + 1, vendorVaultStateList.size()).clear();

        vendorVaultStateList.add(new VendorVault(currentState));

        currentStatePointer++;
    }

    /**
     * Restores the previous VendorVault state.
     */
    public void undo(VendorVault currentState) {
        if (!canUndo()) {
            throw new IllegalStateException(UndoCommand.MESSAGE_FAILURE);
        }
        currentStatePointer--;
        currentState.resetData(vendorVaultStateList.get(currentStatePointer));
    }

    /**
     * Restores the next VendorVault state after an undo.
     */
    public void redo(VendorVault currentState) {
        if (!canRedo()) {
            throw new IllegalStateException(RedoCommand.MESSAGE_FAILURE);
        }
        currentStatePointer++;
        currentState.resetData(vendorVaultStateList.get(currentStatePointer));
    }

    public boolean canUndo() {
        return currentStatePointer > 0;
    }

    public boolean canRedo() {
        return currentStatePointer < vendorVaultStateList.size() - 1;
    }
}
