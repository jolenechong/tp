package seedu.address.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;
import seedu.address.model.product.Product;

/**
 * Displays the inventory list in the right panel.
 * Supports both filtered and unfiltered views with dynamic layout adjustments.
 */
public class InventoryListPanel extends UiPart<Region> {

    private static final String FXML = "InventoryListPanel.fxml";

    // ── CSS class names ──────────────────────────────────────────────────────
    private static final String STYLE_HEADER_LABEL = "inventory-header-label";
    private static final String STYLE_ID_LABEL = "inventory-id-label";
    private static final String STYLE_NAME_LABEL = "inventory-name-label";
    private static final String STYLE_EMAIL_LABEL = "inventory-email-label";
    private static final String STYLE_CELL = "inventory-list-cell";
    private static final String STYLE_BADGE = "stock-badge";
    private static final String STYLE_BADGE_LOW = "stock-badge-low";
    private static final String STYLE_BADGE_NORMAL = "stock-badge-normal";

    // ── Layout constants ─────────────────────────────────────────────────────
    private static final double COL_SPACING = 16;
    private static final double ROW_PAD_H = 16;
    private static final double ROW_PAD_V = 10;
    private static final double ID_WIDTH = 65;
    private static final double QTY_WIDTH = 95;
    private static final double FLEX_COL_PREF = 120;
    private static final double SCROLLBAR_RESERVE = 18;

    private final BooleanProperty isFiltered = new SimpleBooleanProperty(false);
    private final SortedList<Product> sortedProducts;

    private final Label headerId;
    private final Label headerProduct;
    private final Label headerVendorEmail;
    private final Label headerQty;

    @FXML private ListView<Product> inventoryListView;
    @FXML private VBox statsContainer;
    @FXML private HBox headerRow;

    /**
     * Creates an {@code InventoryListPanel}.
     *
     * @param productList The currently displayed (possibly filtered) product list.
     * @param activePersonList List of active persons.
     * @param allPersonList Full list of persons.
     * @param allProductList Full list of products (used to determine filtering state).
     */
    public InventoryListPanel(ObservableList<Product> productList,
                             ObservableList<Person> activePersonList,
                             ObservableList<Person> allPersonList,
                             ObservableList<Product> allProductList) {
        super(FXML);

        getRoot().getStylesheets().add(
                getClass().getResource("/view/InventoryListPanel.css").toExternalForm());

        InventoryStatsPanel statsPanel = new InventoryStatsPanel(
                productList, activePersonList, allPersonList, allProductList);
        statsContainer.getChildren().setAll(statsPanel.getRoot());

        headerId = makeHeaderLabel("ID");
        applyFixedWidth(headerId, ID_WIDTH);

        headerProduct = makeHeaderLabel("PRODUCT");
        applyFlexWidth(headerProduct);

        headerVendorEmail = makeHeaderLabel("VENDOR");
        applyFlexWidth(headerVendorEmail);

        headerQty = makeHeaderLabel("QUANTITY");
        applyFixedWidth(headerQty, QTY_WIDTH);
        headerQty.setAlignment(Pos.CENTER_LEFT);

        headerRow.setSpacing(COL_SPACING);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setPadding(new Insets(ROW_PAD_V, ROW_PAD_H, ROW_PAD_V, ROW_PAD_H));
        headerRow.getChildren().addAll(headerId, headerProduct, headerVendorEmail, headerQty);

        VBox parentVBox = (VBox) headerRow.getParent();
        headerRow.prefWidthProperty().bind(
                parentVBox.widthProperty().subtract(SCROLLBAR_RESERVE));
        headerRow.maxWidthProperty().bind(
                parentVBox.widthProperty().subtract(SCROLLBAR_RESERVE));

        isFiltered.set(false);

        sortedProducts = new SortedList<>(productList, this::compareInventoryItems);
        inventoryListView.setItems(sortedProducts);
        inventoryListView.setCellFactory(lv -> createInventoryCell());
        inventoryListView.setFocusTraversable(false);
        inventoryListView.setFixedCellSize(Region.USE_COMPUTED_SIZE);

        isFiltered.addListener((obs, was, now) -> {
            updateInventoryComparator(now);
            inventoryListView.setFixedCellSize(Region.USE_COMPUTED_SIZE);
            inventoryListView.refresh();
        });
    }

    /**
     * Sets whether the inventory list is currently in filtered (find) mode.
     * In filtered mode, all text is fully visible (wraps). In unfiltered mode, text is clipped with ellipsis.
     */
    public void setFiltered(boolean filtered) {
        if (isFiltered.get() == filtered) {
            return;
        }
        isFiltered.set(filtered);
    }

    /**
     * Uses model ordering for findproduct; otherwise, keep low-stock prioritization for normal display.
     */
    private void updateInventoryComparator(boolean filtered) {
        if (filtered) {
            sortedProducts.setComparator(null);
            return;
        }
        sortedProducts.setComparator(this::compareInventoryItems);
    }

    /**
     * Applies fixed width constraints to a label.
     */
    private void applyFixedWidth(Label l, double w) {
        l.setMinWidth(w);
        l.setPrefWidth(w);
        l.setMaxWidth(w);
        HBox.setHgrow(l, Priority.NEVER);
    }

    /**
     * Applies flexible width constraints to a label.
     */
    private void applyFlexWidth(Label l) {
        l.setMinWidth(0);
        l.setPrefWidth(FLEX_COL_PREF);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setWrapText(false);
        l.setTextOverrun(OverrunStyle.ELLIPSIS);
        HBox.setHgrow(l, Priority.ALWAYS);
    }

    /**
     * Comparator that prioritizes low-stock items, then sorts by quantity.
     */
    private int compareInventoryItems(Product a, Product b) {
        boolean lowA = a.getQuantity().value <= a.getRestockThreshold().value;
        boolean lowB = b.getQuantity().value <= b.getRestockThreshold().value;

        if (lowA && !lowB) {
            return -1;
        }
        if (!lowA && lowB) {
            return 1;
        }
        return Integer.compare(a.getQuantity().value, b.getQuantity().value);
    }

    /**
     * Creates a custom ListCell for rendering products.
     */
    private ListCell<Product> createInventoryCell() {
        return new ListCell<>() {
            {
                isFiltered.addListener((obs, was, now) -> {
                    if (getItem() != null) {
                        updateItem(getItem(), false);
                    }
                });
                getStyleClass().add(STYLE_CELL);
            }

            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                HBox row = buildRow(item, isFiltered.get());
                row.prefWidthProperty().bind(
                        getListView().widthProperty().subtract(SCROLLBAR_RESERVE));
                row.maxWidthProperty().bind(
                        getListView().widthProperty().subtract(SCROLLBAR_RESERVE));

                setGraphic(row);
                setText(null);
            }
        };
    }

    /**
     * Builds a row representing a single product.
     */
    private HBox buildRow(Product item, boolean filtered) {
        HBox row = new HBox(
                buildIdLabel(item, filtered),
                buildNameLabel(item, filtered),
                buildEmailLabel(item, filtered),
                buildQtyBox(item, filtered)
        );
        row.setSpacing(COL_SPACING);
        row.setAlignment(filtered ? Pos.TOP_LEFT : Pos.CENTER_LEFT);
        row.setPadding(new Insets(ROW_PAD_V, ROW_PAD_H, ROW_PAD_V, ROW_PAD_H));
        if (filtered) {
            row.setMaxHeight(Double.MAX_VALUE);
        }
        return row;
    }

    /**
     * Builds ID label.
     * Unfiltered: single line with ellipsis. Filtered (find): full text wraps.
     */
    private Label buildIdLabel(Product item, boolean filtered) {
        Label label = new Label(item.getIdentifier().toString());
        label.getStyleClass().add(STYLE_ID_LABEL);
        label.setMinWidth(ID_WIDTH);
        label.setPrefWidth(ID_WIDTH);
        label.setMaxWidth(ID_WIDTH);
        HBox.setHgrow(label, Priority.NEVER);

        if (filtered) {
            label.setWrapText(true);
            label.setMaxHeight(Double.MAX_VALUE);
            VBox.setVgrow(label, Priority.NEVER);
        } else {
            label.setWrapText(false);
            label.setMaxHeight(Region.USE_PREF_SIZE);
            label.setTextOverrun(OverrunStyle.ELLIPSIS);
        }

        return label;
    }

    /**
     * Builds product name label with wrapping/ellipsis behavior.
     * Unfiltered (normal list view): single line with trailing ellipsis.
     * Filtered (after find): full text wraps across as many lines as needed.
     */
    private Label buildNameLabel(Product item, boolean filtered) {
        Label label = new Label(item.getName().toString());
        label.getStyleClass().add(STYLE_NAME_LABEL);
        label.setMinWidth(0);
        label.setPrefWidth(FLEX_COL_PREF);
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);

        if (filtered) {
            label.setWrapText(true);
            label.setMinHeight(Region.USE_PREF_SIZE);
            label.setPrefHeight(Region.USE_COMPUTED_SIZE);
            label.setMaxHeight(Double.MAX_VALUE);
        } else {
            label.setWrapText(false);
            label.setMaxHeight(Region.USE_PREF_SIZE);
            label.setTextOverrun(OverrunStyle.ELLIPSIS);
        }

        return label;
    }

    /**
     * Builds vendor email label with wrapping/ellipsis behavior.
     * Unfiltered: single line with trailing ellipsis.
     * Filtered (after find): full text wraps across as many lines as needed.
     */
    private Label buildEmailLabel(Product item, boolean filtered) {
        Label label = new Label(item.getVendorEmail()
                .map(Object::toString)
                .orElse("\u2014"));

        label.getStyleClass().add(STYLE_EMAIL_LABEL);
        label.setMinWidth(0);
        label.setPrefWidth(FLEX_COL_PREF);
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);

        if (filtered) {
            label.setWrapText(true);
            label.setMinHeight(Region.USE_PREF_SIZE);
            label.setPrefHeight(Region.USE_COMPUTED_SIZE);
            label.setMaxHeight(Double.MAX_VALUE);
        } else {
            label.setWrapText(false);
            label.setMaxHeight(Region.USE_PREF_SIZE);
            label.setTextOverrun(OverrunStyle.ELLIPSIS);
        }

        return label;
    }

    /**
     * Builds quantity badge box.
     */
    private HBox buildQtyBox(Product item, boolean filtered) {
        int qty = item.getQuantity().value;
        int threshold = item.getRestockThreshold().value;

        String qtyText = filtered
                ? qty + " (" + threshold + ")"
                : String.valueOf(qty);

        Label badge = new Label(qtyText);
        badge.getStyleClass().addAll(
                STYLE_BADGE,
                qty <= threshold ? STYLE_BADGE_LOW : STYLE_BADGE_NORMAL
        );
        badge.setMinWidth(Region.USE_PREF_SIZE);

        HBox box = new HBox(badge);
        box.setMinWidth(QTY_WIDTH);
        box.setPrefWidth(QTY_WIDTH);
        box.setMaxWidth(QTY_WIDTH);
        box.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(box, Priority.NEVER);

        return box;
    }

    /**
     * Creates a styled header label.
     */
    private Label makeHeaderLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add(STYLE_HEADER_LABEL);
        return l;
    }
}
