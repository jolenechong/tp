package seedu.address.model;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.commands.UndoCommand;

/**
 * Represents a VendorVault that keeps track AddressBook version history.
 */
public class VersionedVendorVault {

    private final List<AddressBook> vaultStateList;
    private int currentStatePointer;

    /**
     * Creates a VersionedVendorVault with the given initial state.
     *
     * @param initial Initial state of the VendorVault.
     */
    public VersionedVendorVault(ReadOnlyAddressBook initial) {
        this.vaultStateList = new ArrayList<>();
        this.vaultStateList.add(new AddressBook(initial));
        this.currentStatePointer = 0;
    }

    /**
     * Saves a copy of the current VendorVault state to the history.
     */
    public void commit(AddressBook currentState) {
        vaultStateList.subList(currentStatePointer + 1, vaultStateList.size()).clear();

        vaultStateList.add(new AddressBook(currentState));

        currentStatePointer++;
    }

    /**
     * Restores the previous VendorVault state.
     */
    public void undo(AddressBook currentState) {
        if (!canUndo()) {
            throw new IllegalStateException(UndoCommand.MESSAGE_FAILURE);
        }
        currentStatePointer--;
        currentState.resetData(vaultStateList.get(currentStatePointer));
    }

    /**
     * Restores the next VendorVault state after an undo.
     */
    public void redo(AddressBook currentState) {
        if (!canRedo()) {
            // TODO: Replace this when RedoCommand is implemented
            throw new IllegalStateException(UndoCommand.MESSAGE_FAILURE);
        }
        currentStatePointer++;
        currentState.resetData(vaultStateList.get(currentStatePointer));
    }

    public boolean canUndo() {
        return currentStatePointer > 0;
    }

    public boolean canRedo() {
        return currentStatePointer < vaultStateList.size() - 1;
    }
}
