package seedu.address.model;

import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.model.product.Product;

/**
 * Unmodifiable view of an inventory
 */
public interface ReadOnlyInventory {

    /**
     * Returns an unmodifiable view of the products list.
     * This list will not contain any duplicate products.
     */
    ObservableList<Product> getProductList();

    /**
     * Returns the first product in the list whose name is similar to {@code candidate},
     * excluding {@code exclude} (may be null).
     */
    Optional<Product> findSimilarNameMatch(Product candidate, Product exclude);

}
