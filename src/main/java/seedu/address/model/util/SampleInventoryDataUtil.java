package seedu.address.model.util;

import seedu.address.model.Inventory;
import seedu.address.model.product.Identifier;
import seedu.address.model.product.Name;
import seedu.address.model.product.Product;
import seedu.address.model.product.Quantity;

/**
 * Utility class for generating sample inventory data used when the application starts.
 */
public class SampleInventoryDataUtil {

    /**
     * Returns a list of sample products for the inventory.
     */
    public static Product[] getSampleProducts() {
        return new Product[] {
            new Product(new Identifier("P001"), new Name("Milk"), new Quantity("30")),
            new Product(new Identifier("P002"), new Name("Apples"), new Quantity("8")),
            new Product(new Identifier("P003"), new Name("Bread"), new Quantity("150000")),
            new Product(new Identifier("P004"), new Name("Eggs"), new Quantity("5")),
            new Product(new Identifier("P005"), new Name("Cheese"), new Quantity("20")),
            new Product(new Identifier("P006"), new Name("Butter"), new Quantity("12")),
            new Product(new Identifier("P007"), new Name("Yogurt"), new Quantity("3")),
            new Product(new Identifier("P008"), new Name("Orange Juice"), new Quantity("25")),
            new Product(new Identifier("P009"), new Name("Cereal"), new Quantity("10"))
        };
    }

    public static Inventory getSampleInventory() {
        Inventory inventory = new Inventory();
        for (Product p : getSampleProducts()) {
            inventory.addProduct(p);
        }
        return inventory;
    }
}
