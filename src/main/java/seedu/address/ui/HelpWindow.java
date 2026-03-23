package seedu.address.ui;

import java.util.List;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart<Stage> {

    public static final String USERGUIDE_URL = "https://ay2526s2-cs2103t-w08-2.github.io/tp/UserGuide.html";
    public static final String DISPLAY_CONTACT_COMMANDS = "Contact Commands";
    public static final String DISPLAY_INVENTORY_COMMANDS = "Inventory Commands";
    public static final String DISPLAY_GENERAL_COMMANDS = "General Commands";

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    @FXML
    private Button copyButton;

    @FXML
    private VBox commandListContainer;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        populateCommandsToCommandListContainer();
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public void show() {
        logger.fine("Showing help page about the application.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    public void populateCommandsToCommandListContainer() {
        commandListContainer.getChildren().addAll(
                createSectionHeadingLabel(DISPLAY_CONTACT_COMMANDS),
                createCommandGroup(List.of(
                        new String[]{"Command 1", "command 1 example", "description"},
                        new String[]{"Command 2", "command 2 example", "description"},
                        new String[]{"Command 3", "command 3 example", "description"}
                )),
                createSectionHeadingLabel(DISPLAY_INVENTORY_COMMANDS),
                createCommandBox("Command 4", "command 4 example", "description"),
                createCommandBox("Command 5", "command 5 example", "description"),
                createCommandBox("Command 6", "command 6 example", "description"),
                createCommandBox("Command 7", "command 7 example", "description"),
                createSectionHeadingLabel(DISPLAY_GENERAL_COMMANDS),
                createCommandBox("Command 8", "command 8 example", "description"),
                createCommandBox("Command 9", "command 9 example", "description")
                );
    }

    private Label createSectionHeadingLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("label-help-header");
        return label;
    }

    private VBox createCommandGroup(List<String[]> commands) {
        VBox group = new VBox(0);
        group.getStyleClass().add("command-group");

        for (int i = 0; i < commands.size(); i++) {
            String[] cmd = commands.get(i);
            group.getChildren().add(createCommandBox(cmd[0], cmd[1], cmd[2]));
            if (i < commands.size() - 1) {
                Region separator = new Region();
                group.getChildren().add(separator);
            }
        }

        return group;
    }

    private HBox createCommandBox(String name, String commandEg, String description) {
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("success");

        Label commandEgLabel= new Label(commandEg);
        commandEgLabel.getStyleClass().add("warn");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label descriptionLabel = new Label(description);

        HBox row = new HBox(10, nameLabel, commandEgLabel, spacer, descriptionLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Copies the URL to the user guide to the clipboard.
     */
    @FXML
    private void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);
    }
}
