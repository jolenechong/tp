package seedu.address.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * Displays the inventory list in the right panel.
 */
public class InventoryListPanel extends UiPart<Region> {

    private static final String FXML = "InventoryListPanel.fxml";

    private static final int LOW_STOCK_THRESHOLD = 10;

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
    private static final int NAME_WIDTH = 400;
    private static final int QTY_COLUMN_WIDTH = 80;

    @FXML
    private ListView<String> inventoryListView;

    /**
     * Creates an InventoryListPanel with sample inventory data.
     */
    public InventoryListPanel() {
        super(FXML);

        ObservableList<String> fakeInventory = FXCollections.observableArrayList(
                "Product AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA:001:25",
                "Product B:002:5000",
                "Product C:003:999999",
                "Product D:004:3"
        );

        SortedList<String> sortedInventory =
                new SortedList<>(fakeInventory, this::compareInventoryItems);

        inventoryListView.setItems(sortedInventory);
        inventoryListView.setCellFactory(list -> createInventoryCell());
    }

    /**
     * Comparator used to sort inventory items.
     * Low-stock items appear first, then items are sorted by quantity.
     */
    private int compareInventoryItems(String a, String b) {

        String[] pa = a.split(":");
        String[] pb = b.split(":");

        int qtyA = Integer.parseInt(pa[2]);
        int qtyB = Integer.parseInt(pb[2]);

        boolean lowA = qtyA <= LOW_STOCK_THRESHOLD;
        boolean lowB = qtyB <= LOW_STOCK_THRESHOLD;

        if (lowA && !lowB) {
            return -1;
        }

        if (!lowA && lowB) {
            return 1;
        }

        return Integer.compare(qtyA, qtyB);
    }

    /**
     * Creates a custom cell for displaying inventory items.
     */
    private ListCell<String> createInventoryCell() {
        return new ListCell<>() {

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                String[] parts = item.split(":");

                String name = parts[0];
                String id = parts[1];
                int qty = Integer.parseInt(parts[2]);

                Label idLabel = new Label(id);
                idLabel.setPrefWidth(ID_WIDTH);
                idLabel.setTextFill(Color.LIGHTGRAY);

                Label nameLabel = new Label(name);
                nameLabel.setPrefWidth(NAME_WIDTH);
                nameLabel.setTextFill(Color.WHITE);
                nameLabel.setEllipsisString("...");
                nameLabel.setWrapText(false);

                Label qtyLabel = new Label(String.valueOf(qty));
                qtyLabel.setAlignment(Pos.CENTER);
                qtyLabel.setMinWidth(40);
                qtyLabel.setMaxWidth(80);

                qtyLabel.setStyle(getStockStyle(qty));

                HBox qtyCell = new HBox(qtyLabel);
                qtyCell.setPrefWidth(QTY_COLUMN_WIDTH);
                qtyCell.setAlignment(Pos.CENTER_LEFT);

                HBox row = new HBox(idLabel, nameLabel, qtyCell);
                row.setSpacing(20);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-padding: 10 20 10 20;");

                setGraphic(row);
                setText(null);
            }
        };
    }

    /**
     * Returns the correct style depending on stock level.
     */
    private String getStockStyle(int qty) {
        if (qty <= LOW_STOCK_THRESHOLD) {
            return LOW_STOCK_STYLE;
        }
        return NORMAL_STOCK_STYLE;
    }
}
