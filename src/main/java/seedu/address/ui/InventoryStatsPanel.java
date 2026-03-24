package seedu.address.ui;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import seedu.address.model.person.Person;
import seedu.address.model.product.Product;

/**
 * A UI component that displays inventory-related statistics.
 *
 * This panel contains:
 * - Two stat cards showing active contacts and active products
 * - Mini donut charts for visualising proportions
 * - A main donut chart showing stock status (in stock vs low stock)
 *
 * The layout is defined in {@code InventoryStatsPanel.fxml}
 * and styling is handled in {@code InventoryStatsPanel.css}.
 *
 * Mini donuts are drawn manually using {@link Arc} and {@link Circle}
 * as {@link PieChart} is not suitable for small compact visualisations.
 */
public class InventoryStatsPanel extends UiPart<Region> {

    private static final String FXML = "InventoryStatsPanel.fxml";

    // ── CSS classes ─────────────────────────────
    private static final String CHART_COLORS =
        "-fx-background-color: transparent;"
        + "CHART_COLOR_1: #4bac80;"
        + "CHART_COLOR_2: #e55d5d;";


    private static final String STYLE_MINI_DONUT_BG = "mini-donut-bg";
    private static final String STYLE_MINI_DONUT_ACTIVE = "mini-donut-active";

    private static final String STYLE_DONUT_IN = "donut-in";
    private static final String STYLE_DONUT_LOW = "donut-low";

    private static final String STYLE_DONUT_BG = "donut-bg";
    private static final String STYLE_CHART_IN = "chart-slice-in";
    private static final String STYLE_CHART_LOW = "chart-slice-low";

    // ── Geometry constants ─────────────────────

    private static final double START_ANGLE = 90;
    private static final double FULL_CIRCLE = 360.0;
    private static final double CENTER = 24;

    private static final double OUTER_RADIUS = 22;
    private static final double INNER_RADIUS = 13;
    private static final double STROKE_WIDTH = OUTER_RADIUS - INNER_RADIUS;
    private static final double RADIUS = (OUTER_RADIUS + INNER_RADIUS) / 2.0;

    // ── Font scaling constants ─────────────────

    private static final double TITLE_SCALE = 0.065;
    private static final double VALUE_SCALE = 0.12;

    private static final double TITLE_MIN = 10;
    private static final double TITLE_MAX = 14;

    private static final double VALUE_MIN = 16;
    private static final double VALUE_MAX = 26;

    // ── FXML components ────────────────────────

    @FXML private Label contactTitle;
    @FXML private Label contactValue;
    @FXML private Label contactTotal;
    @FXML private Label productTitle;
    @FXML private Label productValue;
    @FXML private Label productTotal;
    @FXML private Pane contactMiniChart;
    @FXML private Pane productMiniChart;
    @FXML private VBox lhsCards;
    @FXML private PieChart pieChart;
    @FXML private Circle donutHole;
    @FXML private Label countLabel;
    @FXML private Label legendInLabel;
    @FXML private Label legendLowLabel;
    @FXML private Circle dotIn;
    @FXML private Circle dotLow;

    /**
     * Creates an {@code InventoryStatsPanel}.
     *
     * @param filteredProducts the currently displayed (filtered) products
     * @param activePersons the currently displayed (filtered) persons
     * @param allPersons the full list of persons (for totals)
     * @param allProducts the full list of products (for totals)
     */
    public InventoryStatsPanel(ObservableList<Product> filteredProducts,
                               ObservableList<Person> activePersons,
                               ObservableList<Person> allPersons,
                               ObservableList<Product> allProducts) {
        super(FXML);
        initMainDonut();
        bindTitleFontSize();
        bindData(filteredProducts, activePersons, allPersons, allProducts);
    }

    /**
     * Binds font sizes of titles and values to container width,
     * ensuring responsive scaling instead of truncation.
     */
    private void bindTitleFontSize() {
        var titleFontBinding = Bindings.createObjectBinding(()
            -> javafx.scene.text.Font.font(
                "System",
                javafx.scene.text.FontWeight.BOLD,
                Math.max(TITLE_MIN,
                    Math.min(TITLE_MAX, lhsCards.getWidth() * TITLE_SCALE))
            ),
            lhsCards.widthProperty()
        );

        contactTitle.fontProperty().bind(titleFontBinding);
        productTitle.fontProperty().bind(titleFontBinding);

        var valueFontBinding = Bindings.createObjectBinding(()
            -> javafx.scene.text.Font.font(
                "System",
                javafx.scene.text.FontWeight.BOLD,
                Math.max(VALUE_MIN,
                    Math.min(VALUE_MAX, lhsCards.getWidth() * VALUE_SCALE))
            ),
            lhsCards.widthProperty()
        );

        contactValue.fontProperty().bind(valueFontBinding);
        productValue.fontProperty().bind(valueFontBinding);
    }

    /**
     * Initialises the main donut chart with styling and behaviour.
     */
    private void initMainDonut() {
        pieChart.setAnimated(false);
        pieChart.setStartAngle(START_ANGLE);

        donutHole.getStyleClass().add(STYLE_DONUT_BG);
        dotIn.getStyleClass().add(STYLE_DONUT_IN);
        dotLow.getStyleClass().add(STYLE_DONUT_LOW);

        PieChart.Data sliceIn = new PieChart.Data("In stock", 1);
        PieChart.Data sliceLow = new PieChart.Data("Low stock", 0);

        pieChart.setData(FXCollections.observableArrayList(sliceIn, sliceLow));
        pieChart.setStyle(CHART_COLORS);

        sliceIn.nodeProperty().addListener((obs, o, node) -> {
            if (node != null) {
                node.getStyleClass().add(STYLE_CHART_IN);
            }
        });

        sliceLow.nodeProperty().addListener((obs, o, node) -> {
            if (node != null) {
                node.getStyleClass().add(STYLE_CHART_LOW);
            }
        });
    }

    /**
     * Draws a mini donut chart inside a given pane.
     *
     * @param pane the pane to render the donut
     * @param active number of active items
     * @param total total number of items
     */
    private void drawMiniDonut(Pane pane, int active, int total) {
        pane.getChildren().clear();

        Circle bg = new Circle(CENTER, CENTER, RADIUS);
        bg.setFill(Color.TRANSPARENT);
        bg.setStrokeWidth(STROKE_WIDTH);
        bg.getStyleClass().add(STYLE_MINI_DONUT_BG);

        double fraction = total > 0 ? (double) active / total : 0;
        double degrees = fraction * FULL_CIRCLE;

        Arc arc = new Arc(CENTER, CENTER, RADIUS, RADIUS, START_ANGLE, -degrees);
        arc.setType(ArcType.OPEN);
        arc.setFill(Color.TRANSPARENT);
        arc.setStrokeWidth(STROKE_WIDTH);
        arc.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.BUTT);
        arc.getStyleClass().add(STYLE_MINI_DONUT_ACTIVE);

        pane.getChildren().addAll(bg, arc);
    }

    /**
     * Binds observable lists to UI updates.
     */
    private void bindData(ObservableList<Product> filteredProducts,
                          ObservableList<Person> activePersons,
                          ObservableList<Person> allPersons,
                          ObservableList<Product> allProducts) {

        updateContacts(allPersons);
        allPersons.addListener((ListChangeListener<Person>) c -> updateContacts(allPersons));

        updateProducts(allProducts);
        allProducts.addListener((ListChangeListener<Product>) c -> updateProducts(allProducts));

        refreshDonut(allProducts);
        allProducts.addListener((ListChangeListener<Product>) c -> refreshDonut(allProducts));
    }

    /**
     * Updates contact statistics and mini donut.
     */
    private void updateContacts(ObservableList<Person> allPersons) {
        long activeCount = allPersons.stream()
            .filter(person -> !person.isArchived())
            .count();

        contactValue.setText(String.valueOf(activeCount));
        contactTotal.setText("/ " + allPersons.size() + " total");
        drawMiniDonut(contactMiniChart, (int) activeCount, allPersons.size());
    }

    /**
     * Updates product statistics and mini donut.
     */
    private void updateProducts(ObservableList<Product> allProducts) {
        long activeCount = allProducts.stream()
            .filter(product -> !product.isArchived())
            .count();

        productValue.setText(String.valueOf(activeCount));
        productTotal.setText("/ " + allProducts.size() + " total");
        drawMiniDonut(productMiniChart, (int) activeCount, allProducts.size());
    }

    /**
     * Updates the main donut chart based on stock levels.
     */
    private void refreshDonut(ObservableList<Product> allProducts) {
        long low = allProducts.stream()
            .filter(product -> !product.isArchived())
            .filter(product -> product.getQuantity().value <= product.getRestockThreshold().value)
            .count();

        long activeProducts = allProducts.stream()
            .filter(product -> !product.isArchived())
            .count();

        long in = activeProducts - low;

        ObservableList<PieChart.Data> data = pieChart.getData();
        if (data != null && data.size() == 2) {
            data.get(0).setPieValue(Math.max(in + 0.0001, 0));
            data.get(1).setPieValue(Math.max(low, 0));
        }

        countLabel.setText(String.valueOf(low));
        legendInLabel.setText("In stock (" + in + ")");
        legendLowLabel.setText("Low stock (" + low + ")");
    }
}
