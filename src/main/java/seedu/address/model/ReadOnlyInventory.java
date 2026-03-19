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
     *
     * @param candidate the product whose name is being compared against the list.
     * @param exclude the product to exclude from the search (may be null).
     * @return Optional Product the matched product (if any).
     */
    Optional<Product> findSimilarNameMatch(Product candidate, Product exclude);

}
