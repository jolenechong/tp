package seedu.address.ui;

import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import seedu.address.model.product.Product;

/**
 * Displays the inventory list in the right panel.
 */
public class InventoryListPanel extends UiPart<Region> {

    private static final String FXML = "InventoryListPanel.fxml";

    private static final String LOW_STOCK_STYLE =
            "-fx-background-color: #e55d5d;"
            + "-fx-background-radius: 12;"
            + "-fx-padding: 4 12 4 12;"
            + "-fx-text-fill: white;";

    private static final String NORMAL_STOCK_STYLE =
            "-fx-background-color: #4bac80;"
            + "-fx-background-radius: 12;"
            + "-fx-padding: 4 12 4 12;"
            + "-fx-text-fill: white;";

    private static final int ID_WIDTH = 80;
    private static final int QTY_COLUMN_WIDTH = 80;
    private static final int NAME_COLUMN_MIN_WIDTH = 80;

    @FXML
    private ListView<Product> inventoryListView;

    /**
     * Creates an InventoryListPanel using real product data.
     */
    public InventoryListPanel(ObservableList<Product> productList) {
        super(FXML);

        SortedList<Product> sortedInventory =
                new SortedList<>(productList, this::compareInventoryItems);

        inventoryListView.setItems(sortedInventory);
        inventoryListView.setCellFactory(list -> createInventoryCell());
        inventoryListView.setFocusTraversable(false);
    }

    /**
     * Comparator used to sort inventory items.
     * Low-stock items appear first, then sorted by quantity.
     */
    private int compareInventoryItems(Product a, Product b) {
        int qtyA = a.getQuantity().value;
        int qtyB = b.getQuantity().value;
        int thresholdA = a.getRestockThreshold().value;
        int thresholdB = b.getRestockThreshold().value;

        boolean lowA = qtyA <= thresholdA;
        boolean lowB = qtyB <= thresholdB;

        if (lowA && !lowB) {
            return -1;
        }

        if (!lowA && lowB) {
            return 1;
        }

        return Integer.compare(qtyA, qtyB);
    }

    /**
     * Creates a custom cell for displaying inventory rows.
     */
    private ListCell<Product> createInventoryCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                String name = item.getName().toString();
                String id = item.getIdentifier().toString();
                int qty = item.getQuantity().value;
                int threshold = item.getRestockThreshold().value;

                Label idLabel = new Label(id);
                idLabel.setMinWidth(ID_WIDTH);
                idLabel.setMaxWidth(ID_WIDTH);
                idLabel.setTextFill(Color.LIGHTGRAY);

                Label nameLabel = new Label(name);
                nameLabel.setTextFill(Color.WHITE);
                nameLabel.setWrapText(true);
                nameLabel.setMinWidth(NAME_COLUMN_MIN_WIDTH);
                nameLabel.setMaxWidth(600);
                HBox.setHgrow(nameLabel, Priority.ALWAYS);

                Label qtyLabel = new Label(String.valueOf(qty));
                qtyLabel.setAlignment(Pos.CENTER);
                qtyLabel.setMinWidth(40);
                qtyLabel.setMaxWidth(80);
                qtyLabel.setStyle(getStockStyle(qty, threshold));

                HBox qtyCell = new HBox(qtyLabel);
                qtyCell.setPrefWidth(QTY_COLUMN_WIDTH);
                qtyCell.setMinWidth(QTY_COLUMN_WIDTH);
                qtyCell.setMaxWidth(QTY_COLUMN_WIDTH);
                qtyCell.setAlignment(Pos.CENTER_LEFT);

                HBox row = new HBox(idLabel, nameLabel, qtyCell);
                row.setSpacing(20);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-padding: 10 20 10 20;");

                row.prefWidthProperty().bind(getListView().widthProperty().subtract(18));
                row.maxWidthProperty().bind(getListView().widthProperty().subtract(18));

                setGraphic(row);
                setText(null);
            }
        };
    }

    /**
     * Returns the correct style depending on stock level.
     */
    private String getStockStyle(int qty, int threshold) {
        if (qty <= threshold) {
            return LOW_STOCK_STYLE;
        }
        return NORMAL_STOCK_STYLE;
    }
}
