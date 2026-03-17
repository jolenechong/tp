package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;
import seedu.address.model.product.Product;


/**
 * Wraps all VendorVault data as a single aggregate root.
 */
public class VendorVault implements ReadOnlyVendorVault {

    private final AddressBook addressBook;
    private final Inventory inventory;

    /** Creates an empty VendorVault aggregate. */
    public VendorVault() {
        this.addressBook = new AddressBook();
        this.inventory = new Inventory();
    }

    /**
     * Creates a VendorVault using data in {@code toBeCopied}.
     */
    public VendorVault(ReadOnlyVendorVault toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    /**
     * Creates a VendorVault using data in addressbook and inventory.
     */
    public VendorVault(ReadOnlyAddressBook ab, ReadOnlyInventory inv) {
        requireNonNull(ab);
        requireNonNull(inv);

        this.addressBook = new AddressBook(ab);
        this.inventory = new Inventory(inv);
    }

    /**
     * Resets the existing data of this {@code VendorVault} with {@code newData}.
     */
    public void resetData(ReadOnlyVendorVault newData) {
        requireNonNull(newData);
        addressBook.resetData(newData);
        inventory.resetData(newData);
    }

    /**
     * Replaces only address book data with {@code newData}.
     */
    public void setAddressBook(ReadOnlyAddressBook newData) {
        requireNonNull(newData);
        addressBook.resetData(newData);
    }

    /**
     * Replaces only inventory data with {@code newData}.
     */
    public void setInventory(ReadOnlyInventory newData) {
        requireNonNull(newData);
        inventory.resetData(newData);
    }

    /** Returns the mutable AddressBook backing this aggregate. */
    public AddressBook getAddressBook() {
        return addressBook;
    }

    /** Returns the mutable Inventory backing this aggregate. */
    public Inventory getInventory() {
        return inventory;
    }

    // =============================== AddressBook ===============================

    @Override
    public ObservableList<Person> getPersonList() {
        return addressBook.getPersonList();
    }

    @Override
    public Optional<Person> findSimilarNameMatch(Person candidate, Person exclude) {
        return addressBook.findSimilarNameMatch(candidate, exclude);
    }

    @Override
    public Optional<Person> findSimilarAddressMatch(Person candidate, Person exclude) {
        return addressBook.findSimilarAddressMatch(candidate, exclude);
    }

    // =============================== Inventory ===============================

    @Override
    public ObservableList<Product> getProductList() {
        return inventory.getProductList();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("addressBook", addressBook)
                .add("inventory", inventory)
                .toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof VendorVault)) {
            return false;
        }

        VendorVault otherVendorVault = (VendorVault) other;
        return addressBook.equals(otherVendorVault.addressBook)
                && inventory.equals(otherVendorVault.inventory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressBook, inventory);
    }
}
