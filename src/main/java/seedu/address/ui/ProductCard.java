package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * UI component that represents a product card.
 */
public class ProductCard extends UiPart<HBox> {

    private static final String FXML = "ProductCard.fxml";
    private static final int LOW_STOCK_THRESHOLD = 10;

    public final String product;

    @FXML
    private Label productName;

    @FXML
    private Label quantity;

    /**
     * Creates a ProductCard displaying the specified {@code Product}.
     *
     * @param product Product to display.
     * @param displayedIndex Index shown in the inventory list.
     */
    public ProductCard(String product, int displayedIndex) {
        super(FXML);
        this.product = product;

        String[] parts = product.split(":");
        String name = parts[0];
        int qty = Integer.parseInt(parts[1]);

        productName.setText(displayedIndex + ". " + name);
        quantity.setText("Stock: " + qty);

        if (qty > LOW_STOCK_THRESHOLD) {
            quantity.setStyle("-fx-text-fill: #00ff7f;");
        } else {
            quantity.setStyle("-fx-text-fill: #ff4c4c;");
        }
    }
}
