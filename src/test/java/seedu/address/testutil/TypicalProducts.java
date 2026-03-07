package seedu.address.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.model.product.Product;

/**
 * A utility class containing a list of {@code Product} objects to be used in
 * tests.
 */
public class TypicalProducts {

    public static final Product RICE = new ProductBuilder().withIdentifier("SKU-1001")
            .withName("Brown Rice 5kg").withQuantity("24").build();
    public static final Product OIL = new ProductBuilder().withIdentifier("SKU-1002")
            .withName("Cooking Oil 2L").withQuantity("18").build();
    public static final Product EGGS = new ProductBuilder().withIdentifier("SKU-1003")
            .withName("Eggs Tray 30").withQuantity("36").build();
    public static final Product SUGAR = new ProductBuilder().withIdentifier("SKU-1004")
            .withName("Sugar 1kg").withQuantity("42").build();
    public static final Product NOODLES = new ProductBuilder().withIdentifier("SKU-1005")
            .withName("Instant Noodles Chicken").withQuantity("80").build();
    public static final Product CUPS = new ProductBuilder().withIdentifier("SKU-1006")
            .withName("Paper Cups 50 Pack").withQuantity("15").build();
    public static final Product WATER = new ProductBuilder().withIdentifier("SKU-1007")
            .withName("Bottled Water 500ml").withQuantity("120").build();

    // TODO: Manually added - Product's details found in {@code CommandTestUtil}

    private TypicalProducts() {
    } // prevents instantiation

    /*
     **
     * Returns an {@code Inventory} with all the typical products.
     *
     * public static Inventory getTypicalInventory() {
     * Inventory ab = new Inventory();
     * for (Product product : getTypicalProducts()) {
     * ab.addProduct(product);
     * }
     * return ab;
     * }
     */

    public static List<Product> getTypicalProducts() {
        return new ArrayList<>(Arrays.asList(RICE, OIL, EGGS, SUGAR, NOODLES, CUPS, WATER));
    }
}
