package seedu.address.storage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.Inventory;
import seedu.address.model.ReadOnlyInventory;
import seedu.address.model.product.Product;

@JsonRootName(value = "inventory")
class JsonSerializableInventory {

    public static final String MESSAGE_DUPLICATE_PRODUCT = "Products list contains duplicate product id(s).";


    private final List<JsonAdaptedProduct> products = new ArrayList<>();

    @JsonCreator
    public JsonSerializableInventory(@JsonProperty("products") List<JsonAdaptedProduct> products) {
        this.products.addAll(products);
    }

    public JsonSerializableInventory(ReadOnlyInventory source) {
        products.addAll(source.getProductList().stream().map(JsonAdaptedProduct::new).collect(Collectors.toList()));
    }

    public Inventory toModelType() throws IllegalValueException {
        Inventory inventory = new Inventory();
        for (JsonAdaptedProduct jsonAdaptedProduct : products) {
            Product product = jsonAdaptedProduct.toModelType();
            if (inventory.hasProduct(product)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PRODUCT);
            }
            inventory.addProduct(product);
        }

        return inventory;
    }

    List<String> findDuplicateIdentifiers() {
        Map<String, Integer> counts = new LinkedHashMap<>();

        for (JsonAdaptedProduct product : products) {
            String identifier = product.getIdentifier();
            if (identifier == null) {
                continue;
            }
            counts.merge(identifier, 1, Integer::sum);
        }

        return counts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
