package seedu.address.testutil;

import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_IDENTIFIER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_QUANTITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_THRESHOLD;

import seedu.address.logic.commands.AddProductCommand;
import seedu.address.logic.commands.EditProductCommand.EditProductDescriptor;
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
        sb.append(PREFIX_THRESHOLD + product.getRestockThreshold().toString() + " ");
        product.getVendorEmail().ifPresent(email -> sb.append(PREFIX_EMAIL + email.value + " "));
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditProductDescriptor}'s details.
     */
    public static String getEditProductDescriptorDetails(EditProductDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.getName().ifPresent(name -> sb.append(PREFIX_NAME).append(name.fullName).append(" "));
        descriptor.getQuantity().ifPresent(quantity -> sb.append(PREFIX_QUANTITY).append(quantity).append(" "));
        descriptor.getThreshold().ifPresent(threshold -> sb.append(PREFIX_THRESHOLD).append(threshold).append(" "));
        if (descriptor.isVendorEmailEdited()) {
            sb.append(PREFIX_EMAIL);
            descriptor.getVendorEmail().ifPresent(email -> sb.append(email.value));
            sb.append(" ");
        }
        return sb.toString();
    }
}
