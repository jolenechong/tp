package seedu.address.testutil;

import static seedu.address.logic.parser.CliSyntax.PREFIX_IDENTIFIER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_QUANTITY;

import seedu.address.logic.commands.AddProductCommand;
import seedu.address.model.product.Product;

/**
 * A utility class for Product.
 */
public class ProductUtil {

    /**
     * Returns an add command string for adding the {@code product}.
     */
    public static String getAddCommand(Product product) {
        return AddProductCommand.COMMAND_WORD + " " + getProductDetails(product);
    }

    /**
     * Returns the part of command string for the given {@code product}'s details.
     */
    public static String getProductDetails(Product product) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_IDENTIFIER + product.getIdentifier().value + " ");
        sb.append(PREFIX_NAME + product.getName().fullName + " ");
        sb.append(PREFIX_QUANTITY + product.getQuantity().toString() + " ");
        return sb.toString();
    }

    /*
    **
     * Returns the part of command string for the given {@code EditProductDescriptor}'s details.
     */
}
