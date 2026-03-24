package seedu.address.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
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
 */
public class InventoryListPanel extends UiPart<Region> {

    private static final String FXML = "InventoryListPanel.fxml";

    // ── CSS class names (must match InventoryListPanel.css) ───────────────────
    private static final String STYLE_HEADER_LABEL = "inventory-header-label";
    private static final String STYLE_ID_LABEL = "inventory-id-label";
    private static final String STYLE_NAME_LABEL = "inventory-name-label";
    private static final String STYLE_EMAIL_LABEL = "inventory-email-label";
    private static final String STYLE_CELL = "inventory-list-cell";
    private static final String STYLE_BADGE = "stock-badge";
    private static final String STYLE_BADGE_LOW = "stock-badge-low";
    private static final String STYLE_BADGE_NORMAL = "stock-badge-normal";

    // ── Layout constants ──────────────────────────────────────────────────────
    private static final double COL_SPACING = 16;
    private static final double ROW_PAD_H = 16;
    private static final double ROW_PAD_V = 10;
    private static final double ID_WIDTH_UNFILTERED = 90;
    private static final double ID_WIDTH_FILTERED = 70;
    private static final double QTY_WIDTH = 80;
    private static final double PRODUCT_MIN = 100;
    private static final double PRODUCT_MIN_FILTERED = 160;
    private static final double EMAIL_MIN = 100;
    private static final double SCROLLBAR_RESERVE = 18;

    private final BooleanProperty isFiltered = new SimpleBooleanProperty(false);

    private final Label headerId;
    private final Label headerProduct;
    private final Label headerVendorEmail;
    private final Label headerQty;
    private final Region headerSpacer;

    @FXML private ListView<Product> inventoryListView;
    @FXML private VBox statsContainer;
    @FXML private HBox headerRow;

    /**
     * Creates an {@code InventoryListPanel}.
     *
     * @param productList List of currently displayed products
     * @param activePersonList List of active persons
     * @param allPersonList List of all persons
     * @param allProductList List of all products
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
        headerId.setMinWidth(ID_WIDTH_UNFILTERED);
        headerId.setPrefWidth(ID_WIDTH_UNFILTERED);
        headerId.setMaxWidth(ID_WIDTH_UNFILTERED);

        headerProduct = makeHeaderLabel("PRODUCT");
        headerProduct.setMinWidth(PRODUCT_MIN);

        headerSpacer = new Region();
        headerSpacer.setMinWidth(0);

        headerVendorEmail = makeHeaderLabel("VENDOR");
        headerVendorEmail.setMinWidth(EMAIL_MIN);

        headerQty = makeHeaderLabel("QUANTITY");
        headerQty.setMinWidth(QTY_WIDTH);
        headerQty.setPrefWidth(QTY_WIDTH);
        headerQty.setMaxWidth(QTY_WIDTH);
        headerQty.setAlignment(Pos.CENTER_LEFT);

        headerRow.setSpacing(COL_SPACING);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setPadding(new Insets(ROW_PAD_V, ROW_PAD_H, ROW_PAD_V, ROW_PAD_H));
        headerRow.getChildren().addAll(
                headerId, headerProduct, headerSpacer, headerVendorEmail, headerQty);

        isFiltered.set(productList.size() < allProductList.size());
        applyColumnState(isFiltered.get());

        productList.addListener((ListChangeListener<Product>) c ->
                isFiltered.set(productList.size() < allProductList.size()));

        isFiltered.addListener((obs, was, now) -> {
            applyColumnState(now);
            inventoryListView.refresh();
        });

        inventoryListView.widthProperty().addListener((obs, old, w) ->
                syncHeaderWidth(w.doubleValue()));

        SortedList<Product> sorted =
                new SortedList<>(productList, this::compareInventoryItems);
        inventoryListView.setItems(sorted);
        inventoryListView.setCellFactory(lv -> createInventoryCell());
        inventoryListView.setFocusTraversable(false);
    }

    // ─────────────────────────────────────────────────────────────────────────

    private void syncHeaderWidth(double listViewWidth) {
        double w = listViewWidth - SCROLLBAR_RESERVE;
        headerRow.setPrefWidth(w);
        headerRow.setMaxWidth(w);
    }

    private void applyColumnState(boolean filtered) {
        if (filtered) {
            headerId.setMinWidth(ID_WIDTH_FILTERED);
            headerId.setPrefWidth(ID_WIDTH_FILTERED);
            headerId.setMaxWidth(ID_WIDTH_FILTERED);

            headerSpacer.setVisible(false);
            headerSpacer.setManaged(false);
            HBox.setHgrow(headerSpacer, null);

            headerVendorEmail.setVisible(true);
            headerVendorEmail.setManaged(true);
            HBox.setHgrow(headerVendorEmail, Priority.ALWAYS);
            headerVendorEmail.setMaxWidth(Double.MAX_VALUE);

            HBox.setHgrow(headerProduct, Priority.NEVER);
            headerProduct.setPrefWidth(PRODUCT_MIN_FILTERED);
            headerProduct.setMaxWidth(PRODUCT_MIN_FILTERED * 2);
        } else {
            headerId.setMinWidth(ID_WIDTH_UNFILTERED);
            headerId.setPrefWidth(ID_WIDTH_UNFILTERED);
            headerId.setMaxWidth(ID_WIDTH_UNFILTERED);

            headerSpacer.setVisible(true);
            headerSpacer.setManaged(true);
            HBox.setHgrow(headerSpacer, Priority.ALWAYS);

            headerVendorEmail.setVisible(false);
            headerVendorEmail.setManaged(false);
            HBox.setHgrow(headerVendorEmail, null);

            HBox.setHgrow(headerProduct, Priority.NEVER);
            headerProduct.setPrefWidth(PRODUCT_MIN);
            headerProduct.setMaxWidth(Double.MAX_VALUE);
        }
    }

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

    // ── Cell factory ──────────────────────────────────────────────────────────

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

                boolean filtered = isFiltered.get();
                HBox row = buildRow(item, filtered);
                row.prefWidthProperty().bind(
                        getListView().widthProperty().subtract(SCROLLBAR_RESERVE));
                row.maxWidthProperty().bind(
                        getListView().widthProperty().subtract(SCROLLBAR_RESERVE));

                setGraphic(row);
                setText(null);
            }
        };
    }

    // ── Row builders ──────────────────────────────────────────────────────────

    private HBox buildRow(Product item, boolean filtered) {
        Label idLabel = buildIdLabel(item, filtered);
        Label nameLabel = buildNameLabel(item, filtered);
        HBox qtyBox = buildQtyBox(item, filtered);

        HBox row;
        if (filtered) {
            row = new HBox(idLabel, nameLabel, buildEmailLabel(item), qtyBox);
        } else {
            Region spacer = new Region();
            spacer.setMinWidth(0);
            HBox.setHgrow(spacer, Priority.ALWAYS);
            row = new HBox(idLabel, nameLabel, spacer, qtyBox);
        }

        row.setSpacing(COL_SPACING);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(ROW_PAD_V, ROW_PAD_H, ROW_PAD_V, ROW_PAD_H));
        return row;
    }

    private Label buildIdLabel(Product item, boolean filtered) {
        double idW = filtered ? ID_WIDTH_FILTERED : ID_WIDTH_UNFILTERED;
        Label label = new Label(item.getIdentifier().toString());
        label.setMinWidth(idW);
        label.setPrefWidth(idW);
        label.setMaxWidth(idW);
        label.getStyleClass().add(STYLE_ID_LABEL);
        if (filtered) {
            label.setWrapText(true);
        } else {
            label.setTextOverrun(OverrunStyle.ELLIPSIS);
        }
        return label;
    }

    private Label buildNameLabel(Product item, boolean filtered) {
        Label label = new Label(item.getName().toString());
        label.setMinWidth(PRODUCT_MIN);
        label.getStyleClass().add(STYLE_NAME_LABEL);
        if (filtered) {
            label.setWrapText(true);
            label.setPrefWidth(PRODUCT_MIN_FILTERED);
            label.setMaxWidth(PRODUCT_MIN_FILTERED);
            HBox.setHgrow(label, Priority.NEVER);
        } else {
            label.setWrapText(false);
            label.setTextOverrun(OverrunStyle.ELLIPSIS);
            label.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(label, Priority.NEVER);
        }
        return label;
    }

    private Label buildEmailLabel(Product item) {
        Label label = new Label(item.getVendorEmail().map(Object::toString).orElse("\u2014"));
        label.setMinWidth(EMAIL_MIN);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setWrapText(true);
        label.getStyleClass().add(STYLE_EMAIL_LABEL);
        HBox.setHgrow(label, Priority.ALWAYS);
        return label;
    }

    private HBox buildQtyBox(Product item, boolean filtered) {
        int qty = item.getQuantity().value;
        int threshold = item.getRestockThreshold().value;
        String qtyText = filtered
                ? qty + " (" + threshold + ")"
                : String.valueOf(qty);

        Label badge = new Label(qtyText);
        badge.getStyleClass().addAll(STYLE_BADGE,
                qty <= threshold ? STYLE_BADGE_LOW : STYLE_BADGE_NORMAL);

        HBox box = new HBox(badge);
        box.setMinWidth(QTY_WIDTH);
        box.setPrefWidth(QTY_WIDTH);
        box.setMaxWidth(QTY_WIDTH);
        box.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(box, Priority.NEVER);
        return box;
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private Label makeHeaderLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add(STYLE_HEADER_LABEL);
        return l;
    }
}
