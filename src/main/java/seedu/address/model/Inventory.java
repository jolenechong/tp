package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.product.Product;
import seedu.address.model.product.UniqueProductList;

/**
 * Wraps all data at the inventory level
 * Duplicates are not allowed (by .isSameProduct comparison)
 */
public class Inventory implements ReadOnlyInventory {

    private final UniqueProductList products;

    /*
     * The 'unusual' code block below is a non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        products = new UniqueProductList();
    }

    public Inventory() {}

    /**
     * Creates an Inventory using the Products in the {@code toBeCopied}
     */
    public Inventory(ReadOnlyInventory toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the product list with {@code products}.
     * {@code products} must not contain duplicate products.
     */
    public void setProducts(List<Product> products) {
        this.products.setProducts(products);
    }

    /**
     * Resets the existing data of this {@code Inventory} with {@code newData}.
     */
    public void resetData(ReadOnlyInventory newData) {
        requireNonNull(newData);

        setProducts(newData.getProductList());
    }

    //// product-level operations

    /**
     * Returns true if a product with the same identity as {@code product} exists in the inventory.
     */
    public boolean hasProduct(Product product) {
        requireNonNull(product);
        return products.contains(product);
    }

    /**
     * Adds a product to the inventory.
     * The product must not already exist in the inventory.
     */
    public void addProduct(Product p) {
        products.add(p);
    }

    /**
     * Replaces the given product {@code target} in the list with {@code editedProduct}.
     * {@code target} must exist in the inventory.
     * The product identity of {@code editedProduct} must not be the same as another existing product in the inventory.
     */
    public void setProduct(Product target, Product editedProduct) {
        requireNonNull(editedProduct);

        products.setProduct(target, editedProduct);
    }

    /**
     * Removes {@code key} from this {@code Inventory}.
     * {@code key} must exist in the inventory.
     */
    public void removeProduct(Product key) {
        products.remove(key);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("products", products)
                .toString();
    }

    @Override
    public ObservableList<Product> getProductList() {
        return products.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Inventory)) {
            return false;
        }

        Inventory otherInventory = (Inventory) other;
        return products.equals(otherInventory.products);
    }

    @Override
    public int hashCode() {
        return products.hashCode();
    }
}
